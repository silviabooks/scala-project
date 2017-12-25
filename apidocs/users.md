# Users resource

* [List users](#get-list-of-users)
* [Create user](#create-an-user)
* [Get user](#get-an-user)

## Get list of users
`GET /users will return a list of the users stored`

Example of response:

```json
[
  {
    "telNumber": "1235",
    "name": "Silvia",
    "isAdmin": true,
    "email": "silvia@scala",
    "_id": "5a4120790553d00ceab7d7c8"
  }, ... 
]
 ```
**This could be achieved only by an authenticated admin**
 
## Create an user

`POST /users will return 200 OK if the given JSON object user will be correctly stored`

Example of JSON payload:

```json
  {
    "telNumber": "1235",
    "name": "Silvia",
    "isAdmin": true,
    "email": "silvia@scala",
    "_id": ""
  }, ...
```

## Get an user

`GET /users/:id`

Get the user with the given id

**This could be achieved only by an authenticated  user (an admin or the given user that is identified by the `:id`)**
