package commpattern;

import java.util.HashMap;

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
	private int[][] favourite_transmitter;
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
		this.favourite_transmitter = new int[receiver.length][3];
		HashMap<Integer, Integer[]> visit = graph.modifiedVisit(sender);
		for (int i = 0; i < receiver.length; ++i) {
			Integer[] current = visit.get(receiver[i]);
			// favourite_transmitter[i][0]=bfs[receiver[i]-1];
			// favourite_transmitter[i][1]=sender;
			favourite_transmitter[i][0] = current[0];
			favourite_transmitter[i][1] = sender;
			favourite_transmitter[i][2] = current[2];
		}
//		updateFavouriteTransmitter(2);
//		updateFavouriteTransmitter(4);
//		
//		int cnt = 0;
//		for (int[] i : favourite_transmitter) {
//			System.err.print(receiver[cnt] + " ");
//			for (int j : i) {
//				System.err.print(j + " ");
//			}
//			System.err.println();
//			cnt++;
//		}
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
			if (receiving[i])
				continue;
			demand = new Demand(sender, receiver[i], min_bitrate[i],
					max_bitrate[i], false, 0, 0, 1, this);
			dl.addDemand(demand);
		}
		for (int i = 0; i < owner.length; ++i) {
			if (!owner[i])
				continue;
			for (int j = 0; j < receiver.length; ++j) {
				if (receiving[j])
					continue;
				demand = new Demand(receiver[i], receiver[j], max_bitrate[i],
						this);
				dl.addDemand(demand);
			}
		}
		activeDemands = dl;
		return dl;
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
		setReceivingFromReceiver(allocatedDemand.getReceiver(), true);
		allocatedDemand.setAllocated(true);
		//activeDemands.addDemand(allocatedDemand);
		return;

	}

	@Override
	public void updateTransmissionEvent(Object[] obj) {
		double now = (Double) obj[0];
		Demand modifiedDemand = (Demand) obj[1];
		// modifiedDemand.setAllocated(false);
		// assert(activeDemands.remove(modifiedDemand));
		boolean check = removeFromList(modifiedDemand.getSender(),
				modifiedDemand.getReceiver());
		assert (check);
		modifiedDemand.setAllocated(true);
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
		modifiedDemand.setEndTime(trxModel.computeRemainingTransmissionTime(
				now, modifiedDemand.getStartTime(),
				modifiedDemand.getEndTime(), modifiedDemand.getMin_bandwidth(),
				new_bit_rate, arr));
		modifiedDemand.setStartTime(now);
		modifiedDemand.setMin_bandwidth(new_bit_rate);
		activeDemands.addDemand(modifiedDemand);
		return;

	}

	@Override
	public void endTransmissionEvent(Object[] obj) {
		Demand d = (Demand) obj[0];
		setOwnerFromReceiver(d.getReceiver(), true);
		updateFavouriteTransmitter(d.getReceiver());
		assert (activeDemands.remove(d));
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
		HashMap<Integer, Integer[]> visit = graph.modifiedVisit(newOwner);
		for (int i=0;i<receiver.length;++i) {
//			if (receiver[i] == newOwner)
//				continue;
			Integer array[] = visit.get(receiver[i]);
			if (favourite_transmitter[i][0] > array[0]
					|| (favourite_transmitter[i][0] == array[0] && favourite_transmitter[i][2] > array[2])) {
				favourite_transmitter[i][0] = array[0];
				favourite_transmitter[i][1] = newOwner;
				favourite_transmitter[i][2] = array[2];
			} else
				continue;
		}

	}

	public void setReceivingFromReceiver(int receiver, boolean b) {
		for (int i = 0; i < this.receiver.length; i++)
			if (this.receiver[i] == receiver) {
				this.receiving[i] = b;
				return;
			}
	}

	public int getReceiverFromReceiving(int receiving) {
		return receiver[receiving];
	}

	public int getReceiverFromOwner(int owner) {
		return receiver[owner];
	}

	public boolean removeFromList(int sender, int receiver) {
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
		return wasFound;
	}



	public void debug() {
		for (int i = 0; i < favourite_transmitter.length; ++i) {
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
		}
	}

}