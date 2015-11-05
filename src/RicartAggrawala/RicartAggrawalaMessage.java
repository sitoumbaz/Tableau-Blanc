package RicartAggrawala;

import visidia.simulation.process.messages.Message;
import Message.MsgType;

public class RicartAggrawalaMessage extends Message {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	MsgType type;
	int procId;
	int procRecipient;
	int H;

	public RicartAggrawalaMessage(	final MsgType t,
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
		return new RicartAggrawalaMessage(MsgType.REQ, procId, procRecipient, H);
	}

	@Override
	public String toString() {

		String r = "REQ(" + procId + "," + procRecipient + ")";
		if (getMsgType() == MsgType.REL) {

			r = "REL(" + procId + "," + procRecipient + ")";
		}
		return r;
	}

	@Override
	public String getData() {

		return this.toString();
	}

}
