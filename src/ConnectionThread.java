import java.net.Socket;
import java.io.*;
import java.net.*;

public class ConnectionThread extends Thread{

	private Socket socket;
	private int ClientId;
	ObjectInputStream in;
	ObjectOutputStream out;

	public ConnectionThread(Socket socket, int ClientID) {
		this.socket = socket;
		this.ClientId = ClientID;
	}
	
	
	public void run() {
		try {
			System.out.println("test");
			out = new ObjectOutputStream(socket.getOutputStream());
			out.flush();
			in = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
		
	}
	
	 public void sendMessage(Message message) {
	        try {
	            if (out != null) {
	                out.writeObject(message);
	                out.flush();  
	                System.out.println("sent");
	            } 
	            else {
	            	//jank
	            	out = new ObjectOutputStream(socket.getOutputStream());
	    			out.flush();
	    			out.writeObject(message);
	                out.flush();  
	                System.out.println("sent");
	            }
	            
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
}
