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
    private final CountDownLatch latch;

    public ConnectionThread(Socket socket, int ClientID, CountDownLatch latch) {
        this.socket = socket;
        this.ClientId = ClientID;
        this.latch = latch;
    }

    public void run() {
        try {
            //System.out.println("test");
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            latch.countDown();  // Signal that initialization is complete
        }

        while (true) {
            // ... your existing code ...
        }
    }

    public void sendMessage(Message message) {
        try {
            if (out != null) {
                out.writeObject(message);
                out.flush();  
                System.out.println("sent");
            } else {
                //jank
                System.out.println("not sent 1");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
