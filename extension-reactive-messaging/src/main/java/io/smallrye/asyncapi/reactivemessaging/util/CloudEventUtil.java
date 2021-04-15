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
package io.smallrye.asyncapi.reactivemessaging.util;

import io.smallrye.asyncapi.core.api.models.schema.SchemaImpl;
import io.smallrye.asyncapi.core.runtime.scanner.spi.AnnotationScannerContext;
import io.smallrye.asyncapi.spec.annotations.schema.SchemaType;
import io.smallrye.asyncapi.spec.models.schema.Schema;
import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.DotName;
import org.jboss.jandex.Type;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class CloudEventUtil {

  private final static DotName CLOUD_EVENT = DotName.createSimple("io.cloudevents.CloudEvent");
  private final static DotName CLOUD_EVENT_V1 = DotName.createSimple("io.cloudevents.v1.CloudEventImpl");
  private final static DotName CLOUD_EVENT_V2 = DotName.createSimple("io.cloudevents.v02.CloudEventImpl");
  private final static DotName CLOUD_EVENT_V3 = DotName.createSimple("io.cloudevents.v03.CloudEventImpl");

  public static final List<DotName> SUPPORTED_DOTNAMES = Collections.unmodifiableList(Arrays.asList(
      CLOUD_EVENT,
      CLOUD_EVENT_V1,
      CLOUD_EVENT_V2,
      CLOUD_EVENT_V3
  ));

  private static final String COMPONENTS_SCHEMAS = "#/components/schemas/";

  private static final String COMPONENTS_SCHEMAS_FORMAT = "#/components/schemas/%s";

  private static final String CLOUD_EVENT_FORMAT = "CloudEvent%s";

  public static Schema readCloudEventReturnType(final AnnotationInstance instance, final AnnotationScannerContext context){
    try {
      Type type = instance.target()
          .asMethod()
          .returnType()
          .asParameterizedType()
          .arguments()
          .get(0);

      return createCloudEventSchema(new SchemaImpl().ref(createRef(type)), context, instance);
    } catch(Exception e) {
      return new SchemaImpl();
    }
  }

  public static Schema readCloudEventParameter(final AnnotationInstance instance, final AnnotationScannerContext context){
    try {
      List<Type> parameters = instance.target()
          .asMethod()
          .parameters();

      Optional<Type> first = parameters.stream()
          .filter(CloudEventUtil::isCloudEvent)
          .filter(TypeUtil::isParameterized)
          .findFirst();

      if (!first.isPresent()){
        return new SchemaImpl();
      }

      Schema data = readCloudEventInterface(first.get());
      if (data.equals(new SchemaImpl())){
        return createCloudEventSchema(new SchemaImpl().ref(createRef(first.get())), context, instance);
      }

      return createCloudEventSchema(data, context, instance);
    } catch(Exception e) {
      return null;
    }
  }

  private static Schema readCloudEventInterface(final Type type){

    if (!type.name().equals(CLOUD_EVENT)){
      return null;
    }

    List<Type> arguments = type.asParameterizedType().arguments();

    if (arguments.size() != 2){
      return null;
    }

    Type innerType = arguments.get(1);

    return TypeUtil.readPrimitiveClass(innerType);
  }

  public static String createRef(final Type type){
    return COMPONENTS_SCHEMAS + type.name().local();
  }

  /**
   * Creates a cloudevent schema with the given data payload
   *
   * @param data payload of the channel
   * @return cloudevent
   */
  public static Schema createCloudEventSchema(final Schema data, final AnnotationScannerContext context, final AnnotationInstance instance) {
    Schema schema = new SchemaImpl()
        .addProperty("id", new SchemaImpl()
            .type(SchemaType.STRING))
        .addProperty("source", new SchemaImpl()
            .type(SchemaType.STRING)
            .format("uri"))
        .addProperty("specversion", new SchemaImpl()
            .type(SchemaType.STRING)
            .enumeration(Arrays.asList("v1.0.1")))
        .addProperty("type", new SchemaImpl()
            .type(SchemaType.STRING))
        .addProperty("datacontenttype", new SchemaImpl()
            .type(SchemaType.STRING))
        .addProperty("dataschema", new SchemaImpl()
            .type(SchemaType.STRING)
            .format("uri"))
        .addProperty("subject", new SchemaImpl()
            .type(SchemaType.STRING))
        .addProperty("dataschema", new SchemaImpl()
            .type(SchemaType.STRING)
            .format("uri"))
        .addProperty("data", data);

    String name = "";
    if (data.getRef() != null && !data.getRef().isEmpty()){
      name = data.getRef().substring(COMPONENTS_SCHEMAS.length());
    } else if (data.getName() != null && !data.getName().isEmpty()){
      name = data.getName();
    } else {
      List<Type> parameters = TypeUtil.getParameters(instance);
      Type type = parameters.get(0);

      Type msg = type.asParameterizedType()
          .arguments()
          .get(1);

      name = msg.name().local();
    }

    String key = String.format(CLOUD_EVENT_FORMAT, name);

    context.getAsyncAPI()
        .getComponents()
        .addSchema(key, schema);

    String ref = String.format(COMPONENTS_SCHEMAS_FORMAT, key);
    return new SchemaImpl().ref(ref);
  }

  public static boolean isCloudEvent(final AnnotationInstance instance){
    Type type = TypeUtil.getReturnType(instance);

    if(!TypeUtil.isParameterized(type)){
      return false;
    }

    return isCloudEvent(type);
  }

  public static boolean isCloudEvent(final Type type) {
    return SUPPORTED_DOTNAMES.contains(type.name());
  }
}
