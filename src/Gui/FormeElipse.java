
package Gui;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Implementation de Forme pour le dessin d'une elipse.
 */
public class FormeElipse extends Forme {

	public FormeElipse(Color bg, Color fg, float trait) {
		super(bg, fg, trait);
	}

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
	/**
	 * M��thode de dessin de l'arriere plan.
	 * 
	 * @param g
	 *            Le contexte de le dessin.
	 */
	public void dessineArrierePlan(Graphics2D g) {
		int x = Math.min(p1.x, p2.x);
		int y = Math.min(p1.y, p2.y);
		int width = Math.abs(p1.x - p2.x);
		int height = Math.abs(p1.y - p2.y);
		g.fillOval(x, y, width, height);
	}

	/**
	 * M��thode de dessin de l'avant plan.
	 * 
	 * @param g
	 *            Le contexte de le dessin.
	 */
	public void dessineAvantPlan(Graphics2D g) {
		int x = Math.min(p1.x, p2.x);
		int y = Math.min(p1.y, p2.y);
		int width = Math.abs(p1.x - p2.x);
		int height = Math.abs(p1.y - p2.y);
		g.drawOval(x, y, width, height);
	}

	/**
	 * Retourne vrai si cette forme est d��finit par 2 points, faux pour un
	 * point.
	 * 
	 * @return vrai si cette forme est d��finit par 2 points, faux pour un point.
	 */
	public boolean aDeuxPoints() {
		return true;
	}
}
