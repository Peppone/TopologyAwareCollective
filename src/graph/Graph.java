package graph;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.StringTokenizer;

public class Graph {
	int[][] avertex;
	int[][] bvertex;
	int[] edge;
	int [] maxEdge;
	ArrayList<ArrayList<Integer>> list;
	ArrayList<Integer> sender_edge;
	ArrayList<Integer> receiver_edge;
	int n_vertex;
	int h_edge;
	int n_edge;
	ArrayList<String[]> pairs;

	@SuppressWarnings("unchecked")
	Graph(Graph g) {
		// TODO Demands should be copied
		avertex = new int[g.avertex.length][];
		bvertex = new int[g.bvertex.length][];
		for (int i = 0; i < avertex.length; ++i) {
			avertex[i] = g.avertex[i].clone();
			bvertex[i] = g.bvertex[i].clone();
		}
		edge = new int[g.edge.length];
		edge = g.edge.clone();
		
		n_vertex = g.n_vertex;
		h_edge = g.h_edge;
		n_edge = g.n_edge;
		sender_edge = (ArrayList<Integer>) g.sender_edge.clone();
		receiver_edge = (ArrayList<Integer>) g.receiver_edge.clone();
		sender_edge = new ArrayList<Integer>();
		receiver_edge = new ArrayList<Integer>();
		pairs = (ArrayList<String[]>) g.pairs.clone();
	}

	public Graph(String adjMatrixFile) {
		try {
			// readAdjMatrixFromFile(adjMatrixFile);
			readAdjListFromFile(adjMatrixFile);
		} catch (IOException e) {
			e.printStackTrace();
		}

		sender_edge = new ArrayList<Integer>();
		receiver_edge = new ArrayList<Integer>();
		maxEdge = edge.clone();
	}

	private String writeMatrix(String name, int mat[][]) {
		String matrix = name + " = [";
		for (int i = 0; i < mat.length; ++i) {
			matrix += "[ ";
			for (int j = 0; j < mat[i].length; ++j) {
				matrix += mat[i][j] + " ";
			}
			matrix += "] ";
		}
		matrix += "];\n";
		return matrix;
	}

	private String writeVector(String name, int vec[]) {
		String vector = name + " = [";
		for (int i = 0; i < vec.length; ++i) {
			vector += " " + vec[i];
		}
		vector += "];\n";
		return vector;
	}

	private String writeArrayList(String name, ArrayList<Integer> al) {
		String vector = name + " = [";
		for (int i = 0; i < al.size(); ++i) {
			vector += " " + (al.get(i));
		}
		vector += "];\n";
		return vector;
	}

	public String writeCplexCode() {
		return writeCplexTrailer() + writeCplexFooter();
	}

	public String writeCplexTrailer() {
		String code = "";
		code += "n_vertex = " + n_vertex + ";\n";
		code += "n_edge = " + n_edge + ";\n";
		code += "h_edge = " + h_edge + ";\n";
		code += "n_sender = " + sender_edge.size() + ";\n";
		code += "n_receiver = " + receiver_edge.size() + ";\n";
		return code;
	}

	public String writeCplexFooter() {
		String code = "";
		code += writeMatrix("avertex", avertex);
		code += writeMatrix("bvertex", bvertex);
		code += writeVector("max_capacity", edge);
		code += writeArrayList("sender_edge", sender_edge);
		code += writeArrayList("receiver_edge", receiver_edge);
		return code;
	}

	private void readAdjListFromFile(String filename) throws IOException {
		File adjListFile = new File(filename);
		BufferedReader br = new BufferedReader(new FileReader(adjListFile));
		int line_counter = 1;
		String line;
		boolean isAdj = false;
		boolean isDot = false;
		while ((line = br.readLine().trim()).equalsIgnoreCase("")) {
			line_counter++;
		}
		line = line.replace(";", "");
		n_vertex = Integer.parseInt(line);
		line_counter++;
		int adjMatrix[][] = new int[n_vertex][];
		for (int i = 0; i < n_vertex; ++i) {
			adjMatrix[i] = new int[n_vertex];
		}
		pairs = new ArrayList<String[]>();
		while ((line = br.readLine()) != null) {
			line = line.trim();
			if (line.equalsIgnoreCase("")) {
				line_counter++;
				continue;
			}
			// StringTokenizer st = new StringTokenizer(line, "[->][<->]",true);
			ArrayList<String> st = MyTokenizer.tokenize(line);

			// int numTokens = st.countTokens();
			int numTokens = st.size();
			String lastToken = st.get(st.size() - 1);
			if (numTokens < 3) {
				System.err.println("Error occurred in line " + line_counter);
				return;
			}
			if (lastToken.contains("->")) {
				System.err.println("Missing some numbers" + line_counter);
				return;
			}
			String source = st.remove(0);
			String operator = null;
			String destination = null;
			String next = null;

			int capacitySize = 0;

			while (!st.isEmpty()) {
				if (next == null) {
					operator = st.remove(0);
				} else {
					operator = next;
				}
				destination = st.remove(0);
				if (!st.isEmpty()) {
					next = st.remove(0);
				} else {
					next = null;
				}
				if (operator.equalsIgnoreCase(";")) {
					isAdj = true;
					if (isDot) {
						System.err.println("Mixed notation");
						return;
					}
					capacitySize = Integer.parseInt(next);
					int src = Integer.parseInt(source);
					int dest = Integer.parseInt(destination);
					if (adjMatrix[src][dest] == 0) {
						adjMatrix[src][dest] = capacitySize;
						n_edge++;
					}
					if (!st.isEmpty()) {
						next = st.remove(0);
					}

				} else {
					isDot = true;
					if (isAdj) {
						System.err.println("Mixed notation");
						return;
					}
					if (operator.equalsIgnoreCase("->")) {
						if (next == null || next.equalsIgnoreCase("->")
								|| next.equalsIgnoreCase("<->")) {
							capacitySize = 100;
						} else {
							capacitySize = Integer.parseInt(next);
							next = null;
						}
						int src = Integer.parseInt(source);
						int dest = Integer.parseInt(destination);
						if (adjMatrix[src][dest] == 0) {
							adjMatrix[src][dest] = capacitySize;
							n_edge++;
						}

					} else if (operator.equalsIgnoreCase("<->")) {
						if (next == null || next.equalsIgnoreCase("->")
								|| next.equalsIgnoreCase("<->")) {
							capacitySize = 100;

						} else {
							capacitySize = Integer.parseInt(next);
							next = null;
						}
						int src = Integer.parseInt(source);
						int dest = Integer.parseInt(destination);
						if (adjMatrix[src][dest] == 0) {
							adjMatrix[src][dest] = capacitySize;
							n_edge++;
						}
						if (adjMatrix[dest ][src] == 0) {
							adjMatrix[dest][src] = capacitySize;
							n_edge++;
						}
					} else {
						System.err.println("Bad token in line " + line_counter);
						return;
					}
					source = destination;
				}

			}
			/*
			 * while (st.hasMoreTokens()) { destination = st.nextToken();
			 * destination = destination.trim(); String[] tokens = null; if
			 * (source != null) { tokens = destination.split("[\\s+]"); if
			 * (tokens.length == 1) { capacitySize = 100; } else { capacitySize
			 * = Integer.parseInt(tokens[1]); } int src =
			 * Integer.parseInt(source); int dest = Integer.parseInt(tokens[0]);
			 * if (adjMatrix[src - 1][dest - 1] == 0) { adjMatrix[src - 1][dest
			 * - 1] = capacitySize; n_edge++; } destination = tokens[0]; }
			 * source = destination; }
			 */
		}

		avertex = new int[n_edge][n_vertex];
		bvertex = new int[n_edge][n_vertex];
		edge = new int[n_edge];
		int edgeCounter = 0;
		for (int i = 0; i < n_edge; ++i) {
			avertex[i] = new int[n_vertex];
			bvertex[i] = new int[n_vertex];
		}
		for (int i = 0; i < n_edge; ++i) {
			for (int j = i + 1; j < n_vertex; ++j) {
				if (adjMatrix[i][j] > 0) {
					avertex[edgeCounter][i] = 1;
					bvertex[edgeCounter][j] = 1;
					edge[edgeCounter] = adjMatrix[i][j];
					String end[] = new String[2];
					end[0] = "" + (i + 1);
					end[1] = "" + (j + 1);
					pairs.add(end);
					edgeCounter++;
				}
				if (adjMatrix[j][i] > 0) {
					avertex[edgeCounter][j] = 1;
					bvertex[edgeCounter][i] = 1;
					edge[edgeCounter] = adjMatrix[j][i];
					String end[] = new String[2];
					end[0] = "" + (j + 1);
					end[1] = "" + (i + 1);
					pairs.add(end);
					edgeCounter++;
				}
			}
		}
		h_edge = n_edge >> 1;
		br.close();

	}

	private void readAdjMatrixFromFile(String filename) throws IOException {
		File adjMatrixFile = new File(filename);
		BufferedReader br = new BufferedReader(new FileReader(adjMatrixFile));
		String line = br.readLine();
		StringTokenizer st = new StringTokenizer(line);
		n_vertex = st.countTokens();
		n_edge = 0;

		int adjMatrix[][] = new int[n_vertex][];
		for (int i = 0; i < n_vertex; ++i) {
			adjMatrix[i] = new int[n_vertex];
		}
		int counter = 0;
		for (int i = 0; i < n_vertex; ++i) {

			Integer value = Integer.parseInt(st.nextToken());
			adjMatrix[counter][i] = value;
			if (value > 0)
				n_edge++;
		}
		counter++;
		while ((line = br.readLine()) != null) {
			st = new StringTokenizer(line);
			for (int i = 0; i < n_vertex; ++i) {
				Integer value = Integer.parseInt(st.nextToken());
				adjMatrix[counter][i] = value;
				if (value > 0)
					n_edge++;
			}
			counter++;
		}

		avertex = new int[n_edge][n_vertex];
		bvertex = new int[n_edge][n_vertex];
		edge = new int[n_edge];
		int edgeCounter = 0;
		for (int i = 0; i < n_edge; ++i) {
			avertex[i] = new int[n_vertex];
			bvertex[i] = new int[n_vertex];
		}
		for (int i = 0; i < n_edge; ++i) {
			for (int j = i + 1; j < n_vertex; ++j) {
				if (adjMatrix[i][j] > 0) {
					avertex[edgeCounter][i] = 1;
					bvertex[edgeCounter][j] = 1;
					edge[edgeCounter] = adjMatrix[i][j];
					edgeCounter++;
				}
				if (adjMatrix[j][i] > 0) {
					avertex[edgeCounter][j] = 1;
					bvertex[edgeCounter][i] = 1;
					edge[edgeCounter] = adjMatrix[j][i];
					edgeCounter++;
				}
			}
		}
		h_edge = n_edge >> 1;
		br.close();
	}

	public int findEndNodeEdge(int vertex, int[][] matrix) {
		int counter = 0;
		int position = -1;
		for (int i = 0; i < n_edge; ++i) {
			if (matrix[i][vertex] == 1) {
				counter++;
				position = i;
			}
		}
		if (counter == 1)
			return position + 1;
		else
			return -1;

	}

	public int[] bfs(int root) {
		ArrayList<Integer> node = new ArrayList<Integer>();
		node.add(root);
		int[] result = new int[avertex[0].length];
		for (int i = 0; i < result.length; ++i) {
			result[i] = -1;
		}
		result[root] = 0;
		while (!node.isEmpty()) {
			int current_node = node.remove(0);
			for (int i = 0; i < avertex.length; ++i) {
				if (avertex[i][current_node] == 1) {
					for (int j = 0; j < avertex[i].length; ++j) {
						if (bvertex[i][j] == 1) {
							if (result[j] == -1) {
								result[j] = result[current_node] + 1;
								node.add(j);
							}
							break;
						}
					}
				}
			}
		}
		return result;
	}
	/**
	 * This method returns a customized bfs visit.
	 * If we want to separate 
	 * @param root
	 * @param possibleReceiver
	 * @param receiving
	 * @return
	 */
	public HashMap<Integer, Integer> modifiedBfsVisit(int root,
			HashSet<Integer> possibleReceiver, HashSet<Integer> receiving) {
		/*
		 * value = distance
		 */
		ArrayList<Integer> node = new ArrayList<Integer>();
		node.add(root);
		HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
		int distance = 0;
		map.put(root, distance);
		while (!node.isEmpty()) {
			int current_node = node.remove(0);
			for (int i = 0; i < avertex.length; ++i) {
				if (avertex[i][current_node - 1] == 1) {
					for (int j = 0; j < avertex[i].length; ++j) {
						if (bvertex[i][j] == 1) {
							if (!map.containsKey(j + 1)) {
								distance = (map.get(current_node) + 1);
								//if (!possibleReceiver.contains(j + 1)) {
									node.add(j + 1);
								//}
								map.put(j + 1, distance);
							}
							break;
						}
					}
				}
			}
		}
		map.put(root, -1);
		return map;
	}

	/**
	 * This method returns the modified visit for a given root The visit
	 * consists of a set of array with these values: 0 - the maximum
	 * availability on the path 1 - the parent node in the path 2 - the number
	 * of hops between source and destination.
	 */
	public HashMap<Integer, Integer[]> modifiedVisit(int root) {
		HashMap<Integer, Integer[]> map = new HashMap<Integer, Integer[]>();
		Integer[] value = new Integer[3];
		value[1] = root;
		value[0] = Integer.MAX_VALUE;
		value[2] = 0;
		map.put(root, value);
		// ArrayList <Integer> node = new ArrayList<Integer>();
		map = modifiedVisitRecursive(root, Integer.MAX_VALUE, map, 0);
		value[0] = -1;
		map.put(root, value);
		return map;
	}

	private HashMap<Integer, Integer[]> modifiedVisitRecursive(int root,
			int max_bw, HashMap<Integer, Integer[]> map, int depth) {
		// Cerca figli validi
		ArrayList<Integer> children = searchValidNodes(root, max_bw, map,
				depth + 1);
		// If no figli validi, return map;
		if (children.isEmpty())
			return map;
		// Ricorsione sui figli
		for (Integer i : children) {
			map = modifiedVisitRecursive(i, map.get(i)[0], map, depth + 1);
		}

		return map;
	}

	private ArrayList<Integer> searchValidNodes(int root, int max_bw,
			HashMap<Integer, Integer[]> map, int depth) {
		ArrayList<Integer> arr = new ArrayList<Integer>();
		for (int i = 0; i < avertex.length; ++i) {
			if (avertex[i][root - 1] == 1) {
				for (int j = 0; j < bvertex[i].length; ++j) {
					if (bvertex[i][j] == 1) {
						if (!map.containsKey(j + 1)) {
							Integer vect[] = new Integer[3];
							vect[0] = (max_bw < edge[i]) ? max_bw : edge[i];
							vect[1] = root;
							vect[2] = depth;
							map.put(j + 1, vect);
							arr.add(j + 1);
							break;
						}
						int minimum = (max_bw < edge[i]) ? max_bw : edge[i];
						Integer previous_value[] = map.get(j + 1);
						if (previous_value[0] > minimum
								|| (previous_value[0] == minimum && previous_value[2] <= depth))
							break;
						// if(previous_value[0] == minimum && previous_value[2]
						// == depth){
						//
						// }
						Integer[] new_value = previous_value;
						new_value[0] = minimum;
						new_value[1] = root;
						new_value[2] = depth;
						map.put(j + 1, new_value);
						arr.add(j + 1);
						break;
					}

				}
			}
		}

		return arr;
	}

	public int getEdgeNumber() {
		return n_edge;
	}

	public int getVertexNumber() {
		return n_vertex;
	}

	public String[] edge(int edge) {
		return pairs.get(edge);
	}

	public void addVertexEdges(int vertex) {
		for (int i = 0; i < n_edge; ++i) {
			if (avertex[i][vertex] == 1) {
				if (!sender_edge.contains(i))
					sender_edge.add(i);
			}
			if (bvertex[i][vertex] == 1) {
				if (!receiver_edge.contains(i))
					receiver_edge.add(i);
			}
		}
	}
	
	public void setEdgeCapacity(int edge, int capacity){
		this.edge[edge]=capacity;
	}
	public void increaseEdgeCapacity(int edge, int capacityToIncrease){
		this.edge[edge]+=capacityToIncrease;
		assert(this.edge[edge]<= maxEdge[edge]);
	}
	public void decreaseEdgeCapacity(int edge, int capacityToDecrease){
		this.edge[edge]-=capacityToDecrease;
		assert(this.edge[edge]>=0);
		
	}
}