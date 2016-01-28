package partitioner;

import java.util.ArrayList;

public class PartitionLeaf implements PartitionTreeElement {
	private Partition leaf;
	private PartitionNode parent;

	PartitionLeaf(Partition partition, PartitionNode parent){
		leaf = partition;
		this.parent = parent;
	}
	
	PartitionLeaf(){
		this(null,null);
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
		ArrayList<Partition> result =new ArrayList<Partition>();
		result.add(leaf);
		return result;
	}
	

}
