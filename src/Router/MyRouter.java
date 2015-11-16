package Router;

public class MyRouter {

	/** Array which contain the correponce between the proc-Id and the door 
	 * door = myRoute[Proc-Id]
	 */
	private int myRoute[]; 
	
	/** Array which allow to proc-Id to know if all the other proc-Id
	 *  are ready before exiting in the process of establishing the Routing table
	 *  true/false = myRoute[Proc-Id]
 	 */
	private boolean tabReady[];
	
	/** Count number of message of type READY received by a proc-Id */
	public int ready = 1;
	
	/** Count how match route are in the Routing Table (myRoute) */
	public int complete = 1;
	
	
	/**
	 * Constructor
	 * @return void 
	 * @param the size of the network
	 * 
	 */
	public MyRouter(int netSize){
		
		myRoute = new int[netSize];
		tabReady = new boolean[netSize];
		for(int i=0; i< netSize; i++){
			
			/** init Array myRoute by -1 value */ 
			myRoute[i] = -1;
			
			/** init Array tabReady by false value */ 
			tabReady[i] = false;
			
		}
		
	}
	
	/**
	 * Adding door  for a procId
	 * @return void 
	 * @param integer value of the procId
	 * @param integer value of the door
	 * 
	 */
	public void setDoorToMyRoute(int procId, int door){
		
		myRoute[procId] = door;
	}
	
    
	/**
	 * Getting a door by procId given
	 * @return integer value of the door 
	 * @param integer value of the procId
	 * 
	 */
    public int getDoorOnMyRoute(int procId){
		
		return myRoute[procId];
	}
	
    /**
	 * Getting the array of the routing table
	 * @return integer array the routing table 
	 * 
	 */
	public int[] getMyRoute(){
		
		return myRoute;
	}
	
	
	/**
	 * Getting the array of the tabReady
	 * @return integer array tabReady 
	 */
   public boolean[] getTabReady(){
		
		return tabReady;
	}

   /**
	 * Adding state true of a procId, when I receive 
	 * the routing message READY
	 * @return void 
	 * @param integer value of the procId
	 * @param boolean value of the door (true)
	 * 
	 */
    public void ProcBecomeReady(int procId, boolean state){
		
    	tabReady[procId] = state;
	}
	
    /**
	 * Getting state by procId given
	 * @return integer value of the door 
	 * @param boolean value of the procId
	 * 
	 */
   public boolean getStateOfProc(int procId){
		
		return tabReady[procId];
	}
}
