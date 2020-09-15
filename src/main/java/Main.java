
/*
  @author sahil
 * Main Driver Class

 */
import java.sql.*;
import java.util.*;
import java.io.*;

import static java.lang.System.exit;

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
            DataBaseConnect database = new DataBaseConnect(slot, floor, user , pass);
            database.callQueries();
        }else if(functionToUse.equals("mongoDB")){
            String user = properties.getProperty("user");
            String pass = properties.getProperty("password");
            MongoDbConnect mongoDb = new MongoDbConnect(floor,slot,user,pass);
            mongoDb.callQueries();
        }
    }
}
