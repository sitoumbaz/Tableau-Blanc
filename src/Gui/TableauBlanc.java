package Gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * La classe TableauBlanc d��finit un tableau blanc qui contient une liste de
 * forme �� dessiner.
 */
public class TableauBlanc extends JPanel {

	private static final long serialVersionUID = 4382875267309728710L;

	/** La dimension fixe de ce canvas. */
	private static final Dimension dim = new Dimension(256, 256);
	/** La liste des formes �� dessiner (dans l'ordre de dession) . */
	private LinkedList<Forme> formes = new LinkedList<Forme>();
	/** La couleur de fond du tableau blanc. */
	private Color bg = new Color(255, 255, 255);

	/**
	 * Constructeur.
	 */
	public TableauBlanc() {

		super(true);
	}

	/**
	 * M��thode de dessin.
	 * 
	 * @param g
	 *            Le contexte de le dessin.
	 */
	public void paint(final Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.clipRect(0, 0, dim.width, dim.height);
		// sauvegarde de la couleur courante.
		Color sauve = g2d.getColor();
		// Le fond
		g2d.setColor(bg);
		g2d.fillRect(0, 0, dim.width, dim.height);
		// restauration de la couleur courante.
		g2d.setColor(sauve);

		synchronized (formes) {
			// Chacune des formes �� la charge de son dessin
			Iterator<Forme> it = formes.iterator();
			while (it.hasNext()) {
				Forme forme = it.next();
				forme.paint(g2d);
			}
		}
	}

	/**
	 * Ajout d'une forme �� dessiner.
	 * 
	 * @param forme
	 *            La forme �� dessiner.
	 */
	public void delivreForme(final Forme forme) {
		synchronized (formes) {
			formes.add(forme);
		}
		// Cette m��thode sera appeler dans un thread cr���� par un object
		// distant. L'appel qui suit permet de d��l��guer la mise �� jour
		// du dessin au thread Swing.
		SwingUtilities.invokeLater(new Redessin(this));
	}

	/**
	 * D��marre une selection.
	 * 
	 * @param forme
	 *            la forme �� renseigner.
	 * @param sl
	 *            L'��couteur de selection.
	 */
	public void demarreSelection(final Forme forme, final SelectionListener sl) {
		SelectionThread selectionThread = new SelectionThread(this, sl, forme);
		selectionThread.start();
	}

	// force la dimension fixe
	public Dimension getMinimumSize() {
		return dim;
	}

	// force la dimension fixe
	public Dimension getMaximumSize() {
		return dim;
	}

	// force la dimension fixe
	public Dimension getPreferredSize() {
		return dim;
	}

	/**
	 * Thread g��rant la selection des coordonn��es d'une forme.
	 */
	private static class SelectionThread extends Thread
			implements
				MouseListener {
		/** Le premier point lors d'une selection. */
		private Point p1 = null;
		/** Le second point lors d'une selection. */
		private Point p2 = null;
		/** Le verrou sur le premier point. */
		private Object verrouP1 = new Object();
		/** Le verrou sur le second point. */
		private Object verrouP2 = new Object();
		/** Le num��ro du point en cours de selection. */
		private int numPoint = -1;
		/** La forme �� renseigner. */
		private Forme forme;
		/** Le composant propri��taire. */
		private Component proprio;
		/** L'��couteur de selection. */
		private SelectionListener sl;

		/**
		 * D��marre une selection.
		 * 
		 * @param proprio
		 *            Le composant propri��taire.
		 * @param sl
		 *            L'��couteur de selection.
		 * @param forme
		 *            la forme �� renseigner.
		 */
		public SelectionThread(	final Component proprio,
								final SelectionListener sl,
								final Forme forme) {
			this.proprio = proprio;
			this.forme = forme;
			this.sl = sl;
			proprio.addMouseListener(this);
		}

		/**
		 * Main() tu thread.
		 */
		public void run() {
			do {
				if (forme.aDeuxPoints())
					sl.messageSelection("S��lectionnez le premier point.");
				else
					sl.messageSelection("S��lectionnez le point.");
				synchronized (verrouP1) {
					numPoint = 1;
					if (p1 == null) {
						// On attend le point p1
						try {
							verrouP1.wait();
						} catch (InterruptedException e) {
							System.err.print("TableauBlanc::Selection::run(): Oh oh :");
							e.printStackTrace(System.err);
						}
					}
					numPoint = -1;
				}
			} while (p1 == null);
			forme.setPoint1(p1);

			if (forme.aDeuxPoints()) {
				do {
					sl.messageSelection("S��lectionnez le second point.");
					synchronized (verrouP2) {
						numPoint = 2;
						if (p2 == null) {
							// On attend le point p2
							try {
								verrouP2.wait();
							} catch (InterruptedException e) {
								System.err.print("TableauBlanc::Selection::run(): Oh oh :");
								e.printStackTrace(System.err);
							}
						}
						numPoint = -1;
					}
				} while (p2 == null);
				forme.setPoint2(p2);
			}
			proprio.removeMouseListener(this);
			sl.finDeSelection(forme);
		}

		// evenement souris
		public void mouseClicked(final MouseEvent e) {
			Point p = e.getPoint();
			if (numPoint == 1) {
				synchronized (verrouP1) {
					p1 = p;
					verrouP1.notifyAll();
				}
			} else if (numPoint == 2) {
				synchronized (verrouP2) {
					p2 = p;
					verrouP2.notifyAll();
				}
			}
		}

		// evenement souris
		public void mouseEntered(final MouseEvent e) {
		}

		// evenement souris
		public void mouseExited(final MouseEvent e) {
		}

		// evenement souris
		public void mousePressed(final MouseEvent e) {
		}

		// evenement souris
		public void mouseReleased(final MouseEvent e) {
		}

	}

	/**
	 * Classe permetant le dessin dans le thread Swing.
	 */
	private static class Redessin implements Runnable {
		private Component comp;

		public Redessin(final Component comp) {
			this.comp = comp;
		}

		public void run() {
			comp.repaint();
		}
	}
}
