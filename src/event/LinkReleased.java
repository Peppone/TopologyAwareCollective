package event;

public class LinkReleased extends Event {
	int link;
	double capacityReleased;
	
	LinkReleased(){
		this(-1,-1,0);
	}
	
	LinkReleased(double time, int link, int capacityToRelease){
		super("LinkReleased");
		setTime(time);
		this.link = link;
		capacityReleased = capacityToRelease;
	}

	public int getLink() {
		return link;
	}

	public void setLink(int link) {
		this.link = link;
	}

}
