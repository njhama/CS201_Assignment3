import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    
    private Socket socket;
    private ObjectInputStream input;
    private ObjectOutputStream output;

    public static void main(String[] args) throws ClassNotFoundException {
        Client client = new Client();
        client.run();
    }

    public void run() throws ClassNotFoundException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to the program!");
        System.out.print("Enter the server hostname: ");
        String hostname = scanner.nextLine();
        System.out.print("Enter the server port: ");
        int port = scanner.nextInt();
        //scanner.nextLine();  // Consume the newline

        try {
        	System.out.println("jere");
            socket = new Socket(hostname, port);
            System.out.println("jere1");
            output = new ObjectOutputStream(socket.getOutputStream());
            System.out.println("jere2");
            output.flush();
            System.out.println("jere3");
            input = new ObjectInputStream(socket.getInputStream());
            System.out.println("jere4");
            //get from teh server
            System.out.println("Connected to server: " + socket.getRemoteSocketAddress());
            
            while (true) {
            	Message test = (Message) input.readObject();
                System.out.println(test.getType());
                
            }
            
            

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to connect to server. :(");
        } finally {
            //closeResources();
        }
        
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
