package starter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import database.MongoDbConnect;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.TreeSet;

@SpringBootApplication
public class ParkingApi {
    public static  int slotsPerFloor;
    public static int totalFloors;
    public static TreeSet<Integer> slotSet = new TreeSet<>();
    public static MongoDbConnect mongoDb;

    public  ParkingApi()throws IOException {
        FileReader propertiesFile = new FileReader("src/main/resources/function.properties");
        Properties properties = new Properties();
        properties.load(propertiesFile);
        String functionToUse = properties.getProperty("functionToBeUsed");
        String floors = properties.getProperty("totalFloors");
        String slots = properties.getProperty("slotsPerFloor");
        totalFloors = Integer.parseInt(floors);
        slotsPerFloor = Integer.parseInt(slots);
        fillAssignSlot();

    }
    public void apiStart(String args[]){
        SpringApplication.run(ParkingApi.class,args);
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
