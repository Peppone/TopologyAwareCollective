package algorithm;

import graph.Graph;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
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
	private DemandList storyline;

	public Core(int edge, String resultFile, String dataFile,
			Collective... collective) throws IOException {
		/**
		 * public Core(int edge, String demandfile, String resultFile,
		 * Collective... collective) throws IOException
		 */
		collectives = new ArrayList<Collective>();
		this.edge = edge;
		for (Collective c : collective) {
			collectives.add(c);
		}
		this.dataFile = dataFile;
		this.resultFile = resultFile;
		u = null;
		time = 0;
	}

	public DemandList execute(Graph g) throws IOException, InterruptedException {
		// TODO: Migliorare la gestione dell'output file generato CPLEX. Fa
		// troppo schifo a causa del formato delle variabili.
		
		storyline = new DemandList();
		while (collectives.size() != 0) {

			demands = new DemandList();
			for (Collective c : collectives) {
				demands.merge(c.generateDemands());
			}

			if (demands.atLeastOneNotAllocatedDemand()) {
				File data = new File(dataFile);
				data.delete();
				FileWriter fw = new FileWriter(data);
				fw.write(g.writeCplexTrailer() + demands.writeCplexTrailer()
						+ g.writeCplexFooter() + demands.writeCplexFooter());
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

				// Comunica tutti i risultati alle varie collectives
				int counter = collectives.get(0).getDemandNumber();
				int collective = 0;
				ArrayList<Demand> allDemands = demands.getDemands();
				for (int i = 0; i < demands.getN_demand(); ++i) {
					if (i > counter) {
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
					;
					if (d.isAllocated()) {
						current.updateTransmissionEvent(obj);
					} else {
						storyline.addDemand(d);
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

}
