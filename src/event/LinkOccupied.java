package event;

public class LinkOccupied extends Event{
	int link;
	double capacityOccupied;
	
	LinkOccupied(){
		this(-1,-1,0);
	}
	
	LinkOccupied(double time, int link, int capacityToOccupy){
		super("LinkOccupied");
		setTime(time);
		this.link = link;
		capacityOccupied = capacityToOccupy;
	}

	public int getLink() {
		return link;
	}

	public void setLink(int link) {
		this.link = link;
	}

	public double getCapacityOccupied() {
		return capacityOccupied;
	}

	public void setCapacityOccupied(double capacityOccupied) {
		this.capacityOccupied = capacityOccupied;
	}
	
}
