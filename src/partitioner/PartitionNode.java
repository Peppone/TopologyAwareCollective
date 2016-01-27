package partitioner;

public class PartitionNode implements PartitionTreeElement{
	PartitionTreeElement left;
	PartitionTreeElement right;
	PartitionTreeElement parent;

	
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
	
	public PartitionLeaf getPartitionLeaf (Integer vertex){
		PartitionLeaf leftLeaf = left.getPartitionLeaf(vertex);
		if (leftLeaf != null)
			return leftLeaf;
		else {
			return right.getPartitionLeaf(vertex);
		}
	}
}
