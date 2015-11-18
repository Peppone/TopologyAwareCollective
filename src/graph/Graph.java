package graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

public class Graph {
	private ArrayList<Edge> edges;
	private HashMap<Integer, Vertex> vertices;
	private double adjacencyMatrix[][];
	//private ArrayList<Integer>[] adjacencyList;
	private boolean finalized;
	private int link;
	private int vertex;
	private int demand;

	public Graph() {
		edges = new ArrayList<Edge>();
		vertices = new HashMap<Integer, Vertex>();
		adjacencyMatrix = null;
	//	adjacencyList = null;
		finalized = false;
		link = 0;
		demand = 0;
		vertex = 0;

	}

	public ArrayList<Edge> getEdges() {
		return edges;
	}

	public void setEdges(ArrayList<Edge> edges) {
		this.edges = edges;
		finalized = false;
	}

	public void addEdge(Edge e) {
		finalized = false;
		edges.add(e);
		Vertex source = e.getSource();
		Vertex destination = e.getDestination();
		if (!vertices.containsKey(source.getId())) {
			vertices.put(source.getId(), new Vertex(source.getId()));
			++vertex;
		}
		if (!vertices.containsKey(destination.getId())) {
			vertices.put(destination.getId(), new Vertex(destination.getId()));
			vertex++;
		}
	}

	public HashMap<Integer, Vertex> getVertices() {
		return vertices;
	}

	public void setVertices(HashMap<Integer, Vertex> vertices) {
		this.vertices = vertices;
	}

	public void setAdjacencyMatrix(double adjacencyMatrix[][]) {
		this.adjacencyMatrix = adjacencyMatrix;
	}

	public double[][] getAdjacencyMatrix() {
		return adjacencyMatrix;
	}

	public int getLink() {
		return link;
	}

	public void setLink(int link) {
		this.link = link;
	}

	public int getVertex() {
		return vertex;
	}

	public void setVertex(int vertex) {
		this.vertex = vertex;
	}

	public int getDemand() {
		return demand;
	}

	public void setDemand(int demand) {
		this.demand = demand;
	}

	public void setFinalized(boolean finalized) {
		this.finalized = finalized;
		if (finalized == false)
			adjacencyMatrix = null;
	}

	public void removeEdge(Edge e) {
		setFinalized(false);
		edges.remove(e);
		Vertex source = e.getSource();
		Vertex destination = e.getDestination();
		boolean removeSource = true;
		boolean removeDestination = true;
		for (Edge ed : edges) {
			Vertex newSource = ed.getSource();
			if (newSource == source)
				removeSource = false;
			if (newSource == destination)
				removeDestination = false;
			Vertex newDestination = ed.getDestination();
			if (newDestination == source)
				removeSource = false;
			if (newDestination == destination)
				removeDestination = false;
			if (!(removeSource || removeDestination))
				break;
		}
		if (removeSource) {
			vertices.remove(source);
			--vertex;
		}
		if (removeDestination) {
			vertices.remove(destination);
			--vertex;
		}
	}

	public int getVertexNumber() {
		return vertex;
	}

	public void close() {
		if (!finalized) {
			adjacencyMatrix = new double[getVertexNumber()][];
			for (int i = 0; i < getVertexNumber(); ++i) {
				adjacencyMatrix[i] = new double[getVertexNumber()];
			}
			for (Edge ed : edges) {
				int source = ed.getSource().getId();
				int dest = ed.getDestination().getId();
				adjacencyMatrix[source - 1][dest - 1] = ed.getFreeCapacity();
			}
			setFinalized(true);
		}
	}

	public HashMap<Integer, Integer> favouriteSenders(Set<Integer> sender,
			Set<Integer> receiver) {
		// TODO Bisogna renderlo in funzione dei SENDERS/RECEIVERS non di tutti quelli
		// che in questo momento hanno delle domande.
		HashMap<Integer, Integer> favourite = new HashMap<Integer, Integer>();
		int matrix[][] = new int[this.vertex][2];
		// dimension 2 because 1 is the sender, the second is
		// the distance ;
		for (int i = 0; i < this.vertex; ++i) {
			matrix[i][1] = Integer.MAX_VALUE;
		}
		for (Integer i : sender) {
			HashMap<Integer, Integer> bfs = bfs(i);
			for (Integer j : receiver) {
				if (bfs.get(j)<=0){
					continue;
				}
				
				
				if (matrix[j - 1][1] > bfs.get(j) && bfs.get(j) > 0) {
					matrix[j - 1][0] = i;
					matrix[j - 1][1] = bfs.get(j);

				}
//				if (matrix[i - 1][1] > bfs.get(j) && bfs.get(j) > 0) {
//					matrix[i - 1][0] = j;
//					matrix[i - 1][1] = bfs.get(j);
//
//				}
			}
		}
		for (int i = 0; i < matrix.length; ++i) {
			if (matrix[i][1] < Integer.MAX_VALUE) {
				favourite.put(i + 1, matrix[i][0]);
			}
		}
		return favourite;
	}

	public HashMap<Integer, Integer> bfs(int vertex) {
		LinkedList<Integer> queue = new LinkedList<Integer>();
		HashMap<Integer, Integer> distance = new HashMap<Integer, Integer>();
		for (int i = 1; i <= this.vertex; ++i) {
			if (i == vertex) {
				distance.put(i, 0);
			} else {
				distance.put(i, -1);
			}
		}
		queue.push(vertex);
		while (!queue.isEmpty()) {
			int current = queue.removeFirst();
			for (int i = 0; i < this.vertex; ++i) {
				if (i == current - 1 || adjacencyMatrix[current-1][i] == 0) {
					continue;
				}
				if (distance.get(i + 1) == -1) {
					queue.push(i + 1);
					distance.put(i + 1, distance.get(current) + 1);
				}
			}

		}

		// Set<Integer> s = distance.keySet();
		// ArrayList<Integer> toRemove = new ArrayList<Integer>();
		// for (Integer i : s) {
		// if (!vertices.containsKey(i)) {
		// toRemove.add(i);
		// }
		// }
		// s.removeAll(toRemove);
		return distance;
	}

}
