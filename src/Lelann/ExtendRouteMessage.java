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
    ArrayList<Integer> listProc = new ArrayList<Integer>();
    
    public ExtendRouteMessage(MsgType t, int id1, int id2) {
    	
		type = t;
		myProcId = id1;
		ProcIdToFind = id2;
	
    }

    public void addProcId(int procId){
  
    	listProc.add(procId);
    }
    
    public int getProcId(int index){
    	
    	return listProc.get(index);
    }
    
    public ArrayList<Integer> getListProc(){
    	
    	return listProc;
    }
    
    public MsgType getMsgType() { return type; }
    
    @Override
    public Message clone() {
    	return new ExtendRouteMessage(MsgType.WHEREIS, myProcId,ProcIdToFind);
    }
    
    @Override 
    public String toString() {
    	
    	String r = "";
    	if(getMsgType().equals(MsgType.HEREIS)){
    		
    		r = "HEREIS("+myProcId+","+ProcIdToFind+")";
    	}
    	else if(getMsgType().equals(MsgType.WHEREIS)){
    		
    		r = "WHEREIS("+myProcId+","+ProcIdToFind+")";
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
