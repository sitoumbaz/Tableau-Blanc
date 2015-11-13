package logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/*
 * Créer une fonction qui crée le dossier log
 * 
 */
public class Logger {

	
	public static void write(final String logFile, final String str){
		
		BufferedWriter bufWriter = null;
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter("log/"+logFile+".log", true);
            bufWriter = new BufferedWriter(fileWriter);
            //Insérer un saut de ligne
            bufWriter.newLine();
            bufWriter.write(str);
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
