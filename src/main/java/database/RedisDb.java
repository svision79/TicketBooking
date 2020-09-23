package database;

import redis.clients.jedis.Jedis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import Object.Car;

public class RedisDb {
    private static Jedis jedis;
    private static int slotsPerFloor;
    private static int totalFloors;
    private static TreeSet<Integer> slotSet = new TreeSet<>();
    private static Object list[];
    private static String hostURL;
    private static int hostPort;

    public RedisDb(int floor, int slot, String host, int port) {
        slotsPerFloor = slot;
        totalFloors = floor;
        hostURL = host;
        hostPort = port;
        fillAssignSlot();
        connectRadis();
    }

    private void connectRadis() {
        jedis = new Jedis(hostURL, hostPort);
        System.out.println("Server is running: Ping-" + jedis.ping());
        list = jedis.keys("*").toArray();
        for(int i = 0; i<list.length; i++) {
            String ticket = list[i].toString();
            Map<String, String> carInfo = jedis.hgetAll(ticket);
            int floorC= Integer.parseInt(carInfo.get("floor"));
            int slotC = Integer.parseInt(carInfo.get("slot"));
            System.out.println(floorC + " " + slotC);
            int addedSlot = ((floorC-1)*slotsPerFloor)+ slotC;
            slotSet.remove(addedSlot);
        }
    }

    public static void callQueries() throws IOException {
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
                insertIntoRadisDb(car);

            }else if(query == 2){
                System.out.println("Enter Ticket No. ");
                String ticket = br.readLine();
                removeFromRadisDb(ticket);
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
                            getRegisteredCarsWithColorRadisDB(color);
//                            System.out.println(getRegisteredCarsWithColor(color));
                            break;
                        }
                        case "b": {
                            System.out.println("Enter Color: ");
                            String color = br.readLine();
                            getSlotsWithColorRadisDb(color);
//                            System.out.println(getSlotsWithColor(color));
                            break;
                        }
                        case "c":
                            System.out.println("Enter Reg. No.: ");
                            String regNo = br.readLine();
                            getSlotWithRegNoRadisDb(regNo);
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

    private static void getSlotWithRegNoRadisDb(String regNo) {
        list= jedis.keys("*").toArray();
        for(int i = 0; i<list.length; i++) {
            String ticket = list[i].toString();
            Map<String, String> carInfo = jedis.hgetAll(ticket);
            if(carInfo.get("registration").equals(regNo)){
                System.out.println("Floor: " + carInfo.get("floor") + " Slot: " + carInfo.get("slot"));
            }
        }
    }

    private static void getSlotsWithColorRadisDb(String color) {
        list= jedis.keys("*").toArray();
        for(int i = 0; i<list.length; i++) {
            String ticket = list[i].toString();
            Map<String, String> carInfo = jedis.hgetAll(ticket);
            if(carInfo.get("color").equals(color)){
                System.out.println("Floor: " + carInfo.get("floor") + " Slot: " + carInfo.get("slot"));
            }
        }
    }

    private static void getRegisteredCarsWithColorRadisDB(String color) {
        list= jedis.keys("*").toArray();
        for(int i = 0; i<list.length; i++) {
            String ticket = list[i].toString();
            Map<String, String> carInfo = jedis.hgetAll(ticket);
            if(carInfo.get("color").equals(color)){
                System.out.println("Reg. NO: " + carInfo.get("registration"));
            }
        }

    }

    private static void removeFromRadisDb(String ticket) {
        Map<String, String> carInfo = jedis.hgetAll(ticket);
        int floorC= Integer.parseInt(carInfo.get("floor"));
        int slotC = Integer.parseInt(carInfo.get("slot"));
        jedis.hdel(ticket, "color", "registration", "floor" , "slot");
        int addedSlot = ((floorC-1)*slotsPerFloor)+ slotC;
        slotSet.add(addedSlot);
    }

    private static void insertIntoRadisDb(Car car) {
        Map<String, String> carInfo = new HashMap<>();
        carInfo.put("color", car.getCarColor());
        carInfo.put("registration", car.getRegNo());
        carInfo.put("floor", (""+ car.getFloorNo()));
        carInfo.put("slot", (""+car.getSlotNo()));
        jedis.hmset(car.getTicketNo() , carInfo);
    }

    protected static void fillAssignSlot() {
        int totalSlots = totalFloors * slotsPerFloor;
        /*
        Filling slot sets with total number of slots
         */
        for (int i = 1; i <= totalSlots; i++) {
            slotSet.add(i);
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
}
