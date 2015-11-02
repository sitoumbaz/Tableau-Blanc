package Gui;

import java.awt.Point;
import java.util.Scanner;

public class MoteurTest {
	final int max = 200;
	final int min = 51;

	static Lanceur lanc;
	public MoteurTest(final Lanceur lanceur) {
		lanc = lanceur;
	}

	// On créer une forme de manière aléatoire
	// On commence par séléctionner la forme point, rectangle, elipse, ligne (
	// entre 0 et 3 )
	// ensuite on choisit le premier point a minimum 50 px de la bordure ( pour
	// eviter que la forme ne dépasse le cadre
	// pour finir on choisit le deuxieme point dans un rayon de 50 px du premier
	// pour eviter d'avoir de trop grosses formes qui prennent tout le tableau

	public void creerForme() {
		java.util.Random rand = new java.util.Random();

		int choixForme = rand.nextInt((3) + 1);
		System.out.println("forme : " + choixForme);

		int p1x = rand.nextInt((max - min) + 1) + min;
		int p1y = rand.nextInt((max - min) + 1) + min;
		Point p1 = new Point(p1x, p1y);
		System.out.println("point 1 : " + p1x + " : " + p1y);

		int p2x = rand.nextInt((p1x + min - p1x - min) + 1) + p1x - min;
		int p2y = rand.nextInt((p1y + min - p1y - min) + 1) + p1y - min;
		Point p2 = new Point(p2x, p2y);
		System.out.println("point 2 : " + p2x + " : " + p2y);

		lanc.ajouteForme(p1, p2, choixForme);
	}
	public void main(final String args[]) {

		Scanner scanner = new Scanner(System.in);

		for (;;) {
			creerForme();
			scanner.nextLine();
		}

	}
}
