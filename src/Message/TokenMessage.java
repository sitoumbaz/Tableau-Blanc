package Message;

import visidia.simulation.process.messages.Message;

public class TokenMessage extends Message {
    
    public MsgType type;
    public int idProc;
    
    public TokenMessage(MsgType t, int proc) {
		type = t;
		idProc = proc;
    }

    public MsgType getMsgType() { return type; }
    
    @Override
    public Message clone() {
    	return new TokenMessage(MsgType.TOKEN, idProc);
    }
    
    @Override 
    public String toString() {

	String r = "TOKEN";
	return r;
    }

    @Override 
    public String getData() {

	return this.toString();
    }

}
