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

package io.smallrye.asyncapi.core.runtime.scanner;

import java.util.List;
import java.util.stream.Collectors;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.IndexView;
import org.jboss.jandex.MethodInfo;

import io.smallrye.asyncapi.core.runtime.scanner.spi.AbstractAnnotationScanner;
import io.smallrye.asyncapi.core.runtime.scanner.spi.AnnotationScannerContext;
import io.smallrye.asyncapi.spec.models.AsyncAPI;

public class DefaultAnnotationScanner extends AbstractAnnotationScanner {

    public String getName() {
        return "Default";
    }

    public AsyncAPI scan(final AnnotationScannerContext context, final AsyncAPI aai) {

        processChannelsMethods(context, aai);

        processMessagesMethod(context, aai);

        processParametersMethod(context, aai);

        processSecuritySchemesClass(context, aai);

        processMessageTraitMethod(context, aai);

        processOperationTraitMethod(context, aai);

        return aai;
    }

    private void processChannelsMethods(final AnnotationScannerContext context, final AsyncAPI aai) {
        getChannelsMethods(context.getIndex())
                .forEach(methodInfo -> processChannelItem(context, methodInfo, aai.getChannels()));
    }

    private List<MethodInfo> getChannelsMethods(IndexView index) {
        return index.getAnnotations(DefaultScannerConstants.CHANNEL_ITEM)
                .stream()
                .map(AnnotationInstance::target)
                .map(annotationTarget -> annotationTarget.asMethod())
                .distinct() // CompositeIndex instances may return duplicates
                .collect(Collectors.toList());
    }

    private void processMessagesMethod(final AnnotationScannerContext context, final AsyncAPI aai) {
        getMessageMethods(context.getIndex()).forEach(methodInfo -> processMessages(context, methodInfo, aai));
    }

    private List<MethodInfo> getMessageMethods(IndexView index) {
        return index.getAnnotations(DefaultScannerConstants.MESSAGE)
                .stream()
                .map(AnnotationInstance::target)
                .map(annotationTarget -> annotationTarget.asMethod())
                .distinct() // CompositeIndex instances may return duplicates
                .collect(Collectors.toList());
    }

    private void processParametersMethod(final AnnotationScannerContext context, final AsyncAPI aai) {
        getParameterMethods(context.getIndex()).forEach(methodInfo -> processParameters(context, methodInfo, aai));
    }

    private List<MethodInfo> getParameterMethods(IndexView index) {
        return index.getAnnotations(DefaultScannerConstants.PARAMETER)
                .stream()
                .map(AnnotationInstance::target)
                .map(annotationTarget -> annotationTarget.asMethod())
                .distinct() // CompositeIndex instances may return duplicates
                .collect(Collectors.toList());
    }

    private void processSecuritySchemesClass(final AnnotationScannerContext context, final AsyncAPI aai) {
        getSecuritySchemesClass(context.getIndex()).forEach(classInfo -> processSecuritySchemes(context, classInfo, aai));
    }

    private List<ClassInfo> getSecuritySchemesClass(IndexView index) {
        return index.getAnnotations(DefaultScannerConstants.SECURITY_SCHEMES)
                .stream()
                .map(AnnotationInstance::target)
                .map(annotationTarget -> annotationTarget.asClass())
                .distinct() // CompositeIndex instances may return duplicates
                .collect(Collectors.toList());
    }

    private void processMessageTraitMethod(final AnnotationScannerContext context, final AsyncAPI aai) {
        getMessageTraitMethods(context.getIndex()).forEach(methodInfo -> processMessageTraits(context, methodInfo, aai));
    }

    private List<MethodInfo> getMessageTraitMethods(IndexView index) {
        return index.getAnnotations(DefaultScannerConstants.MESSAGE_TRAITS)
                .stream()
                .map(AnnotationInstance::target)
                .map(annotationTarget -> annotationTarget.asMethod())
                .distinct() // CompositeIndex instances may return duplicates
                .collect(Collectors.toList());
    }

    private void processOperationTraitMethod(final AnnotationScannerContext context, final AsyncAPI aai) {
        getOperationTraitMethods(context.getIndex()).forEach(methodInfo -> processOperationTraits(context, methodInfo, aai));
    }

    private List<MethodInfo> getOperationTraitMethods(IndexView index) {
        return index.getAnnotations(DefaultScannerConstants.OPERATION_TRAITS)
                .stream()
                .map(AnnotationInstance::target)
                .map(annotationTarget -> annotationTarget.asMethod())
                .distinct() // CompositeIndex instances may return duplicates
                .collect(Collectors.toList());
    }
}
