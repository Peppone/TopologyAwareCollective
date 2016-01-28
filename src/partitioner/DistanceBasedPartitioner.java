package partitioner;

import graph.Graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

public class DistanceBasedPartitioner {

	Graph graph;
	HashMap<Integer,int[]> bfsVisits;

	// ArrayList <Partition> currentPartition;
	public DistanceBasedPartitioner(Graph g) {
		graph = g;
		// currentPartition = new ArrayList<Partition>();
		// Partition graph = new Partition(this.graph);
		// currentPartition.add(graph);
		 bfsVisits = new HashMap<Integer,int[]>(g.getVertexNumber());

	}

	/**
	 * Create two new partitions based on the hop distance choosing vertex as
	 * pivot
	 * 
	 * @param p
	 * @param vertex
	 * @return the two partitions
	 */
	public Partition[] partition(Partition p, int vertex) {
		Partition[] result;
		if (p.size() == 1) {
			result = new Partition[1];
			result[0] = p;
			return result;
		}
		//DEBUG
		System.err.print(vertex+" ");
		//
		int[] initialVisit;
		if(!bfsVisits.containsValue(vertex)){
			initialVisit = graph.bfs(vertex - 1);
			bfsVisits.put(vertex, initialVisit);
		}else{
			initialVisit = bfsVisits.get(vertex);
		}
		int oppositeNode = furthestNode(initialVisit, p);
		//DEBUG
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
			if (p.contains(currentElement+1)) {
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

}
