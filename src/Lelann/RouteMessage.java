package Lelann;


import visidia.simulation.process.messages.Message;

public class RouteMessage extends Message {
    
    MsgType type;
    int procId;
    
    public RouteMessage(MsgType t, int id) {
    	
		type = t;
		procId = id;
	
    }

    public MsgType getMsgType() { return type; }
    
    @Override
    public Message clone() {
    	return new RouteMessage(MsgType.ROUTE, procId);
    }
    
    @Override 
    public String toString() {

		String r = "ROUTE("+procId+")";
		return r;
    }

    @Override 
    public String getData() {

	return this.toString();
    }

}
