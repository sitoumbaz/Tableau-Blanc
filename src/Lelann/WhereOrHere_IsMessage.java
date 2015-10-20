package Lelann;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import visidia.simulation.process.messages.Message;

public class WhereOrHere_IsMessage extends Message {
    
    MsgType type;
    int myProcId;
    int ProcIdToFind;
    int step;
    ArrayList<Integer> listProc = new ArrayList<Integer>();
    
    public WhereOrHere_IsMessage(MsgType t, int id1, int id2, int s) {
    	
		type = t;
		myProcId = id1;
		ProcIdToFind = id2;
		step = s;
	
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
    	return new WhereOrHere_IsMessage(MsgType.WHEREIS, myProcId,ProcIdToFind,step);
    }
    
    @Override 
    public String toString() {

		String r = "WhereOrHereIs("+myProcId+","+ProcIdToFind+","+step+")";
		return r;
    }
    
    
    @Override 
    public String getData() {

	return this.toString();
    }

}
