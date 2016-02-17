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

public class NewBroadcast implements Collective {

	private HashSet<Integer> owner;
	private HashSet<Integer> transmitting;
	private HashSet<Integer> receiver;
	private HashSet<Integer> receiving;
	private DistanceBasedPartitioner partitioner;
	private Graph graph;
	private DemandList activeDemands;
	private TransmissionModel trxModel;
	private int minBitrate;
	private int maxBitrate;

	public NewBroadcast(int sender, int receiver[], int minBitrate,
			int maxBitrate, Graph graph) {
		this.owner = new HashSet<Integer>(receiver.length);
		owner.add(sender);
		this.receiver = new HashSet<Integer>(receiver.length);
		for (int i : receiver) {
			this.receiver.add(i);
		}
		this.transmitting = new HashSet<Integer>(receiver.length);
		this.receiving = new HashSet<Integer>(receiver.length);
		this.activeDemands = new DemandList();
		this.trxModel = new SimpleModel(100,2);
		this.partitioner = new DistanceBasedPartitioner(graph);
		this.minBitrate = minBitrate;
		this.maxBitrate = maxBitrate;
	}

	public NewBroadcast(int sender, int receiver[], int maxBitrate, Graph graph) {
		this(sender, receiver, 0, maxBitrate, graph);
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
			HashMap<Integer, Integer> d = partitioner.generateDemands(sender,
					receiver);
			for (Integer k : d.keySet()) {
				Integer destination = k;
				Integer distance = d.get(k);
				demand = new Demand(sender+1, destination+1, maxBitrate, this);
				demand.setWeight(distance*100);
				dl.addDemand(demand);
			}
		}
		//DEBUG
		partitioner.printPartitions();
		//END DEBUG
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
		int destination = allocatedDemand.getReceiver()-1;
		int source = allocatedDemand.getSender()-1;
		receiver.remove(destination);
		receiving.add(destination);
		transmitting.add(source);
		int hopCounter = 0;
		for (Integer i : link) {
			if (i > 0)
				hopCounter++;
		}
		Object arr[] = new Object[1];
		arr[0] = hopCounter;
		allocatedDemand.setStartTime(now);
		allocatedDemand.setEndTime(trxModel.computeTotalTransmissionTime(now,
				bit_rate, arr));
		allocatedDemand.setMin_bandwidth(bit_rate);
		allocatedDemand.setAllocated(true);
		return;

	}

	@Override
	public void updateTransmissionEvent(Object[] obj) {
		// TODO Auto-generated method stub

	}

	@Override
	public void endTransmissionEvent(Object[] obj) {
		Demand d = (Demand) obj[0];
		// setOwnerFromReceiver(d.getReceiver(), true);
		HashSet<Integer> hs = new HashSet<Integer>();
		int receiver = d.getReceiver()-1;
		int sender = d.getSender()-1;

		if(d.getSender()!=d.getReceiver()){
			double now = d.getEndTime();
			Demand fake = new Demand(d.getReceiver(),d.getReceiver(),minBitrate,maxBitrate,true,now,trxModel.computeTotalTransmissionTime(now,-1, null), 0, this);
			activeDemands.addDemand(fake);
			owner.add(sender);
			transmitting.remove(sender);
			Partition recPart = partitioner.getPartition(receiver);
			if (recPart.contains(sender)){
			partitioner.bipartite(sender, receiver);
			}else{
				//DEBUG
				System.err.println("Spedito a qualcuno che non appartiene alla mia partizione");
				//END DEBUG
				for (Integer in:recPart.getVertex()){
					if(in==receiver) continue;
					if(owner.contains(in)){
						partitioner.bipartite(receiver, in);
						break;
					}
				}
			}
		}
		else{
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
