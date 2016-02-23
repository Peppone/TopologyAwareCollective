package commpattern;

import java.util.HashMap;
import java.util.HashSet;

import demand.Demand;
import demand.DemandList;
import graph.Graph;
import model.SimpleModel;
import model.TransmissionModel;
import partitioner.DistanceBasedPartitioner;
import partitioner.Partition;

public class Broadcast implements Collective {

	private HashSet<Integer> owner;
	private HashSet<Integer> transmitting;
	private HashSet<Integer> receiver;
	private HashSet<Integer> receiving;
	private DistanceBasedPartitioner partitioner;
	private Graph graph;
	private DemandList activeDemands;
	private TransmissionModel trxModel;

	private int maxBitrate;

	public Broadcast(int sender, int receiver[], int maxBitrate, Graph graph) {
		this.owner = new HashSet<Integer>(receiver.length);
		owner.add(sender);
		this.receiver = new HashSet<Integer>(receiver.length);
		for (int i : receiver) {
			this.receiver.add(i);
		}
		this.transmitting = new HashSet<Integer>(receiver.length);
		this.receiving = new HashSet<Integer>(receiver.length);
		this.activeDemands = new DemandList();
		this.trxModel = new SimpleModel(100, 10);
		this.partitioner = new DistanceBasedPartitioner(graph);
		this.maxBitrate = maxBitrate;
		this.graph = graph;
	}

	@Override
	public DemandList generateDemands() {
		Demand demand;
		DemandList dl = new DemandList();
		dl = DemandList.mergeAllocated(dl, activeDemands);
		for (Integer sender : owner) {
			if (transmitting.contains(sender)) {
				continue;
			}
			HashMap<Integer, Integer> d = partitioner.generateDemands(sender, receiver);
			for (Integer k : d.keySet()) {
				Integer destination = k;
				Integer distance = d.get(k);
				demand = new Demand(sender + 1, destination + 1, maxBitrate, this);
				demand.setWeight(distance * 100);
				dl.addDemand(demand);
			}
		}
		// DEBUG
		// partitioner.printPartitions();
		// END DEBUG
		activeDemands = dl;
		return dl;
	}

	@Override
	public int getDemandNumber() {
		return activeDemands.getRealDemandNumber();
	}

	@Override
	public void startTransmissionEvent(Object[] obj) {
		double now = (Double) obj[0];
		Demand allocatedDemand = (Demand) obj[1];
		Integer[] link = (Integer[]) obj[2];
		int bit_rate = (Integer) obj[3];
		int destination = allocatedDemand.getReceiver() - 1;
		int source = allocatedDemand.getSender() - 1;
		receiver.remove(destination);
		receiving.add(destination);
		transmitting.add(source);
		int hopCounter = 0;
		for (int i = 0; i < link.length; ++i) {
			if (link[i] > 0) {
				hopCounter++;
				graph.decreaseEdgeCapacity(i, bit_rate);
			}
		}
		allocatedDemand.setBitrate(bit_rate);
		allocatedDemand.setLinkUtilization(link);
		Object arr[] = new Object[1];
		arr[0] = hopCounter;
		allocatedDemand.setStartTime(now);
		allocatedDemand.setEndTime(trxModel.computeTotalTransmissionTime(now, bit_rate, arr));
		allocatedDemand.setAllocated(true);
		return;

	}


	@Override
	public void endTransmissionEvent(Object[] obj) {
		Demand d = (Demand) obj[0];
		int receiver = d.getReceiver() - 1;
		int sender = d.getSender() - 1;

		if (d.getSender() != d.getReceiver()) {
			double now = d.getEndTime();
			Demand fake = new Demand(d.getReceiver(), d.getReceiver(), maxBitrate, true, now,
					trxModel.computeTotalTransmissionTime(now, -1, null), 0, this);
			activeDemands.addDemand(fake);
			owner.add(sender);
			transmitting.remove(sender);
			Partition recPart = partitioner.getPartition(receiver);
			int link[] = d.getLinkUtilization();
			for (int i = 0; i < link.length; ++i) {
				if (link[i] > 0)
					graph.increaseEdgeCapacity(i, d.getBitrate());
			}
			if (recPart.contains(sender)) {
				partitioner.bipartite(sender, receiver);
			} else {
				for (Integer in : recPart.getVertex()) {
					if (in == receiver)
						continue;
					if (owner.contains(in)) {
						partitioner.bipartite(receiver, in);
						break;
					}
				}
			}
		} else {
			owner.add(receiver);
			receiving.remove(receiver);
		}
		activeDemands.remove(d);
		return;

	}

	@Override
	public boolean isEnd() {
		return (receiver.size() == 0 && receiving.size() == 0);
	}

}
