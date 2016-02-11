package test;

import partitioner.DistanceBasedPartitioner;
import graph.Graph;

public class DistanceTester {
	public static void main(String args[]){
		String base = System.getProperty("user.dir")+"/src/test/";
		Graph line = new Graph(base + "line.txt");
		DistanceBasedPartitioner dbp = new DistanceBasedPartitioner(line);
		dbp.bipartite(0, 15);
		dbp.bipartite(0,4);
		dbp.bipartite(10,12);
		
		
	}
}
