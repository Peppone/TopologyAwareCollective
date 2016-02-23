package demand;

import java.util.ArrayList;
import java.util.Iterator;

public class DemandList {
	private ArrayList<Demand> list;
	private int n_demand;

	public DemandList() {
		list = new ArrayList<Demand>();
		n_demand = 0;
	}

	public static DemandList mergeAllocated(DemandList a, DemandList b) {
		DemandList res = new DemandList();
		for (Demand d : a.list) {
			if (d.isAllocated()) {
				res.addDemand(d);
			}
		}
		for (Demand d : b.list) {
			if (d.isAllocated())
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

	public int getAllDemandNumber() {
		return list.size();
	}

	public int getAllNonAllocatedDemandNumber() {
		Iterator<Demand> it = list.iterator();
		int demand = 0;
		while (it.hasNext()) {
			Demand current = it.next();
			if (!current.isAllocated())
				demand++;
		}
		return demand;
	}

	public ArrayList<Demand> getAllUsefuldDemands() {
		ArrayList<Demand> dm = new ArrayList<Demand>();
		Iterator<Demand> it = list.iterator();
		while (it.hasNext()) {
			Demand current = it.next();
			if (!current.isAllocated()&&current.getReceiver()!=current.getSender())
				dm.add(current);
		}
		return dm;
	}

	public int getRealDemandNumber() {
		Iterator<Demand> it = list.iterator();
		int demand = 0;
		while (it.hasNext()) {
			Demand current = it.next();
			if (current.getSender() != current.getReceiver())
				demand++;
		}
		return demand;
	}

	public void addDemand(Demand d) {

		list.add(d);
		n_demand++;
	}

	public String writeCplexCode() {
		return writeCplexTrailer() + writeCplexFooter();

	}

	public String writeCplexTrailer() {
		for (Demand d : list) {
			if (d.getSender() == d.getReceiver() || d.isAllocated())
				n_demand--;
		}
		String code = "";
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
		for (Demand d : list) {
			if (d.getReceiver() != d.getSender() && !d.isAllocated()) {
				demArray += d.writeDemand();
				maxBwArray += d.writeMaxBW() + " ";
			}
		}
		demArray += "];\n";
		maxBwArray += "];\n";
		return demArray + maxBwArray;
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

	public void clear() {
		list.clear();
	}

	public boolean atLeastOneNotAllocatedDemand() {
		for (Demand d : list) {
			if (!d.isAllocated())
				return true;
		}
		return false;
	}

	public String printList() {
		String print = "";
		for (Demand d : list) {
			print += d.getSender() + " -> " + d.getReceiver() + " s:" + d.getStartTime() + " e:" + d.getEndTime()
					+ "\n";
		}
		return print;
	}

//	public String writeBroadcastGoalFile() {
//		String goal = "";
//		Broadcast b = (Broadcast) list.get(0).getCollective();
//		int root = b.getSender();
//		int destN = b.getReceiver().length;
//		goal += "num_ranks " + (1 + destN) + "\n";
//		String sender = "";
//
//		sender += "rank " + 0 + "{\n\t";
//		String receiver[] = new String[destN];
//		int[] rcvFlag = new int[destN];
//		int sndFlag = 0;
//
//		for (int i = 0; i < destN; ++i) {
//			receiver[i] = "";
//			receiver[i] += "rank " + (i + 1) + "{\n\t";
//		}
//		int dest;
//		int src;
//		for (Demand d : list) {
//			src = d.getSender();
//			dest = d.getReceiver();
//			if (src == root) {
//				sender += "l" + sndFlag + ": send 100b to " + (b.getIndexFromReceiver(dest) + 1) + " tag 0\n\t";
//				if (sndFlag != 0) {
//					sender += "l" + (sndFlag) + " requires l" + (sndFlag - 1) + "\n\t";
//				}
//				sndFlag++;
//			} else {
//				receiver[b.getIndexFromReceiver(src)] += "l" + rcvFlag[b.getIndexFromReceiver(src)] + ": send 100b to "
//						+ (b.getIndexFromReceiver(dest) + 1) + " tag 0\n\t";
//				if (rcvFlag[b.getIndexFromReceiver(src)] != 0) {
//					receiver[b.getIndexFromReceiver(src)] += "l" + rcvFlag[b.getIndexFromReceiver(src)] + " requires l"
//							+ (rcvFlag[b.getIndexFromReceiver(src)] - 1) + "\n\t";
//				}
//				rcvFlag[b.getIndexFromReceiver(src)]++;
//			}
//			receiver[b.getIndexFromReceiver(dest)] += "l" + rcvFlag[b.getIndexFromReceiver(dest)] + ": recv 100b from "
//					+ (b.getIndexFromReceiver(src) + 1) + " tag 0\n\t";
//			if (rcvFlag[b.getIndexFromReceiver(dest)] != 0) {
//				receiver[b.getIndexFromReceiver(dest)] += "l" + (rcvFlag[b.getIndexFromReceiver(dest)]) + " requires l"
//						+ (rcvFlag[b.getIndexFromReceiver(dest)] - 1) + "\n\t";
//			}
//			rcvFlag[b.getIndexFromReceiver(dest)]++;
//		}
//		sender += "}\n";
//		goal += sender + "\n";
//
//		for (int i = 0; i < n_receiver; ++i) {
//			goal += receiver[i] + "\n}\n";
//		}
//		return goal;
//	}

	public Demand getDemand(int index) {
		return list.get(index);
	}

}
