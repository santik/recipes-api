# Recipes application

A simple application that allows to save, view, update, delete and search recipes.
## API

### Specifications
Repository contains API specifications in OpenAPI format.
The overview can be viewed by the url:
[Swagger](https://petstore.swagger.io/?url=https://raw.githubusercontent.com/santik/recipes-api/master/openapi/RecipesAPI.json)
### Implementation
For implementation WebFlux library is used. It allows to serve requests and responses asynchronously and has a lot of tools to provide stabily of the application.

## Datastore
To store the data MongoDB is used.
The application starts up the embedded MongoDB server for testing.

### Database operations
Application supports the standard CRUD operations for recipes and also search operation including full text search by recipe instructions.
In code it is achieved by creating 2 separate (sub)repositories:

- CRUD repository
- Search repository in combination with query builder

## Application
Application is a simple maven application with Spring Boot as a parent. So running and testing is very simple:
Running all the tests and building the application:
> mvn clean package

or look at [Actions tab](https://github.com/santik/recipes-api/actions) of the repository for the latest builds

### Tests
The codebase contains multiple Unit and Integration Tests.
In combination, it provides the coverage almost 100%.
