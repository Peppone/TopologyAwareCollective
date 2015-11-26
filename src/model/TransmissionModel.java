package model;

public interface TransmissionModel {
	public double computeTotalTransmissionTime(double now, int bw, Object[] obj);

	public double computeRemainingTransmissionTime(double now, double start,
			double expectedTimeEnd, int initialBw, int newBw, Object obj[]);
}
