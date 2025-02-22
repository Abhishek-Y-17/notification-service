Overview:
This project is a scalable SMS Notification Service built with Spring Boot, leveraging Kafka, MySQL, Redis, and Elasticsearch for efficient and high-performance message delivery.

🚀 Features:
Send SMS Notifications via third-party APIs.
Kafka Integration for async message processing.
Blacklist Management using Redis.
Message Search & Logs stored in Elasticsearch.
Scalability & Reliability with microservices-friendly architecture.

🛠️ Tech Stack:
Spring Boot – Core framework
Kafka – Asynchronous processing
MySQL – Persistent message storage
Redis – Fast blacklist lookups
Elasticsearch – Efficient message search
Log4j – Centralized logging
Database Schema

SMS_Requests_Table Schema

It store the details related to a particular sms with following attributes

Database Schema

1. SMS_Requests_Table Schema

  `id` int NOT NULL AUTO_INCREMENT,
  `phone_no` varchar(50) ,
  `message` varchar(100) ,
  `failure_code` varchar(50) ,
  `failure_comments` varchar(50) ,
  `created_at` varchar(50) ,
  `updated_at` varchar(50) ,
  `status` varchar(100) ,
  PRIMARY KEY (`id`)
2. Blacklist Table Schema 
   It contains id, phone numbers which are blacklisted.



  `id` int NOT NULL AUTO_INCREMENT,
  `phone_no` varchar(50),
  PRIMARY KEY (`id`),
  UNIQUE KEY `phone_no` (`phone_no`)
3. Elasticsearch Schema
It will contain the same details as sms_requests_table but with updated status . 

 
API Contracts: 

Send SMS API :

Method: POST

path: POST /v1/sms/send:

Request Body 



{ 
    "phoneNo":"+919999999999",
    "message":"helloji"
}
Response Body 



{
  "msgId":10,
  "status":"pending"
}
Get sms by Id:

Method : GET

path: 1/GET /v1/sms/id/{id}:sms//coda

Request Body 



{
    "id": 9,
    "phoneNo": "+919999999999",
    "message": "hello",
    "status": "pending",
    "failure_code": null,
    "failure_comments": null,
    "created_at": "2025-02-06T14:27:14.174",
    "updated_at": "2025-02-06T14:27:14.174"
}
Add phone numbers to blacklist

Method: POST

path: POST /v1/blacklist

Request Body 



{
    "phoneNumbers":[
        "7777777778",
        "7777777777"
    ]
}
Response Body 



[
    {
        "id": 7,
        "phoneNo": "7777777778"
    },
    {
        "id": 8,
        "phoneNo": "7777777777"
    }
]
Remove phone numbers from blacklist 

Method : DELETE

Path : DELETE /v1/blacklist

Request Body :   



{
    "phoneNumbers":[
        "7777777777",
        "+917777777777"
    ]
}
Response Body :  



Successfully whitelisted phone numbers: 7777777777, 7777777778
Get all phone numbers from blacklist

Method : GET

Path : GET /v1/blacklist

Response Body :  



[Blacklist(id=6, phoneNo=+914444444444), Blacklist(id=1, phoneNo=0000000000), Blacklist(id=2, phoneNo=1111111111), Blacklist(id=4, phoneNo=3333333333)]
Search all sms by text

Method : GET

Path : GET /v1/search/text/{text}

Response Body :  



[
    {
        "id": 3,
        "phoneNo": "8957110385",
        "message": "Hii",
        "status": "success",
        "failure_code": "SUCCESS",
        "failure_comments": "success",
        "created_at": "2025-02-05T15:16:26.896Z",
        "updated_at": "2025-02-05T15:16:26.896Z"
    }
]
Search all sms by a phone number between FromDateTime and ToDateTime 

Method : POST

Path : POST /v1/search/dateAndPhoneNo

Request Body: 



{   
    "phoneNo":"8888888888",
    "fromDateTime":"2025-02-05T15:15:49.143Z",
    "toDateTime":"2025-02-05T15:20:35.548Z"
}
Response Body :  



[
    {
        "failure_code": "201",
        "updated_at": "2025-02-05T15:20:35.548Z",
        "failure_comments": "success",
        "created_at": "2025-02-05T15:20:35.548Z",
        "_class": "org.example.entity.SmsReqElastic",
        "id": 6,
        "message": "hello",
        "phoneNo": "8888888888",
        "status": "success"
    }
]
Third-party API integration

Method: POST

Endpoint: https://api.imiconnect.in/resources/v1/messaging

Headers:

Content-Type: application/json

API-Key: <API_Key>

Request Body




 [ 
  { 
    "deliverychannel": "sms", 
    "channels": { 
      "sms": { 
        "text": "Hello, Greetings from Meesho.Click here to know more about us:https://meesho.com." 
      } 
    }, 
    "destination": [ 
      { 
        "msisdn": [
           "+919940630272" 
        ], 
        "correlationId": "some_unique_id" 
      } 
    ] 
  } 
Why the following have been used ?

Kafka: 

High Throughput & Fault Tolerance: Acts as a robust messaging backbone that handles heavy loads.

Decoupled Architecture: Separates API operations from SMS processing, enabling asynchronous handling.

Message Durability: Persists messages with retry mechanisms and supports dead-letter queues.

Scalability: Distributes messages across partitions for horizontal scaling.

Low Latency: Efficient streaming processes thousands of messages per second.

Redis :

Redis is used to quickly determine if a phone number is blacklisted, thereby ensuring that the SMS sending process is efficient and not delayed by slower database queries.

Elasticsearch:

Purpose: Elasticsearch is used to index and search SMS details, enabling quick, efficient retrieval of data based on various query criteria (like text search or time-range filtering)

Benefits:

Enhanced performance for search and analytics.

Scalable and resilient handling of large volumes of SMS data.

Real-time insights and retrieval capabilities, improving the overall responsiveness of the Notification Service.
