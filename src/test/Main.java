package test;

import generator.Generator;
import generator.MyArray;
import generator.MyMatrix;
import graph.Graph;
import graph.Vertex;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import algorithm.Broadcast;

public class Main {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		Vertex a, b, c;
		MyMatrix<Integer> m = new MyMatrix<Integer>(new Integer(0), 3, 3);
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 3; ++j) {
				m.set(i, j, i + j);
			}
		}
		System.out.println(m.toString());
		MyArray<Integer> arr = new MyArray<Integer>(new Integer(0), 5);
		for (int i = 0; i < 5; ++i) {
			arr.set(i, i);
		}
		System.out.println(arr.toString());
		Graph g = Generator
				.readGraphFromMatrixFile(
						new File(
								"/home/peppone/git/TopologyAwareCollective/src/test/dummygraph.txt"),
						new File(
								"/home/peppone/git/TopologyAwareCollective/src/test/demands.txt"));
		Generator.generateCplexDataFromMatrix(g,
				"/home/peppone/git/TopologyAwareCollective/src/test/data.dat");
		g.bfs(3);
		Set<Integer> set = new HashSet<Integer>();
		set.add(1);

		Set<Integer> receiver = new HashSet<Integer>();
		receiver.add(2);
		receiver.add(3);
		receiver.add(4);
		HashMap<Integer, Integer> fav = g.favouriteSenders(set, receiver);
		for (Integer i : receiver)
			System.out.println(i + " " + fav.get(i));

//		Broadcast br = new Broadcast(1, g);
//		br.addAvailableReceiver(2);
//		br.addAvailableReceiver(3);
//		br.addAvailableReceiver(4);
//		br.execute();
	}

}
