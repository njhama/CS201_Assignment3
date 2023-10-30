import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TimeZone;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

public class Server {

    private static final int PORT = 3456;
    private ServerSocket serverSocket;
    private ExecutorService executorService;
    private List<ConnectionThread> myConnections = new ArrayList<>();;
    private int ClientId = 0;
    private Semaphore availableDriversSemaphore;
    private BlockingQueue<ConnectionThread> availableDriversQueue;
    private Map<String, Coordinate> restaurantCoordinates = new ConcurrentHashMap<>();
    private Coordinate homeCoords;
    private long startTime;
    
    
    public static void main(String[] args) throws Exception {
    	
        Server server = new Server();
        server.start();
    }
    
    private List<Order> readOrders(String filename) throws Exception {
        List<Order> orders = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] orderData = line.split(",");
                int readyTime = Integer.parseInt(orderData[0].trim());
                String restaurant = orderData[1].trim();
                String foodItem = orderData[2].trim();
                
                
                Coordinate restCoords = YelpAPI.getRestaurantCoordinates(restaurant, homeCoords);     
                restaurantCoordinates.put(restaurant, restCoords);
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
    
    
    
    public void start() throws Exception {
    	
    	//USER INPUT STUFFS
    	Scanner scanner = new Scanner(System.in);
    	System.out.println("What is the name of the schedule file?");
    	//String fileName = scanner.nextLine();
    	
    	//temp
    	String fileName = "schedule.csv";
    	Double myLat = 34.02116;
    	Double myLong = -118.287132;
    	
    	
    	System.out.println("What is your latitude?");
    	//Double myLat = scanner.nextDouble();
    	System.out.println("What is your longitude?");
    	//Double myLong = scanner.nextDouble();
    	
    	System.out.println("How many drivers will be in service today?");
    	//int numDrivers = scanner.nextInt();
    	int numDrivers = 2;
    	Coordinate coords = new Coordinate(myLat, myLong);
    	homeCoords = coords;
    	
    	
    	
    	
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
                ConnectionThread newConnect = new ConnectionThread(clientSocket, ClientId, this);
                ClientId += 1;
                newConnect.start();
                myConnections.add(newConnect);
                
                Message init = new Message("init", coords);
                newConnect.sendMessage(init);
                Message initMap = new Message("initMap", restaurantCoordinates);
                newConnect.sendMessage(initMap);
                
                System.out.println("Connection from " + clientSocket.getInetAddress());
                
                
                if (myConnections.size() == numDrivers) {
                	System.out.println("Starting service");
                	Message startMsg = new Message("start", "All drivers are connected. Service is now starting.");
                    for (ConnectionThread it : myConnections) {
                        it.sendMessage(startMsg);
                    }
                    
                    startTime = System.currentTimeMillis();
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
        	System.out.println("We DONE");
            //shutdown();
        }
        
        
      //init
    	availableDriversSemaphore = new Semaphore(numDrivers);
        availableDriversQueue = new LinkedBlockingQueue<>(myConnections);
        processOrders(myOrders);
        while (true) {
        	//send something to the drivers so thhey can start
        	//start processing orders
        	
        	
        	//send message with payload of min heap of orders, client should do in order
        	//we have myOrders
        	//start doing the processing
        	
        	//should send some sort of lists to 
        	
        	
        	//call smth to break out
        	
        	
        	
        }
    }
    
    //funtion to handle order proessing
    private void processOrders(List<Order> orders) throws InterruptedException {
        Map<Integer, List<Order>> ordersByReadyTime = orders.stream()
                .collect(Collectors.groupingBy(Order::getReadyTime));

        for (Map.Entry<Integer, List<Order>> entry : ordersByReadyTime.entrySet()) {
            int readyTime = entry.getKey();
            List<Order> readyOrders = entry.getValue();
            
            long currentTime = System.currentTimeMillis();
            System.out.println("CURRENT: " + currentTime);
            System.out.println("START: " + startTime); 
            System.out.println("DIFF: " + (currentTime - startTime));
            
            long sleepTime = Math.max(startTime - currentTime, (readyTime * 1000) - (currentTime - startTime));
            if (sleepTime > 0) {
                try {
                    System.out.println("Order not ready or service not started, waiting for " + sleepTime + " milliseconds...");
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            else {
            	System.out.println("WE GOOD");
 
            }
            
            if (true) {
	            try {
	                availableDriversSemaphore.acquire();
	                ConnectionThread driver = availableDriversQueue.take();
	                
	                
	                sendOrdersToDriver(readyOrders, driver);
	            } catch (InterruptedException e) {
	                Thread.currentThread().interrupt();
	            }
            }
        }
        
        System.out.println();
        SimpleDateFormat sdf = new SimpleDateFormat("[HH:mm:ss:SSS]");
        long timeSinceStart = System.currentTimeMillis() - startTime;
        Date elapsedTime = new Date(timeSinceStart - TimeZone.getDefault().getRawOffset());
        System.out.println(sdf.format(elapsedTime) + "\nWE DONE!!!)");
        
        Message doneMSG = new Message("done", "void");
        for (ConnectionThread i: myConnections) {
        	i.sendMessage(doneMSG);
        }
        
    }

    private void sendOrdersToDriver(List<Order> orders, ConnectionThread driver) throws InterruptedException {
        Message orderMessage = new Message("order", orders);
        System.out.println("SENDING MESSAGE FROM " + orderMessage.getType());
        driver.sendMessage(orderMessage);
        availableDriversQueue.put(driver);  // Put the driver back in the queue when done
    }

    
    public void releaseDriver(ConnectionThread driver ) {
    	availableDriversQueue.add(driver);
        availableDriversSemaphore.release();
        System.out.println("Driver  is now available");
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