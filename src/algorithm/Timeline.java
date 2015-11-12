package algorithm;

import java.util.ArrayList;

import event.Event;

public class Timeline {

	private ArrayList<Event> timeline;
	int actualEvent;

	public Timeline() {
		timeline = new ArrayList<Event>();
		actualEvent = 0;
	}
	
	public void addEvent(Event e) {
		if(timeline.size()==0){
			timeline.add(e);
			return;
		}
		double eventTime = e.getTime();
		double currentTime = (timeline.get(actualEvent).getTime());
		if(eventTime < currentTime){
			
			try {
				throw new Exception("Is not possible to time travel!");
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}else 
		if (eventTime == currentTime){
			timeline.add(1, e);
		}
		boolean added = false;
		for(int i=actualEvent;i<timeline.size();++i){
			double scheduledTime = timeline.get(i).getTime();
			if(eventTime < scheduledTime){
				timeline.add(i, e);
				added = true;
				break;
			}
		}
		if (!added){
			timeline.add(e);
		}
		
	}
	
	public Event pop(){
		if(actualEvent<timeline.size())
		return timeline.get(actualEvent++);
		else return null;
	}

}
