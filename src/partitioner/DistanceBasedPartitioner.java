package partitioner;

import graph.Graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

public class DistanceBasedPartitioner {

	Graph graph;
	HashMap<Integer, int[]> bfsVisits;
	PartitionTree tree;

	// ArrayList <Partition> currentPartition;
	public DistanceBasedPartitioner(Graph g) {
		graph = g;
		// currentPartition = new ArrayList<Partition>();
		// Partition graph = new Partition(this.graph);
		// currentPartition.add(graph);
		bfsVisits = new HashMap<Integer, int[]>(g.getVertexNumber());
		tree = new PartitionTree(g);

	}

	/**
	 * returns an Hashmap of tuple <Integer,Integer> key is the possible
	 * destination value is the distance
	 * 
	 * @param sender
	 * @param probableDestinations
	 * @return
	 */
	public HashMap<Integer, Integer> generateDemands(Integer sender,
			Collection<Integer> probableDestinations) {
		int[] visit;
		
		if (bfsVisits.containsKey(sender)) {
			visit = bfsVisits.get(sender);
		} else {
			visit = graph.bfs(sender);
			bfsVisits.put(sender, visit);
		}
		ArrayList<Integer> destinations = tree.findPossibleDestinations(sender,
				probableDestinations);
		HashMap<Integer, Integer> map = new HashMap<Integer, Integer>(
				destinations.size());
		//IMPROVEMENT
		int maxDistance = -1;
		ArrayList<Integer> candidate = new ArrayList<Integer>();
		for (Integer i : destinations) {
			if(visit[i]>maxDistance){
				candidate.clear();
				candidate.add(i);
				maxDistance = visit[i];
			}
			if(visit[i]==maxDistance){
				candidate.add(i);
			}
			
		}
		for(Integer i:candidate){
		map.put(i,maxDistance);
		}
		return map;

	}

	/**
	 * This method bipartite the graph considering two vertices and
	 * creating a bipartition 
	 * @param sender
	 * @param destination
	 */
	public void bipartite(Integer sender, Integer destination) {
		Partition[] vect = new Partition[2];
		vect[0] = tree.getPartition(sender);
		if (!vect[0].contains(destination)) {
			// DEBUG
			System.err
					.println("Sender and destination belong to different partitions");
			//
			return;
		}
		int[] senderVisit;
		int[] destinationVisit;
		if (bfsVisits.containsKey(sender)) {
			senderVisit = bfsVisits.get(sender);
		} else {
			senderVisit = graph.bfs(sender);
			bfsVisits.put(sender, senderVisit);
		}
		if (bfsVisits.containsKey(destination)) {
			destinationVisit = bfsVisits.get(destination);
		} else {
			destinationVisit = graph.bfs(destination);
			bfsVisits.put(destination, destinationVisit);
		}
		vect = vect[0].split(senderVisit, destinationVisit);
		PartitionLeaf leaf =tree.getPartitionLeaf(sender);
		leaf.updateTree(vect);

	}

	/**
	 * Create two new partitions based on the hop distance choosing one vertex as
	 * pivot
	 * 
	 * @param p
	 * @param vertex
	 * @return the two partitions
	 */

	public Partition[] partite(Partition p, int vertex) {
		Partition[] result;
		if (p.size() == 1) {
			result = new Partition[1];
			result[0] = p;
			return result;
		}
		// DEBUG
		System.err.print(vertex + " ");
		//
		int[] initialVisit;
		if (!bfsVisits.containsValue(vertex)) {
			initialVisit = graph.bfs(vertex - 1);
			bfsVisits.put(vertex, initialVisit);
		} else {
			initialVisit = bfsVisits.get(vertex);
		}
		int oppositeNode = furthestNode(initialVisit, p);
		// DEBUG
		System.err.println(oppositeNode);
		//
		int[] pivotVisit = graph.bfs(oppositeNode);
		result = p.split(initialVisit, pivotVisit);

		/*
		 * DEBUG
		 */
		System.err.println("Partition 1:");
		Iterator<Integer> it = result[0].getIterator();
		while (it.hasNext()) {
			System.err.print(it.next() + ", ");
		}
		System.err.println("\nPartition 2:");
		it = result[1].getIterator();
		while (it.hasNext()) {
			System.err.print(it.next() + ", ");
		}
		System.err.println();
		/*
		 * DEBUG
		 */

		return result;

	}

	private int furthestNode(int[] visit, Partition p) {
		ArrayList<Integer> position = new ArrayList<Integer>();
		int currentMax = -1;
		for (int i = 0; i < visit.length; ++i) {
			int currentElement = visit[i];
			if (p.contains(currentElement + 1)) {
				if (currentMax < currentElement) {
					position.clear();
					position.add(i);
					currentMax = currentElement;
				} else if (currentMax == currentElement) {
					position.add(i);
				}
			}
		}
		Random r = new Random();
		int pivot = r.nextInt(position.size());
		return position.get(pivot);
	}

	public void printPartitions(){
		tree.printPartitions();
	}

	public Partition getPartition(Integer vertex){
		return tree.getPartition(vertex);
	}
}
