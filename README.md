# RESTful API application in a multi-tenant architecture using Spring Boot and the Holon platform (JWT tenant implementation)

This is the **jwt** branch of the source code of the API application described in the article: [Building RESTful API in a multi-tenant architecture: the easy way](https://holon-platform.com/blog/multi-tenancy-in-the-api-world-made-easy/). The **master** branch implementation uses a custom HTTP header to pass the tenant identifier. In this branch we use **JWT** to provide the tenant identifier as a _JSON token claim_. 

# Running

To run the application you can use the Spring Boot Maven plugin:

```
mvn spring-boot:run
```
