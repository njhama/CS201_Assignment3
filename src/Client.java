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
    
   

    @SuppressWarnings("unused")
	public void run() throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to JoesTable v2.0!");
        System.out.println("Enter the server hostname: ");
        //String hostname = scanner.nextLine();
        String hostname = "localhost";
        System.out.print("Enter the server port: ");
        //int port = scanner.nextInt();
        int port = 3456;
        System.out.println();
        //scanner.nextLine();  // Consume the newline
        SimpleDateFormat sdf = new SimpleDateFormat("[HH:mm:ss:SSS]");
        sdf.setTimeZone(TimeZone.getTimeZone("PST"));
        try {
            socket = new Socket(hostname, port);
            output = new ObjectOutputStream(socket.getOutputStream());
            output.flush();
            input = new ObjectInputStream(socket.getInputStream());
            //System.out.println("Connected to server: " + socket.getRemoteSocketAddress());
            System.out.println();
            while (true) {
            	Message test = (Message) input.readObject();
            	
            	if ("init".equals(test.getType())) {
            		homeCoords = (Coordinate) test.getPayload();
            		currCoords = homeCoords;
            	}
            	
            	if ("waitMsg".equals(test.getType())) {
            		 System.out.println(test.getPayload());
            	}
            	
            	if ("start".equals(test.getType())) {
            		System.out.println("All drivers have arrived!");
            		System.out.println("Starting service.");
            		System.out.println();
            	}
            	
            	if ("initMap".equals(test.getType())) {
            		restaurantCoordinates = (Map<String, Coordinate>) test.getPayload();
            	}
            	
            	if ("done".equals(test.getType())) {
            		System.out.println(sdf.format(test.getPayload()) + "\nAll orders completed!");
            	}
            	
                if ("order".equals(test.getType())) {
                	
                	if (first) {
                		startTime = System.currentTimeMillis();
                		first = false;
                		
                	}
                	

                	
                	
	                List<Order> myOrders = (List<Order>) test.getPayload();
	                for (Order i: myOrders) {
	                	long timeSinceStart = System.currentTimeMillis() - startTime;
	                	Date elapsedTime = new Date(timeSinceStart - TimeZone.getDefault().getRawOffset());
                        System.out.println(sdf.format(elapsedTime) + "\nStarting delivery of " + i.getFoodItem() + " from " + i.getRestaurant() + ".");
	                }
	                

	                String prev = null;
	                while (!myOrders.isEmpty()) {
	                    Order closestOrder = null;
	                    double closestDistance = Double.MAX_VALUE;
	                    Coordinate closestCoords = null;
	                    //String prev = null;
	                    
	                    
	                    for (int i = 0; i < myOrders.size(); i++) {
	                    	
	                        Order o = myOrders.get(i);
	                        Coordinate restaurantCoords = restaurantCoordinates.get(o.getRestaurant());
	                        double distance = calcDistance(currCoords, restaurantCoords);
	                        if (distance < closestDistance) {
	                            closestDistance = distance;
	                            closestOrder = o;
	                            closestCoords = restaurantCoords;
	                        }
	                    }
	                    
	
	                    
	                    if (closestOrder != null) {
	                    	if (prev != null) {
	                    		if (!(prev.equals(closestOrder.getRestaurant()))) {
		                    		long timeSinceStart = System.currentTimeMillis() - startTime;
			                        Date elapsedTime = new Date(timeSinceStart - TimeZone.getDefault().getRawOffset());
		                    		System.out.println(sdf.format(elapsedTime) + "\nContuing delivery to " + closestOrder.getRestaurant());
		                    	}
	                    	}
	                    	
	                        long timeSinceStart = System.currentTimeMillis() - startTime;
	                        Date elapsedTime = new Date(timeSinceStart - TimeZone.getDefault().getRawOffset());

	                        currCoords = closestCoords;
	                        myOrders.remove(closestOrder);

	                        
	                        Thread.sleep((long) (closestDistance * 1000));

	                        
	                        timeSinceStart = System.currentTimeMillis() - startTime;
	                        elapsedTime = new Date(timeSinceStart - TimeZone.getDefault().getRawOffset());
	                        System.out.println(sdf.format(elapsedTime) + "\nFinished delivery of " + closestOrder.getFoodItem() + " to " + closestOrder.getRestaurant() + ".");
	                        
	                        //CHECK IF THE NEXT  O
	                        prev = closestOrder.getRestaurant();
	                        
	                        
	                    } else {
	                        System.out.println("Failed to find the closest restaurant.");
	                        break;
	                    }
	                }
	                
	                
	                
	                
	                long timeSinceStart = System.currentTimeMillis() - startTime;
                    Date elapsedTime = new Date(timeSinceStart - TimeZone.getDefault().getRawOffset());
                    System.out.println(sdf.format(elapsedTime) + "\nFinished all deliveries, returning back to HQ.");
	                //System.out.println("Finished all deliveries, returning back to HQ.");
	              
	                double distance = calcDistance(currCoords, homeCoords);
	                Thread.sleep((long) (distance * 1000));
	                currCoords = homeCoords;
	                timeSinceStart = System.currentTimeMillis() - startTime;
                    elapsedTime = new Date(timeSinceStart - TimeZone.getDefault().getRawOffset());
                    System.out.println(sdf.format(elapsedTime) + "\nReturned to HQ.");
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
