package logger;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class ProcLogger {

	FileHandler fh;
	String log;
	final int procId;

	Logger logger;

	public ProcLogger(final int procid) {
		this.procId = procid;

		logger = Logger.getLogger("MyLog");
		new File("log_proc_" + procid).delete();
		try {
			fh = new FileHandler("log_proc_" + procid, true);
			logger.addHandler(fh);
			logger.setLevel(Level.ALL);
			BriefFormatter formatter = new BriefFormatter();
			fh.setFormatter(formatter);

		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public void logMsg(final String mess) {

		logger.log(Level.INFO, mess);

		System.out.println("n°" + procId + " : " + mess);

	}

	public void close() {
		fh.close();

	}

	public class BriefFormatter extends Formatter {
		public BriefFormatter() {
			super();
		}

		@Override
		public String format(final LogRecord record) {
			return record.getMessage() + "\n";
		}
	}
}
