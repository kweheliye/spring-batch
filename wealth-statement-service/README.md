# Wealth Statement Service 
This application will send statements to customers by email.
There are two ways to send statements by email which are:

- End of the Month
- Ad-hoc

## Class Diagrams
The class diagrams of this project can be found on this  [link](wealth_service_classd.png)
 
 `PlantUML has been used to design the class diagrams`

To edit the class-diagram in Intellij please install [PlantUML](https://plantuml.com) plugin.
 
## Getting Started
These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Prerequisites
What things you need to install the software and how to install them

```
Maven version 3 or above is needed. Once you have downloaded the source-code you need 
to run two maven goals 'clean package' to clean the project and package it 
```
```
Docker version 19 or 18 is needed. Docker is needed to build the image  
```


## Running The application 
The first step to run the application is to execute maven commands, 
which will clean directories and build...

- Maven: execute below command to clean and build the project 

    ` mvn clean package`


- Docker Image: to build an image a docker is need it. Install Docker on your machine and run below commands. 
   
   ` docker build  --no-cache -t wealth-statement-service .`

- Docker-compose :to run the application through docker-compose, execute below commands
  `docker-compose rm -v`  
  `docker-compose up --build`
  