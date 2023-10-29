
import java.io.Serializable;
public class Order implements Serializable{
    private int readyTime;       // Order ready time in seconds
    private String restaurant;   // Name of the restaurant
    private String foodItem;     // Food item being delivered

    // Constructor
    public Order(int readyTime, String restaurant, String foodItem)  {
        this.readyTime = readyTime;
        this.restaurant = restaurant;
        this.foodItem = foodItem;
    }

    // Getters and setters
    public int getReadyTime() {
        return readyTime;
    }

    public void setReadyTime(int readyTime) {
        this.readyTime = readyTime;
    }

    public String getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(String restaurant) {
        this.restaurant = restaurant;
    }

    public String getFoodItem() {
        return foodItem;
    }

    public void setFoodItem(String foodItem) {
        this.foodItem = foodItem;
    }

}
