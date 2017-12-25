# Tickets resource

* [List tickets](#get-list-of-tickets)
* [Create ticket](#create-a-ticket)
* [Get ticket](#get-a-ticket)
* [Delete ticket](#delete-a-ticket)

## Get List of Tickets
`GET /tickets will return a list of the tickets stored`

**This could be achieved only by an authenticated user. If the user is an Admin it returns all the tickets, else just the user's ones**

Example of response:

```json
[
  {
    "ticketHolder": "Aleskandro",
    "boughtFrom": "5a4120790553d00ceab7d7bf",
    "event": "5a4120790553d00ceab7d0ac",
    "_id": "5a4120790553d00ceab7d6ac",
  }, { ...
 ]
 ```
 
## Create a Ticket

`POST /tickets will return 200 OK if the given JSON object event will be correctly stored`

Example of JSON payload:

```json
  {
    "ticketHolder": "Aleskandro",
    "boughtFrom": "5a4120790553d00ceab7d7bf",
    "event": "5a4120790553d00ceab7d0ac",
    "_id": "5a4120790553d00ceab7d6ac",
  }
  ```
**This could be achieved only by an authenticated user, the boughtFrom will be checked.**

## Get a Ticket 

get a ticket with the given id

`GET /tickets/:id`

Returns the ticket with the given Id

**This could be achieved only by an authenticated user. If the user is an Admin or it is the one whom bought the ticket it returns the ticket, else a 404**

## Delete a ticket

`DELETE /tickets/:id` deletes the Ticket with the given `:id`

**This could be achieved only by an authenticated admin.**
