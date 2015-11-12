package event;

public class Send extends Event {
	private int source;
	private int destination;
	
	public Send(){
		this(-1,-1,-1);
	}
	
	public Send(double time, int src, int dest){
		super("Send");
		setTime(time);
		source = src;
		destination = dest;
	}

	public int getSource() {
		return source;
	}

	public void setSource(int source) {
		this.source = source;
	}

	public int getDestination() {
		return destination;
	}

	public void setDestination(int destination) {
		this.destination = destination;
	}

}
