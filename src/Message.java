import java.io.Serializable;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    private String type;
    private Object payload;

    public Message(String type, Object payload) {
        this.type = type;
        this.payload = payload;
    }

    public String getType() {
        return type;
    }

    public Object getPayload() {
        return payload;
    }
}
