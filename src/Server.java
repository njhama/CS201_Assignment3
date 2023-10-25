import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private static final int PORT = 3456;
    private ServerSocket serverSocket;
    private ExecutorService executorService;
    private List<ConnectionThread> myConnections = new ArrayList<>();;
    private int ClientId = 0;
    
    public static void main(String[] args) {
    	
        Server server = new Server();
        server.start();
    }
    
    private static List<Order> readOrders(String filename) {
		//“write a function to read in this csv file....(15 lines) ChatGPT, 27 Sep. version, OpenAI, 27 Sep. 2023, chat.openai.com/chat.
        List<Order> orders = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] orderData = line.split(",");
                int readyTime = Integer.parseInt(orderData[0].trim());
                String restaurant = orderData[1].trim();
                String foodItem = orderData[2].trim();
                Order myOrder = new Order(readyTime, restaurant, foodItem);
                orders.add(myOrder);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return orders;
    }

    
    
    //need to have a ds to keep track of the threads for each connection
    //iterate thru the restaurants and make a new ConnectionThread for each one
    //keep track of how many clients have join and send things to those clinets 
    
    
    
    public void start() {
    	
    	//USER INPUT STUFFS
    	Scanner scanner = new Scanner(System.in);
    	System.out.println("What is the name of the schedule file?");
    	String fileName = scanner.nextLine();
    	
    	System.out.println("What is your latitude?");
    	Double myLat = scanner.nextDouble();
    	System.out.println("What is your longitude?");
    	Double myLong = scanner.nextDouble();
    	
    	System.out.println("How many drivers will be in service today?");
    	int numDrivers = scanner.nextInt();
    	
    	//read thru the file
    	List<Order> myOrders = readOrders(fileName);
    	for (Order it : myOrders) {
    		System.out.println(it.getReadyTime());
    	}
    	
    	
        try {
            serverSocket = new ServerSocket(PORT);
            executorService = Executors.newFixedThreadPool(10);  // Adjust the thread pool size as needed
            System.out.println("Server listening on port " + PORT);
            InetAddress inetAddress = InetAddress.getLocalHost();
            System.out.println("Host Name: " + inetAddress.getHostName());

            while (true) {
                Socket clientSocket = serverSocket.accept();
                ConnectionThread newConnect = new ConnectionThread(clientSocket, ClientId);
                ClientId += 1;
                myConnections.add(newConnect);
                //create a new connecetionthread
                //migt have to handle teh rquest
                //System.out.println(myConnections.size());
                System.out.println("Connection from " + clientSocket.getInetAddress());
                if (myConnections.size() == numDrivers) {
                	
                	System.out.println("Starting service");
                	break;
                }
                else {
                	System.out.println("Waiting for " + String.valueOf(numDrivers - myConnections.size()) + " more driver(s)...");
                }
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //shutdown();
        }
        
        
        //hanlde logic
        
        
        
        while (true) {
        	//send something to the drivers so thhey can start
        }
    }

    
    //close everything
    public void shutdown() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            if (executorService != null && !executorService.isShutdown()) {
                executorService.shutdown();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}