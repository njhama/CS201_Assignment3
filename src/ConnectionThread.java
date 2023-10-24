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
			in = new ObjectInputStream(socket.getInputStream());
			out = new ObjectOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
		
	}
	
	 public void sendMessage(String message) {
	        try {
	            if (out != null) {
	                out.writeObject(message);
	                out.flush();  
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
}
