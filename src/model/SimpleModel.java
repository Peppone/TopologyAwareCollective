package model;

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
	public double computeRemainingTransmissionTime(double now, double start,
			double expectedTimeEnd, int initialBw, int newBw, Object[] obj) {
		Integer nHops = (Integer)obj[0];
		if(initialBw==newBw)return expectedTimeEnd;
		double percentageToTransmit = 1.0*(expectedTimeEnd - now)/(expectedTimeEnd - start);
		return now + messageSize* (percentageToTransmit*nHops)/newBw;
	}

}
