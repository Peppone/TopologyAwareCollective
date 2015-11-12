package generator;

import graph.Edge;
import graph.Graph;
import graph.Vertex;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

public class Generator {

	private static File init(String namefile, boolean overwrite) {

		File data = new File(namefile);
		if (data.exists())
			if (!overwrite)
				try {
					throw new Exception("File already present");
				} catch (Exception e) {
					e.printStackTrace();
				}
			else {
				data.delete();
			}
		return new File(namefile);
	}

	public static void generateCplexDataFromEdge(Graph g, String filename)
			throws IOException {
		File f = init(filename, true);
		g.close();
		ArrayList<Edge> edges = g.getEdges();
		int link = edges.size();
		int vertex = g.getVertexNumber();
		MyMatrix<Integer> avertex = new MyMatrix<Integer>(new Integer(0), link,
				vertex);
		MyMatrix<Integer> bvertex = new MyMatrix<Integer>(new Integer(0), link,
				vertex);
		MyArray<Double> edge = new MyArray<Double>(new Double(0), link);
		int edgeCounter = 0;
		double[][] matrix = g.getAdjacencyMatrix();
		for (int i = 0; i < g.getVertexNumber(); ++i) {
			for (int j = i + 1; j < g.getVertexNumber(); ++j) {
				if (matrix[i][j] > 0) {
					avertex.set(edgeCounter, i, 1);
					bvertex.set(edgeCounter, j, 1);
					edge.set(edgeCounter, matrix[i][j]);
					edgeCounter++;
				}
				if (matrix[j][i] > 0) {
					avertex.set(edgeCounter, j, 1);
					bvertex.set(edgeCounter, i, 1);
					edge.set(edgeCounter, matrix[j][i]);
					edgeCounter++;
				}

			}
		}
		// for(Edge e : edges){
		// int source= e.getSource().getId();
		// int dest = e.getDestination().getId();
		// avertex.set(edgeCounter,source-1,1);
		// bvertex.set(edgeCounter,dest-1,1);
		// edge.set(edgeCounter, e.getFreeCapacity());
		// edgeCounter ++;
		// }
		String data = "";
		data += "node=" + vertex + ";\n" + "link=" + link + ";\n";
		data += "edge=" + edge.toString() + ";\n";
		data += "avertex=" + avertex.toString() + ";\n";
		data += "bvertex=" + bvertex.toString() + ";\n";
		FileWriter fw = new FileWriter(f);
		fw.write(data);
		fw.close();

	}

	public static void generateCplexDataFromMatrix(Graph g, String filename)
			throws IOException {
		File f = init(filename, true);
		// g.close();
		int link = g.getLink();
		int vertex = g.getVertexNumber();
		MyMatrix<Integer> avertex = new MyMatrix<Integer>(new Integer(0), link,
				vertex);
		MyMatrix<Integer> bvertex = new MyMatrix<Integer>(new Integer(0), link,
				vertex);
		MyArray<Double> edge = new MyArray<Double>(new Double(0), link);
		MyMatrix<Double> demand = new MyMatrix<Double>(new Double(0),
				g.getDemand(),3);
		int edgeCounter = 0;
		int demandCounter = 0;
		double[][] matrix = g.getAdjacencyMatrix();
		for (int i = 0; i < g.getVertexNumber(); ++i) {
			Vertex src = g.getVertices().get(i + 1);
			for (int j = i + 1; j < g.getVertexNumber(); ++j) {
				Vertex dst = g.getVertices().get(j + 1);

				if (matrix[i][j] > 0) {
					avertex.set(edgeCounter, i, 1);
					bvertex.set(edgeCounter, j, 1);
					edge.set(edgeCounter, matrix[i][j]);
					edgeCounter++;
				}
				if (matrix[j][i] > 0) {
					avertex.set(edgeCounter, j, 1);
					bvertex.set(edgeCounter, i, 1);
					edge.set(edgeCounter, matrix[j][i]);
					edgeCounter++;
				}

				if (src != null && dst != null) {
					double dmd = src.getDemand(j+1);
					if (dmd > 0) {
						demand.set(demandCounter, 1,i+1.0);
						demand.set(demandCounter, 2,j+1.0);
						demand.set(demandCounter, 0,dmd);
						demandCounter++;
					}
					dmd = dst.getDemand(i+1);
					if (dmd > 0) {
						demand.set(demandCounter, 1,j+1.0);
						demand.set(demandCounter, 2,i+1.0);
						demand.set(demandCounter, 0,dmd);
						demandCounter++;
					}
				}

			}
		}
		String data = "";
		data += "node=" + vertex + ";\n" + "link=" + link + ";\n";
		data += "d="+demandCounter+";\n";
		data += "edge=" + edge.toString() + ";\n";
		data += "avertex=" + avertex.toString() + ";\n";
		data += "bvertex=" + bvertex.toString() + ";\n";
		data += "demand=" +demand.toString()+";\n";
		FileWriter fw = new FileWriter(f);
		fw.write(data);
		fw.close();

	}

	public static Graph readGraphFromEdgeFile(File f) throws IOException {
		Graph g = new Graph();
		BufferedReader br = new BufferedReader(new FileReader(f));
		String line;
		while ((line = br.readLine()) != null) {
			StringTokenizer str = new StringTokenizer(line);
			Integer source = Integer.parseInt(str.nextToken());
			Integer destination = Integer.parseInt(str.nextToken());
			Double freeCapacity = Double.parseDouble(str.nextToken());

			Vertex src = new Vertex(source);
			Vertex dest = new Vertex(destination);
			Edge e = new Edge(freeCapacity, src, dest);
			g.addEdge(e);
		}
		br.close();
		return g;
	}

	public static Graph readGraphFromMatrixFile(File f, File d)
			throws IOException {
		Graph g = new Graph();
		BufferedReader br = new BufferedReader(new FileReader(f));
		String line = br.readLine();
		StringTokenizer str = new StringTokenizer(line);
		int vertex = str.countTokens();
		int counter = 0;
		int edgecounter = 0;
		double matrix[][] = new double[vertex][];
		for (int i = 0; i < vertex; ++i) {
			matrix[i] = new double[vertex];
		}
		while (line != null) {
			str = new StringTokenizer(line);
			for (int i = 0; i < vertex; ++i) {
				matrix[counter][i] = Double.parseDouble(str.nextToken());
				if (matrix[counter][i] > 0)
					edgecounter++;
			}
			counter++;
			line = br.readLine();
		}
		g.setAdjacencyMatrix(matrix);
		g.setLink(edgecounter);
		g.setVertex(matrix.length);
		importDemands(d, g);
		br.close();
		return g;
	}

	
	public static void importDemands(File d, Graph g)
			throws NumberFormatException, IOException {
		BufferedReader br = new BufferedReader(new FileReader(d));
		String line;
		HashMap<Integer, Vertex> v = new HashMap<Integer, Vertex>();
		int counter = 0;
		while ((line = br.readLine()) != null) {
			StringTokenizer str = new StringTokenizer(line);
			Integer source = Integer.parseInt(str.nextToken());
			Integer destination = Integer.parseInt(str.nextToken());
			Double demand = Double.parseDouble(str.nextToken());
			if (!v.containsKey(source)) {
				Vertex src = new Vertex(source);
				v.put(source, src);
			}
			if (!v.containsKey(destination)) {
				Vertex dest = new Vertex(destination);
				v.put(destination, dest);
			}
			Vertex src = v.get(source);
			src.addReceiver(destination, demand);
			counter++;
		}
		br.close();
		g.setVertices(v);
		g.setDemand(counter);

	}
	
}
