package test;

import graph.Graph;

import java.io.IOException;
import java.util.HashMap;

import algorithm.Core;

import commpattern.Broadcast;

public class Main {

	/**
	 * @param args
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws IOException,
			InterruptedException {

		String base = "/home/peppone/git/TopologyAwareCollective/src/test/";
		Graph g = new Graph(base + "dummygraph.txt", base + "demands.txt");
		int res []=g.bfs(2);
//		for(int i:res){
//			System.out.print(i+" ");
//		}
//		System.out.println();
		HashMap<Integer,Integer[]>map=g.modifiedVisit(3);
//		for(Integer i:map.keySet()){
//			System.out.println(i+" "+map.get(i)[0]+" "+map.get(i)[1]+" "+map.get(i)[2]);
//		}
		int prova =0;
////		System.out.println(g.writeCplexCode());
		Broadcast b = new Broadcast(1, new int[] { 2, 3, 4 }, new int[] { 10,
				10, 10 },g);
	//	b.getMinBitrate(1);
		String dataFile = "/home/peppone/opl/multidemandallocation/data.dat";// args[2];
		String resultFile = "/home/peppone/opl/multidemandallocation/resultpro.txt";
		Core c = new Core(12,
				resultFile, dataFile,
				b);
		c.execute(g);

	}
}
