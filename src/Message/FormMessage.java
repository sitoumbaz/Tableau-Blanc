package Message;

import java.awt.Color;
import java.awt.Point;

import visidia.simulation.process.messages.Message;

public class FormMessage extends Message {
    
    /** The type of the message, in this case the type is FORME */
	public MsgType type;
	
	/** The proc-Id of the process who create the form */
    public int procId;
    
    /** The proc-Id to whom the form is intended */
    public int nextProcId;
    
    /** The first point of the form  */
    public Point point1;
    
    /** The the second point of the form  */
    public Point point2;
    
    /** The the size of the form  */
    public float tailleForm;
    
    /** Background color of the form  */
    public Color bg;
    
    /** Foreground color of the form  */
    public Color fg;
    
    /** The the kind of the form to be drawn circle, rectangle, square, dot or line  */
    public int typeForm;
    
    
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
