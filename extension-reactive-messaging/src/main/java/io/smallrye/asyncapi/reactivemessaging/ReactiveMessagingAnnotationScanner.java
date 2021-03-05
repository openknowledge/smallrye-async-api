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
package io.smallrye.asyncapi.reactivemessaging;

import static io.smallrye.asyncapi.reactivemessaging.ReactiveMessagingConstants.INCOMING;
import static io.smallrye.asyncapi.reactivemessaging.ReactiveMessagingConstants.INCOMING_PREFIX;
import static io.smallrye.asyncapi.reactivemessaging.ReactiveMessagingConstants.OUTGOING_PREFIX;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.IndexView;
import org.jboss.jandex.MethodInfo;

import io.smallrye.asyncapi.core.runtime.scanner.spi.AbstractAnnotationScanner;
import io.smallrye.asyncapi.core.runtime.scanner.spi.AnnotationScannerContext;
import io.smallrye.asyncapi.reactivemessaging.io.channel.ChannelReader;
import io.smallrye.asyncapi.reactivemessaging.io.server.ServerReader;
import io.smallrye.asyncapi.spec.models.AsyncAPI;
import io.smallrye.asyncapi.spec.models.channel.ChannelItem;
import io.smallrye.asyncapi.spec.models.channel.Channels;

public class ReactiveMessagingAnnotationScanner extends AbstractAnnotationScanner {

  @Override
  public String getName() {
    return "Reactive Messaging";
  }

  @Override
  public AsyncAPI scan(final AnnotationScannerContext context, final AsyncAPI aai) {

    ReactiveMessagingContext rmContext = new ReactiveMessagingContext(
        processProperties(INCOMING_PREFIX, context), processProperties(OUTGOING_PREFIX, context));

    processServer(rmContext, aai);

    processChannels(context, rmContext, aai);

    return aai;
  }

  private void processServer(final ReactiveMessagingContext rmContext,
      final AsyncAPI aai) {
    ServerReader serverReader = new ServerReader(rmContext, aai);
    serverReader.readServers();
  }

  private void processChannels(final AnnotationScannerContext context, final ReactiveMessagingContext rmContext, final AsyncAPI aai) {
    getOutgoingMethods(context.getIndex())
        .forEach(methodInfo -> processOutgoingChannel(context, rmContext, methodInfo, aai.getChannels()));
    getIncomingMethods(context.getIndex())
        .forEach(methodInfo -> processIncomingChannel(context, rmContext, methodInfo, aai.getChannels()));
  }

  private void processOutgoingChannel(final AnnotationScannerContext context, final ReactiveMessagingContext rmContext, final MethodInfo method, Channels channels) {
    AnnotationInstance channelItemAnnotation = method.annotation(ReactiveMessagingConstants.OUTGOING);

    ChannelItem channelItem = ChannelReader.readOutgoing(context, rmContext, channelItemAnnotation);
    channels.addChannel(channelItem.getChannel(), channelItem);
  }

  private List<MethodInfo> getOutgoingMethods(final IndexView index) {
    return index.getAnnotations(ReactiveMessagingConstants.OUTGOING)
        .stream()
        .map(AnnotationInstance::target)
        .map(annotationTarget -> annotationTarget.asMethod())
        .distinct() // CompositeIndex instances may return duplicates
        .collect(Collectors.toList());
  }

  private void processIncomingChannel(final AnnotationScannerContext context, final ReactiveMessagingContext rmContext, final MethodInfo method, Channels channels) {
    AnnotationInstance channelItemAnnotation = method.annotation(INCOMING);

    ChannelItem channelItem = ChannelReader.readIncoming(context, rmContext, channelItemAnnotation);
    channels.addChannel(channelItem.getChannel(), channelItem);
  }

  private List<MethodInfo> getIncomingMethods(final IndexView index) {
    return index.getAnnotations(INCOMING)
        .stream()
        .map(AnnotationInstance::target)
        .map(annotationTarget -> annotationTarget.asMethod())
        .distinct() // CompositeIndex instances may return duplicates
        .collect(Collectors.toList());
  }

  private List<ReactiveMessagingProperty> processProperties(final String prefix, final AnnotationScannerContext context) {
    HashMap<String, String> properties = new HashMap<>();

    Config config = ConfigProvider.getConfig(context.getClassLoader());
    config.getConfigSources().forEach(configSource -> {
      properties.putAll(configSource.getProperties());
    });

    List<ReactiveMessagingProperty> channels = new ArrayList<>();

    properties.forEach((o, o2) -> {
      String key = String.valueOf(o);
      String value = String.valueOf(o2);

      if (key.startsWith(prefix)) {
        String tmp = key.substring(prefix.length());
        String channel = tmp.substring(0, tmp.indexOf('.'));
        String attribute = tmp.substring(tmp.indexOf('.') + 1);

        ReactiveMessagingProperty property = new ReactiveMessagingProperty(prefix, channel, attribute, value);
        channels.add(property);
      }
    });

    return channels;
  }
}
