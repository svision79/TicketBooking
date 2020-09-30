package database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import Object.Car;

public class InMemory {
    private static int totalFloors;
    private static int slotsPerFloor;

    /**
     * slotSet a treeset for storing slots in sorted manner.
     */
    private static TreeSet<Integer> slotSet = new TreeSet<>();
    /**
     * Hashmap of cars storing ticket as key and the car to which it is assigned as value.
     */

    static HashMap<String, Car> allCars = new HashMap<>();

    public InMemory(int floors , int slots){
        totalFloors = floors;
        slotsPerFloor = slots;
        fillAssignSlot();
    }

    public static void callQueries() throws IOException, SQLException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        boolean end = false;
        /*
         *  Input Query Loop
         */
        while(!end){
            System.out.println("Enter 1 to enter car , 2 to exit car , 3 for search queries , 0 to exit");
            int query;
            try {
                query = Integer.parseInt(br.readLine());
            }catch(Exception e){
                query = -1;
            }
            if(query == 1){
                System.out.println("Enter Color of The car ");
                String color = br.readLine();
                System.out.println("Enter Registration No of The car ");
                String reg = br.readLine();
                carEnters(color , reg);
                printEverything();

            }else if(query == 2){
                System.out.println("Enter Ticket No. ");
                String ticket = br.readLine();
                carExits(ticket);
                printEverything();
            }
            else if(query == 3){
                boolean searchEnd = false;
                /*
                 * Search Query Loop
                 */
                while(!searchEnd){
                    System.out.println("a. for getting all registered cars with color\n" +
                            "b. Slots of particular color\n" +
                            "c. Slot number in which a car with a given registration number is parked \n" +
                            "x. end search queries");
                    String qq = br.readLine();
                    switch (qq) {
                        case "a": {
                            System.out.println("Enter Color: ");
                            String color = br.readLine();
//                            getRegisteredCarsWithColorDB(color);
                            System.out.println(getRegisteredCarsWithColor(color));
                            break;
                        }
                        case "b": {
                            System.out.println("Enter Color: ");
                            String color = br.readLine();
//                            getSlotsWithColorDB(color);
                            System.out.println(getSlotsWithColor(color));
                            break;
                        }
                        case "c":
                            System.out.println("Enter Reg. No.: ");
                            String regNo = br.readLine();
//                            getSlotWithRegNoDB(regNo);
                            System.out.println(getSlotWithRegNo(regNo));
                            break;
                        case "x":
                            searchEnd = true;
                            break;
                        default:
                            System.out.println("Wrong Query");
                            break;
                    }
                }
            }else if(query == 0){
                end = true;
            }else{
                System.out.println("Wrong query");
            }
        }
    }

    /**
     * Main function to be called when a new car enters the parking lot.
     * Takes nearest slot available from slot set and assign floor and slot accordingly to the car.
     * @param color color of the entered car
     * @param reg   registration number of the car
     */
    public static Car carEnters(String color, String reg) {
        int assignSlot=-1;
        try {
            assignSlot = slotSet.pollFirst();
        }catch (NullPointerException e) {
            System.out.println("No Slot Empty");
        }
        if(assignSlot != -1) {
            int floor = (assignSlot / slotsPerFloor) + 1;
            int slot = assignSlot % slotsPerFloor;
            Car car;
            String ticket;
//        System.out.println(assignSlot+" " + slotsPerFloor);
            if (assignSlot < slotsPerFloor) {
                ticket = floor + "tt" + assignSlot;
                car = new Car(color, reg, floor, assignSlot, ticket);
            } else {
                if (slot == 0) {
                    floor -= 1;
                    slot = slotsPerFloor;
                }
                ticket = floor + "tt" + slot;
                car = new Car(color, reg, floor, slot, ticket);
            }
            System.out.println("Ticket Assigned: " +ticket);
            allCars.put(ticket, car);
            return car;

        }else{
            System.out.println("No more slots");
            return null;
        }
    }



    /**
     * Main function to call when car exists the parking which takes ticket as an input.
     * Removes car from the hashmap and adds the empty slot to slot set
     * @param ticket Ticket assigned to car when it enters
     */
    public static void carExits(String ticket) throws SQLException {
        Car exitCar = allCars.get(ticket);
        if(exitCar == null){
            System.out.println("Wrong Ticket Number");
            return;
        }
        int addedSlot = ((exitCar.getFloorNo()-1)*slotsPerFloor)+ exitCar.getSlotNo();
        slotSet.add(addedSlot);
        allCars.remove(ticket, exitCar);
    }



    /**
     * Method to search all the cars with a given specified color and returns their registration numbers
     * @param color color of the car to be searched
     * @return registeredCars  String containing registration number of all the cars with given color
     */
    public static String getRegisteredCarsWithColor(String color){
        StringBuilder registeredCars = new StringBuilder();
        for (Map.Entry<String, Car> set : allCars.entrySet()) {
            Car infoCar = set.getValue();
            if (infoCar.getCarColor().equals(color)) {
                registeredCars.append(infoCar.getRegNo()).append(" \n");
            }
        }
        if(registeredCars.toString().equals("")){
            return ("No Such Cars");
        }else{
            return registeredCars.toString();
        }
    }

    /**
     * Method which gives all the slots in which a car of particular color is parked
     * @param color color of the car to be searched
     * @return returnCars  String containing floor and slot of cars of a given color
     */
    public static String getSlotsWithColor(String color){
        StringBuilder returnCars= new StringBuilder();
        for (Map.Entry<String, Car> set : allCars.entrySet()) {
            Car infoCar = set.getValue();
            if (infoCar.getCarColor().equals(color)) {
                returnCars.append(" Floor: ").append(infoCar.getFloorNo()).append(" Slot: ").append(infoCar.getSlotNo()).append("\n");
            }
        }
        return returnCars.toString();
    }

    /**
     *
     * @param registrationToSearch registration number of the car to be searched
     * @return slotParked   Slot of car of given registered Number
     */
    public static String getSlotWithRegNo(String registrationToSearch) {
        int floor = -1;
        int slot = -1;
        String slotParked;
        for (Map.Entry<String, Car> set : allCars.entrySet()) {
            Car infoCar = set.getValue();
//            System.out.println(registrationToSearch+" "+ infoCar.getRegNo());
            if ((infoCar.getRegNo()).equals(registrationToSearch)) {
                floor = infoCar.getFloorNo();
                slot = infoCar.getSlotNo();
                break;
            }
        }
        if(floor == -1 && slot == -1){
            slotParked = "No Such Cars";
        }else{
            slotParked = "Floor: " + floor + " Slot: " + slot;
        }
        return slotParked;
    }

    /**
     * Generic function to print all the details of parking lot
     */
    protected static void printEverything(){
        for (Map.Entry<String, Car> set : allCars.entrySet()) {
            Car infoCar = set.getValue();
            System.out.println("Floor No " + infoCar.getFloorNo() + " Slot No " + infoCar.getSlotNo() + " Reg. No " + infoCar.getRegNo() + " Color " + infoCar.getCarColor());
        }
    }

    /**
     * Add total no of slots in treeset
     */
    protected static void fillAssignSlot() {
        int totalSlots = totalFloors*slotsPerFloor;
        /*
        Filling slot sets with total number of slots
         */
        for(int i = 1 ; i <= totalSlots ; i++){
            slotSet.add(i);
        }
    }
}
