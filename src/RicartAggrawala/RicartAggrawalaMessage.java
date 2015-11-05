package RicartAggrawala;


import Message.MsgType;
import visidia.simulation.process.messages.Message;

public class RicartAggrawalaMessage extends Message {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	MsgType type;
    int procId;
    int procRecipient;
    
    public RicartAggrawalaMessage(MsgType t, int id1, int id2) {
    	
		type = t;
		procId = id1;
		procRecipient = id2;
	
    }

    public MsgType getMsgType() { return type; }
    
    @Override
    public Message clone() {
    	return new RicartAggrawalaMessage(MsgType.REQ, procId,procRecipient);
    }
    
    @Override 
    public String toString() {

		String r = "REQ("+procId+","+procRecipient+")";
		if(getMsgType() == MsgType.REL){
			
			r = "REL("+procId+","+procRecipient+")";
		}
		return r;
    }

    @Override 
    public String getData() {

	return this.toString();
    }

}
