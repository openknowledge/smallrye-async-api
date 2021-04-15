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
package io.smallrye.asyncapi.reactivemessaging.io.schema;

import io.smallrye.asyncapi.core.runtime.scanner.spi.AnnotationScannerContext;
import io.smallrye.asyncapi.reactivemessaging.util.CloudEventUtil;
import io.smallrye.asyncapi.spec.models.schema.Schema;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationValue;

import java.util.concurrent.atomic.AtomicBoolean;

public class CloudEventConfigReader {

  public static Schema readCloudEventFromConfig(final AnnotationInstance instance, final AnnotationScannerContext context, final Schema payload) {
    AnnotationValue value = instance.value("value");

    if (isCloudEvent(context, value)) {
      return CloudEventUtil.createCloudEventSchema(payload, context, instance);
    }

    return payload;
  }

  /**
   * Checks whether the channel is defined as a cloud-event via config
   *
   * @param context annotation context
   * @param value current annotation value
   * @return whether the channel is defined as a cloud-event via config
   */
  public static boolean isCloudEvent(final AnnotationScannerContext context, final AnnotationValue value) {
    AtomicBoolean containsCloudEvent = new AtomicBoolean(false);
    Config config = ConfigProvider.getConfig(context.getClassLoader());
    config.getConfigSources()
        .forEach(configSource -> {
          if (hasCloudEventProperty(configSource, value)) {
            containsCloudEvent.set(true);
          }
        });

    return containsCloudEvent.get();
  }

  private static boolean hasCloudEventProperty(final ConfigSource configSource, final AnnotationValue value) {
    String format = String.format("%s.cloud-events", value.value());

    return configSource.getProperties()
        .entrySet()
        .stream()
        .anyMatch(stringStringEntry -> stringStringEntry.getKey().contains(format));
  }
}
