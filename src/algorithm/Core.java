package algorithm;

import graph.Graph;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

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

	public Core(int edge, String resultFile, String dataFile,
			Collective... collective) throws IOException {
		/**
		 * public Core(int edge, String demandfile, String resultFile,
		 * Collective... collective) throws IOException
		 */
		collectives = new ArrayList<Collective>();
		// File demFile = new File(demandfile);
		// FileWriter fw = new FileWriter(demFile);
		// int demand = 0;
		this.edge = edge;
		for (Collective c : collective) {
			collectives.add(c);
			// String demands = c.generateDemands();
			// demand += demands.split("\r\n|\r|\n").length;
			// fw.append(c.generateDemands());
		}
		this.dataFile = dataFile;
		this.resultFile = resultFile;
		// u = new int[demand][edge];
		u = null;
		// u = new ArrayList<Integer[]>(demand);
		// this.resultFile = resultFile;
		//
		// fw.close();
		time = 0;
	}

	public void execute(Graph g) throws IOException, InterruptedException {

		// DEBUG
		int mycounter = 0;
		while (collectives.size() != 0 && mycounter <10) {
			// DEBUG
			mycounter++;

			demands = new DemandList();
			for (Collective c : collectives) {
				demands.merge(c.generateDemands());
			}
			System.err.println(mycounter+" "+demands.getN_demand());
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
				String res = esc.executeCommand(command, "-v", model, dataFile,
						resultFile);

				// Leggi l'ouput di CPLEX
				int allocation[] = new int[demands.getN_demand()];
				int bit_rate[] = new int[demands.getN_demand()];
				u = new ArrayList<Integer[]>(demands.getN_demand());
				for (int i = 0; i < demands.getN_demand(); ++i) {
					u.add(i, new Integer[edge]);
				}
				// Leggi tutti i dati che servono

				BufferedReader br = new BufferedReader(new StringReader(res));
				String alloc = br.readLine();
				String token[] = alloc.split("\\s+");
				for (int i = 0; i < token.length; ++i) {
					allocation[i] = Integer.parseInt(token[i]);
				}

				String bitRate = br.readLine();
				token = bitRate.split("\\s+");
				for (int i = 0; i < token.length; ++i) {
					bit_rate[i] = Integer.parseInt(token[i]);
				}
				int counter = 0;
				String uRow = null;
				while ((uRow = br.readLine()) != null) {
					// str = tokenize(uRow);

					token = uRow.split("\\s+");

					Integer u_temp[] = new Integer[edge];

					// Reads only if the demand is allocated
					if (allocation[counter] > 0) {
						for (int i = 0; i < token.length; ++i) {
							u_temp[i] = Integer.parseInt(token[i]);
						}
					}
					u.set(counter, u_temp);
					counter++;

				}
				br.close();

				// Comunica tutti i risultati alle varie collectives
				counter = collectives.get(0).getDemandNumber();
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
						current.startTransmissionEvent(obj);
					}
				}
			}
			// Avanza il tempo
			time = findNextEvent();
//			//Pop degli eventi
//			ArrayList<Demand> pop =new ArrayList<Demand>();
			
			// Verify end condition
			ArrayList<Collective> toRemove = new ArrayList<Collective>();
			for (int i = 0; i < collectives.size(); ++i) {
				if (collectives.get(i).isEnd()) {
					toRemove.add(collectives.get(i));
				}
			}
			if (toRemove.size() > 0)
				collectives.removeAll(toRemove);

		}
	}

	// private StringTokenizer tokenize(String string) {
	// return new StringTokenizer(string,
	// "abcdefghikjlmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ \t \r \n \\[ \\] = ;");
	// }

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
			Object obj[] = toObjectArray(d);			;
			current.endTransmissionEvent(obj);
		}
		return minimumTime;
	}

}
