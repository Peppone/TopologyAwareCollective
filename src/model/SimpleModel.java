package model;

import demand.Demand;

public class SimpleModel implements TransmissionModel {

	private int messageSize;
	private double overhead;

	/**
	 * Create a new simple transmission model
	 * 
	 * @param message
	 *            size in B
	 * @param overhead
	 *            time needed to push on the software stack the package
	 */
	public SimpleModel(int size, double overhead) {
		messageSize = size;
		this.overhead = overhead;
	}

	@Override
	public double computeTotalTransmissionTime(double now, int bw, Object[] obj) {
		if (bw > 0) {
			Integer nHops = (Integer) obj[0];
			return now + 1.0 * (nHops * messageSize) / bw;
		} else {
			return now+overhead;
		}
	}

	@Override
	public double computeRemainingTransmissionTime(double now, int newBw,
			Demand d, Object[] obj) {
		double start = d.getLastUpdateTime();
		double expectedTimeEnd = d.getEndTime();
		int initialBw = 0;
		if (initialBw == newBw)
			return expectedTimeEnd;
		Integer nHops = (Integer) obj[0];
		double previousPercentage = d.getTransmittedPercentage();
		double percentageToTransmit = (1.0 - previousPercentage)
				* (expectedTimeEnd - now) / (expectedTimeEnd - start);
		d.setTransmittedPercentage(1 - percentageToTransmit);
		d.setLastUpdateTime(now);
		return now + messageSize * (percentageToTransmit * nHops) / newBw;
	}

}
