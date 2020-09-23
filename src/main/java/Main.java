
/*
  @author sahil
 * Main Driver Class

 */
import database.*;

import java.sql.*;
import java.util.*;
import java.io.*;

class Main {

    /**
     * Driver function for input and registering car adn various search queries
     * @param args  Input arguments array
     * @throws IOException  BufferedReader exception
     */


    public static void main(String[] args) throws IOException, SQLException {
        FileReader propertiesFile = new FileReader("src/main/resources/function.properties");
        Properties properties = new Properties();
        properties.load(propertiesFile);
        String functionToUse = properties.getProperty("functionToBeUsed");
        String floors = properties.getProperty("totalFloors");
        String slots = properties.getProperty("slotsPerFloor");
        int floor = Integer.parseInt(floors);
        int slot = Integer.parseInt(slots);
        if(functionToUse.equalsIgnoreCase("InMemory")){
            InMemory inMemory = new InMemory(floor,slot);
            inMemory.callQueries();
        }else if(functionToUse.equals("mysqlDB")){
            String user = properties.getProperty("user");
            String pass = properties.getProperty("password");
            String databaseURL = properties.getProperty("database");
            MysqlConnect database = new MysqlConnect(slot, floor, user , pass, databaseURL);
            database.callQueries();
        }else if(functionToUse.equals("mongoDB")){
            String user = properties.getProperty("user");
            String pass = properties.getProperty("password");
            String host = properties.getProperty("host");
            int port1 = Integer.parseInt(properties.getProperty("port1"));
            MongoDbConnect mongoDb = new MongoDbConnect(floor,slot,user,pass,host,port1);
            mongoDb.callQueries();
        }else if(functionToUse.equals("radisDB")){
            String host = properties.getProperty("host");
            int port1 = Integer.parseInt(properties.getProperty("port1"));
            RedisDb radisDB = new RedisDb(floor, slot, host, port1);
            radisDB.callQueries();
        }else if(functionToUse.equals("ES")){
            String host = properties.getProperty("host");
            int port1 = Integer.parseInt(properties.getProperty("port1"));
            int port2 = Integer.parseInt(properties.getProperty("port2"));
            elasticsearch es = new elasticsearch(floor,slot,host,port1,port2);
            es.callQueries();
        }
    }

}
