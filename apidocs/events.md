# Events resource

* [List events](#get-list-of-events)
* [Create event](#create-an-event)
* [Search event](#search-an-event)
* [get categories](#get-list-of-categories)
* [Get event](#get-an-event)
* [Edit event](#edit-an-event)
* [Delete event](#delete-an-event)

## Get List of Events
`GET /events will return a list of the events stored`

Example of response:

```json
[
  {
    "name": "Roger Waters",
    "quantity": 197,
    "description": "The wall",
    "_id": "5a4120790553d00ceab7d7bf",
    "price": 60.5,
    "date": "2018-07-22T22:00:00",
    "category": "Rock"
  }, { ...
 ]
 ```
 
## Create an Event


`POST /events will return 200 OK if the given JSON object event will be correctly stored`

Example of JSON payload:

```json
  {
    "name": "Roger Waters",
    "quantity": 197,
    "description": "The wall",
    "_id": "",
    "price": 60.5,
    "date": "2018-07-22T22:00:00",
    "category": "Rock"
  }, ...
```

**This could be achieved only by an authenticated admin.**

## Search an event

`GET /events/:somestring`

Returns a list of events that matches :somestring

## Get list of categories

`GET /events/categories`

Returns a list of all the categories for the events stored

```json
[
  "Classic",
  "Reggae",
  "Rock"
]
```

## Get an event

`GET /events/:id`

Get the event with the given Id

## Edit an event

`PUT /events/:id` takes an Event object (with a correct objectId) to be edited.

Returns `200 OK` if the update was correct.

**This could be achieved only by an authenticated admin.**

## Delete an event

`DELETE /events/:id` deletes the Event with the given `:id`

**This could be achieved only by an authenticated admin.**
