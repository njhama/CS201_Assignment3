import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

public class Server {

    private static final int PORT = 3456;
    private ServerSocket serverSocket;
    private ExecutorService executorService;
    private List<ConnectionThread> myConnections = new ArrayList<>();;
    private int ClientId = 0;
    private Semaphore availableDriversSemaphore;
    private BlockingQueue<ConnectionThread> availableDriversQueue;
    
    
    public static void main(String[] args) throws InterruptedException {
    	
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
    
    
    
    public void start() throws InterruptedException {
    	
    	//USER INPUT STUFFS
    	Scanner scanner = new Scanner(System.in);
    	System.out.println("What is the name of the schedule file?");
    	//String fileName = scanner.nextLine();
    	
    	//temp
    	String fileName = "schedule.csv";
    	Double myLat = 1.0;
    	Double myLong = 1.0;
    	
    	
    	System.out.println("What is your latitude?");
    	//Double myLat = scanner.nextDouble();
    	System.out.println("What is your longitude?");
    	//Double myLong = scanner.nextDouble();
    	
    	System.out.println("How many drivers will be in service today?");
    	//int numDrivers = scanner.nextInt();
    	int numDrivers = 3;
    	
    	
    	
    	
    	
    	
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
                //CountDownLatch latch = new CountDownLatch(1);  // Create a new latch for each connection
                ConnectionThread newConnect = new ConnectionThread(clientSocket, ClientId);
                ClientId += 1;
                newConnect.start();
                myConnections.add(newConnect);
                

                System.out.println("Connection from " + clientSocket.getInetAddress());
                
                
                if (myConnections.size() == numDrivers) {
                	System.out.println("Starting service");
                	Message startMsg = new Message("FROM SERVER: Service Starting", "All drivers are connected. Service is now starting.");
                    for (ConnectionThread it : myConnections) {
                        it.sendMessage(startMsg);
                    }
                	break;
                }
                else {
                	int remainingDrivers = numDrivers - myConnections.size();
                    Message waitMsg = new Message("Awaiting More Drivers", remainingDrivers + " more driver is\r\n"
                    		+ "needed before the\r\n"
                    		+ "service can begin.\r\n"
                    		+ "Waiting...\r\n"
                    		+ "");
                    for (ConnectionThread it : myConnections) {
                        it.sendMessage(waitMsg);
                    }
                	System.out.println("Waiting for " + String.valueOf(numDrivers - myConnections.size()) + " more driver(s)...");
                }
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //shutdown();
        }
        
        
      //init
    	availableDriversSemaphore = new Semaphore(numDrivers);
        availableDriversQueue = new LinkedBlockingQueue<>(myConnections);
    
        while (true) {
        	//send something to the drivers so thhey can start
        	//start processing orders
        	
        	
        	//send message with payload of min heap of orders, client should do in order
        	//we have myOrders
        	//start doing the processing
        	
        	
        	
        	
        	
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