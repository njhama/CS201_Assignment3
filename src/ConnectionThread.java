import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;

public class ConnectionThread extends Thread{

    private Socket socket;
    private int ClientId;
    ObjectInputStream in;
    ObjectOutputStream out;
    private final CountDownLatch latch = new CountDownLatch(1);
    private Server server;

    public ConnectionThread(Socket socket, int ClientID, Server server) {
        this.socket = socket;
        this.ClientId = ClientID;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            //System.out.println("test");
        	out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());
            latch.countDown();
            while (true) {
                Message receivedMessage = (Message) in.readObject();
   
                if ("freed".equals(receivedMessage.getType())) {
                	server.releaseDriver(this);
                }
                
                if ("done".equals(receivedMessage.getType())) {
                	server.finishedProcess(this);
                }
     
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {

        }
    }

    public void sendMessage(Message message) throws InterruptedException {
        try {
        	latch.await();
            if (out != null) {
                out.writeObject(message);
                out.flush();  
            } 
            else {
                System.out.println("Something went extraordinarily wrong from client " + ClientId );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
