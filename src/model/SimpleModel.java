package model;


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


}
