package utility;

import java.util.ArrayList;

public class MyTokenizer {

	public static ArrayList<String> tokenize(String s) {
		ArrayList<String> result = new ArrayList<String>();
		char vector[] = s.toCharArray();
		String accumulator = "";
		for (int i = 0; i < s.length(); ++i) {
			if (vector[i] == ';') {
				if (accumulator.equalsIgnoreCase("")) {
					result.add(";");
					continue;
				} else {
					result.add(accumulator);
					accumulator = "";
					result.add(";");
					continue;
				}
			}
			if (vector[i] == ' ' || vector[i] == '\t') {
				if (accumulator.equalsIgnoreCase(""))
					continue;
				else {
					result.add(accumulator);
					accumulator = "";
					continue;
				}
			}
			if (vector[i] == '-') {
				if (!accumulator.equalsIgnoreCase("")) {
					result.add(accumulator);
					accumulator = "";
				}
				if (vector[i + 1] != '>') {
					return null;
				} else {
					result.add("->");
					i = i + 1;
					continue;
				}
			}
			if (vector[i] == '<') {
				if (!accumulator.equalsIgnoreCase("")) {
					result.add(accumulator);
					accumulator = "";
				}
				if (!(vector[i + 1] == '-' && vector[i + 2] == '>')) {
					return null;
				} else {
					result.add("<->");
					i += 2;
					continue;
				}
			} else {
				accumulator += vector[i];
			}
		}
		if (!accumulator.equalsIgnoreCase("")) {
			result.add(accumulator);
		}
		return result;
	}
}
