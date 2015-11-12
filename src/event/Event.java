package event;

public abstract class Event {

	private double time;
	private final String  type;
	
	Event(String type){
		this.type=type;
	}
	

	
//	public Event(double time, int source, int destination, String type){
//		this.time = time;
//		this.source = source;
//		this.destination = destination;
//		this.type = type;
//	}
	


	public String getType() {
		return type;
	}


	public double getTime() {
		return time;
	}

	public void setTime(double time) {
		this.time = time;
	}
	
	
	

}
