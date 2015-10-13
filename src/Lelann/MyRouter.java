package Lelann;

public class MyRouter {

	private int myRoute[];
	
	public MyRouter(int netSize){
		
		myRoute = new int[netSize];
		for(int i=0; i< netSize; i++){
			
			myRoute[i] = -1;
		}
		
	}
	
	public void setDoorToMyRoute(int procId, int door){
		
		myRoute[procId] = door;
	}
	
	public int getDoorOnMyRoute(int procId){
		
		return myRoute[procId];
	}
	
	public int[] getMyRoute(){
		
		return myRoute;
	}
	 
}
