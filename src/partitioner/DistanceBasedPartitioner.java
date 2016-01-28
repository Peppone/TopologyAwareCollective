package partitioner;

import graph.Graph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;


public class DistanceBasedPartitioner {
	
	Graph graph;
	ArrayList <Partition> currentPartition;
	public DistanceBasedPartitioner(Graph g){
		graph = g;
		currentPartition = new ArrayList<Partition>();
		Partition graph = new Partition(this.graph);
		currentPartition.add(graph);
		
	}
	public Partition[] partition(Partition p, int vertex){
		int[] initialVisit = graph.bfs(vertex-1);
		int oppositeNode = furthestNode(initialVisit);
		int[] pivotVisit = graph.bfs(oppositeNode);
		Partition [] result = p.split(initialVisit,pivotVisit);
		
		/*
		 * DEBUG
		 */
		System.err.println("Partition 1:");
		Iterator<Integer> it = result[0].getIterator();
		while(it.hasNext()){
			System.err.print(it.next()+", ");
		}
		System.err.println("\nPartition 2:");
		it = result[1].getIterator();
		while(it.hasNext()){
			System.err.print(it.next()+", ");
		}
		System.err.println();
		/*
		 * DEBUG 
		 */
		
		return result;
		
	}
	private int furthestNode (int [] visit){
		ArrayList <Integer> position=new ArrayList<Integer>();
		int currentMax = -1;
		for(int i=0;i<visit.length;++i){
			int currentElement = visit[i];
			if(currentMax<currentElement){
				position.clear();
				position.add(i);
				currentMax = currentElement;
			}else if(currentMax == currentElement){
				position.add(i);
			}
		}
		Random r = new Random();
		int pivot = r.nextInt(position.size());
		return position.get(pivot);
	}
		
	
}
