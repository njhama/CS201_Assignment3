import java.io.*;
import java.net.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;

public class Client {
    
    private Socket socket;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Coordinate homeCoords;
    private Coordinate currCoords;
    private long startTime;
    private boolean first = true;
    private Map<String, Coordinate> restaurantCoordinates;
    
    public static void main(String[] args) throws Exception {
        Client client = new Client();
        //startTime = System.currentTimeMillis();
        client.run();
        // = System.currentTimeMillis();
    }
    
   

    public void run() throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to the program!");
        System.out.print("Enter the server hostname: ");
        //String hostname = scanner.nextLine();
        String hostname = "localhost";
        System.out.print("Enter the server port: ");
        //int port = scanner.nextInt();
        int port = 3456;
        //scanner.nextLine();  // Consume the newline
        SimpleDateFormat sdf = new SimpleDateFormat("[HH:mm:ss:SSS]");
        sdf.setTimeZone(TimeZone.getTimeZone("PST"));
        try {
            socket = new Socket(hostname, port);
            output = new ObjectOutputStream(socket.getOutputStream());
            output.flush();
            input = new ObjectInputStream(socket.getInputStream());
            System.out.println("Connected to server: " + socket.getRemoteSocketAddress());
            
            while (true) {
            	Message test = (Message) input.readObject();
            	
            	if ("init".equals(test.getType())) {
            		homeCoords = (Coordinate) test.getPayload();
            		currCoords = homeCoords;
            	}
            	
            	if ("initMap".equals(test.getType())) {
            		restaurantCoordinates = (Map<String, Coordinate>) test.getPayload();
            	}
            	
                if ("order".equals(test.getType())) {
                	
                	if (first) {
                		startTime = System.currentTimeMillis();
                		first = false;
                	}
                	
                	//TEMP
                    long timeSinceStart1 = System.currentTimeMillis() - startTime;
                    Date elapsedTime1 = new Date(timeSinceStart1 - TimeZone.getDefault().getRawOffset());
                    //System.out.println(sdf.format(elapsedTime1) + " INIT TIME");
                    //TEMP
                	
                	
	                List<Order> myOrders = (List<Order>) test.getPayload();
	                for (Order i: myOrders) {
	                	long timeSinceStart = System.currentTimeMillis() - startTime;
	                	Date elapsedTime = new Date(timeSinceStart - TimeZone.getDefault().getRawOffset());
                        System.out.println(sdf.format(elapsedTime) + " Starting delivery of " + i.getFoodItem() + " from " + i.getRestaurant() + "!");
	                }
	                
	                //TEMP
                    long timeSinceStart2 = System.currentTimeMillis() - startTime;
                    Date elapsedTime2 = new Date(timeSinceStart2 - TimeZone.getDefault().getRawOffset());
                    //System.out.println(sdf.format(elapsedTime2) + " 2 TIME");
                    //TEMP
	                
	                while (!myOrders.isEmpty()) {
	                    Order closestOrder = null;
	                    double closestDistance = Double.MAX_VALUE;
	                    Coordinate closestCoords = null;
	                    
	                    
	                  //TEMP
	                    long timeSinceStart3 = System.currentTimeMillis() - startTime;
	                    Date elapsedTime3 = new Date(timeSinceStart3 - TimeZone.getDefault().getRawOffset());
	                    //System.out.println(sdf.format(elapsedTime3) + " BEFORE API");
	                    //TEMP
	                    
	                    for (Order o : myOrders) {
	                        Coordinate restaurantCoords = restaurantCoordinates.get(o.getRestaurant());
	                        double distance = calcDistance(currCoords, restaurantCoords);
	                        if (distance < closestDistance) {
	                            closestDistance = distance;
	                            closestOrder = o;
	                            closestCoords = restaurantCoords;
	                        }
	                    }
	                    
	                    
	                    //TEMP
	                    long timeSinceStart4 = System.currentTimeMillis() - startTime;
	                    Date elapsedTime4 = new Date(timeSinceStart4 - TimeZone.getDefault().getRawOffset());
	                    //System.out.println(sdf.format(elapsedTime4) + " AFTER API");
	                    //TEMP
	                    
	                    if (closestOrder != null) {
	                        long timeSinceStart = System.currentTimeMillis() - startTime;
	                        Date elapsedTime = new Date(timeSinceStart - TimeZone.getDefault().getRawOffset());
	                        //System.out.println(sdf.format(elapsedTime) + " Starting delivery of " + closestOrder.getFoodItem() + " from " + closestOrder.getRestaurant() + "!");
	                        //System.out.println("CURR: 	" + currCoords.getLatitude() + ", " + currCoords.getLongitude());
	                        currCoords = closestCoords;
	                        myOrders.remove(closestOrder);

	                        
	                        //System.out.println("CLOSEST: " + closestCoords.getLatitude() + ", " + closestCoords.getLongitude());
	                        //System.out.println("SLEEPIONG FOR : " + closestDistance * 1000);
	                        
	                        //TEMP
	                        timeSinceStart = System.currentTimeMillis() - startTime;
	                        elapsedTime = new Date(timeSinceStart - TimeZone.getDefault().getRawOffset());
	                        //System.out.println(sdf.format(elapsedTime) + " CURRENT TIME");
	                        //TEMP
	                        
	                        Thread.sleep((long) (closestDistance * 1000));
	                        //TEMP
	                        timeSinceStart = System.currentTimeMillis() - startTime;
	                        elapsedTime = new Date(timeSinceStart - TimeZone.getDefault().getRawOffset());
	                        //System.out.println(sdf.format(elapsedTime) + " CURRENT TIME");
	                        //TEMP
	                        
	                        timeSinceStart = System.currentTimeMillis() - startTime;
	                        elapsedTime = new Date(timeSinceStart - TimeZone.getDefault().getRawOffset());
	                        System.out.println(sdf.format(elapsedTime) + " Did delivery of " + closestOrder.getFoodItem() + " from " + closestOrder.getRestaurant() + "!");
	                        
	                        
	                    } else {
	                        System.out.println("Failed to find the closest restaurant.");
	                        break;
	                    }
	                }
	                
	                System.out.println("WE ARE GOING BACK TO HQ");
	              
	                double distance = calcDistance(currCoords, homeCoords);
	                Thread.sleep((long) (distance * 1000));
	                currCoords = homeCoords;
	                long timeSinceStart = System.currentTimeMillis() - startTime;
                    Date elapsedTime = new Date(timeSinceStart - TimeZone.getDefault().getRawOffset());
                    System.out.println(sdf.format(elapsedTime) + "Returned to HQ.");
                	Message done = new Message("freed", "smth");
                	output.writeObject(done);
                	output.flush();
                }

                	
                	
                	
                	
                	
                	
                }
                
        	
                
            
            
            

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to connect to server. :(");
        } finally {
            //closeResources();
        }
        
    }
    
    //calc how long to sleep for
    public double calcDistance(Coordinate coord1, Coordinate coord2) {
        double lat1Radians = Math.toRadians(coord1.getLatitude());
        double long1Radians = Math.toRadians(coord1.getLongitude());
        double lat2Radians = Math.toRadians(coord2.getLatitude());
        double long2Radians = Math.toRadians(coord2.getLongitude());
        double distance = 3963.0 * Math.acos(Math.sin(lat1Radians) * Math.sin(lat2Radians) 
                                            + Math.cos(lat1Radians) * Math.cos(lat2Radians) 
                                            * Math.cos(long2Radians - long1Radians));
        return distance;
    }

    
    private void closeResources() {
        try {
            if (input != null) input.close();
            if (output != null) output.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}



//there is no definite sequence of read and writes
//should i have a unique identifier forteh object sends to handle?
