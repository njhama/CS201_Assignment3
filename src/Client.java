import java.io.*;
import java.net.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Scanner;
import java.util.TimeZone;

public class Client {
    
    private Socket socket;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Coordinate homeCoords;
    private Coordinate currCoords;
    private long startTime;
    private boolean first = true;

    public static void main(String[] args) throws ClassNotFoundException, InterruptedException {
        Client client = new Client();
        //startTime = System.currentTimeMillis();
        client.run();
        // = System.currentTimeMillis();
    }
    
   

    public void run() throws ClassNotFoundException, InterruptedException {
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
            	}
            	
                if ("order".equals(test.getType())) {
                	
                	//if its the first order, start the startTime
                	if (first) {
                		startTime = System.currentTimeMillis();
                		first = false;
                	}
                	
                	
                	//the payload is an array of orders so it has to be iterated thru
                	
                	
                	List<Order> myOrders = (List<Order>) test.getPayload();           
                	for (Order o: myOrders) {
                		long timeSinceStart = System.currentTimeMillis() - startTime;
                		Date elapsedTime = new Date(timeSinceStart - TimeZone.getDefault().getRawOffset());
                		
                		System.out.println(sdf.format(elapsedTime) + " Starting delivery of " + o.getFoodItem() + " from " + o.getRestaurant() + "!");

                		//System.out.println(o.getReadyTime() + " " + o.getRestaurant() + " " + o.getFoodItem());
                		
                		//SAY WE ARE STARTING 
                		//CALC DISTANCE TO EACH ORDER
                		//
                		
                		
                		
                	}
                	Thread.sleep(5000);
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
    public double calcDistance() {
    	
    	double distance = 0;
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
