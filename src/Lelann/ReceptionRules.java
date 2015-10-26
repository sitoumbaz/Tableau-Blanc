package Lelann;

import visidia.simulation.process.messages.Door;

// Reception thread
public class ReceptionRules extends Thread {

	LelannMutualExclusion algo;

	public ReceptionRules(final LelannMutualExclusion a) {

		algo = a;

	}

	@Override
	public void run() {

		Door d = new Door();

		while (true) {

			TokenMessage m = algo.recoit(d);
			int door = d.getNum();

			switch (m.getMsgType()) {

				case TOKEN :
					algo.receiveTOKEN(door);
					break;

				default :
					System.out.println("Error message type");
			}
		}
	}
}
