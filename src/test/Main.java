package test;

import graph.Graph;

import java.io.IOException;
import java.util.HashMap;

import algorithm.Core;

import commpattern.Broadcast;

import demand.DemandList;

public class Main {

	/**
	 * @param args
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws IOException,
			InterruptedException {

		String base = "/home/peppone/git/TopologyAwareCollective/src/test/";
		Graph g = new Graph(base + "graph.txt");
		int res []=g.bfs(2);
//		for(int i:res){
//			System.out.print(i+" ");
//		}
//		System.out.println();
		HashMap<Integer,Integer[]>map=g.modifiedVisit(3);;
		Broadcast b = new Broadcast(1, new int[] { 2, 3, 4,5,6,7,8,9}, new int[] { 10,
				10, 10,10,10,10,10,10 },g);
		//Broadcast b = new Broadcast(1, new int[] {2,3,4}, new int[] { 10,10,10},g);
	//	b.getMinBitrate(1);
		String dataFile = "/home/peppone/opl/multidemandallocation/data.dat";// args[2];
		String resultFile = "/home/peppone/opl/multidemandallocation/resultpro.txt";
		Core c = new Core(g.getEdgeNumber(),
				resultFile, dataFile,
				b);
		long startTime = System.nanoTime();
		DemandList result=c.execute(g);
		long endTime = System.nanoTime();
		System.out.println((endTime-startTime)/1E9+" sec\n"+result.printList());
		
	}
}
