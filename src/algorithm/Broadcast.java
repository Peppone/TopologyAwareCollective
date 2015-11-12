package algorithm;

import event.Event;
import event.Send;
import graph.Graph;

import java.util.HashMap;

public class Broadcast extends Collective {
	Graph graph;
	private HashMap<Integer, Boolean> receivers;
	private HashMap<Integer, Boolean> sender;
	private int root;

	public Broadcast(int root, Graph g) {
		this.sender = new HashMap<Integer, Boolean>();
		this.sender.put(root, true);
		this.receivers = new HashMap<Integer, Boolean>();
		graph = g;
		this.root = root;
	}

	public void addAvailableReceiver(int r) {
		receivers.put(r, true);
	}

	@Override
	public Timeline execute() {
		Timeline tl = new Timeline();

		HashMap<Integer, Integer> bfs = graph.bfs(root);
		Integer maxDistance = 0;
		int actualReceiver = root;
		for (Integer i : receivers.keySet()) {
			int distance = bfs.get(i);
			if (distance > maxDistance) {
				maxDistance = distance;
				actualReceiver = i;
			}
		}

		if (actualReceiver == root)
			return tl;
		tl.addEvent(new Send(0, root, actualReceiver));

		Event e;
		while ((e = tl.pop()) != null) {
			String eventType = e.getType();
			if (eventType.equalsIgnoreCase("send")) {
				//compute path with cplex
				//setup the path
				//add link expire time
				//ad receive events to the list.
				//Otherwise
				//put myself in sleep mode.
			}

			if (eventType.equalsIgnoreCase("receive")) {
				//if some preferred receivers are available,
					//send to the furthest available.
				//else if no preferred receivers are available
					//send to the furthest available
			}
			
			if (eventType.equalsIgnoreCase("linkReleased")) {
				//release all the capacity of the link, update adj_matrix
				//foreach element present in sleep:
					//wake up and schedule send
			}

		}

		return tl;

	}

}
