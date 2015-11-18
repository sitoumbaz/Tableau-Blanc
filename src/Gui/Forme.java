package Gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;

/**
 * La classe Forme definit le contrat d'une forme a dessiner.
 * 
 * 
 */
public abstract class Forme {
	/** Le premier point de la forme. */
	protected Point p1 = null;
	/** Le second point de la forme (optionnel). */
	protected Point p2 = null;
	/** L'arriere plan. */
	protected Color bg;
	/** L'avant plan. */
	protected Color fg;
	/** L'��paisseur du trait. */
	protected float trait;

	/**
	 * M��thode de dessin de l'arriere plan.
	 * 
	 * @param g
	 *            Le contexte de le dessin.
	 */
	public abstract void dessineArrierePlan(Graphics2D g);

	/**
	 * M��thode de dessin de l'avant plan.
	 * 
	 * @param g
	 *            Le contexte de le dessin.
	 */
	public abstract void dessineAvantPlan(Graphics2D g);

	/**
	 * Retourne vrai si cette forme est d��finie par 2 points, faux pour un
	 * point.
	 * 
	 * @return vrai si cette forme est d��finie par 2 points, faux pour un
	 *         point.
	 */
	public abstract boolean aDeuxPoints();

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
	public Forme(final Color bg, final Color fg, final float trait) {
		this.bg = bg;
		this.fg = fg;
		this.trait = trait;
	}

	/**
	 * M��thode de dessin.
	 * 
	 * @param g
	 *            Le contexte de le dessin.
	 */
	public void paint(final Graphics2D g) {
		// sauvegarde du trait.
		Stroke sauveStroke = g.getStroke();
		// application de notre trait.
		g.setStroke(new BasicStroke(trait));

		// sauvegarge de la couleur courrante
		Color sauveColor = g.getColor();
		g.setColor(bg);
		// dessin de l'arriere plan
		dessineArrierePlan(g);
		g.setColor(fg);
		// dessin de l'avant plan
		dessineAvantPlan(g);
		// restauration de la couleur courrante
		g.setColor(sauveColor);

		// restauration du trait
		g.setStroke(sauveStroke);
	}

	/**
	 * D��finit le premier point de la forme.
	 * 
	 * @param p1
	 *            Le premier point de la forme.
	 */
	public void setPoint1(final Point p1) {
		this.p1 = p1;
	}

	/**
	 * D��finit le second point de la forme.
	 * 
	 * @param p2
	 *            Le second point de la forme.
	 */
	public void setPoint2(final Point p2) {
		this.p2 = p2;
	}
}
