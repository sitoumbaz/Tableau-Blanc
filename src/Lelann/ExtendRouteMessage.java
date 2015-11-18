package Lelann;

import visidia.simulation.process.messages.Message;
import Message.MsgType;

/**
 * 
 * Classe permettant d'envoyer des messages en rapport avec la création de la
 * table de routage
 */
public class ExtendRouteMessage extends Message {

	private static final long serialVersionUID = 1L;
	/** Type de message */
	MsgType type;
	/** Id du processeur */
	int myProcId;
	/** Id du processeur qu'on cherche */
	int ProcIdToFind;
	/** La table de routage */
	int routingTable[];

	/**
	 * 
	 * @param t
	 *            Type du message
	 * @param id1
	 *            Id du processus courant
	 * @param id2
	 *            Id du processus à charger
	 */
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

	@Override
	public String getData() {

		return this.toString();
	}

}
