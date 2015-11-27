package model;

import demand.Demand;

public class SimpleModel implements TransmissionModel {

	private int messageSize;
	
	public SimpleModel(int size){
		messageSize = size;
	}
	
	@Override
	public double computeTotalTransmissionTime(double now, int bw, Object[] obj) {
		Integer nHops = (Integer)obj[0];
		return now + 1.0*(nHops*messageSize)/bw;
	}

	@Override
	public double computeRemainingTransmissionTime(double now, int newBw, Demand d, Object[] obj) {
		double start=d.getLastUpdateTime();
		double expectedTimeEnd=d.getEndTime();
		int initialBw =d.getMin_bandwidth();
		if(initialBw==newBw)return expectedTimeEnd;
		Integer nHops = (Integer)obj[0];
		double previousPercentage = d.getTransmittedPercentage();
		double percentageToTransmit = (1.0-previousPercentage)*(expectedTimeEnd - now)/(expectedTimeEnd - start);
		d.setTransmittedPercentage(1-percentageToTransmit);
		d.setMin_bandwidth(newBw);
		d.setLastUpdateTime(now);
		return now + messageSize* (percentageToTransmit*nHops)/newBw;
	}

}
