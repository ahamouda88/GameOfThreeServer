# Game Of Three
### Introduction to the game:
When a player starts, it incepts a random (whole) number and sends it to the second player as an approach of starting the game. 
The receiving player can now always choose between adding one of {­1, 0, 1} to get to a number that is divisible by 3. Divide it by three. 

The resulting whole number is then sent back to the original sender. The same rules are applied until one player reaches the number 1(after the division).

This repository contains the Server side of the game, which contains the business logic, and RESTful API for the game

## Technologies Used:
- Java 8
- Spring Boot
- JPA as the ORM
- Maven building tool
- H2 Embedded Database
- JUnit, and Mockito frameworks for unit and integration testing

## Running the application:
- Clone the repository
- Build the application using the following command on the root project: mvn clean install
- Two ways for running the application:
	1. A jar file will be create at `./target` folder, copy the file and deploy it to a web server
	2. Simply you can either run the application by executing `/src/main/java/com/callenge/got/config/MainApplication.java` or use the following command: mvn spring-boot:run
- From your localhost (http://localhost:8080) you can starting using the API using the below endpoints.

## RESTful Web Service
### Players End-Points:
|              URI                   |                  Description                     		              |    Method   |
|------------------------------------|------------------------------------------------------------------------|-------------|
| /api/v1/got/players                        	 | Returns the a of all players       	    						  	  |     GET     |
| /api/v1/got/players                        	 | Adds a new player, given a player JSON object as the request body	  |    POST     |
| /api/v1/got/players/{id}                   	 | Deletes a player, given a player's Id                            	  |    DELETE   |
| /api/v1/got/players/{id}                  	 | Returns a player, given a player's id       					  		  |     GET     |

### Games End-Points:
|              URI                   |                  Description                     		              |    Method   |
|------------------------------------|------------------------------------------------------------------------|-------------|
| /api/v1/got/games                        	 | Returns a list of all games		           						  	  |     GET     |
| /api/v1/got/games                        	 | Adds a new game, given a game JSON object as the request body	  	  |    POST     |
| /api/v1/got/games/{id}                   	 | Deletes a game, given a game's Id                            	  	  |    DELETE   |
| /api/v1/got/games/{id}                  	 	 | Returns a game, given a game's id       					  		  	  |     GET     |
| /api/v1/got/games/{gameId}/join?playerid={playerId}  | Register a player to a game, given the game & player's ids |     GET     |
| /api/v1/got/games/{gameId}/players/{playerId}?add={value}    | Add a value and calculates the result for a given player  	  	  |     GET     |
