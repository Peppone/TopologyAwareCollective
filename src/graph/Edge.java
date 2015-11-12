package graph;

public class Edge {
	
	private double capacity;
	private double freeCapacity;
	private Vertex source;
	private Vertex destination;

	public Edge(double capacity) {
		this(capacity,capacity,null,null);
	}
	
	public Edge(double capacity, Vertex source,
			Vertex destination) {
		this(capacity, capacity, source,
				 destination);
	}

	public Edge(double capacity, double freeCapacity, Vertex source,
			Vertex destination) {
		super();
		this.capacity = capacity;
		this.freeCapacity = freeCapacity;
		this.source = source;
		this.destination = destination;
	}



	public double getCapacity() {
		return capacity;
	}

	public void setCapacity(double capacity) {
		this.capacity = capacity;
	}

	public double getFreeCapacity() {
		return freeCapacity;
	}

	public void setFreeCapacity(double freeCapacity) {
		this.freeCapacity = freeCapacity;
	}
	
	

	public Vertex getSource() {
		return source;
	}



	public void setSource(Vertex source) {
		this.source = source;
	}



	public Vertex getDestination() {
		return destination;
	}



	public void setDestination(Vertex destination) {
		this.destination = destination;
	}



	public void decrementFreeCapacity(double toDecrease) {
		if (toDecrease > freeCapacity) {
			try {
				throw new Exception();
			} catch (Exception e) {
				System.err.println("Not enough free capacity: present "
						+ freeCapacity + "; requested " + toDecrease);
				e.printStackTrace();
			}
		}
		freeCapacity -= toDecrease;
	}

	public void incrementFreeCapacity(double toIncrement) {
		if (toIncrement + freeCapacity > capacity) {
			try {
				throw new Exception();
			} catch (Exception e) {
				System.err
						.println("Deallocating more capacity than allowed: capacity "
								+ capacity
								+ "; requested "
								+ toIncrement
								+ freeCapacity);
				e.printStackTrace();
			}
		}
		freeCapacity += toIncrement;
	}
	
}
