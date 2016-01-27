package partitioner;

public class PartitionTree {
	PartitionTreeElement root;
	
	boolean containsElement(Integer el){
		return root.containsElement(el);
	}
	
	Partition getPartition(Integer vertex){
		return root.getPartition(vertex);
	}
	
	Partition findPossiblePartition(){
		//TODO
		return null;
	}
}
