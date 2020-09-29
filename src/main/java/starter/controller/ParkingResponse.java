package starter.controller;

import org.springframework.web.bind.annotation.*;
import Object.Car;

import java.io.IOException;

import static starter.ParkingApi.mongoDb;

@RestController
public class ParkingResponse {


    public ParkingResponse() throws IOException {
    }

    @RequestMapping("/parkingDB")
    public String showAll(){
        return mongoDb.printAll();
    }
    @RequestMapping("/parkingDB/slotsWithReg/{regNo}")
    public String getSlotWithRegNo(@PathVariable String regNo){
        return mongoDb.getSlotWithRegNoMongoDb(regNo);
    }

    @RequestMapping("/parkingDB/slotsWithColor/{color}")
    public String getSlotWithColor(@PathVariable String color){
        return mongoDb.getSlotsWithColorMongoDb(color);
    }

    @RequestMapping("/parkingDB/registeredCars/{color}")
    public String getCarWithColor(@PathVariable String color){
        return mongoDb.getRegisteredCarsWithColorMongoDB(color);
    }

    @RequestMapping(method= RequestMethod.POST,value="/parkingDB/newCar")
    public String enterCar(@RequestBody Car car){
//        System.out.println("xxx");
        if(car == null){
            return "Not created";
        }
//        System.out.println(car.getRegNo());
        boolean checkCar = mongoDb.checkCarExists(car.getRegNo());
        Car Ncar = mongoDb.getCar(car.getCarColor(),car.getRegNo());
        if (Ncar == null){
            return "No Slots Empty";
        }
        if(checkCar){
            return("Car already exists");
        }else {
            mongoDb.insertIntoMongoDb(Ncar);
        }
        return "Ticket Assign"+ Ncar.getTicketNo();
    }
    @RequestMapping(method= RequestMethod.DELETE,value="/parkingDB/carExit/{ticket}")
    public String carExit(@PathVariable String ticket){
        return mongoDb.removeFromMongoDb(ticket);

    }
}
