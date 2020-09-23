

import database.InMemory;

import java.sql.SQLException;
import Object.Car;
import database.*;
import static org.junit.jupiter.api.Assertions.*;

class TicketTest {

    @org.junit.jupiter.api.Test

    void main() throws SQLException {
        Car car = new Car("red","JK02AA123",1,2,"1tt2"); //New Car Object

        /*
            Testing getter Setter
        */

        assertEquals("1tt2", car.getTicketNo());
        assertEquals("red", car.getCarColor());
        assertEquals("JK02AA123", car.getRegNo());
        assertEquals(1,car.getFloorNo());
        car.setSlotNo(1);
        assertEquals(1,car.getSlotNo());
        car.setTicketNo("1tt1");
        assertEquals("1tt1", car.getTicketNo());

        /*
            Main Class Testing
         */
        InMemory testMain = new InMemory(5,20);

        /*
            Testing Main Class Methods
         */

        testMain.carEnters("red","JK01RRR1");
        testMain.carEnters("black", "JK01BBB1");
        testMain.carEnters("black","JK01BBB2");
        testMain.carEnters("white","JK01WWW1");
        String outputS = testMain.getSlotWithRegNo("JK01RRR1");
        String expected = "Floor: " + 1 + " Slot: " + 1;
        assertEquals(expected,outputS);
        String outputR = testMain.getRegisteredCarsWithColor("red");
        StringBuilder expectedR = new StringBuilder();
        expectedR.append("JK01RRR1").append(" \n");
        assertEquals(expectedR.toString(),outputR);
        StringBuilder returnCars= new StringBuilder();
        returnCars.append(" Floor: ").append(1).append(" Slot: ").append(4).append("\n");
        String outputM =  testMain.getSlotsWithColor("white");
        assertEquals(returnCars.toString(),outputM);

        testMain.carExits("1tt3");
        testMain.carEnters("blue","JK01NNN1");
        String outputNew = testMain.getSlotWithRegNo("JK01NNN1");
        String expectedNew = "Floor: " + 1 + " Slot: " + 3;
        assertEquals(expectedNew,outputNew);

        System.out.println("All Test Cases Passed Successfully");

    }
}