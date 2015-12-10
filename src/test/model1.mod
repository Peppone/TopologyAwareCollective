/*********************************************
 * OPL 12.6.2.0 Model
 * Author: peppone
 * Creation Date: 17/nov/2015 at 14:29:04
 *********************************************/
 using CP;
 
/****** INDICES ******/ 
int n_demand=...;
int n_vertex=...;
int n_edge=...;
int h_edge=...;
int n_receiver=...;
int n_sender=...;
/*********************/

/************ DEMANDS ************/
float max_bitrate[1..n_demand]=...;
float min_bitrate[1..n_demand]=...;
int demand [1..n_demand][1..3]=...;//Source / destination / weight
int ensure_allocation[1..n_demand]=...;
int receiver_edge[1..n_receiver]=...;
int sender_edge[1..n_sender]=...;

/**************** TOPOLOGY ****************/
int avertex [1..n_edge][1..n_vertex]=...;
int bvertex [1..n_edge][1..n_vertex]=...;
float max_capacity [1..n_edge]=...;
/******************************************/


/************VARIABLES************/
dvar int allocation[1..n_demand];
dvar int bitrate [1..n_demand];
dvar int u[1..n_demand][1..n_edge];
/*********************************/



/**************************OBJECTIVE**************************/
maximize sum(i in 1..n_demand)(allocation[i]*bitrate[i]*demand[i][3]-sum(j in 1..n_edge)u[i][j]);
/*************************************************************/

subject to {
	forall(i in 1..n_demand){
		ensure_allocation[i]<=allocation[i]<=1;
		min_bitrate[i]<=bitrate[i]<=max_bitrate[i];
		allocation[i]*bitrate[i]>=bitrate[i];
		allocation[i]*bitrate[i]>=allocation[i];	
		forall(j in 1..n_edge){
			0<=u[i][j]<=1;
			bitrate[i]*u[i][j]>=u[i][j];	
		}
	}
	
		forall(i in 1..n_demand){	
   						forall(j in 1..h_edge){
   						 u[i][2*j]+u[i][2*j-1]<2;
        				}   						 					
		}
	
		forall(i in 1..n_demand){
		forall(j in 1..n_vertex){
		
			if(j==demand[i][1]){
				sum(k in 1..n_edge)avertex[k][j]*u[i][k] - sum(k in 1..n_edge)bvertex[k][j]*u[i][k]== allocation[i];
  			}
  			else if(j==demand[i][2]){
  				sum(k in 1..n_edge)avertex[k][j]*u[i][k] - sum(k in 1..n_edge)bvertex[k][j]*u[i][k]== -allocation[i];  			
  			}
  			else{
   				sum(k in 1..n_edge)avertex[k][j]*u[i][k] - sum(k in 1..n_edge)bvertex[k][j]*u[i][k]== 0;
   			}  			  			
  					 
		}
	}
			
		forall(i in 1..n_edge){	
  			sum(j in 1..n_demand)bitrate[j]*u[j][i]<=max_capacity[i];
  		}
  		
  		//AVOID MULTI-RECEPTIONS for a single node
  		forall(j in 1..n_receiver)
  			sum(i in 1..n_demand)u[i][receiver_edge[j]]<=1;
  			
  		//AVOID MULTI-Sending for a single node
 		forall(j in 1..n_sender)
  			sum(i in 1..n_demand)u[i][sender_edge[j]]<=1;
  			
  		//AVOID Cycles
  		forall(d in 1..n_demand){
  			forall(v in 1..n_vertex){  		
   				sum(l in 1..n_edge)u[d][l]*avertex[l][v]<=1;
   				sum(l in 1..n_edge)u[d][l]*bvertex[l][v]<=1;
 				}  		  		
 		 }
 		 
 		 forall(d in 1..(n_demand-1)){
 		 	forall(f in (d+1)..n_demand){
 		 			 	if(demand[d][2]==demand[f][2]){
 		 			 	allocation[d]*allocation[f]==0; 		 			 	 		
 		 		} 		 	
 		 	} 		 
 		 }
 		  		
  		
}
