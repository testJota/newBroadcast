// Broadcast message Class
// Here we write the format of the protocol messages.

public class BdMessage{
	public final String[] data = {"",""};
		
	public BdMessage(String phase, String value) {
		// The format is: [PROTOCOL_PHASE,VALUE]
		this.data[0] = phase;
		this.data[1] = value;
	}
}