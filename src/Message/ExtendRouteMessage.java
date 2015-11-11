package Message;

import visidia.simulation.process.messages.Message;

public class ExtendRouteMessage extends Message {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	MsgType type;

	int ProcIdToFind;
	int routingTable[];

	public int[] getRoutingTable() {
		return routingTable;
	}

	public void setRoutingTable(final int[] routingTable) {
		this.routingTable = routingTable;
	}

	public ExtendRouteMessage(final MsgType t, final int id1, final int id2) {

		type = t;
		myProcId = id1;
		ProcIdToFind = id2;

	}

	public MsgType getMsgType() {
		return type;
	}

	@Override
	public Message clone() {
		return new ExtendRouteMessage(MsgType.TABLE, myProcId, ProcIdToFind);
	}

	@Override
	public String toString() {

		String r = "";
		if (getMsgType().equals(MsgType.TABLE)) {

			r = "TABLE(" + myProcId + "," + ProcIdToFind + ")";
		}
		if (getMsgType().equals(MsgType.READY)) {

			r = "READY(" + myProcId + "," + ProcIdToFind + ")";
		}

		return r;
	}

	public void setType(final MsgType type) {
		this.type = type;
	}

	int myProcId;
	public int getMyProcId() {
		return myProcId;
	}

	public void setMyProcId(final int myProcId) {
		this.myProcId = myProcId;
	}

	@Override
	public String getData() {

		return this.toString();
	}

}
