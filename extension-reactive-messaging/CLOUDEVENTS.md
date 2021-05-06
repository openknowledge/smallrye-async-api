# CloudEvents with MicroProfile ReactiveMessaging

## Via Kafka Configuration

The SmallRye Reactive Messaging Kafka Connector supports Cloud Events. The connector sends the outbound record as Cloud Events if:

* the message metadata contains an `io.smallrye.reactive.messaging.ce.OutgoingCloudEventMetadata instance,
* the channel configuration defines the cloud-events-type and cloud-events-source attribute.

**Sample 1 - Outgoing Method**
````java

@AsyncAPI(
    asyncapi = "2.0.0",
    info = @Info(
        title = "CloudEventsService",
        version = "1.0.0"
    ),
    defaultContentType = "application/json"
)
public class CloudEventsService {

  @Outgoing("greeting-out")
  public Greeting sendGreeting() {
    return new Greeting("Hello World");
  }

  @Schema
  public class Greeting {
    
    @Schema(required = true, example = "Hello World")
    private String greet;

    public Greeting(final String greet) {
      this.greet = greet;
    }

    public String getGreet() {
      return greet;
    }

    public void setGreet(final String greet) {
      this.greet = greet;
    }
  }
}
````

**Sample 1 - `application.properties`**
```properties
mp.messaging.outgoing.greeting-out.cloud-events-source=cloudevent-service
mp.messaging.outgoing.greeting-out.cloud-events-type=greeting.v1
mp.messaging.outgoing.greeting-out.topic=greeting
```

**Sample 1 - AsyncAPI document**
````json
{
  "asyncapi": "2.0.0",
  "defaultContentType": "application/json",
  "info": {
    "title": "CloudEventsService",
    "version": "1.0.0"
  },
  "channels": {
    "greeting": {
      "publish": {
        "operationId": "greeting-out",
        "message": {
          "payload": {
            "$ref": "#/components/schemas/CloudEventGreeting"
          },
          "name": "sendGreetingPayload"
        }
      }
    }
  },
  "components": {
    "schemas": {
      "Greeting": {
        "required": [
          "greet"
        ],
        "type": "object",
        "properties": {
          "greet": {
            "type": "string",
            "example": "Hello World"
          }
        }
      },
      "CloudEventGreeting": {
        "properties": {
          "id": {
            "type": "string"
          },
          "source": {
            "format": "uri",
            "type": "string"
          },
          "specversion": {
            "enum": [
              "v1.0.1"
            ],
            "type": "string"
          },
          "type": {
            "type": "string"
          },
          "datacontenttype": {
            "type": "string"
          },
          "dataschema": {
            "format": "uri",
            "type": "string"
          },
          "subject": {
            "type": "string"
          },
          "data": {
            "$ref": "#/components/schemas/Greeting"
          }
        }
      }
    }
  }
}
````
## Via method return type

The AsyncAPI library generates a CloudEvent Schema from method parameter/returntype

**Sample 2 - Outgoing Method**
````java
@AsyncAPI(
    asyncapi = "2.0.0",
    info = @Info(
        title = "CloudEventsService",
        version = "1.0.0"
    ),
    defaultContentType = "application/json"
)
public class CloudEventsService {

  @ChannelItem(
      channel = "prices",
      publish = @Operation(
          operationId = "generated-price",
          message = @Message(
              contentType = "text/plain",
              summary = "A random price"
          )
      )
  )
  @Outgoing("generated-price")
  public CloudEventImpl<RandomNumber> generate() {
    return CloudEventBuilder.<RandomNumber>builder()
        .withType("org.acme.generate.v1")
        .withId(UUID.randomUUID().toString())
        .withTime(ZonedDateTime.now())
        .withSource(URI.create("acme.org"))
        .withData(new RandomNumber())
        .build();
  }
  
  @Schema
  public class RandomNumber{

    @Schema(required = true, example = "2")
    private int number;

    public RandomNumber() {
      this.number = new Random().nextInt();
    }

    public int getNumber() {
      return number;
    }

    public void setNumber(final int number) {
      this.number = number;
    }
  }
}
````

**Sample 2 - AsyncAPI document**
````json
{
  "asyncapi" : "2.0.0",
  "defaultContentType" : "application/json",
  "info" : {
    "title" : "CloudEventsService",
    "version" : "1.0.0"
  },
  "channels" : {
    "prices" : {
      "publish" : {
        "operationId" : "generated-price",
        "message" : {
          "payload" : {
            "$ref" : "#/components/schemas/CloudEventRandomNumber"
          },
          "contentType" : "text/plain",
          "name" : "generatePayload",
          "summary" : "A random price"
        }
      }
    }
  },
  "components" : {
    "schemas" : {
      "RandomNumber" : {
        "required" : [ "number" ],
        "type" : "object",
        "properties" : {
          "number" : {
            "format" : "int32",
            "type" : "integer",
            "example" : 2
          }
        }
      },
      "CloudEventRandomNumber" : {
        "properties": {
          "id": {
            "type": "string"
          },
          "source": {
            "format": "uri",
            "type": "string"
          },
          "specversion": {
            "enum": [
              "v1.0.1"
            ],
            "type": "string"
          },
          "type": {
            "type": "string"
          },
          "datacontenttype": {
            "type": "string"
          },
          "dataschema": {
            "format": "uri",
            "type": "string"
          },
          "subject": {
            "type": "string"
          },
          "data": {
            "$ref": "#/components/schemas/RandomNumber"
          }
        }
      }
    }
  }
}

````
