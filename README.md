# auth-service

This service caters to user-create and login use cases

You can use Login and Signup requests on the attached postman file (Melih-v1.postman_collection.json), located under root directory
Default Port: 8081
Also, swagger is available on http://localhost:8081/swagger-ui/index.html

Api version is v1, and it is provided by Accept header "application/vnd.melih.api.v1+json"

There are 2 locales, Turkish and English(default). If you want to change the locale, simply add Locale(en or tr) header to your requests.

There are 2 built-in roles, which are created automatically on roles table via data.sql in resource folder. To create seller, simply set roleId = 1 on the signup request body. For creating buyer, you should set it to 2

Successfull login responds with signed JWT token, which can be used on auction service requests.

H2 database is available on http://localhost:8081/h2-console/  via username:sa and password:password
