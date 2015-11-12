package event;

public class Receive extends Event{
	private int source;
	private int destination;
	
	public Receive(){
		this(-1,-1,-1);
	}
	
	public Receive(double time, int src, int dest){
		super("Receive");
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
