# Recipes application

A simple application that allows to save, view, update, delete and search recipes.
## API

### Specifications
Repository contains API specifications in OpenAPI format.
The overview can be viewed by the url:
>>>>>>>
### Implementation
For implementation WebFlux library is used. It allows to serve requests and responses asyncroniosly and has a lot of tools to provide stabily of the application.

## Datastore
To store the data MongoDB is used.
For testing application strats up the embedded MongoDB server.

### Database operations
Apllication supports the standard CRUD operations for recipes and also search operation including full text search by recipe instructions.
In code it is achieved by creating 2 separate repositories:

- CRUD repository
- Search repository in combination with query builder

## Application
Applicaton is a simple maven application with Spring Boot as parent. So running and testing is very simple:
Running all the tests and building the application:
> mvn clean package

### Tests
The codebase contains multiple Unit and Integration Tests.
In combination it provides the coverage almost 100%.
