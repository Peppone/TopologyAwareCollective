package model;

import demand.Demand;

public interface TransmissionModel {
	public double computeTotalTransmissionTime(double now, int bw, Object[] obj);

	public double computeRemainingTransmissionTime(double now, int newBw, Demand d, Object obj[]);
}
