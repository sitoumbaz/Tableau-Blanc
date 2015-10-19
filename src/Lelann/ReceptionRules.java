package Lelann;


// Visidia imports
import visidia.simulation.process.algorithm.Algorithm;
import visidia.simulation.process.messages.Door;

// Reception thread
public class ReceptionRules extends Thread {
    
    LelannMutualExclusion algo;
    
    public ReceptionRules( LelannMutualExclusion a ) {
	
	algo = a;
	
    }
    
    public void run() {
	
	Door d = new Door();

	while( true ) {

	    TokenMessage m = (TokenMessage) algo.recoit(d);
	    int door = d.getNum();

	    switch (m.getMsgType()) {
		
	    case TOKEN :
		algo.receiveTOKEN( door );
		break;

	    default:
		System.out.println("Error message type");
	    }
	}
    }
}

