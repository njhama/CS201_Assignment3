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
        //String hostname = scanner.nextLine();
        String hostname = "localhost";
        System.out.print("Enter the server port: ");
        //int port = scanner.nextInt();
        int port = 3456;
        //scanner.nextLine();  // Consume the newline

        try {
            socket = new Socket(hostname, port);
            output = new ObjectOutputStream(socket.getOutputStream());
            output.flush();
            input = new ObjectInputStream(socket.getInputStream());
            System.out.println("Connected to server: " + socket.getRemoteSocketAddress());
            
            while (true) {
            	Message test = (Message) input.readObject();
                System.out.println(test.getPayload());
                
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
