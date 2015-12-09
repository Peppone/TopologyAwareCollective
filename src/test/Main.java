package test;

import graph.Graph;

import java.io.IOException;
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
		Graph g = new Graph(base + "tree.txt");
		Graph mesh = new Graph(base + "mesh.txt");
		Graph line = new Graph(base + "line.txt");
		Broadcast bMesh = new Broadcast(1, new int[] { 2, 3, 4,5,6,7,8,9}, new
				 int[] { 100, 100, 100,100,100,100,100,100,100},mesh);
		// System.out.println(Converter.convert2readablegraph(base +
		// "mlist.txt"));
		//
		// }}
		// Graph g = new Graph(base + "dummygraph.txt");
		// System.out.println(Converter.convert(base + "SF.200.bsconf"));
		// for(int i:res){
		// System.out.print(i+" ");
		// }
		// System.out.println();
		// Broadcast mac = new Broadcast(51, new int[] { 52, 53, 54,55,56}, new
		// int[] { 100,
		// 100, 100,100,100},g);
//		Broadcast b4 = new Broadcast(4, new int[] { 1, 2, 3 }, new int[] { 100,
//				100, 100 }, g);
		// Broadcast b = new Broadcast(1, new int[] { 2, 3, 4,5,6,7,8,9}, new
		// int[] { 100,
		// 100, 100,100,100,100,100,100,100},g);
	//	Broadcast b = new Broadcast(3, new int[] { 1, 2, 4 }, new int[] { 100,
	//			100, 100 }, g);
	Broadcast tree = new Broadcast(1, new int[] { 8, 2, 3, 4, 5, 6, 7 },
				new int[] { 100, 100, 100, 100, 100, 100, 100 }, g);
	Broadcast bLine = new Broadcast(1,new int[] { 2,3,4,5 },
				new int[] { 100, 100, 100, 100}, line);
		// Broadcast b2 = new Broadcast(2, new int[] {3,4}, new int[]
		// {90,100},g);
		// Broadcast b1 = new Broadcast(1, new int[] {2,8}, new int[]
		// {100,100},g);
		// b.getMinBitrate(1);
		String dataFile = "/home/peppone/opl/multidemandallocation/data.dat";// args[2];
		String resultFile = "/home/peppone/opl/multidemandallocation/resultpro.txt";
		String dotFile = "/home/peppone/opl/multidemandallocation/dotfile.dot";
		Core c = new Core(g.getEdgeNumber(), resultFile, dataFile, dotFile, line,bLine);
		long startTime = System.nanoTime();
		DemandList result = c.execute(g);
		long endTime = System.nanoTime();
		System.out.println((endTime - startTime) / 1E9 + " sec\n"
				+ result.printList());
		// System.out.println(result.writeBroadcastGoalFile());
	}
}
