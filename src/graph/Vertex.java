package graph;

import java.util.HashMap;

public class Vertex {
	private int id;
	private HashMap<Integer, Double> receivers;

	public Vertex(int id) {
		this.id = id;
		receivers= new HashMap<Integer, Double> ();
	}

	public int getId() {
		return id;
	}
	public void addReceiver(Vertex v, double demand) {
		addReceiver(v.getId(), demand);
	}

	public void addReceiver(int v, double demand) {
		if (!receivers.containsKey(v)) {
			receivers.put(v, demand);
		}
	}
	
	public double getDemand(Integer destination){
		if(receivers.containsKey(destination)){
			return receivers.get(destination);
		}else return 0;
	}
}
