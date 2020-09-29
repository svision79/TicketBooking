package database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.TreeSet;

import Object.Car;

import static java.lang.System.exit;

public class MySqlDb {
    private static Connection con;
    private static  int slotsPerFloor;
    private static int totalFloors;
    private static TreeSet<Integer> slotSet = new TreeSet<>();
    public MySqlDb(int floors , int Slots , String user , String password){
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if(con != null){
                try {
                    con.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }));
        slotsPerFloor = Slots;
        totalFloors = floors;
        fillAssignSlot();
        connectDatabase(user, password);
    }
    private void connectDatabase(String user, String pass) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/parking", user,pass);
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
        }catch (Exception e){
            System.out.println(e);
            exit(1);
        }
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
                Car car = getCar(color, reg);
                insertIntoDatabase(car);

            }else if(query == 2){
                System.out.println("Enter Ticket No. ");
                String ticket = br.readLine();
                removeFromDatabase(ticket);
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
                end = true;
            } else{
                System.out.println("Wrong query");
            }
        }
    }

    private static Car getCar(String color, String reg) {
        Car car = null;
        int assignSlot = -1;
        try {
            assignSlot = slotSet.pollFirst();
        } catch (NullPointerException e) {
            System.out.println("No Slot Empty");
        }
        if (assignSlot != -1) {
            int floor = (assignSlot / slotsPerFloor) + 1;
            int slot = assignSlot % slotsPerFloor;

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
        }
        return car;
    }
    private static void insertIntoDatabase(Car car) throws SQLException {
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
    private static void removeFromDatabase(String ticket) throws SQLException {
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
                slotSet.add(addedSlot);

            }
        }catch(Exception e){
            System.out.println(e);
            System.out.println("No Such Ticket");
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
