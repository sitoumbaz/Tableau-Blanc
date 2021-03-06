package Message;

import visidia.simulation.process.messages.Message;

public class RulesMessage extends Message {

	private static final long serialVersionUID = 1L;
	
	/** The type of the message, in this case the type is REQ, REL or TOKEN */
	MsgType type;
	
	/** The proc-Id of the message creator */
	public int procId;
	
	/** The proc-Id to whom the form is intended */
	public int procRecipient;
	
	/** The Lamport clock */
	public int H;

	public RulesMessage(	final MsgType t,
									final int id1,
									final int id2,
									final int h) {

		type = t;
		procId = id1;
		procRecipient = id2;
		H = h;

	}

	public MsgType getMsgType() {
		return type;
	}

	@Override
	public Message clone() {
		return new RulesMessage(MsgType.REQ, procId, procRecipient, H);
	}

	@Override
	public String toString() {

		String r = "REQ(" + procId + "," + procRecipient + ")";
		if (getMsgType() == MsgType.REL) {

			r = "REL(" + procId + "," + procRecipient + ")";
		}
		else if(getMsgType() == MsgType.TOKEN){
			
			r = "JETON()";
		}
		return r;
	}

	@Override
	public String getData() {

		return this.toString();
	}

}
