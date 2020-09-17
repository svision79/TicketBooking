import org.apache.http.HttpHost;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.mapper.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.net.UnknownHostException;
import java.util.*;


public class elasticsearch {
    private static int slotsPerFloor;
    private static int totalFloors;
    private static TreeSet<Integer> slotSet = new TreeSet<>();
    private static String HOST;
    private static int port1 ;
    private static int port2;
    private static final String SCHEME = "http";
    private static ArrayList<String> tickets = new ArrayList();
    private static String ticketsA = "";
    private static RestHighLevelClient client;
    private static ObjectMapper objM;



    public elasticsearch(int floor, int slot, String host, int port_1, int port_2) throws UnknownHostException {
        totalFloors = floor;
        slotsPerFloor = slot;
        HOST = host;
        port1 = port_1;
        port2 = port_2;
        fillAssignSlot();
        connectES();
    }

    private static void  connectES() throws UnknownHostException {
        client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http"),
                        new HttpHost("localhost", 9201, "http")));
        try {
            GetRequest getPersonRequest = new GetRequest("tickets");
            getPersonRequest.id("ticketIDs");
            GetResponse getResponse = null;
            try {
                getResponse = client.get(getPersonRequest, RequestOptions.DEFAULT);
            } catch (java.io.IOException e){
                e.getLocalizedMessage();
            }
            if(getResponse != null){
                String ticketsA = (String) getResponse.getSource().get("all");
                System.out.println(ticketsA);
                String tArr[] = ticketsA.split(" " );
                tickets.addAll(Arrays.asList(tArr));
            }
        }catch(Exception e1){
            System.out.println("No previously saved data");
        }
        GetRequest getPersonRequest = new GetRequest("car");
        for(int i = 0; i < tickets.size() ; i++){
//            System.out.println(i + " " +tickets.get(i));
            getPersonRequest.id(tickets.get(i));
            GetResponse getResponse = null;
            try {
                getResponse = client.get(getPersonRequest, RequestOptions.DEFAULT);
            } catch (java.io.IOException e){
                e.getLocalizedMessage();
            }
            try {
                if (getResponse != null) {
//                System.out.println(getResponse);
                    int floorC = (int) getResponse.getSource().get("floor");
                    int slotC = (int) getResponse.getSource().get("slot");
                    int addedSlot = ((floorC - 1) * slotsPerFloor) + slotC;
                    slotSet.remove(addedSlot);
                } else {
                    System.out.println("null");
                }
            }catch(Exception e3){

            }
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
                insertIntoES(car);

            }else if(query == 2){
                System.out.println("Enter Ticket No. ");
                String ticket = br.readLine();
                removeFromES(ticket);
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
                            getRegisteredCarsWithColorES(color);
//                            System.out.println(getRegisteredCarsWithColor(color));
                            break;
                        }
                        case "b": {
                            System.out.println("Enter Color: ");
                            String color = br.readLine();
                            getSlotsWithColorES(color);
//                            System.out.println(getSlotsWithColor(color));
                            break;
                        }
                        case "c":
                            System.out.println("Enter Reg. No.: ");
                            String regNo = br.readLine();
                            getSlotWithRegNoES(regNo);
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
                client.close();
                end = true;
            } else{
                System.out.println("Wrong query");
            }
        }
    }

    private static void getSlotWithRegNoES(String regNo) {
        GetRequest getPersonRequest = new GetRequest("car");
        for(String id : tickets){
            getPersonRequest.id(id);
            GetResponse getResponse = null;
            try {
                getResponse = client.get(getPersonRequest, RequestOptions.DEFAULT);
            } catch (java.io.IOException e){
                e.getLocalizedMessage();
            }
            if(getResponse != null){
                String reg = (String) getResponse.getSource().get("registration");
                if(reg.equals(regNo)){
                    System.out.println("Floor: "+getResponse.getSource().get("floor")
                            +"Slot: " + getResponse.getSource().get("slot"));
                }

            }else{
                System.out.println("null");
            }
        }
    }

    private static void getSlotsWithColorES(String color) {
        GetRequest getPersonRequest = new GetRequest("car");
        for(String id : tickets){
            getPersonRequest.id(id);
            GetResponse getResponse = null;
            try {
                getResponse = client.get(getPersonRequest, RequestOptions.DEFAULT);
            } catch (java.io.IOException e){
                e.getLocalizedMessage();
            }
            if(getResponse != null){
                String col = (String) getResponse.getSource().get("color");
                if(col.equals(color)) {
                    System.out.println("Floor: " + getResponse.getSource().get("floor")
                            + "Slot: " + getResponse.getSource().get("slot"));
                }
            }else{
                System.out.println("null");
            }
        }

    }

    private static void getRegisteredCarsWithColorES(String color) {
        GetRequest getPersonRequest = new GetRequest("car");
        for(String id : tickets){
            getPersonRequest.id(id);
            GetResponse getResponse = null;
            try {
                getResponse = client.get(getPersonRequest, RequestOptions.DEFAULT);
            } catch (java.io.IOException e){
                e.getLocalizedMessage();
            }
            try {
                if (getResponse != null) {
                    String col = (String) getResponse.getSource().get("color");
                    if (col.equals(color)) {
                        System.out.println(getResponse.getSource().get("registration"));
                    }
                } else {
                    System.out.println("null");
                }
            }catch(Exception e4){

            }
        }
    }

    private static void removeFromES(String ticket) {
        GetRequest getPersonRequest = new GetRequest("car");
        getPersonRequest.id(ticket);
        GetResponse getResponse = null;
        try {
            getResponse = client.get(getPersonRequest, RequestOptions.DEFAULT);
        } catch (java.io.IOException e){
            e.getLocalizedMessage();
        }
        if(getResponse != null){
            int floorC = (int) getResponse.getSource().get("floor");
            int slotC = (int) getResponse.getSource().get("slot");
            int addedSlot = ((floorC-1)*slotsPerFloor)+ slotC;
            slotSet.add(addedSlot);

        }else{
            System.out.println("null");
        }
        DeleteRequest deleteRequest = new DeleteRequest("car", ticket);
        try {
            DeleteResponse deleteResponse = client.delete(deleteRequest,RequestOptions.DEFAULT);
        } catch (java.io.IOException e){
            e.getLocalizedMessage();
        }
        if(tickets.contains(ticket)){
            tickets.remove(ticket);
        }
    }

    private static void insertIntoES(Car car) {
        IndexRequest iReq = new IndexRequest("tickets");
        iReq.id("ticketIDs");
        ticketsA += car.getTicketNo()+" ";
        Map<String, String> map = new HashMap<>();
        map.put("all", ticketsA);
        iReq.source(map);
        try {
            IndexResponse response = client.index(iReq, RequestOptions.DEFAULT);
        } catch(ElasticsearchException e) {
            e.getDetailedMessage();
        } catch (java.io.IOException ex){
            ex.getLocalizedMessage();
        }
        Map<String, Object> data = new HashMap<>();
        data.put("color", car.getCarColor());
        data.put("ticket", car.getTicketNo());
        data.put("registration", car.getRegNo());
        data.put("floor",car.getFloorNo());
        data.put("slot",car.getSlotNo());

        IndexRequest indexRequest = new IndexRequest("car");
        indexRequest.source(data);
        indexRequest.id(car.getTicketNo());
        try {
            IndexResponse response = client.index(indexRequest, RequestOptions.DEFAULT);
        } catch(ElasticsearchException e) {
            e.getDetailedMessage();
        } catch (java.io.IOException ex){
            ex.getLocalizedMessage();
        }
        if(!tickets.contains(car.getTicketNo())){
            tickets.add(car.getTicketNo());
        }


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
    private static Car getCar(String color,  String reg) {
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
