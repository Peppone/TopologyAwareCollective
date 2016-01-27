package partitioner;

public interface PartitionTreeElement {

	boolean containsElement(Integer el);

	Partition getPartition(Integer vertex);

	PartitionLeaf getPartitionLeaf(Integer vertex);

}
