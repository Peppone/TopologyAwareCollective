package partitioner;

import graph.Graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class PartitionTree {
	private PartitionRoot root;

	public PartitionTree(Graph graph) {
		Partition p = new Partition(graph);
		root = new PartitionRoot();
		root.setLeft(new PartitionLeaf(p,root));
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
	
	public void printPartitions(){
		root.printPartitions();
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
			PartitionNode oldParent;
			PartitionTreeElement toInvestigate = null;
			while (solutions.size() == 0) {
				oldParent = parent;
				parent = parent.getParent();
				if (parent == null)
					break;
				if (oldParent == parent.getLeft()) {
					toInvestigate = parent.getRight();
				} else {
					assert(parent.getRight() == oldParent);
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

			}
			return solutions;
		}
	}
	
	public PartitionLeaf getPartitionLeaf(Integer vertex){
		return root.getPartitionLeaf(vertex);
	}
}
