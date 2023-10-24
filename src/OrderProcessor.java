import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.sql.Date;
import java.text.SimpleDateFormat;

public class OrderProcessor {

    private final Map<String, Semaphore> restaurantSemaphores = new HashMap<>();
    private final Map<String, Datum> nameToRestaurant = new HashMap<>();
    private final long startTime;
    private double deliverLat;
    private double deliverLong;
    private int num;
    private int count = 0;

    public OrderProcessor(Restaurant myRestaurant, double myLat, double myLong, int num) {
        this.deliverLat = myLat;
        this.deliverLong = myLong;
        startTime = System.currentTimeMillis();
        for (Datum d : myRestaurant.getData()) {
        
            Semaphore semaphore = new Semaphore(d.getDrivers());
            restaurantSemaphores.put(d.getName(), semaphore);
            nameToRestaurant.put(d.getName(), d);
        }
        this.num = num;
        System.out.println("Starting execution of program...");
    }
    
    
    
    public void processOrder(Order order) {
        String restaurantName = order.getRestaurant();

        Semaphore semaphore = restaurantSemaphores.get(restaurantName);
      //“how to have the restaurants be run at the same time on diff threads...(3 lines). ChatGPT, 27 Sep. version, OpenAI, 27 Sep. 2023, chat.openai.com/chat.
        if (semaphore != null) {
            Thread thread = new Thread(() -> {
                try {
                    semaphore.acquire();
                  //“how to get the time since start...(2 lines). ChatGPT, 27 Sep. version, OpenAI, 27 Sep. 2023, chat.openai.com/chat.
                    long readyTimeInMillis = order.getReadyTime();
                    long timeSinceStart = System.currentTimeMillis() - startTime;
                    
                    //handles if the order is not ready yet
                    if (timeSinceStart < readyTimeInMillis * 1000) {
                    	timeSinceStart = System.currentTimeMillis() - startTime;
                        Thread.sleep(1000 * readyTimeInMillis - timeSinceStart);
                    }
                    timeSinceStart = System.currentTimeMillis() - startTime;
                  //“how to print out the date in this format...(1 lines). ChatGPT, 27 Sep. version, OpenAI, 27 Sep. 2023, chat.openai.com/chat.
                    SimpleDateFormat sdf = new SimpleDateFormat("[HH:mm:ss.SSS]");
                    
        
                    double restaurantLatitude = nameToRestaurant.get(order.getRestaurant()).getLatitude();
                    double restaurantLongitude = nameToRestaurant.get(order.getRestaurant()).getLongitude();

                    Driver driver = new Driver(restaurantLatitude, restaurantLongitude, deliverLat, deliverLong);
                    System.out.println(sdf.format(new Date(timeSinceStart-57600000 )) + " Starting delivery of " + order.getFoodItem() + " from " + restaurantName + "!");
                    driver.start();
                    driver.join();
       

                    timeSinceStart = System.currentTimeMillis() - startTime;
                    System.out.println(sdf.format(new Date(timeSinceStart - 57600000)) + " Finished delivery of " + order.getFoodItem() + " from " + restaurantName + "!");

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    semaphore.release();
                    count += 1;
                    if (count == num) {
                    	System.out.println("All orders complete");
                    }
                    
                    
                }
            });
            thread.start();
        } else {
            System.out.println("Restaurant not found: " + restaurantName);
        }

    }
 
}
