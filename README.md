# Auth Service

This service caters to signup and login use cases. 

## Postman Collection
You can use the Login and Signup requests provided in the attached Postman file (`Melih-v1.postman_collection.json`), located under the root directory.

## Default Port
Default Port: 8081

## Swagger
Swagger is available on [http://localhost:8081/swagger-ui/index.html](http://localhost:8081/swagger-ui/index.html)

## API Version
Api version is v1, and it is provided by the Accept header `application/vnd.melih.api.v1+json`.

## Locales
There are 2 locales: Turkish and English (default). To change the locale, simply add a `Locale` header to your requests with values `en` or `tr`.

## Built-in Roles
There are 2 built-in roles, automatically created on the roles table via `data.sql` in the resource folder.
- To create a seller, set `roleId = 1` on the signup request body.
- To create a buyer, set it to `2`.

## Successful Login
A successful login responds with a signed JWT token, which can be used on auction service requests.

## H2 Database
H2 database is available on [http://localhost:8081/h2-console/](http://localhost:8081/h2-console/) with the following credentials:
- Username: sa
- Password: password

## Tests
Tests are located under src/test folder
