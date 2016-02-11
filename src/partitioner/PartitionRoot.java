package partitioner;

import java.util.ArrayList;

public class PartitionRoot extends PartitionNode{

	
	PartitionRoot(PartitionTreeElement child){
		this.left= child;
		this.right=child;
	}
	
	PartitionRoot(){
		this(null);
	}
	
	@Override
	public boolean containsElement(Integer el) {
		return left.containsElement(el);
	}

	@Override
	public Partition getPartition(Integer vertex) {
		return left.getPartition(vertex);
	}

	@Override
	public PartitionLeaf getPartitionLeaf(Integer vertex) {
		return left.getPartitionLeaf(vertex);
	}

	@Override
	public ArrayList<Partition> retriveLeafPartitions() {
		return left.retriveLeafPartitions();
	}
	@Override
	public void setLeft(PartitionTreeElement pet){
		left = pet;
		right = pet;
	}
	
	@Override
	public void setRight(PartitionTreeElement pet){
		setLeft(pet);
	}

	@Override
	public void printPartitions(){
		left.printPartitions();
	}


}
