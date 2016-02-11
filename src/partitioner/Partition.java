package partitioner;

import graph.Graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

public class Partition {

	private HashSet<Integer> vertex;

	public Partition() {
		this.vertex = new HashSet<Integer>();
	}

	public Partition(HashSet<Integer> vertex) {
		this.vertex = vertex;
	}

	/**
	 * This constructor generates a new partition from a specific graph
	 * 
	 * @param graph
	 */
	public Partition(Graph graph) {
		int vertexNum = graph.getVertexNumber();
		vertex = new HashSet<Integer>(vertexNum);
		for (int i = 0; i < vertexNum; ++i) {
			vertex.add(i);
		}
	}
	
	

	public HashSet<Integer> getVertex() {
		return vertex;
	}

	public void setVertex(HashSet<Integer> vertex) {
		this.vertex = vertex;
	}

	public Partition(Collection<Integer> vertex) {
		this.vertex = new HashSet<Integer>(vertex.size());
		for (Integer i : vertex) {
			this.vertex.add(i);
		}
	}

	public boolean contains(Integer v) {
		return vertex.contains(v);
	}

	public boolean add(Integer v) {
		return vertex.add(v);
	}

	public boolean remove(Integer v) {
		return vertex.remove(v);
	}

	public Iterator<Integer> getIterator() {
		return vertex.iterator();
	}

	public int size() {
		return vertex.size();
	}

	public void merge(Partition p) {
		for (Integer i : p.vertex) {
			vertex.add(i);
		}
	}

	public boolean removeAll(Collection<Integer> collection) {
		return this.vertex.removeAll(collection);
	}

	public Partition randomBipartite(HashSet<Integer> inner,
			HashSet<Integer> border) {
		Partition p = new Partition(inner);
		boolean check = this.removeAll(inner);
		if (check == false) {
			System.err
					.println("Error during partioning phase: collection includes elements not present in partition");
			return null;
		}
		if (inner == null)
			return p;

		int currentSize = this.size();
		int otherSize = border.size();
		Iterator<Integer> it = inner.iterator();
		while (it.hasNext()) {
			Integer v = it.next();
			if (otherSize < currentSize) {
				p.add(v);
			} else {
				this.add(v);
			}
		}
		return p;
	}

	public Partition[] split(int[] initialVisit, int[] pivotVisit) {
		Partition result[] = new Partition[2];
		result[0] = new Partition();
		result[1] = new Partition();
		ArrayList<Integer> borderline = new ArrayList<Integer>();
		for (int i : vertex) {
			if (initialVisit[i] < pivotVisit[i]) {
				result[0].add(i);
			} else if (initialVisit[i] > pivotVisit[i]) {
				result[1].add(i);
			} else {
				borderline.add(i);
			}
		}
		Random r = new Random();
		while (borderline.size() > 0) {
			int pivot = r.nextInt(borderline.size());
			Integer vertex = borderline.remove(pivot);
			int differentialSize = result[0].size() - result[1].size();
			if (differentialSize > 0) {
				result[1].add(vertex);
			} else if (differentialSize < 0) {
				result[0].add(vertex);
			} else
				result[r.nextInt(2)].add(vertex);
		}
		return result;
	}
}
