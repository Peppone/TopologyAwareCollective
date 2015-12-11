package algorithm;

import graph.Graph;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import utility.ExecuteShellCommand;

import commpattern.Collective;

import demand.Demand;
import demand.DemandList;

public class Core {

	private ArrayList<Collective> collectives;
	private ArrayList<Integer[]> u;
	private DemandList demands;
	private double time;
	private int edge;
	private String resultFile;
	private String dataFile;
	private String dotFile;
	private DemandList storyline;
	private Graph graph;

	public Core(String resultFile, String dataFile, String dotFile,
			Graph graph, Collective... collective) throws IOException {
		/**
		 * public Core(int edge, String demandfile, String resultFile,
		 * Collective... collective) throws IOException
		 */
		collectives = new ArrayList<Collective>();
		for (Collective c : collective) {
			collectives.add(c);
		}
		this.graph = graph;
		this.edge = graph.getEdgeNumber();
		this.dataFile = dataFile;
		this.resultFile = resultFile;
		this.dotFile = dotFile;
		u = null;
		time = 0;
	}

	public DemandList execute() throws IOException, InterruptedException {
		// TODO: Migliorare la gestione dell'output file generato CPLEX. Fa
		// troppo schifo a causa del formato delle variabili.

		storyline = new DemandList();
		int iteration_counter = 0;
		while (collectives.size() != 0) {
			// DEBUG
			long start = System.nanoTime();
			//
			demands = new DemandList();
			for (Collective c : collectives) {
				demands.merge(c.generateDemands());
			}

			if (demands.atLeastOneNotAllocatedDemand()) {
				File data = new File(dataFile);
				data.delete();
				FileWriter fw = new FileWriter(data);
				fw.write(graph.writeCplexTrailer()
						+ demands.writeCplexTrailer()
						+ graph.writeCplexFooter() + demands.writeCplexFooter());
				fw.close();
				// EXECUTE CPLEX
				String command = "/usr/local/bin/oplrun";
				String model = "/home/peppone/opl/multidemandallocation/output.o";// args[1];
				ExecuteShellCommand esc = new ExecuteShellCommand();
				StringTokenizer res[] = esc.executeCommand(command, "-v",
						model, dataFile, resultFile);

				// Leggi l'ouput di CPLEX
				int allocation[] = new int[demands.getN_demand()];
				int bit_rate[] = new int[demands.getN_demand()];
				u = new ArrayList<Integer[]>(demands.getN_demand());
				for (int i = 0; i < demands.getN_demand(); ++i) {
					u.add(i, new Integer[edge]);
				}
				// Leggi tutti i dati che servono
				int tokenCounter = 0;
				while (res[0].hasMoreTokens()) {
					allocation[tokenCounter] = Integer.parseInt(res[0]
							.nextToken());
					tokenCounter++;
				}
				tokenCounter = 0;
				while (res[1].hasMoreTokens()) {
					bit_rate[tokenCounter] = Integer.parseInt(res[1]
							.nextToken());
					tokenCounter++;
				}
				int rowCounter = 0;
				while (res[2].hasMoreTokens()) {
					Integer row[] = new Integer[edge];
					for (int i = 0; i < edge; ++i) {
						row[i] = Integer.parseInt(res[2].nextToken());
					}
					u.add(rowCounter, row);
					rowCounter++;
				}
				// Crea il file per il simulatore
				writeDotFile(demands, u, allocation, iteration_counter);

				// Comunica tutti i risultati alle varie collectives
				int counter = collectives.get(0).getDemandNumber();
				int collective = 0;
				ArrayList<Demand> allDemands = demands.getDemands();
				for (int i = 0; i < demands.getN_demand(); ++i) {
					if (i > counter - 1) {
						collective++;
						counter += collectives.get(collective)
								.getDemandNumber();
					}
					if (allocation[i] == 0)
						continue;
					Demand d = allDemands.get(i);
					Collective current = collectives.get(collective);
					int left = counter
							- collectives.get(collective).getDemandNumber();
					int index = i - left;
					Object obj[] = toObjectArray(time, d, u.get(i),
							bit_rate[i], index);
					if (d.isAllocated()) {
						// // DEBUG
						// System.err.println("Ci siamo");
						// System.err.println("Src: " + d.getSender() +
						// ", Dest: "
						// + d.getReceiver() + ", Min:  "
						// + d.getMin_bandwidth() + ", Start:  "
						// + d.getStartTime() + "  " + d.getEndTime()
						// + "  " + d.getWeight());
						// //
						current.updateTransmissionEvent(obj);
					} else {
						storyline.addDemand(d);
						assert ((Integer) obj[3] > 0);
						current.startTransmissionEvent(obj);
					}
				}
			}
			// Avanza il tempo ed effettua il pop degli eventi
			time = findNextEvent();
			// Verify end condition
			ArrayList<Collective> toRemove = new ArrayList<Collective>();
			for (int i = 0; i < collectives.size(); ++i) {
				if (collectives.get(i).isEnd()) {
					toRemove.add(collectives.get(i));
				}
			}
			if (toRemove.size() > 0) {
				collectives.removeAll(toRemove);
			}
			System.out.println("finita iterazione in "
					+ (System.nanoTime() - start) / 1E9);
			iteration_counter++;
		}
		return storyline;
	}

	private Object[] toObjectArray(Object... objects) {
		Object array[] = new Object[objects.length];
		int counter = 0;
		for (Object i : objects) {
			array[counter++] = i;
		}
		return array;
	}

	private double findNextEvent() {
		ArrayList<Demand> toRemove = new ArrayList<Demand>();
		ArrayList<Demand> dem = demands.getDemands();
		double minimumTime = Integer.MAX_VALUE;
		for (Demand d : dem) {
			if (!d.isAllocated())
				continue;
			double currentTime = d.getEndTime();
			if (minimumTime > currentTime) {
				minimumTime = currentTime;
				toRemove = new ArrayList<Demand>();
				toRemove.add(d);
			} else if (minimumTime == currentTime) {
				toRemove.add(d);
			}

		}
		for (Demand d : toRemove) {
			Collective current = d.getCollective();
			Object obj[] = toObjectArray(d);
			;
			current.endTransmissionEvent(obj);
		}
		return minimumTime;
	}

	private String writeDotFile(DemandList demands, ArrayList<Integer[]> u,
			int[] allocation, int iteration_counter) throws IOException {
		String dot = "digraph mygraph {\n";
		int link = u.get(0).length;
		for (int i = 0; i < link; ++i) {
			boolean allocated = false;
			String[] sd = graph.edge(i);
			String support = "\t" + sd[0] + " -> " + sd[1];
			for (int j = 0; j < allocation.length; ++j) {
				if (u.get(j)[i] > 0) {
					if (!allocated) {
						allocated = true;
						support += " [comments=\" ";
					}
					support += "" + demands.getDemand(j).getReceiver() + ",";
					allocated = true;
				}
			}
			if (allocated) {
				support = support.substring(0, support.length() - 1) + "\"]";
			}
			dot += support + ";\n";
		}
		dot += "}\n";
		File res = new File(dotFile + "_" + iteration_counter);
		if (res.exists()) {
			res.delete();
		} else {
			res.createNewFile();
		}
		FileWriter fw = new FileWriter(res);
		fw.append(dot);
		fw.close();
		return dot;

	}

}
