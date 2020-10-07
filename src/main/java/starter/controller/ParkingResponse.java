package starter.controller;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;
import Object.Car;
import starter.ParkingApi;


import java.io.IOException;
import java.util.List;

import static starter.ParkingApi.slotsPerFloor;

@RestController
public class ParkingResponse {
    @Autowired
    private ParkingRepository parkingRepo;
    public ParkingResponse() throws IOException {

    }

    @RequestMapping("/parkingDB/update")
    public void update(){
        List<Car> allCars = parkingRepo.findAll();
        for (Car car1 : allCars) {
            int floorC = car1.getFloorNo();
            int slotC = car1.getSlotNo();
            int addedSlot = ((floorC - 1) * slotsPerFloor) + slotC;
//            System.out.println(addedSlot);
            ParkingApi.slotSet.remove(addedSlot);
        }
    }
    @RequestMapping("/parkingDB")
    public List<Car> showAll(){
        return parkingRepo.findAll();

    }
    @RequestMapping("/parkingDB/slotsWithReg/{regNo}")
    public String getSlotWithRegNo(@PathVariable String regNo){
        StringBuilder result = new StringBuilder();
        List<Car> allCars = parkingRepo.findAll();
        for(Car car : allCars){
            if(car.getRegNo().equals(regNo)){
                result.append("Floor: " + car.getFloorNo());
                result.append(" Slot: " + car.getSlotNo()).append("\n");
            }
        }
        return result.toString();
    }

    @RequestMapping("/parkingDB/slotsWithColor/{color}")
    public String getSlotWithColor(@PathVariable String color){
        StringBuilder result = new StringBuilder();
        List<Car> allCars = parkingRepo.findAll();
        for(Car car : allCars){
            if(car.getCarColor().equals(color)){
                result.append("Floor: " + car.getFloorNo());
                result.append(" Slot: " + car.getSlotNo()).append("\n");
            }
        }
        return result.toString();
    }

    @RequestMapping("/parkingDB/registeredCars/{color}")
    public String getCarWithColor(@PathVariable String color){
        StringBuilder result = new StringBuilder();
        List<Car> allCars = parkingRepo.findAll();
        for(Car car : allCars){
            if(car.getCarColor().equals(color)){
                result.append("Registration No. : " + car.getRegNo()).append("\n");
            }
        }
        return result.toString();
    }

    @RequestMapping(method= RequestMethod.POST,value="/parkingDB/newCar")
    public String enterCar(@RequestBody Car car){
        if(car == null){
            return "Not created";
        }
//        System.out.println(car.getCarColor()+" " +car.getRegNo());
        boolean checkCar = checkCarExists(car.getRegNo());

        Car Ncar = getCar(car.getCarColor(),car.getRegNo());
        if (Ncar == null){
            return "No Slots Empty";
        }
        if(checkCar){
            return("Car already exists");
        }else {
            parkingRepo.save(Ncar);
        }
        return "Ticket Assigned: "+ Ncar.getTicketNo();
    }

    private boolean checkCarExists(String regNo) {
        List<Car> allCars = parkingRepo.findAll();
        for(Car car : allCars){
            if(car.getRegNo().equals(regNo)){
               return true;
            }
        }
        return false;
    }

    @RequestMapping(method= RequestMethod.DELETE,value="/parkingDB/carExit/{ticket}")
    public List<Car> carExit(@PathVariable String ticket){
        List<Car> allCars = parkingRepo.findAll();
        for(Car car : allCars){
            if(car.getTicketNo().equals(ticket)){
                int floorC = car.getFloorNo();
                int slotC = car.getSlotNo();
                int addedSlot = ((floorC-1)*slotsPerFloor)+ slotC;
                System.out.println(addedSlot);
                ParkingApi.slotSet.add(addedSlot);
            }
        }
        parkingRepo.deleteById(ticket);
        return parkingRepo.findAll();
    }

    public static Car getCar(String color, String reg) {
        Car car = null;
        int assignSlot = -1;
        try {
            assignSlot = ParkingApi.slotSet.pollFirst();
        } catch (NullPointerException e) {
            System.out.println("No Slot Empty");
        }
        if (assignSlot != -1) {
            int floor = (assignSlot / slotsPerFloor) + 1;
            int slot = assignSlot % slotsPerFloor;

            String ticket;
            if (assignSlot < slotsPerFloor) {
                ticket = floor + "tt" + assignSlot;
                car = new Car(floor, assignSlot, reg, color, ticket);
            } else {
                if (slot == 0) {
                    floor -= 1;
                    slot = slotsPerFloor;
                }
                ticket = floor + "tt" + slot;
                car = new Car(floor, assignSlot, reg, color, ticket);
            }
        }
        return car;
    }

}
