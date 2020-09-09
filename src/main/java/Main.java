
/*
  @author sahil
 * Main Driver Class

 */
import java.sql.*;
import java.util.*;
import java.io.*;

import static java.lang.System.exit;

class Main {
    private static int totalFloors;
    private static int slotsPerFloor;
    private static Connection con;
    /**
     * slotSet a treeset for storing slots in sorted manner.
     */
    private static TreeSet<Integer> slotSet = new TreeSet<>();
    /**
     * Hashmap of cars storing ticket as key and the car to which it is assigned as value.
     */
    static HashMap<String, Car> allCars = new HashMap<>();
    /**
     * Constructor of main class
     * @param noOfFloors floors in parking lot
     * @param noOfSlots slots in particular floor
     */
    public Main(int noOfFloors , int noOfSlots){
        totalFloors = noOfFloors;
        slotsPerFloor = noOfSlots;
        fillAssignSlot();
        connectDatabase();
//        deleteDatabase();
    }

    private void connectDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/parking","root","");
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM cars");
            int floorC = -1;
            int slotC = -1;
            while(rs.next()){
                floorC = rs.getInt(1);
                slotC = rs.getInt(2);
                System.out.println(floorC+" " + slotC+" " + rs.getString(3)
                        +" " + rs.getString(4)+" " + rs.getString(5));

                int addedSlot = ((floorC-1)*slotsPerFloor)+ slotC;
                slotSet.remove(addedSlot);

            }
//            if(floorC > totalFloors){
//                System.out.println("Changing Total Floors");
//                totalFloors = floorC;
//            }else if (slotC > slotsPerFloor){
//                System.out.println("Changing Slots per Floor");
//                slotsPerFloor = slotC;
//
//            }
        }catch (Exception e){
            System.out.println(e);
            exit(1);
        }
    }

    /**
     * Driver function for input and registering car adn various search queries
     * @param args  Input arguments array
     * @throws IOException  BufferedReader exception
     */


    public static void main(String[] args) throws IOException, SQLException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
//        System.out.println("Enter no of Floors: ");
        int ff = 5;
//        try{
//            ff = Integer.parseInt(br.readLine());
//        }catch(Exception e){
//            System.out.println("Wrong Input Default value 5 is taken ");
//        }
//        System.out.println("Enter Slots per Floors: ");
        int ss = 20;
//        try{
//            ss = Integer.parseInt(br.readLine());
//        }catch(Exception e){
//            System.out.println("Wrong Input Default value 20 is taken ");
//        }
        Main main = new Main(ff,ss);
        boolean end = false;
        /*
         *  Input Query Loop
         */
        while(!end){
            System.out.println("Enter 1 to enter car , 2 to exit car , 3 for search queries , 0 to exit , 9 to change floors and slot");

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
                Car car = carEnters(color , reg);
                if(car != null) {
                    try {
                        insertDatabase(car);
                    } catch (Exception e) {
                        System.out.println(e);
                        System.out.println("Try Again");
                    }
                }
                printEverything();

            }else if(query == 2){
                System.out.println("Enter Ticket No. ");
                String ticket = br.readLine();
                exitDatabase(ticket);
                Car exitCar = allCars.get(ticket);
                if(exitCar != null) {
                    carExits(ticket, exitCar);
                }
//                carExits(ticket);
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
                            getRegisteredCarsWithColorDB(color);
//                            System.out.println(getRegisteredCarsWithColor(color));
                            break;
                        }
                        case "b": {
                            System.out.println("Enter Color: ");
                            String color = br.readLine();
                            getSlotsWithColorDB(color);
//                            System.out.println(getSlotsWithColor(color));
                            break;
                        }
                        case "c":
                            System.out.println("Enter Reg. No.: ");
                            String regNo = br.readLine();
                            getSlotWithRegNoDB(regNo);
//                            System.out.println(getSlotWithRegNo(regNo));
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
//                deleteDatabase();
                con.close();
                end = true;
            }else if (query == 9){
                System.out.println("Enter updated Floor");
                int ff1 =5;
                int ss1 = 20;
                try{
                    ff1 = Integer.parseInt(br.readLine());

                }catch(Exception e){
                    System.out.println("Wrong Input Floor Value Unchanged");
                }
                System.out.println("Enter updated Slots");
                try{
                    ss1 = Integer.parseInt(br.readLine());

                }catch(Exception e){
                    System.out.println("Wrong Input Slots Value Unchanged");
                }
//                System.out.println(allCars.size());
                if(allCars.size() > (ff1*ss1)){
                    System.out.println("can't update");
                }else {
                    updateFloor(ff1);
                    updateSlots(ss1);
                }
            }
            else{
                System.out.println("Wrong query");
            }
        }
    }

    private static void getSlotWithRegNoDB(String regNo) {
        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM cars WHERE RegNo= \'"+ regNo +"\'");
            while(rs.next()){
                System.out.println("Floor: " + rs.getString(1) + "Slot: " + rs.getString(2));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void getSlotsWithColorDB(String color) {
        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM cars WHERE Color= \'"+ color +"\'");
            while(rs.next()){
                System.out.println("Floor: " + rs.getString(1) + "Slot: " + rs.getString(2));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void getRegisteredCarsWithColorDB(String color) {
        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM cars WHERE Color= \'"+ color +"\'");
            while(rs.next()){
                System.out.println(rs.getString(3));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

//    private static void deleteDatabase() {
//        try {
//            String query = "DELETE FROM cars";
//            Statement st = con.createStatement();
//            st.executeUpdate(query);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * Main function to be called when a new car enters the parking lot.
     * Takes nearest slot available from slot set and assign floor and slot accordingly to the car.
     * @param color color of the entered car
     * @param reg   registration number of the car
     */
    protected static Car carEnters(String color, String reg) {
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

    private static void insertDatabase(Car car) throws SQLException {
        String query = " insert into cars (Floor, Slot, RegNo, Color, Ticket)"
                + " values (?, ?, ?, ?, ?)";
        PreparedStatement statement = con.prepareStatement(query);
        statement.setInt(1, car.getFloorNo());
        statement.setInt(2, car.getSlotNo());
        statement.setString(3, car.getRegNo());
        statement.setString(4, car.getCarColor());
        statement.setString(5, car.getTicketNo());
        statement.execute();
    }

    /**
     * Main function to call when car exists the parking which takes ticket as an input.
     * Removes car from the hashmap and adds the empty slot to slot set
     * @param ticket Ticket assigned to car when it enters
     */
    protected static void carExits(String ticket , Car exitCar) throws SQLException {

            int addedSlot = ((exitCar.getFloorNo()-1)*slotsPerFloor)+ exitCar.getSlotNo();
            slotSet.add(addedSlot);
            allCars.remove(ticket, exitCar);
    }

    private static void exitDatabase(String ticket) throws SQLException {
        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM cars WHERE Ticket= \'" +  ticket + "\'");
            String query = "DELETE FROM cars WHERE Ticket = \'"+ ticket +"\'";
            Statement st1 = con.createStatement();
            st1.executeUpdate(query);
            int floorC = -1;
            int slotC = -1;
            while(rs.next()){
                floorC = rs.getInt(1);
                slotC = rs.getInt(2);
                int addedSlot = ((floorC-1)*slotsPerFloor)+ slotC;
                slotSet.remove(addedSlot);

            }
        }catch(Exception e){
            System.out.println(e);
            System.out.println("No Such Ticket");
        }
    }

    /**
     * Method to search all the cars with a given specified color and returns their registration numbers
     * @param color color of the car to be searched
     * @return registeredCars  String containing registration number of all the cars with given color
     */
    protected static String getRegisteredCarsWithColor(String color){
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
    protected static String getSlotsWithColor(String color){
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
    protected static String getSlotWithRegNo(String registrationToSearch) {
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
     * Function to be called if in case of change in number of floors
     * @param noOfFloors Updated number of floors
     */
    protected static void updateFloor(int noOfFloors){
        totalFloors = noOfFloors;
    }

    /**
     * Function to be called in case of change in no of slots on a floor
     * @param noOfSlots Updated number of slots
     */

    protected static void updateSlots(int noOfSlots){
        slotsPerFloor = noOfSlots;
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
