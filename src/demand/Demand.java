package demand;

import commpattern.Collective;

public class Demand {

	private int sender;
	private int receiver;
	private Collective collective;
	// private int s_edge;
	// private int r_edge;
	private int max_bandwidth;
	private int min_bandwidth;
	private int weight;
	private boolean isAllocated;
	private double t_start;
	private double t_end;
	private double lastUpdateTime;
	private double transmitted_percentage;

	public Demand(int s, int r,/* int se, int re, */int max_b,
			Collective collective) {
		this(s, r,/* se,re, */0, max_b, false, 0, 0,1, collective);

	}

	public Demand(int s, int r,/* int se, int re, */int min_b, int max_b,
			boolean allocation, double t_start, double t_end,int weight,
			Collective collective) {
		sender = s;
		receiver = r;
		// s_edge = se;
		// r_edge = re;
		max_bandwidth = max_b;
		min_bandwidth = min_b;
		isAllocated = allocation;
		this.weight=weight;
		this.collective = collective;
		if (isAllocated) {
			this.t_start = t_start;
			lastUpdateTime = t_start;
			this.t_end = t_end;
		}
	}

	public int getSender() {
		return sender;
	}

	public void setSender(int sender) {
		this.sender = sender;
	}

	public int getReceiver() {
		return receiver;
	}

	public void setReceiver(int receiver) {
		this.receiver = receiver;
	}

	public double getStartTime() {
		if (!isAllocated)
			return -1;
		return t_start;
	}

	public double getEndTime() {
		if (!isAllocated)
			return -1;
		return t_end;
	}

	public void setStartTime(double start) {
		t_start = start;
	}

	public void setEndTime(double end) {
		t_end = end;
	}


	public int getMax_bandwidth() {
		return max_bandwidth;
	}

	public void setMax_bandwidth(int max_bandwidth) {
		this.max_bandwidth = max_bandwidth;
	}

	public int getMin_bandwidth() {
		return min_bandwidth;
	}

	public void setMin_bandwidth(int min_bandwidth) {
		this.min_bandwidth = min_bandwidth;
	}

	public boolean isAllocated() {
		return isAllocated;
	}

	public void setAllocated(boolean isAllocated) {
		this.isAllocated = isAllocated;
	}
	
	public Collective getCollective() {
		return collective;
	}

	
	public double getTransmittedPercentage() {
		return transmitted_percentage;
	}

	public void setTransmittedPercentage(double transmitted_percentage) {
		this.transmitted_percentage = transmitted_percentage;
	}
	

	public double getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(double lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public String writeDemand() {
		return "[ " + sender + " " + receiver +" "+weight+ " ]";
	}

	public String writeMaxBW() {
		return "" + max_bandwidth;
	}

	public String writeMinBW() {
		return "" + min_bandwidth;
	}
	
	
	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public String writeWeight(){
		return "" + weight;
	}

	public String writeIsAllocated() {
		int allocation = (isAllocated ? 1 : 0);
		return "" + allocation;
	}

}
