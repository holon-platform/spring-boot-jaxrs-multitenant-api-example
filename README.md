# RESTful API application in a multi-tenant architecture using Spring Boot and the Holon platform (JWT tenant implementation)

This is the **jwt** branch of the source code of the API application described in the article: [Multi-tenancy in the API world made easy](https://holon-platform.com/blog/multi-tenancy-in-the-api-world-made-easy/). The **master** branch implementation uses a custom HTTP header to pass the tenant identifier. In this branch we use **JWT** to provide the tenant identifier as a _JSON token claim_: see [here](https://github.com/holon-platform/spring-boot-jaxrs-multitenant-api-example/blob/jwt/src/main/java/com/holonplatform/example/Application.java#L32) how we modified the master branch to make it works.

# Running

To run the application you can use the Spring Boot Maven plugin:

```
mvn spring-boot:run
```
