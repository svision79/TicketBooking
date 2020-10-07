package Object;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * @author sahil
 * Car class to store information about the carss that enters the parking lot
 */
@Document
public class Car {
    @Field
    private int floorNo;
    @Field
    private int slotNo;
    @Field
    private String regNo;
    @Field
    private String carColor;
    @Id
    private String ticketNo;

    public Car(int floorNo, int slotNo, String regNo, String carColor, String ticketNo) {
        this.floorNo = floorNo;
        this.slotNo = slotNo;
        this.regNo = regNo;
        this.carColor = carColor;
        this.ticketNo = ticketNo;
    }


    /**
     * Getter method for floor number
     * @return floorNo  Floor No on which car is parked
     */
    public int getFloorNo(){
        return this.floorNo;
    }

    /**
     * Setter method for car color
     * @param carColor New color to be assigned to car object
     */
    public void setCarColor(String carColor) {
        this.carColor = carColor;
    }


    /**
     * Setter method for assigning floor
     * @param floorNo Floor no to be given.
     */
    public void setFloorNo(int floorNo) {
        this.floorNo = floorNo;
    }

    /**
     * Setter for registration Number
     * @param regNo registration number of car
     */
    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    /**
     * Setter for slot number
     * @param slotNo slot assigned to the car
     */
    public void setSlotNo(int slotNo) {
        this.slotNo = slotNo;
    }

    /**
     * Getter for slot number
     * @return slotNo slot number assigned
     */
    public int getSlotNo() {
        return slotNo;
    }

    /**
     * Getter for car color
     * @return carColor Gives car color
     */
    public String getCarColor() {
        return carColor;
    }

    /**
     * Getter for registration number
     * @return regNo registration number of the car
     */
    public String getRegNo() {
        return regNo;
    }

    /**
     * getter for ticket number
     * @return ticketNo ticket information of the car parked
     */
    public String getTicketNo() {
        return ticketNo;
    }

    /**
     * Setter for ticket number
     * @param ticketNo ticket number given for that particular car
     */
    public void setTicketNo(String ticketNo) {
        this.ticketNo = ticketNo;
    }
}
