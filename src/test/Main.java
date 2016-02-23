package test;

import graph.Graph;

import java.io.IOException;

import algorithm.Core;

import commpattern.Broadcast;
import commpattern.Broadcast;

import demand.DemandList;

public class Main {

	/**
	 * @param args[0] - libraryPath
	 * @param args[1] - oplide Path
	 * @param args[2] - model Path
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws IOException,
			InterruptedException {
		System.getProperty("user.dir");
		String libPath = args[0];
		String oplidePath = args[1];
		String base = System.getProperty("user.dir")+"/src/test/";
	//	Graph dummy = new Graph(base + "dummygraph.txt");
		//Graph tree = new Graph(base + "tree.txt");
		Graph maciej = new Graph(base + "mlist.txt");
		Graph mesh = new Graph(base + "mesh.txt");
		Graph line = new Graph(base + "line.txt");
		//Graph custom = new Graph(base + "custom.txt");
	//	Graph dFly = new Graph(base + "dragonfly.txt");
		Graph torus2d = new Graph(base + "2dtorus.txt");
		
		Broadcast bMesh = new Broadcast(0,
				new int[] {1,2, 3, 4, 5, 6, 7, 8,9,10,11,12,13,14,15 },100, mesh);

		Broadcast mac = new Broadcast(50, new int[] {51, 52, 53, 54, 55 },
				100, maciej);
		//Broadcast bDummy = new Broadcast(3, new int[] {0, 1, 2 }, 100, dummy);

		//Broadcast bTorus2d = new Broadcast(0, new int[] {1, 2, 3, 4, 5, 6, 7, 8,
			//	9, 10, 11, 12, 13, 14, 15 },  100 ,
				//torus2d);

	/*	Broadcast bCustom = new Broadcast(1, new int[] { 2, 3, 4, 5, 6, 7, 8,
				9, 10, 11, 12, 13, 14, 15, 16, 17, 18 }, new int[] { 100, 100,
				100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100,
				100, 100, 100 }, custom);
	/*	Broadcast bTree = new Broadcast(1, new int[] { 2, 3, 4, 5, 6, 7, 8, 9,
				10, 11, 12, 13, 14, 15, 16 }, new int[] { 100, 100, 100, 100,
				100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100 },
				tree);
				*/
//		Broadcast bLine = new Broadcast(1, new int[] { 2, 3, 4, 5 }, new int[] {
//				100, 100, 100, 100 }, line);
		//debug
		
		Broadcast bLine = new Broadcast(0, new int[] { 1,2, 3, 4,5,6,7,8,9,10,11,12,13,14,15 }, 100, line);
	/*	Broadcast bDFly = new Broadcast(1, new int[] { 2, 3, 4, 5, 6, 7, 8, 9,
				10, 11, 12, 13, 14, 15, 16 }, new int[] { 100, 100, 100, 100,
				100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100 },
				dFly);
				*/
		String dataFile = base+"data.dat";// args[2];
		String resultFile = base+"resultpro.txt";
		String dotFile = base+"dotfile.dot";
		String model = args[2];
		// Core c = new Core(resultFile, dataFile, dotFile, oplidePath, model,libPath, dummy, bDummy);
		 Core c = new Core(resultFile, dataFile, dotFile, oplidePath, model,libPath, line, bLine);
		//Core c = new Core(resultFile, dataFile, dotFile, oplidePath, model,libPath, tree, bTree);
		// Core c = new Core(resultFile, dataFile, dotFile, oplidePath, model,libPath, mesh, bMesh);
		// Core c = new Core(resultFile, dataFile, dotFile, oplidePath, model,libPath, maciej, mac);
		// Core c = new Core(resultFile, dataFile, dotFile, oplidePath, model,libPath, custom, bCustom);
	// Core c = new Core(resultFile, dataFile, dotFile, oplidePath, model,libPath, torus2d, bTorus2d);
		 //Core c = new Core(resultFile, dataFile, dotFile, oplidePath, model,libPath, dFly, bDFly);
		long startTime = System.nanoTime();
		DemandList result = c.execute();
		long endTime = System.nanoTime();
		System.out.println((endTime - startTime) / 1E9 + " sec\n"
				+ result.printList());;
	}
}
