package partitioner;

import java.util.ArrayList;

public class PartitionLeaf implements PartitionTreeElement {
	private Partition leaf;
	private PartitionNode parent;

	PartitionLeaf(Partition partition, PartitionNode parent) {
		leaf = partition;
		this.parent = parent;
	}

	PartitionLeaf() {
		this(null, null);
	}

	public boolean containsElement(Integer el) {
		return leaf.contains(el);
	}

	public Partition getPartition(Integer vertex) {
		if (leaf.contains(vertex))
			return leaf;
		else
			return null;
	}

	public PartitionLeaf getPartitionLeaf(Integer vertex) {
		if (leaf.contains(vertex))
			return this;
		else
			return null;
	}

	public Partition getLeaf() {
		return leaf;
	}

	public void setLeaf(Partition leaf) {
		this.leaf = leaf;
	}

	public PartitionNode getParent() {
		return parent;
	}

	public void setParent(PartitionNode parent) {
		this.parent = parent;
	}

	@Override
	public ArrayList<Partition> retriveLeafPartitions() {
		ArrayList<Partition> result = new ArrayList<Partition>();
		result.add(leaf);
		return result;
	}

	public void bipartite(Partition[] partitions) {
		if (partitions.length > 2 || partitions.length == 1)
			return;
		PartitionNode oldParent = parent;
		PartitionNode newParent = new PartitionNode();
		if (oldParent != null) {

			newParent.setParent(oldParent);
			if (this == oldParent.left) {
				oldParent.left = newParent;
			} else {
				oldParent.right = newParent;
			}
		}
		this.parent = newParent;
		this.leaf = partitions[1];
		newParent.setRight(this);
		newParent.setLeft(new PartitionLeaf(partitions[0], newParent));

	}

}
