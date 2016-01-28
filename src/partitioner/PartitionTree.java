package partitioner;

import graph.Graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class PartitionTree {
	private PartitionTreeElement root;

	public PartitionTree(Graph graph) {
		Partition p = new Partition(graph);
		root = new PartitionLeaf(p,null);
	}

	boolean containsElement(Integer el) {
		return root.containsElement(el);
	}

	public Partition getPartition(Integer vertex) {
		return root.getPartition(vertex);
	}

	Partition findPossiblePartition() {
		// TODO
		return null;
	}

	public PartitionTreeElement getRoot() {
		return root;
	}
	

	public ArrayList<Integer> findPossibleDestinations(Integer vertex,
			Collection<Integer> receivers) {
		PartitionLeaf leaf = root.getPartitionLeaf(vertex);
		Partition current = leaf.getPartition(vertex);
		Iterator<Integer> it = receivers.iterator();
		Integer dest;
		ArrayList<Integer> solutions = new ArrayList<Integer>();
		while (it.hasNext()) {
			dest = it.next();
			if (current.contains(dest)) {
				solutions.add(dest);
			}
		}
		if (solutions.size() > 0)
			return solutions;

		else {
			PartitionNode parent = leaf.getParent();
			PartitionNode oldParent = parent;
			PartitionTreeElement toInvestigate = null;
			while (solutions.size() == 0) {
				parent = parent.getParent();
				if (oldParent == parent.getLeft()) {
					toInvestigate = parent.getRight();
				} else {
					toInvestigate = parent.getLeft();
				}
				ArrayList<Partition> leaves = toInvestigate
						.retriveLeafPartitions();
				for (Partition p : leaves) {
					it = p.getIterator();
					while (it.hasNext()) {
						dest = it.next();
						if (current.contains(dest)) {
							solutions.add(dest);
						}
					}
				}
				if (parent == null)
					break;
				else
					oldParent = parent;

			}
			return solutions;
		}
	}
}
