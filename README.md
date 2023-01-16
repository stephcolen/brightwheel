# Brightwheel Interview Project

This application implements a web API that receives and processes device
readings.

These devices record a count at arbitrary intervals and send readings over the internet to a centralized server for processing
and storage.

## Installation
1. Install [homebrew](https://brew.sh/)
2. Install [openjdk](https://formulae.brew.sh/formula/openjdk)
3. Install [mvn](https://formulae.brew.sh/formula/maven)
4. Clone this repo locally and run `mvn spring-boot:run` from the root of the repository

## Usage
A Postman collection is included for convenience [here](https://github.com/stephcolen/brightwheel/blob/main/src/main/resources/brightwheel.postman_collection.json)

***
### `POST /create` Create reading 
Save a list of readings for the given device. Requires an `id` and a list of `readings`, which each have a `timestamp` and a `count`.

**Example**

The following saves the first two readings for device `my-id-1`
```shell
curl --location --request POST 'http://localhost:8000/create' \
--header 'Content-Type: application/json' \
--data-raw '{
    "id": "my-id-1",
    "readings": [
        {
            "timestamp": "2021-09-29T16:08:15+01:00",
            "count": 2
        },
        {
            "timestamp": "2021-09-29T16:09:15+01:00",
            "count": 15
        }
    ]
}'
```
Sending a subsequent request with a different timestamp would append another reading for device `my-id-1`
```shell
curl --location --request POST 'http://localhost:8000/create' \
--header 'Content-Type: application/json' \
--data-raw '{
  "id": "my-id-1",
  "readings": [
    {
      "timestamp": "2021-09-29T16:09:15+03:00",
      "count": 5
    }
  ]
}'
```

#### Notes

* Readings with duplicate timestamps are silently ignored
* Device readings are reset on application restart
* Returns `HTTP 400 Bad Request` if the request JSON body is not properly formed
***

### `GET /cumulative-count` Get Cumulative Count
Get the cumulative count of all readings for a specific device. Requires the `id` in the request body.

**Example**
```shell
curl --location --request POST 'http://localhost:8000/create' \
--header 'Content-Type: application/json' \
--data-raw '{
    "id": "my-id-1",
    "readings": [
        {
            "timestamp": "2021-09-29T16:08:15+01:00",
            "count": 2
        },
        {
            "timestamp": "2021-09-29T16:09:15+01:00",
            "count": 15
        }
    ]
}' && 
curl --location --request GET 'http://localhost:8000/cumulative-count' \
--header 'Content-Type: application/json' \
--data-raw '{
    "id": "my-id-1"
}'
```
The last command will return:
```json
{
    "cumulativeCount": 17
}
```

#### Notes

* Returns `0` if no readings have been recorded
* Returns `HTTP 400 Bad Request` if the request JSON body is not properly formed

***

### `GET /latest-reading` Get Latest Reading
Get the timestamp of the latest reading for a specific device. Requires the `id` in the request body.
**Example**
```shell
curl --location --request POST 'http://localhost:8000/create' \
--header 'Content-Type: application/json' \
--data-raw '{
    "id": "my-id-1",
    "readings": [
        {
            "timestamp": "2021-09-29T16:08:15+01:00",
            "count": 2
        },
        {
            "timestamp": "2021-09-29T16:09:15+01:00",
            "count": 15
        }
    ]
}' && 
curl --location --request GET 'http://localhost:8000/latest-reading' \
--header 'Content-Type: application/json' \
--data-raw '{
    "id": "my-id-1"
}'
```
The last command will return:
```json
{
    "latestTimestamp":"2021-09-29T15:09Z"
}
```

#### Notes

* Returns `null` if no readings have been recorded
* Returns `HTTP 400 Bad Request` if the request JSON body is not properly formed

***

## Brief Project Overview 
This implementation is a SpringBoot application that uses a map as in-memory storage.

* `BrightwheelApplication` is the **SpringBoot root application**
* `DeviceController` implements a **REST API** for all three endpoints, using **Jackson** for object serializaiton (all request objects are contained in the `org.brightwheel.requests` package)
* All **business logic** is contained in the `DeviceService`
* **lombok** annotations replace boilerplate DTO code
* Some unit tests for Jackson Serialization can be found in the test folder
* The `GET` endpoints both accept parameters via the request body to allow for easier parameter changes

## Future Enhancements
1. More tests! 
   1. I wrote unit tests just to make sure I got my Jackson annotations correct before moving on to implementation, but there are edge cases that I'd like to have unit test coverage for (specifically around malformed JSON objects and the alias that I introduced)
   2. Add unit test coverage of the Service
   3. Add integration level coverage at the application level for all endpoints
2. Consolidate / improve error handling - I'm not sure who is consuming these endpoints, but it's possible that we don't want to expose our error messages to the client. We could create an exception handler to share code that would degrade more gracefully.
3. Enable hot-reload for non-structural changes
4. Add the ability to specify the timezone when accessing the Timezone of the reading (currently all are normalized to UTC)
5. Add the ability to query the count by a device+timestamp combo
6. [Potential Performance Optimization] I use a TreeSet for sorting up front, which allows for O(1) access when querying the latest time stamp. BUT if the usage patterns show that we are getting significantly more `/create` requests than `/latest-timesetamp`, then we could move the sorting to the `/latest-timestamp` endpoint to isoalte the performance overhead until the last responsible moment. If we allowed parallel requests and choose to save that sorted result, we would have to determine how to avoid a concurrent modification race condition.
7. [Scale] Move from in-memory storage to persistent storage :) Depending on scale, we should consider relational databases, NoSQL databases, or persistent S3 buckets. 