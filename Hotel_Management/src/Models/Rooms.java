
package Models;

import javafx.scene.control.Button;

public class Rooms {
    private int room_no;
    private String type;
    private int capacity;
    private String status;
    private int price;
    private Button button;

    public Rooms(int Room_no, String Type, int Capacity, String Status, int Price){
        this.room_no = Room_no;
    	this.type = Type;
        this.capacity = Capacity;
        this.status = Status;
        this.price = Price;
        this.button = button;
    }

    public int getRoom_no() {
    	return room_no;
    }
    public String getType() {
        return type;
    }

    public int getCapacity() {
        return capacity;
    }

    public String getStatus() {
        return status;
    }
    
    public int getPrice() {
        return price;
    }
    
    public Button getButton() {
        return button;
    }
}
