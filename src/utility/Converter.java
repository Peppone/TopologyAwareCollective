package utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

public class Converter {
	public static String convert (String filename) throws IOException{
		String result = "";
		File toBeConverted = new File (filename);
		BufferedReader br = new BufferedReader(new FileReader(toBeConverted));
		String line;
		while((line=br.readLine())!=null){
			String token[] = line.split("\\s+");
			for(int i=0;i<token.length;++i){
				if(token[i].equalsIgnoreCase("node"))break;
				if(token[i].equalsIgnoreCase("router"))continue;
				result+=Integer.parseInt(token[i])+1;
				if(i==1){
					result+="; ";
				}else{
					result +=" 100; ";
				}
			}
			result +='\n';
		}
		br.close();
		return result;
	}
	
	public static String convert2readablegraph(String filename) throws NumberFormatException, IOException{
		String result = "";
		File toBeConverted = new File (filename);
		BufferedReader br = new BufferedReader(new FileReader(toBeConverted));
		String line;
		while((line=br.readLine())!=null){
			int myCounter = 0;
			StringTokenizer st = new StringTokenizer(line,";");
			while(st.hasMoreTokens()){
				if(myCounter==0){
				result+=st.nextToken()+" -> {";
				myCounter ++;
				}else{
					result+=st.nextToken()+" ";
				}
			}
			result +="};\n";
		}
		br.close();
		result=result.replaceAll("100", "");
		return result;
	}
}
