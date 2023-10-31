import java.io.BufferedReader;
import java.io.File;
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
    SimpleDateFormat sdf = new SimpleDateFormat("[HH:mm:ss:SSS]");
    private boolean first;
    private int numDrivers;
    int[] counter = {0};
    CountDownLatch latch;
    private int currDrivers;
    
    public static void main(String[] args) throws Exception {
        Server server = new Server();
        server.start();
    }
    
    
    //“write funtion to read this csv files ... have it return a list of orders ...(20 lines). ChatGPT, 30 Oct. version, OpenAI, 30 Oct. 2023, chat.openai.com/chat.
    private List<Order> readOrders(String filename) throws Exception {
        List<Order> orders = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] orderData = line.split(",");
                int readyTime = Integer.parseInt(orderData[0].trim());
                String restaurant = orderData[1].trim();
                String foodItem = orderData[2].trim();
                if (!restaurantCoordinates.containsKey(restaurant)) {
                    Coordinate restCoords = YelpAPI.getRestaurantCoordinates(restaurant, homeCoords);
                    restaurantCoordinates.put(restaurant, restCoords);
                }

                Order myOrder = new Order(readyTime, restaurant, foodItem);
                orders.add(myOrder);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return orders;
    }



    public void start() throws Exception {
    	Scanner scanner = new Scanner(System.in);
    	System.out.println("What is the name of the schedule file?");
    	String fileName = scanner.nextLine();
    	while (!new File(fileName).exists()) {
			System.out.println();
			System.out.println("That file does not exists. What is the name of the schedule file? ");
			 fileName = scanner.nextLine();
		}
    	
    	System.out.println("What is your latitude?");
    	Double myLat = scanner.nextDouble();
    	System.out.println("What is your longitude?");
    	Double myLong = scanner.nextDouble();
    	
    	System.out.println("How many drivers will be in service today?");
    	int numDrivers = scanner.nextInt();
 
    	currDrivers = numDrivers;
    	Coordinate coords = new Coordinate(myLat, myLong);
    	homeCoords = coords;
    	

    	List<Order> myOrders = readOrders(fileName);
    	

        try {
            serverSocket = new ServerSocket(PORT);
            executorService = Executors.newFixedThreadPool(numDrivers);  
            System.out.println("Listening on port " + PORT);
            InetAddress inetAddress = InetAddress.getLocalHost();
            System.out.println("Waiting for drivers...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                ConnectionThread newConnect = new ConnectionThread(clientSocket, ClientId, this);
                ClientId += 1;
                newConnect.start();
                myConnections.add(newConnect);
                
                Message init = new Message("init", coords);
                newConnect.sendMessage(init);
                Message initMap = new Message("initMap", restaurantCoordinates);
                newConnect.sendMessage(initMap);
                
              //“how to print the socket connection ip ...(1 lines). ChatGPT, 30 Oct. version, OpenAI, 30 Oct. 2023, chat.openai.com/chat.
                System.out.println("Connection from " + clientSocket.getInetAddress().getHostAddress());

                
                
                if (myConnections.size() == numDrivers) {
                	System.out.println("Starting service.");
                	Message startMsg = new Message("start", "All drivers are connected. Service is now starting.");
                    for (ConnectionThread it : myConnections) {
                        it.sendMessage(startMsg);
                    }
                    
   
                    startTime = System.currentTimeMillis();
                	break;
                }
                else {
                	int remainingDrivers = numDrivers - myConnections.size();
                    Message waitMsg = new Message("waitMsg", remainingDrivers + " more driver is needed before the service can begin.\n" + "Waiting...");
                    for (ConnectionThread it : myConnections) {
                        it.sendMessage(waitMsg);
                    }
                	System.out.println("Waiting for " + String.valueOf(numDrivers - myConnections.size()) + " more driver(s)...");
                }
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }
        
        
      //init
    	availableDriversSemaphore = new Semaphore(numDrivers);
        availableDriversQueue = new LinkedBlockingQueue<>(myConnections);
        processOrders(myOrders);
        while (true) {
      	
        }
    }
    
    //funtion to handle order proessing
    private void processOrders(List<Order> orders) throws InterruptedException {
    	int totalDrivers = availableDriversSemaphore.availablePermits();
        latch = new CountDownLatch(totalDrivers);
    	
    	
        Map<Integer, List<Order>> ordersByReadyTime = orders.stream()
                .collect(Collectors.groupingBy(Order::getReadyTime));

        for (Map.Entry<Integer, List<Order>> entry : ordersByReadyTime.entrySet()) {
            int readyTime = entry.getKey();
            List<Order> readyOrders = entry.getValue();
            
            long currentTime = System.currentTimeMillis();
      
            
            long sleepTime = Math.max(startTime - currentTime, (readyTime * 1000) - (currentTime - startTime));
            if (sleepTime > 0) {
                try {
                    
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
       
            
            if (true) {
	            try {
	                availableDriversSemaphore.acquire();
	                ConnectionThread driver = availableDriversQueue.take();
	                
	                
	                sendOrdersToDriver(readyOrders, driver, latch);
	            } catch (InterruptedException e) {
	                Thread.currentThread().interrupt();
	            }
            }
        }
        
        
        //wait for the drivers to return
        while (currDrivers != numDrivers) {
        	Thread.sleep(1);
        }
        
        System.out.println();
      //“print out time since the start ...(3 lines). ChatGPT, 30 Oct. version, OpenAI, 30 Oct. 2023, chat.openai.com/chat.
        long timeSinceStart = System.currentTimeMillis() - startTime;
        Date elapsedTime = new Date(timeSinceStart - TimeZone.getDefault().getRawOffset());
        System.out.println("All orders completed!");
        
        Message doneMSG = new Message("done", elapsedTime);
        for (ConnectionThread i: myConnections) {
        	i.sendMessage(doneMSG);
        }
        shutdown();
        
    }
    
    
    public void finishedProcess(ConnectionThread driver) {
    	latch.countDown();
    }

    private void sendOrdersToDriver(List<Order> orders, ConnectionThread driver, CountDownLatch  latch) throws InterruptedException {
    	currDrivers -= 1;
        Message orderMessage = new Message("order", orders);
        
        driver.sendMessage(orderMessage);
        availableDriversQueue.put(driver);  // Put the driver back in the queue when done
        
    }
 
    public void releaseDriver(ConnectionThread driver ) {
    	currDrivers += 1;
    	availableDriversQueue.add(driver);
        availableDriversSemaphore.release();
        
    }
        
    //close everything
  //“write funtion to propperly close and end everything at end of runtime ...(10 lines). ChatGPT, 30 Oct. version, OpenAI, 30 Oct. 2023, chat.openai.com/chat.
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