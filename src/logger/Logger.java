package logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

	/**
	 * Simple static Function which allow logging event when Simulation is running
	 * @return void 
	 * @param String the name of the log file
	 * @param String the message to write in the log file
	 * 
	 */
	public static void write(final String logFile, final String str){
		
		BufferedWriter bufWriter = null;
        FileWriter fileWriter = null;
        try {
            
        	Date date = new Date();
        	DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			String cu_date = dateFormat.format(date).toString();
			
        	fileWriter = new FileWriter("log/"+logFile+".log", true);
            bufWriter = new BufferedWriter(fileWriter);
            bufWriter.newLine();
            bufWriter.write(cu_date+"  "+str);
            bufWriter.flush();
            bufWriter.close();
        } 
        catch (IOException ex) {System.out.println("Erreur log : "+ex.getMessage());} 
        finally{
        	
        	try {
                bufWriter.close();
                fileWriter.close();
            } 
        	catch (IOException ex) { System.out.println("Erreur log : "+ex.getMessage()); }
        }
	}
	
}
