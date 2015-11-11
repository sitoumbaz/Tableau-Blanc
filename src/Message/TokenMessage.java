package Message;

import visidia.simulation.process.messages.Message;

public class TokenMessage extends Message {
    
    public MsgType type;
<<<<<<< HEAD:src/Message/TokenMessage.java
    public int idProc;
=======
    private int idProc;
>>>>>>> 67440165a6b9183bbefc0481318466b53ea1452d:src/Message/TokenMessage.java
    
    public TokenMessage(MsgType t, int proc) {
		type = t;
		setIdProc(proc);
    }

    public MsgType getMsgType() { return type; }
    
    @Override
    public Message clone() {
    	return new TokenMessage(MsgType.TOKEN, getIdProc());
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

	public int getIdProc() {
		return idProc;
	}

	public void setIdProc(int idProc) {
		this.idProc = idProc;
	}

}
