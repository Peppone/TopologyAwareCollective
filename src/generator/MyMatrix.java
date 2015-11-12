package generator;

import java.lang.reflect.Array;

public class MyMatrix <T extends Number>{
	private T matrix[][];

	@SuppressWarnings("unchecked")
	public MyMatrix(T defaultValue, int row, int col) {
		matrix = (T[][])Array.newInstance(defaultValue.getClass(), row,col);
		for(int i=0;i<row;++i){
			for(int j=0;j<col;++j){
				matrix[i][j]=defaultValue;
			}
		}
		
	}
	
	public void set(int i, int j,T d){
		matrix[i][j]=d;
	}
	
	public T get(int i, int j){
		return matrix[i][j];
	}
	
	public String toString(){
		String tos = "[";
		for(int i=0;i<matrix.length;++i){
			tos+="[";
			for(int j=0;j<matrix[i].length-1;++j){
				tos+=""+matrix[i][j]+",";
			}
			tos+=""+matrix[i][matrix[i].length-1]+"]"
			+(i==matrix.length-1?"":",");
		}
		tos+="]";
		return tos;
	}

}
