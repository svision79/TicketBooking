package starter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import database.MongoDbConnect;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

@SpringBootApplication(exclude = MongoAutoConfiguration.class)
public class ParkingApi {
    public static MongoDbConnect mongoDb;
    public  ParkingApi()throws IOException {
        FileReader propertiesFile = new FileReader("function.properties");
        Properties properties = new Properties();
        properties.load(propertiesFile);
        String functionToUse = properties.getProperty("functionToBeUsed");
        String floors = properties.getProperty("totalFloors");
        String slots = properties.getProperty("slotsPerFloor");
        int floor = Integer.parseInt(floors);
        int slot = Integer.parseInt(slots);
        String user = properties.getProperty("user");
        String pass = properties.getProperty("password");
        String host = properties.getProperty("host");
        int port1 = Integer.parseInt(properties.getProperty("port1"));
        if(mongoDb == null){
            mongoDb = new MongoDbConnect(floor,slot,user,pass,host,port1);
        }
    }
    public void apiStart(String args[]){
        SpringApplication.run(ParkingApi.class,args);
    }

}
