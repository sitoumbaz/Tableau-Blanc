package Lelann;

import java.awt.Color;
import java.awt.Point;

import visidia.simulation.process.messages.Message;

public class FormMessage extends Message {
    
    MsgType type;
    int procId;
    int nextProcId;
    Point point1;
    Point point2;
    float tailleForm;
    Color bg;
    Color fg;
    int typeForm;
    
    
    public FormMessage(MsgType t, int id, int idNext, Point p1, Point p2, float taille, int type_form, Color b, Color f) {
		
    	type = t;
    	procId = id;
    	nextProcId = idNext;
		point1 = p1;
		point2 = p2;
		tailleForm = taille;
		typeForm = type_form; 
		bg = b;
		fg = f;
    }

    public MsgType getMsgType() { return type; }
    
    @Override
    public Message clone() {
    	return new FormMessage(MsgType.FORME,procId,nextProcId,point1,point2,tailleForm,typeForm,bg,fg);
    }
    
    @Override 
    public String toString() {

	String r = "FORM";
	return r;
    }

    @Override 
    public String getData() {

	return this.toString();
    }

}
