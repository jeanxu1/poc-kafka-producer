# kafka-producer-sample-app
This application contains sample code that can be used to bootstrap kafka message producer application.

## Pre-requisites
This application uses java spring-boot and spring-kafka frameworks. This readme document assumes that you have good understanding of java, maven and spring-boot concepts. 

To run this application you will need java 11 and maven. If you don't have it installed then you can install it by following instructions in below links

* java 11 - https://adoptopenjdk.net/?variant=openjdk11&jvmVariant=hotspot
* maven - https://maven.apache.org/install.html

This application is configured to connect to kafka brokers and schema-registry running on your local machine by default. If you dont have these components installed on your machine then you can install them by following below link

https://docs.confluent.io/current/quickstart/ce-quickstart.html#ce-quickstart

You can also configure the application to use lululemon's enterprise kafka cluster and schema-registry instead of local instances by changing below properties in `application.yaml` file

```
spring.kafka:
  bootstrap-servers: "localhost:9093" #set this to the broker URL

  properties:
    schema.registry.url: "http://localhost:8081"
```

## Security configuration for kafka-brokers
This application uses SASL based security for connecting to kafka brokers. If you are using local cluster and dont want to configure the security then you can just comment out below properties and change the broker port from 9093 to 9092

```
#security:
#  protocol: SASL_SSL #set this to SASL_PLAINTEXT if SSL is not enabled for brokers
#sasl:
#  mechanism: PLAIN
#  jaas:
#    config: org.apache.kafka.common.security.plain.PlainLoginModule required username="${kafka_username}" password="${kafka_password}";
```

If you are using lululemon enterprise cluster then you will need the security configuration. Also you will need to get the api key and passphrase from the kafka platform team and set below 2 environment variables before running the application

* kafka_username (set this to api key)
* kafka_password (set this to passphrase)

Please never commit these 2 values to version control. In your application you might want to explore vault or k8s secret or aws kms to store these based on your deployment platform.

## Explore the use cases
This application contains code for 3 different scenarios of publishing messages to kafka topics as described below. You can test this code by sending POST request to the REST endpoints exposed by the application for each use case.

### Produce plain json message
This sample code can be used to produce plain json message to kafka topic. This is useful in the scenario where the kafka topic is used internally by your application and no schema support is needed. If you are planning to publish information that will be used by multiple applications in the enterprise then you should always use AVRO.

To test this case you can run the application using below maven command from the root project folder. (Note: **Do not forget to set the environment variables for broker security if you are using brokers with SASL config enabled.**)

` mvn spring-boot:run -Dspring-boot.run.profiles=plain` 

After starting the application you can test the code by sending below POST request to endpoint `/publish-plain`

```
curl -X POST -H "Content-Type: application/json" \
 -d '{"productId":40,"productName":"plain-product","productDescription":"plain-product-description","colors":[{"colorId":1,"colorName":"sky"},{"colorId":2,"colorName":"aqua"}]}' \
http://localhost:8087/publish-plain
```

This will produce the plain message in `product-plain` topic. You can change the topic name in application.yaml file. If you are using enterprise kafka cluster then make sure that the topic exists before you send the POST request.

### Produce AVRO message
This sample code can be used to produce AVRO message to kafka topic. This is useful in scenario where you are compiling the message using custom code and want to publish in AVRO format. 

Before you can publish the AVRO message the AVRO schema file needs to be created and registered with schema-registry. The schema file used by this application is available @ `resources/avro/product.avsc`. This application uses confluent's schema-registry maven plugin that can automatically register the new versions of the schema with schema-registry. You can execute below command to register the schema with schema-registry using this plugin

`mvn schema-registry:register`

You can also use the REST api provided by schema-registry to register the AVRO schema versions. More details are available here https://docs.confluent.io/current/schema-registry/using.html

Note: The subject name for the schema should be `topic_name-value`. E.g. If topic name is `product` then subject name for schema should be `product-value`

To test this case you can run the application using below maven command from the root project folder. (Note: **Do not forget to set the environment variables for broker security if you are using brokers with SASL config enabled.**)

` mvn spring-boot:run -Dspring-boot.run.profiles=avro` 

After starting the application you can test the code by sending below POST request to endpoint `/publish-avro-specific`

```
curl -X POST -H "Content-Type: application/json" \
 -d '{"id":"30","name":"product-avro","description":"product-avro-description","colors":[{"id":"1","name":"red"},{"id":"2","name":"yellow"}]}' \
http://localhost:8087/publish-avro
```


This will publish AVRO message to topic `product`. You can change the topic name in application.yaml file. If you are using enterprise kafka cluster then make sure that the topic exists before you send the POST request.

This code uses auto-generated class files to compile the AVRO message. These class files get generated from the AVRO schema file during the application build in avro.schema package.

### Produce AVRO message using existing json

This sample code can be used to produce AVRO message to kafka topic. This is useful in scenario where you have an existing json message in correct format that matches the AVRO schema and just need to post it to kafka topic in AVRO format. 

As mentioned in above section, before you can publish the AVRO message the AVRO schema file needs to be created and registered with schema-registry.

To test this case you can run the application using below maven command from the root project folder. (Note: **Do not forget to set the environment variables for broker security if you are using brokers with SASL config enabled.**)

` mvn spring-boot:run -Dspring-boot.run.profiles=avro` 

After starting the application you can test the code by sending below POST request to endpoint `/publish-avro-generic`

```
curl -X POST -H "Content-Type: application/json" \
 -d '{"productId":60,"productName":"avro-product-generic","productDescription":{"string":"avro-product-generic-description"},"colors":[{"colorId":1,"colorName":"purple"},{"colorId":2,"colorName":"pink"}]}' \
http://localhost:8087/publish-avro-generic
```


This will publish AVRO message to topic `product`. You can change the topic name in application.yaml file. If you are using enterprise kafka cluster then make sure that the topic exists before you send the POST request.
This code uses auto-generated class files to compile the AVRO message. These class files get generated from the AVRO schema file during the application build in avro.schema package.