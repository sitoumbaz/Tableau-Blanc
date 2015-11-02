package Lelann;

public class MyRouter {

	private int myRoute[];
	private boolean tabReady[];
	public int ready = 1;
	public int complete = 1;
	
	public MyRouter(int netSize){
		
		myRoute = new int[netSize];
		tabReady = new boolean[netSize];
		for(int i=0; i< netSize; i++){
			
			myRoute[i] = -1;
			tabReady[i] = false;
			
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
	
	
   public boolean[] getTabReady(){
		
		return tabReady;
	}

    public void ProcBecomeReady(int procId, boolean state){
		
    	tabReady[procId] = state;
	}
	
   public boolean getStateOfProc(int procId){
		
		return tabReady[procId];
	}
}
