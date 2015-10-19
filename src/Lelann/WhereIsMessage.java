package Lelann;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import visidia.simulation.process.messages.Message;

public class WhereIsMessage extends Message {
    
    MsgType type;
    int myProcId;
    int hisProcId;
    ArrayList<Integer> listProc = new ArrayList<Integer>();
    
    public WhereIsMessage(MsgType t, int id1, int id2) {
    	
		type = t;
		myProcId = id1;
		hisProcId = id2;
	
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
    	return new WhereIsMessage(MsgType.WHEREIS, myProcId,hisProcId);
    }
    
    @Override 
    public String toString() {

		String r = "ROUTE("+myProcId+","+hisProcId+")";
		return r;
    }
    
    
    @Override 
    public String getData() {

	return this.toString();
    }

}
