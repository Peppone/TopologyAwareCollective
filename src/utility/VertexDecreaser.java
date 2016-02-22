package utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class VertexDecreaser {

	public static String decrease(File f) throws IOException {
		String s = "";
		BufferedReader br = new BufferedReader(new FileReader(f));
		s = br.readLine()+"\n";
		String line="";
		while ((line = br.readLine()) != null) {
			String token[] = line.split("[\\s+]");
			boolean first = true;
			for (String t : token) {
				if (t.equalsIgnoreCase(""))
					continue;
				int num;
				if (first) {
					first = false;
					
					String str = t.substring(0, t.length() - 1);
					num = Integer.parseInt(str);
					s += num - 1 + "; ";
				} else {
					String str = t.substring(0, t.length()>3?t.length()-1:t.length());
					num = Integer.parseInt(str);
					if (num == 100) {
						s += 100 + "; ";
					} else {
						s += num - 1 + " ";
					}
				}
			}
			s += "\n";

		}
		br.close();
		return s;
	}

	public static void main(String[] args) throws IOException {
		System.out.println(decrease(new File(args[0])));
	}
}
