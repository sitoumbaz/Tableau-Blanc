package logger;

public class Logger {

	String log;
	final int procId;

	public Logger(final int procid) {
		this.procId = procid;
	}

	public void logMsg(final String mess) {

		log += mess + "\n";
		System.out.println("n°" + procId + " : " + mess);

	}

	public void sauvegarde() {

	}

}
