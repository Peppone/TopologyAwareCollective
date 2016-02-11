package commpattern;

import demand.DemandList;

public interface Collective {
	//public String generateDemandToString();
	public DemandList generateDemands();
	public int getDemandNumber();
	public void startTransmissionEvent(Object[] obj);
	public void updateTransmissionEvent(Object[]obj);
	public void endTransmissionEvent(Object[] obj);
	public boolean isEnd();
}
