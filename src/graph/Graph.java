package graph;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import demand.Demand;
import demand.DemandList;

public class Graph {
	int [][] avertex;
	int [][] bvertex;
	int [] edge;
	ArrayList<Integer> sender_edge;
	ArrayList<Integer> receiver_edge;
	int n_vertex;
	int h_edge;
	int n_edge;
//	DemandList demands;
	
	@SuppressWarnings("unchecked")
	Graph(Graph g){
		//TODO Demands should be copied
		avertex=new int[g.avertex.length][];
		bvertex=new int[g.bvertex.length][];
		for(int i=0;i<avertex.length;++i){
			avertex[i]=g.avertex[i].clone();
			bvertex[i]=g.bvertex[i].clone();
		}
		edge = new int [g.edge.length];
		edge = g.edge.clone();
		n_vertex = g.n_vertex;
		h_edge = g.h_edge;
		n_edge = g.n_edge;
		sender_edge = (ArrayList<Integer>) g.sender_edge.clone();
		receiver_edge = (ArrayList<Integer>) g.receiver_edge.clone();
		sender_edge=new ArrayList<Integer>();
		receiver_edge=new ArrayList<Integer>();
//		for(int i=0;i<avertex.length;++i){
//			int se =findEndNodeEdge(i,avertex);
//			if(se>-1) {
//				sender_edge.add(i);
//				receiver_edge.add(i);
//			}
//		}
//		demands=new DemandList();
		
	}
	
	
	
	public Graph(String adjMatrixFile, String demandFile){	
			try {
				readAdjMatrixFromFile(adjMatrixFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
//			try {
//				readDemandsFromFile(demandFile);
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
			
			sender_edge=new ArrayList<Integer>();
			receiver_edge=new ArrayList<Integer>();
			for(int i=0;i<avertex[0].length;++i){
				int se =findEndNodeEdge(i,avertex);
				if(se>-1) {
					sender_edge.add(se);
				}
			}
			for(int i=0;i<bvertex[0].length;++i){
				int se =findEndNodeEdge(i,bvertex);
				if(se>-1) {
					receiver_edge.add(se);
				}
			}
//			demands=new DemandList();
	}
	
	private String writeMatrix(String name, int mat[][]){
		String matrix=name+" = [";
		for(int i=0;i<mat.length;++i){
			matrix+="[ ";
			for(int j=0;j<mat[i].length;++j){
				matrix+=mat[i][j]+" ";
			}
			matrix+="] ";
		}
		matrix+="];\n";
		return matrix;
	}
	
	private String writeVector(String name, int vec[]){
		String vector=name+" = [";
		for(int i=0;i<vec.length;++i){
			vector+=" "+vec[i];
		}
		vector+="];\n";
		return vector;
	}
	
	
	private String writeArrayList(String name, ArrayList<Integer> al){
		String vector=name+" = [";
		for(int i=0;i<al.size();++i){
			vector+=" "+(al.get(i));
		}
		vector+="];\n";
		return vector;
	}
	public String writeCplexCode(){
		String code="";
		code+="n_vertex = "+n_vertex+";\n";
		code+="n_edge = "+n_edge+";\n";
		code+="h_edge = "+h_edge+";\n";
//		code+="n_demand= "+demands.getN_demand()+";\n";
		code+=writeMatrix("avertex",avertex);
		code+=writeMatrix("bvertex",bvertex);
		code+=writeVector("edge",edge);
//		code+=demands.writeCplexCode();
		code+=writeArrayList("sender_edge",sender_edge);
		code+=writeArrayList("receiver_edge",receiver_edge);
		
		return code;	
	}
	
	private void readAdjMatrixFromFile(String filename) throws IOException{
		File adjMatrixFile = new File (filename);
		BufferedReader br = new BufferedReader(new FileReader(adjMatrixFile));
		String line=br.readLine();
		StringTokenizer st = new StringTokenizer(line);
		n_vertex = st.countTokens();
		n_edge=0;
		
		int adjMatrix [][] = new int [n_vertex][];
		for(int i=0; i<n_vertex;++i){
			adjMatrix[i]=new int [n_vertex];
		}
		int counter =0;
		for(int i=0;i<n_vertex;++i){
			
			Integer value =Integer.parseInt(st.nextToken());
			adjMatrix[counter][i]=value;
			if(value>0)n_edge++;
		}
		counter ++;
		while((line=br.readLine())!=null){
			st = new StringTokenizer(line);
			for(int i=0;i<n_vertex;++i){
				Integer value =Integer.parseInt(st.nextToken());
				adjMatrix[counter][i]=value;
				if(value>0)	n_edge++;
			}
			counter ++;	
		}

		avertex =new int [n_edge][n_vertex];
		bvertex= new int [n_edge][n_vertex];
		edge = new int [n_edge];
		int edgeCounter=0;
		for(int i=0;i<n_edge;++i){
			avertex[i]=new int[n_vertex];
			bvertex[i]=new int[n_vertex];
		}
		for(int i=0;i<n_edge;++i){
			for(int j=i+1;j<n_vertex;++j){
				if (adjMatrix[i][j] > 0) {
					avertex[edgeCounter][i]= 1;
					bvertex[edgeCounter][j]= 1;
					edge[edgeCounter]=adjMatrix[i][j];
					edgeCounter++;
				}
				if (adjMatrix[j][i] > 0) {
					avertex[edgeCounter][j]= 1;
					bvertex[edgeCounter][i]= 1;
					edge[edgeCounter]=adjMatrix[j][i];
					edgeCounter++;
				}
			}
		}
		h_edge = n_edge>>1;
		br.close();
	}
	
//	private void readDemandsFromFile(String filename) throws IOException{
//		File demandFile = new File (filename);
//		BufferedReader br = new BufferedReader(new FileReader(demandFile));
//		String line;
//		demands= new DemandList();
//		while((line=br.readLine())!=null){
//			StringTokenizer st = new StringTokenizer(line);
//			int source = Integer.parseInt(st.nextToken());
//			int dest = Integer.parseInt(st.nextToken());
//			int min_br = Integer.parseInt(st.nextToken());
//			int max_br = Integer.parseInt(st.nextToken());
////			int rEdge = findEndNodeEdge(dest,bvertex);
////			int sEdge = findEndNodeEdge(source,avertex);
//			Demand d = new Demand(source,dest,/*sEdge,rEdge,*/min_br,max_br,false,0,0);
//			demands.addDemand(d);
//		}
//		br.close();
//	}
	
	public int findEndNodeEdge(int vertex,int[][]matrix){
		int counter=0;
		int position=-1;
		for(int i=0;i<n_edge;++i){
			if(matrix[i][vertex]==1){
				counter++;
				position=i;
			}
		}
		if (counter ==1)return position+1;
		else return -1;
		
	}
}
