
public class Message {
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
