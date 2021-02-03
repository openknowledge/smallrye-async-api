/*
 * Copyright (C) open knowledge GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 */

package io.smallrye.asyncapiui.deployment;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.quarkus.bootstrap.model.AppArtifact;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.GeneratedResourceBuildItem;
import io.quarkus.deployment.builditem.HotDeploymentWatchedFileBuildItem;
import io.quarkus.deployment.builditem.LaunchModeBuildItem;
import io.quarkus.deployment.builditem.LiveReloadBuildItem;
import io.quarkus.deployment.builditem.nativeimage.NativeImageResourceBuildItem;
import io.quarkus.deployment.configuration.ConfigurationError;
import io.quarkus.deployment.pkg.builditem.CurateOutcomeBuildItem;
import io.quarkus.deployment.util.WebJarUtil;
import io.quarkus.vertx.http.deployment.HttpRootPathBuildItem;
import io.quarkus.vertx.http.deployment.NonApplicationRootPathBuildItem;
import io.quarkus.vertx.http.deployment.RouteBuildItem;
import io.quarkus.vertx.http.deployment.devmode.NotFoundPageDisplayableEndpointBuildItem;
import io.smallrye.asyncapi.ui.IndexCreator;
import io.smallrye.asyncapi.ui.Option;
import io.smallrye.asyncapiui.runtime.AsyncApiUiRecorder;
import io.smallrye.asyncapiui.runtime.AsyncApiUiRuntimeConfig;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public class AsyncApiUiProcessor {

    private static final String ASYNC_API_UI_FEATURE = "asyncapi-ui";

    private static final String ASYNC_API_UI_WEBJAR_GROUP_ID = "io.smallrye";

    private static final String ASYNC_API_UI_WEBJAR_ARTIFACT_ID = "smallrye-async-api-ui";

    private static final String ASYNC_API_UI_WEBJAR_PREFIX = "META-INF/resources/async-ui/";

    private static final String ASYNC_API_UI_FINAL_DESTINATION = "META-INF/async-api-ui-files";

    // Branding files to monitor for changes
    private static final String BRANDING_DIR = "META-INF/branding/";

    private static final String BRANDING_LOGO_GENERAL = BRANDING_DIR + "logo.png";

    private static final String BRANDING_LOGO_MODULE = BRANDING_DIR + "smallrye-async-api-ui.png";

    private static final String BRANDING_STYLE_GENERAL = BRANDING_DIR + "style.css";

    private static final String BRANDING_STYLE_MODULE = BRANDING_DIR + "smallrye-async-api-ui.css";

    private static final String BRANDING_FAVICON_GENERAL = BRANDING_DIR + "favicon.ico";

    private static final String BRANDING_FAVICON_MODULE = BRANDING_DIR + "smallrye-async-api-ui.ico";

    @BuildStep
    void feature(BuildProducer<FeatureBuildItem> feature, LaunchModeBuildItem launchMode, AsyncApiUiConfig asyncApiUiConfig) {
        if (shouldInclude(launchMode, asyncApiUiConfig)) {
            asyncApiUiConfig.path = "/asyncapi-ui";
            feature.produce(new FeatureBuildItem(ASYNC_API_UI_FEATURE));
        }
    }

    @BuildStep
    List<HotDeploymentWatchedFileBuildItem> brandingFiles() {
        return Stream
                .of(BRANDING_LOGO_GENERAL, BRANDING_STYLE_GENERAL, BRANDING_FAVICON_GENERAL, BRANDING_LOGO_MODULE,
                        BRANDING_STYLE_MODULE, BRANDING_FAVICON_MODULE)
                .map(HotDeploymentWatchedFileBuildItem::new)
                .collect(Collectors.toList());
    }

    @BuildStep
    public void getAsyncApiUiFinalDestination(BuildProducer<GeneratedResourceBuildItem> generatedResources,
            BuildProducer<NativeImageResourceBuildItem> nativeImageResourceBuildItemBuildProducer,
            BuildProducer<AsyncApiUiBuildItem> asyncapiUiBuildProducer,
            NonApplicationRootPathBuildItem nonApplicationRootPathBuildItem,
            BuildProducer<NotFoundPageDisplayableEndpointBuildItem> displayableEndpoints,
            CurateOutcomeBuildItem curateOutcomeBuildItem,
            LaunchModeBuildItem launchMode, AsyncApiUiConfig asyncApiUiConfig,
            HttpRootPathBuildItem httpRootPathBuildItem, LiveReloadBuildItem liveReloadBuildItem) throws Exception {

        if (shouldInclude(launchMode, asyncApiUiConfig)) {
            if ("/".equals(asyncApiUiConfig.path)) {
                throw new ConfigurationError(
                        "quarkus.asyncapi-ui.path was set to \"/\", this is not allowed as it blocks the application from serving anything else.");
            }

            String asyncApiPath = httpRootPathBuildItem
                    .adjustPath(nonApplicationRootPathBuildItem.adjustPath("/asyncapi-ui"));
            String asyncapiUiPath = httpRootPathBuildItem
                    .adjustPath(nonApplicationRootPathBuildItem.adjustPath(asyncApiUiConfig.path));

            AppArtifact artifact = WebJarUtil.getAppArtifact(curateOutcomeBuildItem, ASYNC_API_UI_WEBJAR_GROUP_ID,
                    ASYNC_API_UI_WEBJAR_ARTIFACT_ID);

            if (launchMode.getLaunchMode()
                    .isDevOrTest()) {
                Path tempPath = WebJarUtil.copyResourcesForDevOrTest(curateOutcomeBuildItem, launchMode, artifact,
                        ASYNC_API_UI_WEBJAR_PREFIX);
                // Update index.html
                WebJarUtil.updateFile(tempPath.resolve("index.html"),
                        generateIndexHtml(asyncApiPath, asyncapiUiPath, asyncApiUiConfig));

                asyncapiUiBuildProducer.produce(new AsyncApiUiBuildItem(tempPath.toAbsolutePath()
                        .toString(), nonApplicationRootPathBuildItem.adjustPath(asyncApiUiConfig.path)));
                displayableEndpoints
                        .produce(new NotFoundPageDisplayableEndpointBuildItem(nonApplicationRootPathBuildItem.adjustPath(
                                asyncApiUiConfig.path + "/"), "Async API UI"));

                // Handle live reload of branding files
                if (liveReloadBuildItem.isLiveReload() && !liveReloadBuildItem.getChangedResources()
                        .isEmpty()) {
                    WebJarUtil.hotReloadBrandingChanges(curateOutcomeBuildItem, launchMode, artifact,
                            liveReloadBuildItem.getChangedResources());
                }
            } else {
                Map<String, byte[]> files = WebJarUtil.copyResourcesForProduction(curateOutcomeBuildItem, artifact,
                        ASYNC_API_UI_WEBJAR_PREFIX);
                for (Map.Entry<String, byte[]> file : files.entrySet()) {
                    String fileName = file.getKey();
                    // Make sure to only include the selected theme
                    byte[] content;
                    if (fileName.endsWith("index.html")) {
                        content = generateIndexHtml(asyncApiPath, asyncapiUiPath, asyncApiUiConfig);
                    } else {
                        content = file.getValue();
                    }
                    fileName = ASYNC_API_UI_FINAL_DESTINATION + "/" + fileName;
                    generatedResources.produce(new GeneratedResourceBuildItem(fileName, content));
                    nativeImageResourceBuildItemBuildProducer.produce(new NativeImageResourceBuildItem(fileName));

                }
                asyncapiUiBuildProducer.produce(new AsyncApiUiBuildItem(ASYNC_API_UI_FINAL_DESTINATION,
                        nonApplicationRootPathBuildItem.adjustPath(asyncApiUiConfig.path)));
            }
        }
    }

    @BuildStep
    @Record(ExecutionTime.RUNTIME_INIT)
    public void registerAsyncApiUiHandler(AsyncApiUiRecorder recorder, BuildProducer<RouteBuildItem> routes,
            AsyncApiUiBuildItem finalDestinationBuildItem, AsyncApiUiRuntimeConfig runtimeConfig,
            LaunchModeBuildItem launchMode,
            AsyncApiUiConfig asyncApiUiConfig) throws Exception {

        if (shouldInclude(launchMode, asyncApiUiConfig)) {
            Handler<RoutingContext> handler = recorder.handler(finalDestinationBuildItem.getAsyncApiUiFinalDestination(),
                    finalDestinationBuildItem.getAsyncApiUiPath(), runtimeConfig);

            routes.produce(new RouteBuildItem.Builder().route(asyncApiUiConfig.path)
                    .handler(handler)
                    .nonApplicationRoute()
                    .build());
            routes.produce(new RouteBuildItem.Builder().route(asyncApiUiConfig.path + "/*")
                    .handler(handler)
                    .nonApplicationRoute()
                    .build());
        }
    }

    private byte[] generateIndexHtml(String asyncApiPath, String swaggerUiPath, AsyncApiUiConfig swaggerUiConfig)
            throws IOException {
        Map<Option, String> options = new HashMap<>();

        Map<String, String> urlsMap = null;
        options.put(Option.url, asyncApiPath);

        return IndexCreator.createIndexHtml(urlsMap, null, options);
    }

    private static boolean shouldInclude(LaunchModeBuildItem launchMode, AsyncApiUiConfig swaggerUiConfig) {
        return launchMode.getLaunchMode()
                .isDevOrTest() || swaggerUiConfig.alwaysInclude;
    }
}
