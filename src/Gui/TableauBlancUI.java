package Gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.SpinnerNumberModel;

/**
 * L'interface utilisateur pour le tableau blanc.
 */
public class TableauBlancUI extends JFrame implements ActionListener,
		SelectionListener {


	private static final long serialVersionUID = 2902412616548012434L;

	/** Le canvas du tableau blanc. */
	public TableauBlanc canvas;
	/**
	 * Les boutons de formes:
	 * <ul>
	 * <li>0: pixel</li>
	 * <li>1: ligne</li>
	 * <li>2: carré</li>
	 * <li>3: ellipse</li>
	 * </ul>
	 */
	public JToggleButton boutons[] = new JToggleButton[4];
	/** L'arriere plan courrant. */
	protected Color bg = Color.WHITE;
	/** L'avant plan courrant. */
	protected Color fg = Color.BLACK;
	/** Le trait courant. */
	protected JSpinner spinnerTrait;

	/**
	 * Constructeur.
	 */
	public TableauBlancUI() {
		super("Tableau blanc");

		canvas = new TableauBlanc();

		getContentPane().add(canvas, BorderLayout.CENTER);
		getContentPane().add(getPanneauBoutons(), BorderLayout.WEST);

		pack();
		setVisible(true);
	}

	/**
	 * Creation des boutons.
	 * 
	 * @return Le panneau des boutons.
	 */
	protected JPanel getPanneauBoutons() {
		JPanel panneau = new JPanel(new BorderLayout());
		// les formes
		JPanel sPanneau = new JPanel(new GridLayout(2, 2));
		boutons[0] = createButton(0, ".", "Dessiner un pixel");
		boutons[1] = createButton(1, "/", "Dessiner une ligne");
		boutons[2] = createButton(2, "[]", "Dessiner un carré");
		boutons[3] = createButton(3, "()", "Dessiner une ellipse");
		for (int i = 0; i < boutons.length; i++)
			sPanneau.add(boutons[i]);
		panneau.add(sPanneau, BorderLayout.NORTH);
		// les propriétées
		JPanel sPanneau2 = new JPanel(new BorderLayout());
		spinnerTrait = new JSpinner(new SpinnerNumberModel(1, 1, 20, 1));
		sPanneau2.add(spinnerTrait, BorderLayout.NORTH);
		JPanel sPanneau3 = new JPanel(new GridLayout(1, 2));
		JButton bbg = new JButton("bg");
		bbg.setForeground(bg);
		bbg.setActionCommand("setcolor-bg");
		bbg.addActionListener(this);
		sPanneau3.add(bbg);
		JButton bfg = new JButton("fg");
		bfg.setForeground(fg);
		bfg.setActionCommand("setcolor-fg");
		bfg.addActionListener(this);
		sPanneau3.add(bfg);
		sPanneau2.add(sPanneau3, BorderLayout.SOUTH);

		panneau.add(sPanneau2, BorderLayout.SOUTH);
		return panneau;
	}

	/**
	 * Creation d'un bouton de forme.
	 */
	public JToggleButton createButton(int numBouton, String forme,
			String tooltip) {
		JToggleButton button = new JToggleButton(new ActionForme(numBouton,
				forme, tooltip));
		button.setPreferredSize(new Dimension(48, 48));
		button.addActionListener(this);
		return button;
	}

	/**
	 * Appelé lors d'un évenement "Action".
	 * 
	 * @param e
	 *            Description de l'évenement.
	 */
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd.startsWith("select-")) {
			int selected = -1;
			int numBouton = Integer.parseInt(cmd.substring(7));
			if (boutons[numBouton].getModel().isSelected())
				selected = numBouton;
			for (int i = 0; i < boutons.length; i++) {
				boutons[i].getModel().setSelected(false);
			}

			if (selected > -1)
				debutDessin(selected);
		} else if (cmd.startsWith("setcolor-")) {
			if (cmd.equals("setcolor-bg")) {
				bg = JColorChooser.showDialog(this,
						"Nouvelle couleur d'arrière plan", bg);
				((JComponent) e.getSource()).setForeground(bg);
			} else {
				fg = JColorChooser.showDialog(this,
						"Nouvelle couleur d'avant plan", fg);
				((JComponent) e.getSource()).setForeground(fg);
			}
		}
	}

	/**
	 * Debute un dession.
	 * 
	 * @param numForme
	 *            Le numéro de forme.
	 */
	public void debutDessin(int numForme) {
		for (int i = 0; i < boutons.length; i++)
			boutons[i].getModel().setEnabled(false);
		float trait = ((SpinnerNumberModel) spinnerTrait.getModel())
				.getNumber().floatValue();
		Forme forme = null;
		switch (numForme) {
		case 0:
			forme = new FormePixel(bg,fg,trait);
			break;	
		case 1:
			forme = new FormeLigne(bg,fg,trait);
			break;
		case 2:
			forme = new FormeRectangle(bg, fg, trait);
			break;
		case 3:
			forme = new FormeElipse(bg,fg,trait);
			break;
		}
		if (forme != null)
			canvas.demarreSelection(forme, this);
		else {
			System.out.println("Je ne connais pas la forme #" + numForme);
			for (int i = 0; i < boutons.length; i++)
				boutons[i].getModel().setEnabled(true);
		}
	}

	/**
	 * Affiche un message de selection.
	 * 
	 * @param msg
	 *            Le message.
	 */
	public void messageSelection(String msg) {
		System.out.println("Selection : " + msg);
	}

	/**
	 * Fin de la selection.
	 * 
	 * @param forme
	 *            La forme saisie.
	 */
	public void finDeSelection(Forme forme) {
		for (int i = 0; i < boutons.length; i++)
			boutons[i].getModel().setEnabled(true);

		// On devrais normalement passer par le groupe
		// ici on court-circuite le groupe
		// et on delivre directement la forme
		canvas.delivreForme(forme);
	}

	class ActionForme extends AbstractAction {

		private static final long serialVersionUID = 2428884176915830386L;

		public ActionForme(int numBouton, String forme, String tooltip) {
			super(forme);
			putValue(ACTION_COMMAND_KEY, "select-" + numBouton);
			putValue(SHORT_DESCRIPTION, tooltip);
		}

		public void actionPerformed(ActionEvent e) {
		}
	}
}
