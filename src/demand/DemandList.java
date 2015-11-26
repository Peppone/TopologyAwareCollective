package demand;

import java.util.ArrayList;
import java.util.HashSet;

public class DemandList {
	// DIREI DI TOGLIERE N_sender, N_receiver, N-Demand
	private ArrayList<Demand> list;
	private int n_sender;
	private int n_receiver;
	private int n_demand;
	private HashSet<Integer> sender;
	private HashSet<Integer> receiver;

	public DemandList() {
		list = new ArrayList<Demand>();
		n_sender = 0;
		n_receiver = 0;
		n_demand = 0;
		receiver = new HashSet<Integer>();
		sender = new HashSet<Integer>();
	}

	
	public static DemandList mergeAllocated(DemandList a, DemandList b){
		DemandList res = new DemandList();
		for (Demand d : a.list) {
			if(d.isAllocated()){
				res.addDemand(d);
			}
		}
		for (Demand d : b.list) {
			if(d.isAllocated())
			res.addDemand(d);
		}
		return res;
	}
	
	public static DemandList merge(DemandList a, DemandList b) {
		DemandList res = new DemandList();
		for (Demand d : a.list) {
			res.addDemand(d);
		}
		for (Demand d : b.list) {
			res.addDemand(d);
		}
		return res;
	}

	public void merge(DemandList a) {
		for (Demand d : a.list) {
			this.addDemand(d);
		}
	}

	public int getN_demand() {
		return n_demand;
	}

	public void addDemand(Demand d) {
		//DEBUG
		boolean isPresent = false;
		for(Demand d2:list){
			if(d.getSender()==d2.getSender() && d.getReceiver()==d2.getReceiver()) {
				isPresent =true;
				break;
			}
		}
		assert(!isPresent);
		//END
		
		
		list.add(d);
		n_demand++;
		// int se =d.getS_edge();
		// int re =d.getR_edge();
		// HashSet<Integer> sender=new HashSet<Integer>();
		// HashSet<Integer> receiver=new HashSet<Integer>();
		// if(!sender_edge.contains(se)){
		if (!sender.contains(d.getSender())) {
			n_sender++;
			// sender_edge.add(se);
		}
		// if(!receiver_edge.contains(re)){
		if (!receiver.contains(d.getReceiver())) {
			n_receiver++;
			// receiver_edge.add(re);
		}
	}

	public String writeCplexCode() {
		return writeCplexTrailer() + writeCplexFooter();

	}

	public String writeCplexTrailer() {
		String code = "";
//		code += "n_sender = " + n_sender + ";\n";
//		code += "n_receiver = " + n_receiver + ";\n";
		code += "n_demand = " + n_demand + ";\n";
		return code;
	}

	public String writeCplexFooter() {
		String code = "";
		code += writeList();
		return code;
	}

	private String writeList() {
		String demArray = "demand = [";
		String maxBwArray = "max_bitrate = [ ";
		String minBwArray = "min_bitrate = [ ";
		String demandAllocation = "ensure_allocation = [ ";
		for (Demand d : list) {
			demArray += d.writeDemand();
			maxBwArray += d.writeMaxBW() + " ";
			minBwArray += d.writeMinBW() + " ";
			demandAllocation += d.writeIsAllocated() + " ";
		}
		demArray += "];\n";
		maxBwArray += "];\n";
		minBwArray += "];\n";
		demandAllocation += "];\n";
		return demArray + minBwArray + maxBwArray + demandAllocation;
	}

	public ArrayList<Demand> getDemands() {
		return list;
	}

	public void removeAll(ArrayList<Demand> toRemove) {
		list.removeAll(toRemove);
		n_demand = list.size();
	}

	public boolean remove(Demand d) {
		return list.remove(d);
	}
	
	public boolean atLeastOneNotAllocatedDemand(){
		for(Demand d:list){
			if(!d.isAllocated())return true;
		}
		return false;
	}
	

	// private String writeEdges(){
	// String sEdges="sender_edge=[";
	// String rEdges="receiver_edge=[";
	// for(Integer i : sender_edge){
	// sEdges+=" "+i;
	// }
	// sEdges+=" ];\n";
	//
	// for(Integer i : receiver_edge){
	// rEdges+=" "+i;
	// }
	//
	// rEdges+=" ];\n";
	//
	// return sEdges+rEdges;
	// }

}
