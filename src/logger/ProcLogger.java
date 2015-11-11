package logger;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/*
 * Créer une fonction qui crée le dossier log
 * 
 */
public class ProcLogger {

	FileHandler fh;
	String log;
	final int procId;

	Logger logger;

	public ProcLogger(final int procid, final String algo) {
		this.procId = procid;

		File dir = new File("log/");

		// attempt to create the directory here
		boolean successful = dir.mkdir();
		if (successful) {
			System.out.println("directory was created successfully : "
					+ dir.getAbsolutePath());
		} else {
			System.out.println("failed trying to create the directory");
		}

		logger = Logger.getLogger("MyLog");
		String logFile = algo + "_log_proc_" + procid;
		new File(logFile).delete();
		try {
			fh = new FileHandler("log/" + logFile, true);
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
