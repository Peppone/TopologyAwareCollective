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
		Graph dummy = new Graph(base + "dummygraph.txt");
		Graph tree = new Graph(base + "tree.txt");
		Graph maciej = new Graph(base + "mlist.txt");
		Graph mesh = new Graph(base + "mesh.txt");
		Graph line = new Graph(base + "line.txt");
		Graph custom = new Graph(base + "custom.txt");
		Graph mesh2d = new Graph(base + "2dtorus.txt");
		Broadcast bMesh = new Broadcast(1,
				new int[] { 2, 3, 4, 5, 6, 7, 8, 9 }, new int[] { 100, 100,
						100, 100, 100, 100, 100, 100, 100 }, mesh);

		Broadcast mac = new Broadcast(51, new int[] { 52, 53, 54, 55, 56 },
				new int[] { 100, 100, 100, 100, 100 }, maciej);
		Broadcast bDummy = new Broadcast(4, new int[] { 1, 2, 3 }, new int[] {
				100, 100, 100 }, dummy);
	Broadcast bCustom = new Broadcast(1,
				new int[] { 2, 3, 4, 5, 6, 7, 8, 9,10,11,12,13,14,15,16,17,18 }, new int[] { 100, 100,
				100, 100, 100, 100, 100, 100, 100, 100,100,100,100,100,100,100,100 }, custom);
	Broadcast bMesh2d =  new Broadcast(1,
			new int[] {2, 3, 4, 5, 6, 7, 8, 9,10,11,12,13,14,15,16}, new int[] { 100, 100,
					100, 100, 100, 100, 100, 100, 100, 100,100,100,100,100,100},mesh);
	/*
		Broadcast bCustom = new Broadcast(1,
				new int[] { 2, 3, 4, 5, 6, 7, 8, 9,10,11,12,13,14}, new int[] { 100, 100,
				100, 100, 100, 100, 100, 100, 100, 100,100,100,100}, custom);
				*/
		// Broadcast b = new Broadcast(1, new int[] { 2, 3, 4,5,6,7,8,9}, new
		// int[] { 100,
		// 100, 100,100,100,100,100,100,100},g);
		// Broadcast b = new Broadcast(3, new int[] { 1, 2, 4 }, new int[] {
		// 100,
		// 100, 100 }, g);
		Broadcast bTree = new Broadcast(1, new int[] {2, 3, 4, 5, 6, 7,8,9,10,11,12,13,14,15,16},
				new int[] { 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100,100,100 }, tree);
		Broadcast bLine = new Broadcast(1, new int[] { 2, 3, 4, 5 }, new int[] {
				100, 100, 100, 100 }, line);
		// Broadcast b2 = new Broadcast(2, new int[] {3,4}, new int[]
		// {90,100},g);
		// Broadcast b1 = new Broadcast(1, new int[] {2,8}, new int[]
		// {100,100},g);
		// b.getMinBitrate(1);
		String dataFile = "/home/peppone/opl/multidemandallocation/data.dat";// args[2];
		String resultFile = "/home/peppone/opl/multidemandallocation/resultpro.txt";
		String dotFile = "/home/peppone/opl/multidemandallocation/dotfile.dot";

		//Core c = new Core(resultFile, dataFile, dotFile, dummy, bDummy);
		//Core c = new Core(resultFile, dataFile, dotFile, line, bLine);
		//Core c = new Core(resultFile, dataFile, dotFile, tree, bTree);
		Core c = new Core(resultFile, dataFile, dotFile, mesh, bMesh2d);
		//Core c = new Core(resultFile, dataFile, dotFile, maciej, mac);
		//Core c = new Core(resultFile, dataFile, dotFile, custom, bCustom);
	//	Core c = new Core(resultFile, dataFile, dotFile, mesh2d, bMesh2d);
		long startTime = System.nanoTime();
		DemandList result = c.execute();
		long endTime = System.nanoTime();
		System.out.println((endTime - startTime) / 1E9 + " sec\n"
				+ result.printList());

		// System.out.println(result.writeBroadcastGoalFile());
	}
}
