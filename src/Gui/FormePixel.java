
package Gui;


import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Implementation de Forme pour le dessin d'un pixel.
 */
public class FormePixel extends Forme {

	/**
	 * Constructeur.
	 * 
	 * @param bg
	 *            L'arriere plan.
	 * @param fg
	 *            L'avant plan.
	 * @param trait
	 *            L'��paisseur du trait.
	 */
	public FormePixel(Color bg, Color fg, float trait) {
		super(bg, fg, trait);
	}
	
	/**
	 * M��thode de dessin de l'arriere plan.
	 * 
	 * @param g
	 *            Le contexte de le dessin.
	 */
	public void dessineArrierePlan(Graphics2D g) {
	}
	
	/**
	 * M��thode de dessin de l'avant plan.
	 * 
	 * @param g
	 *            Le contexte de le dessin.
	 */
	public void dessineAvantPlan(Graphics2D g) {
		int x = Math.min(p1.x, p1.x);
		int y = Math.min(p1.y, p1.y);
		int width = Math.abs(p1.x - p1.x);
		int height = Math.abs(p1.y - p1.y);
		g.drawRect(x, y, width, height);
	}
	
	/**
	 * Retourne vrai si cette forme est d��finit par 2 points, faux pour un
	 * point.
	 * 
	 * @return vrai si cette forme est d��finit par 2 points, faux pour un point.
	 */
	public boolean aDeuxPoints() {
		return false;
	}

}
