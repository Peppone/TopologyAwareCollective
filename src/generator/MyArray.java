package generator;

import java.lang.reflect.Array;

public class MyArray <T extends Number>{

	private T array[];

	@SuppressWarnings("unchecked")
	public MyArray(T defaultValue,int length) {
		array = (T[])Array.newInstance(defaultValue.getClass(), length);
		for(int i=0;i<length;++i){
			array[i]=defaultValue;
		}
	}
	
	public void set(int i, T d){
		array[i]=d;
	}
	
	public T get(int i){
		return array[i];
	}
	
	public String toString(){
		String tos = "[";
		for(int i=0;i<array.length;++i){
			
			tos+=""+array[i]+(i==array.length-1?"":",");
		}
		tos+="]";
		return tos;
	}

}
