package Lelann;

// Java imports
import java.util.Queue;
import java.util.LinkedList;
import java.util.Random;

// Visidia imports
import visidia.simulation.process.algorithm.Algorithm;
import visidia.simulation.process.messages.Door;

public class LelannMutualExclusion extends Algorithm {
    
    // All nodes data
    private int procId;
    private int next = 0;
    private static int step = 2;
    
    // Higher speed means lower simulation speed
    private int speed = 4;
    
    // Request to give response
    private int counter = 0;
    
    // Router
    MyRouter myRouter;
    
    //Test if the route map is complete
  	boolean myRouterIsComplete = true;
  	
    // Token 
    boolean token = false;

    // To display the state
    boolean waitForCritical = false;
    boolean inCritical = false;

    // Critical section thread
    ReceptionRules rr = null;
    // State display frame
    DisplayFrame df;

    public String getDescription() {

	return ("Lelann Algorithm for Mutual Exclusion");
    }

    @Override
    public Object clone() {
	return new LelannMutualExclusion();
    }

    //
    // Nodes' code
    //
    @Override
    public void init() {

	procId = getId();
	Random rand = new Random( procId );
	
	// Init routeMap
	myRouter = new MyRouter(getNetSize());
	myRouter.setDoorToMyRoute(getId(), -2);
	
	setRouteMap();
	
	try { Thread.sleep( 15000 ); } catch( InterruptedException ie ) {}
	
	while(!myRouterIsComplete){
		
		myRouterIsComplete = true;
		extendRouteMap();
	
	}
		
	if(myRouterIsComplete){
		
		df = new DisplayFrame( procId );
		displayState();
		try { Thread.sleep( 15000 ); } catch( InterruptedException ie ) {}
		while(true){
			
			Door d = new Door();
			WhereOrHere_IsMessage mr = recoitHereOrWhere(d);
			if(mr.type == MsgType.WHEREIS){
				
				if(myRouter.getDoorOnMyRoute(mr.ProcIdToFind) > -1){
					
					mr.addProcId(mr.ProcIdToFind);
					mr.type = MsgType.HEREIS;
					mr.step = 0;
					sendTo(d.getNum(), mr);
				}
			}
			
		}
		
	}
	
    }
    //--------------------
    // Rules
    //-------------------

    // Rule 0 : tell to all my neighbor where I am
    synchronized void setRouteMap() {

    	for(int i=0; i< getArity(); i++){
    		
    		RouteMessage mr = new RouteMessage(MsgType.ROUTE, getId());
    		boolean send = sendTo(i,mr);
    	}
    	
    	int i = 0;
    	while(i < getArity()){
    		
    		Door d = new Door();
    		RouteMessage mr = recoitRoute(d);
    		myRouter.setDoorToMyRoute(mr.procId, d.getNum());
    		i++;
    	}
    	setMyRouterIsComplete();
    	
    }
    
    // Rule 1 : Ask to neighbor if they know other processus
    synchronized void extendRouteMap(){
    	
    	for(int procId=0; procId< this.getNetSize(); procId++){
    		
    		if(myRouter.getDoorOnMyRoute(procId) == -1){
    			
    			WhereOrHere_IsMessage mr = new WhereOrHere_IsMessage(MsgType.WHEREIS, getId(), procId,0 );
    			sendWhereIsOrHereIs(mr, -1);
    			
            	Door d = new Door();
            	int door = -1;
            	mr = recoitHereOrWhere(d);
            	if(mr.type == MsgType.WHEREIS){
            		
            		
            		if(mr.myProcId != getId()){// While I'm not the initiator of this message, I accept it
            			
            			
            			if(myRouter.getDoorOnMyRoute(mr.ProcIdToFind) > -1){// If ProcIdToFind is connected to one of my doors
                			
                			mr.addProcId(mr.ProcIdToFind);
                			mr.type = MsgType.HEREIS;
                			mr.step = 0;
                			sendTo(d.getNum(), mr);
                			
                		}else{
                			
                			mr.step++;
                			sendWhereIsOrHereIs(mr, d.getNum());
                		}  
                		
            		}
            		
            	}else if(mr.type == MsgType.HEREIS){
            		
            		
            		for(int i=0; i<mr.getListProc().size(); i++){
            			
            			myRouter.setDoorToMyRoute(mr.getProcId(i), d.getNum());
            		}
            		
            		myRouter.setDoorToMyRoute(getId(), -2);
            		if(mr.myProcId != getId()){
            			
            			mr.step++;
            		    door = myRouter.getDoorOnMyRoute(mr.myProcId);
            		   
            		    if(door > -1){
            		    	
            		    	sendTo(door,mr);
            		    	
            		    }else{
            		    	
            		    	sendWhereIsOrHereIs(mr, d.getNum());
            		    }
            			
            		}
            		
            	}else{
            		
            		
            	}
        		
    		}
    		
    	}
    	setMyRouterIsComplete();
    	System.out.println("PROCESSUS "+getId());
    	if(myRouterIsComplete){
    		
    		System.out.println(+getId()+" PROCESSUS COMPLET");
    	}
    	for(int i=0; i< getNetSize(); i++){
			
    		if(myRouter.getDoorOnMyRoute(i) > -1){
    			
    			System.out.println("porte "+myRouter.getDoorOnMyRoute(i)+" connectee au proc "+i +"\n");
    		}
    	}
    } 
    
    // Rule 2 : ask for critical section
    synchronized void askForCritical() {

	while( !token ) { 
	    displayState();
	    try { this.wait(); } catch( InterruptedException ie) {}
	}
    }

    // Rule 3 : receive TOKEN
    synchronized void receiveTOKEN(int d){

	System.out.println("Process " + procId + " reveiced TOKEN from " + d );
	next = ( d == 0 ? 1 : 0 );

	if ( waitForCritical == true ) {

	    token = true;
	    displayState();
	    notify();

	} else {
	    // Forward token to successor
	    TokenMessage tm = new TokenMessage(MsgType.TOKEN);
	    boolean sent = sendTo( next, tm );
	}
    }

    // Rule 4 :
    void endCriticalUse() {

	token = false;
	TokenMessage tm = new TokenMessage(MsgType.TOKEN);
	boolean sent = sendTo( next, tm );

	displayState();
    }
    
    // Send message Where Is
    public void sendWhereIsOrHereIs(WhereOrHere_IsMessage mr, int exceptDoor){
    
		for(int i=0; i< getArity(); i++){
    		
			if(exceptDoor != i){
    			
        		boolean send = sendTo(i,mr);
    		}
    		
    	}
    }
    // Access to receive function
    public TokenMessage recoit ( Door d ) {

		TokenMessage sm = (TokenMessage)receive( d );
		return sm;
    }
    
 // Access to receive function
    public RouteMessage recoitRoute ( Door d ) {

    	RouteMessage rm = (RouteMessage)receive( d );
    	return rm;
    }
    
    
 // Access to receive function
    public WhereOrHere_IsMessage recoitHereOrWhere ( Door d ) {

    	WhereOrHere_IsMessage rm = (WhereOrHere_IsMessage)receive( d );
    	return rm;
    }
    
    // Test if the route map is complete
    public void setMyRouterIsComplete(){
    	
    	for(int i = 0; i < getNetSize(); i++){
    		
    		if(myRouter.getDoorOnMyRoute(i) == -1){
    			
    			myRouterIsComplete = false;
    		}
    	}
    	if(counter != 0){
    		
    		myRouterIsComplete = false;
    	}
    	
    	
    } 
    // Display state
    void displayState() {

	String state = new String("\n");
	state = state + "--------------------------------------\n";
	if ( inCritical ) 
	    state = state + "** ACCESS CRITICAL **\n";
	else if ( waitForCritical )
	    state = state + "* WAIT FOR *\n";
	else
	    state = state + "-- SLEEPING --\n";
	
	state = state + "#### Route processus "+getId()+" ######\n";
	for(int i=0; i< getNetSize(); i++){
			
		if(myRouter.getDoorOnMyRoute(i) > -1){
			
			state = state +"porte "+myRouter.getDoorOnMyRoute(i)+" connectee au proc "+i +"\n";
		}
	}

	df.display( state );
    }
}
