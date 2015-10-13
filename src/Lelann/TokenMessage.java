package Lelann;

import visidia.simulation.process.messages.Message;

public class TokenMessage extends Message {
    
    MsgType type;
    
    public TokenMessage(MsgType t) {
	type = t;
    }

    public MsgType getMsgType() { return type; }
    
    @Override
    public Message clone() {
	return new TokenMessage(MsgType.TOKEN);
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
