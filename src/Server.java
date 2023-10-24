import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private static final int PORT = 3456;
    private ServerSocket serverSocket;
    private ExecutorService executorService;

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }

    
    
    //need to have a ds to keep track of the threads for each connection
    //iterate thru the restaurants and make a new ConnectionThread for each one
    //keep track of how many clients have join and send things to those clinets 
    
    
    
    public void start() {
    	Scanner scanner = new Scanner(System.in);
    	System.out.println("What is the name of the schedule file?");
    	String fileName = scanner.nextLine();
    	
    	//lat and long
    	System.out.println("What is your latitude?");
    	Double myLat = scanner.nextDouble();
    	System.out.println("What is your longitude?");
    	Double myLong = scanner.nextDouble();
    	
    	//drivers
    	System.out.println("How many drivers will be in service today?");
    	int numDrivers = scanner.nextInt();
    	
    	
        try {
        	
        	
        	
            serverSocket = new ServerSocket(PORT);
            executorService = Executors.newFixedThreadPool(10);  // Adjust the thread pool size as needed
            System.out.println("Server listening on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                Client clientHandler = new Client();
                executorService.execute((Runnable) clientHandler);  // Handle client connection in a separate thread
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            shutdown();
        }
    }

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