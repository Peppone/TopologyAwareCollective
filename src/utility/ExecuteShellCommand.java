package utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;


public class ExecuteShellCommand {

	
	public String executeCommand(String command, String option,String model, String data,String result) throws IOException, InterruptedException {


		ProcessBuilder pb = new ProcessBuilder(command,option,model,data);
		File resultFile = new File(result);
		if(!resultFile.exists())resultFile.createNewFile();
		pb.redirectOutput(resultFile);
		ProcessBuilder out1 = new ProcessBuilder("egrep", "allocation|bitrate|u = |\\[", result);
	//	in.redirectOutput(out.redirectInput());
		pb.redirectErrorStream(true);
	//	in.redirectErrorStream(true);
		out1.redirectErrorStream(true);
		//pb.redirectOutput(out.redirectInput());
		pb.environment()
				.put("LD_LIBRARY_PATH",
						"/home/peppone/ibm/ILOG/CPLEX_Studio1262/opl/bin/x86-64_linux/");
		Process p = pb.start();
		p.waitFor();
		//Process p1 = in.start();
		//p1.waitFor();
		Process p2 = out1.start();
		//Process p2=out.start();
		p2.waitFor();
		InputStream stdout = p2.getInputStream();
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(stdout));
		;
		String line;
		String res="";
		while ((line = reader.readLine()) != null) {
			StringTokenizer str=tokenize(line);
				while(str.hasMoreTokens()){
					res+=str.nextToken()+" ";
				}
				res+='\n';
		}
		System.out.print(res);
		return res;

	}
	
	private StringTokenizer tokenize(String string) {
		return new StringTokenizer(string,
				"abcdefghikjlmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ \t \r \n \\[ \\] = ;");
	}

}
