package Lelann;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import visidia.simulation.process.messages.Message;

public class ExtendRouteMessage extends Message {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	MsgType type;
    int myProcId;
    int ProcIdToFind;
    int routingTable[];
    
    public ExtendRouteMessage(MsgType t, int id1, int id2) {
    	
		type = t;
		myProcId = id1;
		ProcIdToFind = id2;
	
    }
    
    public MsgType getMsgType() { return type; }
    
    @Override
    public Message clone() {
    	return new ExtendRouteMessage(MsgType.TABLE, myProcId,ProcIdToFind);
    }
    
    @Override 
    public String toString() {
    	
    	String r = "";
    	if(getMsgType().equals(MsgType.HEREIS)){
    		
    		r = "HEREIS("+myProcId+","+ProcIdToFind+")";
    	}
    	else if(getMsgType().equals(MsgType.TABLE)){
    		
    		r = "TABLE("+myProcId+","+ProcIdToFind+")";
    	}
        else{
        	
        	if(getMsgType().equals(MsgType.READY)){
        		
        		r = "READY("+myProcId+","+ProcIdToFind+")";
        	}
    	}
    	

		return r;
    }
    
    
    @Override 
    public String getData() {

	return this.toString();
    }

}
