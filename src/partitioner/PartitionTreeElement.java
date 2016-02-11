package partitioner;

import java.util.ArrayList;


public interface PartitionTreeElement {
	

	boolean containsElement(Integer el);

	Partition getPartition(Integer vertex);

	PartitionLeaf getPartitionLeaf(Integer vertex);
	
	ArrayList<Partition> retriveLeafPartitions();

	void printPartitions();
}
