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
    int procId;
    int next = 0;
    // Higher speed means lower simulation speed
    int speed = 4;
    
    // Router
    MyRouter myRouter;
    
    Router allRoute;

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
	
	allRoute = new Router(getNetSize());
	
	
	setRouteMap();
	System.out.println("#### Route processus "+getId()+" ######");
	for(int i=0; i< getNetSize(); i++){
			
		if(myRouter.getDoorOnMyRoute(i) > -1){
			
			System.out.println("porte "+myRouter.getDoorOnMyRoute(i)+"  connecte au prcessus "+i);
		}
	}
	
	try { Thread.sleep( 15000 ); } catch( InterruptedException ie ) {}
	
	
	rr = new ReceptionRules( this );
	rr.start();

	
	// Display initial state + give time to place frames
	df = new DisplayFrame( procId );
	displayState();
	try { Thread.sleep( 15000 ); } catch( InterruptedException ie ) {}

	// Start token round
	if ( procId == 0 ) {
	    token = false;
	    TokenMessage tm = new TokenMessage(MsgType.TOKEN);
	    boolean sent = sendTo( next, tm );
	}

	while( true ) {
	    
	    // Wait for some time
	    int time = ( 3 + rand.nextInt(10)) * speed * 1000;
	    System.out.println("Process " + procId + " wait for " + time);
	    try {
		Thread.sleep( time );
	    } catch( InterruptedException ie ) {}
	    
	    // Try to access critical section
	    waitForCritical = true;
	    askForCritical();

	    // Access critical
	    waitForCritical = false;
	    inCritical = true;

	    displayState();

	    // Simulate critical resource use
	    time = (1 + rand.nextInt(3)) * 1000;
	    System.out.println("Process " + procId + " enter SC " + time);
	    try {
		Thread.sleep( time );
	    } catch( InterruptedException ie ) {}
	    System.out.println("Process " + procId + " exit SC ");

	    // Release critical use
	    inCritical = false;
	    endCriticalUse();
	}
    }

    //--------------------
    // Rules
    //-------------------

    // Rule 0 : set route map
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
    	
    }
    
    // Rule 1 : Where is
    synchronized void whereIs(){
    	
    	for(int procId=0; procId< this.getNetSize(); procId++){
    		
    		
    		for(int i=0; i< getArity(); i++){
        		
        		WhereIsMessage mr = new WhereIsMessage(MsgType.WHEREIS, getId(), procId );
        		boolean send = sendTo(i,mr);
        	}
        	
        	Door d = new Door();
        	int door = -1;
        	WhereIsMessage mr = recoitWhereIs(d);
        	if(mr.getListProc().size() > 0){
        		
        		for(int i= 0; i<mr.getListProc().size(); i++){
        			
        			if(allRoute.getgetMyRoute(getId(), procId) == -1){
        				
                		allRoute.setMyRoute(getId(), mr.getProcId(i) , d.getNum());
                	}
        			
        		}
        		
        	}
    	}
    	
    } 
    
    // Rule 1 : ask for critical section
    synchronized void askForCritical() {

	while( !token ) { 

	    displayState();
	    try { this.wait(); } catch( InterruptedException ie) {}
	}
    }

    // Rule 2 : receive TOKEN
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

    // Rule 3 :
    void endCriticalUse() {

	token = false;
	TokenMessage tm = new TokenMessage(MsgType.TOKEN);
	boolean sent = sendTo( next, tm );

	displayState();
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
    
    public WhereIsMessage recoitWhereIs( Door d ) {

    	WhereIsMessage rm = (WhereIsMessage)receive( d );
    	return rm;
    }

    // Display state
    void displayState() {

	String state = new String("\n");
	state = state + "--------------------------------------\n";
	if ( inCritical ) 
	    state = state + "** ACCESS CRITICAL **";
	else if ( waitForCritical )
	    state = state + "* WAIT FOR *";
	else
	    state = state + "-- SLEEPING --";

	df.display( state );
    }
}
