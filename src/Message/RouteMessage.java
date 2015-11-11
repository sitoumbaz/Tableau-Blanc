package Message;

import visidia.simulation.process.messages.Message;

public class RouteMessage extends Message {

	MsgType type;
	int procId;

	public RouteMessage(final MsgType t, final int id) {

		type = t;
		procId = id;

	}

	public MsgType getMsgType() {
		return type;
	}
	public int getProcId() {
		return procId;
	}

	public void setProcId(final int procId) {
		this.procId = procId;
	}

	@Override
	public Message clone() {
		return new RouteMessage(MsgType.ROUTE, procId);
	}

	@Override
	public String toString() {

		String r = "ROUTE(" + procId + ")";
		return r;
	}

	@Override
	public String getData() {

		return this.toString();
	}

}
