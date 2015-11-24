package test;

import graph.Graph;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import utility.ExecuteShellCommand;
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
		System.out.println(g.writeCplexCode());
		 Broadcast b = new Broadcast(1, new int[] { 2, 3, 4}, new int[] { 10,
		 10,10 });
		 b.getMinBitrate(1);
		 Core c = new Core(10, base + "demands.txt",
		 "/home/peppone/opl/multidemandallocation/result.txt",
		 "/home/peppone/opl/multidemandallocation/newdata.txt",
		 b);
		 c.execute(g);
		// ProcessBuilder builder = new ProcessBuilder("/usr/local/bin/oplrun");
		//
		//
		// builder.redirectErrorStream(true);
		// Process process = builder.start();
		// process.waitFor();
		// builder.command("/usr/local/bin/oplrun -h");
//		String command = "/usr/local/bin/oplrun";
//		String model = "/home/peppone/opl/multidemandallocation/output.o";//args[1];
//		String dataFile ="/home/peppone/opl/multidemandallocation/data.dat";// args[2];
//		String resultFile ="/home/peppone/opl/multidemandallocation/result.txt";
//		ExecuteShellCommand esc =new ExecuteShellCommand();
//		Process p=esc.executeCommand(command, "-v", model, dataFile,resultFile);
//		InputStream stdout = p.getInputStream();
//		InputStream stderr = p.getErrorStream();
//		BufferedReader reader = new BufferedReader(
//				new InputStreamReader(stdout));
//		;
//		String line;
//		while ((line = reader.readLine()) != null) {
//			System.out.println(line);
//		}

	}
}
