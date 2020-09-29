package database;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import org.bson.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import Object.Car;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

public class MongoDbConnect {
    public static  MongoClient client;
    private static MongoDatabase database;
    private static MongoCollection<Document> collection;
    private static  int slotsPerFloor;
    private static int totalFloors;
    private static String user;
    private static String pass;
    private static String hostUrl;
    private static int portNo;
    private static TreeSet<Integer> slotSet = new TreeSet<>();

    public MongoDbConnect(int floor, int slot , String userN , String passW , String host, int port1) throws IOException {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if(client != null){
                client.close();
            }
        }));
        slotsPerFloor = slot;
        totalFloors = floor;
        user = userN;
        pass = passW;
        hostUrl = host;
        portNo = port1;
        fillAssignSlot();
        connectMongoDb();
    }

    public static void connectMongoDb() throws IOException{

        client = new MongoClient(hostUrl,portNo);
        MongoCursor<String> dbsCursor = client.listDatabaseNames().iterator();
        boolean checkDb = false;
        while(dbsCursor.hasNext()) {
            if(dbsCursor.next().equals("parkingDb")) {
                checkDb = true;
                break;
            }
        }
        if(!checkDb){
            MongoCredential cred = MongoCredential.createCredential(
                    user, "parkingdb", pass.toCharArray());
        }
        database = client.getDatabase("parkingDb");
        boolean collectionExists = client.getDatabase("parkingDb").listCollectionNames()
                .into(new ArrayList<String>()).contains("cars");
        if(!collectionExists){
            database.createCollection("cars");
        }
        collection = database.getCollection("cars");
        FindIterable<Document> iterDoc = collection.find().projection(
                Projections.fields(Projections.include("floor", "slot"), Projections.excludeId()));
        for(Document dd : iterDoc){
            int floorC = dd.getInteger("floor");
            int slotC = dd.getInteger("slot");
            int addedSlot = ((floorC-1)*slotsPerFloor)+ slotC;
            slotSet.remove(addedSlot);
        }

    }
    public static void callQueries() throws IOException{
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
                insertIntoMongoDb(car);

            }else if(query == 2){
                System.out.println("Enter Ticket No. ");
                String ticket = br.readLine();
                removeFromMongoDb(ticket);
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
                            getRegisteredCarsWithColorMongoDB(color);
//                            System.out.println(getRegisteredCarsWithColor(color));
                            break;
                        }
                        case "b": {
                            System.out.println("Enter Color: ");
                            String color = br.readLine();
                            getSlotsWithColorMongoDb(color);
//                            System.out.println(getSlotsWithColor(color));
                            break;
                        }
                        case "c":
                            System.out.println("Enter Reg. No.: ");
                            String regNo = br.readLine();
                            getSlotWithRegNoMongoDb(regNo);
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

    public static String getSlotWithRegNoMongoDb(String regNo) {
        StringBuilder result = new StringBuilder();
        FindIterable<Document> iterDoc = collection.find(Filters.eq("registration",regNo)).projection(
                Projections.fields(Projections.include("floor", "slot"), Projections.excludeId()));
        for (Document document : iterDoc) {
            result.append(document).append("\n");
        }
        return result.toString();
    }

    public static String getSlotsWithColorMongoDb(String color) {
        StringBuilder result = new StringBuilder();
        FindIterable<Document> iterDoc = collection.find(Filters.eq("color",color)).projection(
                Projections.fields(Projections.include("floor", "slot"), Projections.excludeId()));
        for (Document document : iterDoc) {
            result.append(document).append("\n");
        }
        return result.toString();
    }

    public static void removeFromMongoDb(String ticket) {
        FindIterable<Document> iterDoc = collection.find(Filters.eq("ticket",ticket)).projection(
                Projections.fields(Projections.include("floor", "slot"), Projections.excludeId()));
        for(Document dd : iterDoc){
            int floorC = dd.getInteger("floor");
            int slotC = dd.getInteger("slot");
            int addedSlot = ((floorC-1)*slotsPerFloor)+ slotC;
            slotSet.add(addedSlot);
        }
        collection.deleteOne(Filters.eq("ticket",ticket));
    }

    public static String getRegisteredCarsWithColorMongoDB(String color) {
        StringBuilder result = new StringBuilder();
        FindIterable<Document> iterDoc = collection.find(Filters.eq("color",color)).projection(
                Projections.fields(Projections.include("registration"), Projections.excludeId()));
        for (Document document : iterDoc) {
            result.append(document).append("\n");
        }
        return result.toString();
    }

    public static Car getCar(String color, String reg) {
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

    public static void insertIntoMongoDb(Car car) {
        String color = car.getCarColor();
        String regNo = car.getRegNo();
        String ticket = car.getTicketNo();
        int floor = car.getFloorNo();
        int slot = car.getSlotNo();
        MongoCollection<Document> collection = database.getCollection("cars");
        Document document = new Document("color", color)
                .append("registration", regNo)
                .append("floor", floor)
                .append("slot", slot)
                .append("ticket", ticket);
        collection.insertOne(document);
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
    public static String printAll(){
        StringBuilder result = new StringBuilder();
        FindIterable<Document> iterDoc = collection.find();
        for (Document document : iterDoc) {
            result.append(document).append("\n");
        }
        return result.toString();
    }

    public boolean checkCarExists(String regNo) {
        FindIterable<Document> iterDoc = collection.find(Filters.eq("registration",regNo)).projection(
                Projections.fields(Projections.include("floor", "slot"), Projections.excludeId()));
        Iterator itr = iterDoc.iterator();
        return itr.hasNext();
    }
}
