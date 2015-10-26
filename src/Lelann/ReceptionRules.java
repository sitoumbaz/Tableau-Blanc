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
			
			
			if((TokenMessage)algo.recoit(d) instanceof TokenMessage){
				
				TokenMessage m = (TokenMessage) algo.recoit(d);
				int door = d.getNum();

				switch (m.getMsgType()) {

					case TOKEN :
						algo.receiveTOKEN(m);
						break;

					default :
						System.out.println("Error message type");
				}
				
			}
			else if((FormMessage)algo.recoit(d) instanceof FormMessage){
				
				FormMessage m = (FormMessage) algo.recoit(d);
				int door = d.getNum();

				switch (m.getMsgType()) {

					case FORME :
						algo.receiveFormMessage(m);
						break;

					default :
						System.out.println("Error message type");
				}
				
			}else{
				
				System.out.println("Error message type");
			}
			
		}
	}
}
