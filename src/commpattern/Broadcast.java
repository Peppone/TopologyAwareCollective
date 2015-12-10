package commpattern;

import java.util.HashMap;
import java.util.HashSet;

import graph.Graph;
import model.SimpleModel;
import model.TransmissionModel;
import demand.Demand;
import demand.DemandList;

public class Broadcast implements Collective {
	private int sender;
	private int[] receiver;
	private int max_bitrate[];
	private int min_bitrate[];
	private boolean owner[];
	private boolean receiving[];
	private DemandList activeDemands;
	private TransmissionModel trxModel;
	/**
	 * old favourite_transmitter: 0 - availability 1 - sender 2 - hop number
	 * 
	 * new favourite_transmitter: 0 - hop number 1 - sender
	 */
	// private int[][] favourite_transmitter;
	private HashMap<Integer, Integer[]> favourite_transmitter;
	private Graph graph;

	public Broadcast(int sender, int receiver[], int max_bitrate[], Graph graph) {
		this(sender, receiver, null, max_bitrate, graph);
	}

	public Broadcast(int sender, int receiver[], int min_bitrate[],
			int max_bitrate[], Graph graph) {
		this.sender = sender;
		this.receiver = receiver;
		this.min_bitrate = min_bitrate;
		if (min_bitrate == null) {
			this.min_bitrate = new int[receiver.length];
		}
		this.max_bitrate = max_bitrate;
		owner = new boolean[receiver.length];
		activeDemands = new DemandList();
		this.receiving = new boolean[receiver.length];
		trxModel = new SimpleModel(100);
		this.graph = graph;
		this.graph.addVertexEdges(sender);
		// this.favourite_transmitter = new int[receiver.length][2];
		// HashMap<Integer, Integer[]> visit = graph.modi			counter++;fiedVisit(sender);
		this.favourite_transmitter = new HashMap<Integer, Integer[]>(
				receiver.length);
		HashMap<Integer, Integer> visit = graph.modifiedBfsVisit(sender,
				getPossibleReceiver(), toHashSet(receiver, receiving));
		for (int i = 0; i < receiver.length; ++i) {
			Integer distance = visit.get(receiver[i]);
			if (distance != null) {/*
									 * //
									 * favourite_transmitter[i][0]=bfs[receiver
									 * [i]-1]; favourite_transmitter[i][0] =
									 * current; favourite_transmitter[i][1] =
									 * sender;
									 */
				Integer value[] = new Integer[2];
				value[0] = distance;
				value[1] = sender;
				favourite_transmitter.put(receiver[i], value);
			}
			// else{
			// favourite_transmitter[i][0] = -1;
			// favourite_transmitter[i][1] = -1;
			// }
			this.graph.addVertexEdges(receiver[i]);
		}
	}

	public String generateDemandToString() {
		String demand = "";
		for (int i = 0; i < receiver.length; ++i) {
			demand += sender + " " + getReceiverFromReceiving(i) + " "
					+ max_bitrate[i] + " " + min_bitrate[i];
		}
		for (int i = 0; i < owner.length; ++i) {
			if (!owner[i])
				continue;
			for (int j = 0; j < receiving.length; ++j) {
				if (receiving[j])
					continue;
				demand += getReceiverFromOwner(i) + " "
						+ getReceiverFromReceiving(j) + " " + max_bitrate[i];
			}
		}
		return demand;
	}

	public DemandList generateDemands() {
		Demand demand;
		DemandList dl = new DemandList();
		dl = DemandList.mergeAllocated(dl, activeDemands);
		for (int i = 0; i < receiver.length; ++i) {
			if (receiving[i] || !favourite_transmitter.containsKey(receiver[i]))
				continue;
			demand = new Demand(sender, receiver[i], min_bitrate[i],
					max_bitrate[i], false, 0, 0, 1, this);
			// if (favourite_transmitter[i][1] == sender) {

			if (favourite_transmitter.get(receiver[i])[1] == sender) {
				demand.setWeight(100 * favourite_transmitter.get(receiver[i])[0]);
			}
			dl.addDemand(demand);
		}
		for (int i = 0; i < owner.length; ++i) {
			if (!owner[i])
				continue;
			for (int j = 0; j < receiver.length; ++j) {
				if (receiving[j] || !favourite_transmitter.containsKey(receiver[j]))
					continue;

				demand = new Demand(receiver[i], receiver[j], max_bitrate[i],
						this);
				// if (favourite_transmitter[j][1] == receiver[i]) {
				if (favourite_transmitter.get(receiver[j])[1] == receiver[i]) {
					// demand.setWeight(100 * favourite_transmitter[j][0]);
					demand.setWeight(100 * favourite_transmitter
							.get(receiver[j])[0]);
				}
				dl.addDemand(demand);
			}
		}
		activeDemands = dl;
		// //DEBUG
		// System.out.println("bw tx hop w ");
		// for(int[] i : favourite_transmitter){
		// for(int j:i){
		// System.out.print(j+" ");
		// }
		// System.out.println();
		// }
		// System.out.println();
		// //
		return dl;
	}

	public HashSet<Integer> toHashSet(int array[], boolean vec[]) {
		HashSet<Integer> res = new HashSet<Integer>();
		for (int i = 0; i < array.length; ++i) {
			if (vec[i]) {
				res.add(array[i]);
			}
		}
		return res;

	}

	public int getSender() {
		return sender;
	}

	public void setSender(int sender) {
		this.sender = sender;
	}

	public int[] getReceiver() {
		return receiver;
	}

	public void setReceiver(int[] receiver) {
		this.receiver = receiver;
	}

	public int getMinBitrate(int index) {
		return min_bitrate[index];
	}

	public int getMaxBitrate(int index) {
		return max_bitrate[index];
	}

	public void setOwner(int index, boolean own) {
		owner[index] = own;
	}

	public boolean getOwner(int index) {
		return owner[index];
	}

	@Override
	public void startTransmissionEvent(Object[] obj) {
		double now = (Double) obj[0];
		Demand allocatedDemand = (Demand) obj[1];
		Integer[] link = (Integer[]) obj[2];
		int bit_rate = (Integer) obj[3];
		int destination = allocatedDemand.getReceiver();
		setReceivingFromReceiver(destination, true);
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
		setReceivingFromReceiver(allocatedDemand.getReceiver(), true);
		allocatedDemand.setAllocated(true);
		// activeDemands.addDemand(allocatedDemand);
		return;

	}

	public HashSet<Integer> getPossibleReceiver() {
		HashSet<Integer> res = new HashSet<Integer>();
		for (int i = 0; i < receiving.length; ++i) {
			if (!receiving[i] || owner[i])
				res.add(receiver[i]);
		}
		return res;
	}

	@Override
	public void updateTransmissionEvent(Object[] obj) {
		// TODO Copiare su ModifiedDemand la vecchia banda
		double now = (Double) obj[0];
		Demand modifiedDemand = (Demand) obj[1];
		Demand old = removeFromList(modifiedDemand.getSender(),
				modifiedDemand.getReceiver());
		// assert (check);
		modifiedDemand.setAllocated(true);
		modifiedDemand.setMin_bandwidth(old.getMin_bandwidth());
		Integer[] link = (Integer[]) obj[2];
		int new_bit_rate = (Integer) obj[3];
		int destination = modifiedDemand.getReceiver();
		setReceivingFromReceiver(destination, true);
		int hopCounter = 0;
		for (Integer i : link) {
			if (i > 0)
				hopCounter++;
		}
		Object arr[] = new Object[1];
		arr[0] = hopCounter;
		trxModel.computeRemainingTransmissionTime(now, new_bit_rate,
				modifiedDemand, arr);
		activeDemands.addDemand(modifiedDemand);
		return;

	}

	@Override
	public void endTransmissionEvent(Object[] obj) {
		Demand d = (Demand) obj[0];
		setOwnerFromReceiver(d.getReceiver(), true);
		updateFavouriteTransmitter(d.getReceiver());
		activeDemands.remove(d);
		// assert ();
		return;

	}

	@Override
	public boolean isEnd() {
		for (int i = 0; i < owner.length; ++i) {
			if (!owner[i])
				return false;
		}
		return true;
	}

	@Override
	public int getDemandNumber() {
		return activeDemands.getN_demand();
	}

	public void setOwnerFromReceiver(int receiver, boolean b) {
		for (int i = 0; i < this.receiver.length; i++)
			if (this.receiver[i] == receiver) {
				this.owner[i] = b;
				updateFavouriteTransmitter(this.receiver[i]);
				return;
			}
	}

	private void updateFavouriteTransmitter(int newOwner) {
		// HashMap<Integer, Integer[]> visit = graph.modifiedVisit(newOwner);
		HashMap<Integer, Integer> visit = graph.modifiedBfsVisit(newOwner,
				getPossibleReceiver(), toHashSet(receiver, receiving));
		for (int i = 0; i < receiver.length; ++i) {
			// if (receiver[i] == newOwneri)
			// continue;
			Integer current = visit.get(receiver[i]);
			if (current != null) {
				/*
				 * if (favourite_transmitter[i][0] > current) {
				 * favourite_transmitter[i][0] = current;
				 * favourite_transmitter[i][1] = newOwner;
				 */
				Integer array[] = favourite_transmitter.get(receiver[i]);
				if (array==null ){
					array = new Integer[2];
					array[0] = current;
					array[1] = newOwner;
					favourite_transmitter.put(receiver[i], array);
				}else{
					if(array[0] > current) {
						array[0] = current;
						array[1] = newOwner;
						favourite_transmitter.put(receiver[i], array);
					} else
						continue;
				}
			}
		}

	}

	public void setReceivingFromReceiver(int receiver, boolean b) {
		for (int i = 0; i < this.receiver.length; i++)
			if (this.receiver[i] == receiver) {
				this.receiving[i] = b;
				return;
			}
	}

	public int getIndexFromReceiver(int receiver) {
		for (int i = 0; i < this.receiver.length; i++)
			if (this.receiver[i] == receiver) {
				return i;
			}
		return -1;
	}

	public int getReceiverFromReceiving(int receiving) {
		return receiver[receiving];
	}

	public int getReceiverFromOwner(int owner) {
		return receiver[owner];
	}

	public Demand removeFromList(int sender, int receiver) {
		Demand toRemove = null;
		boolean wasFound = false;
		for (Demand d : activeDemands.getDemands()) {
			if (d.getSender() != sender)
				continue;
			if (d.getReceiver() == receiver) {
				toRemove = d;
				wasFound = true;
				break;
			}
		}
		if (wasFound) {
			activeDemands.remove(toRemove);
		}
		return toRemove;
	}

	public void debug() {
		/*for (int i = 0; i < favourite_transmitter.length; ++i) {
			System.out.println(receiver[i] + " " + favourite_transmitter[i][0]
					+ " " + favourite_transmitter[i][1]);
		}
		System.out.println();
		for (int i = receiver.length - 1; i >= 0; --i) {
			updateFavouriteTransmitter(receiver[i]);
			for (int j = 0; j < favourite_transmitter.length; ++j) {
				System.out.println(receiver[j] + " "
						+ favourite_transmitter[j][0] + " "
						+ favourite_transmitter[j][1]);
			}
			System.out.println();
		}*/
	}

}
