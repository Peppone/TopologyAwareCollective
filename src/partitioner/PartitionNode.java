package partitioner;

import java.util.ArrayList;

public class PartitionNode implements PartitionTreeElement {
	PartitionTreeElement left;
	PartitionTreeElement right;
	PartitionNode parent;

	public boolean containsElement(Integer el) {
		boolean leftTree = left.containsElement(el);
		if (leftTree)
			return true;
		else {
			boolean rightTree = left.containsElement(el);
			return rightTree;
		}

	}

	public Partition getPartition(Integer vertex) {
		Partition leftPartition = left.getPartition(vertex);
		if (leftPartition != null)
			return leftPartition;
		else {
			return right.getPartition(vertex);
		}

	}

	public PartitionLeaf getPartitionLeaf(Integer vertex) {
		PartitionLeaf leftLeaf = left.getPartitionLeaf(vertex);
		if (leftLeaf != null)
			return leftLeaf;
		else {
			return right.getPartitionLeaf(vertex);
		}
	}

	public ArrayList<Partition> retriveLeafPartitions() {
		ArrayList<Partition> result = new ArrayList<Partition>();
		result.addAll(left.retriveLeafPartitions());
		result.addAll(right.retriveLeafPartitions());
		return result;
	}
	
	public PartitionNode getParent(){
		return parent;
	}

	public PartitionTreeElement getLeft() {
		return left;
	}

	public PartitionTreeElement getRight() {
		return right;
	}

	public void setLeft(PartitionTreeElement left) {
		this.left = left;
	}

	public void setRight(PartitionTreeElement right) {
		this.right = right;
	}

	public void setParent(PartitionNode parent) {
		this.parent = parent;
	}
	
}
