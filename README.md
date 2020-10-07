# TicketBooking
Automated Ticket Assigning
# New Branch
Switch between inmemory and database
# function.properties
Properties file to assign function whether to use inmemory or database.
Change functionToBeUsed in properties file to either database or InMemory accordingly.
# MongoDb added
Choose InMemory or mysqlDB or mongoDB in properties file for the respective functionalities
# Radis added
Choose radisDB in properties file for radis database.
# Springboot
Springboot has been added and car parking can be accesed on port 8080 of localhost
URL for various functionalities are as:
/parkingDB for databse information
/parkingDB/slotsWithReg/{regNo} add regNo for checking slots
/parkingDB/slotsWithColor/{color} add color for slots of that particular color car
/parkingDB/registeredCars/{color} for registration no of given color car
/parkingDB/newCar for adding new car
/parkingDB/carExit/{ticket} car exits with given ticket no.

Note: Move the jar file to root of project in order to work

# Docker
DockerFile has been added
Run the following to run the jar in docker container
>> docker build -f Dockerfile -t parking-manager .
>> docker run -d -p 27017:27017 mongodb mongo 
>> docker run --network = "host" --name parking -d parking-manager 

# Spring-boot-starter-data-mongodb
Spring api of mongodb used. Functionality similar to springboot.
Use /parkingDB/update in the start to retrieve all previously stored data
