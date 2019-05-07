package client.agent;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.Timer;
import javax.swing.WindowConstants;

import entity.User;

public class GridGUI {

	private JFrame frame;
	private JList list;
	private RemoteClient remoteClient;
	private CellButton[][] buttons;
	private JButton passButton;
	private CellButton selectedButton;
	private int[][] selectedWord = new int[2][4];
	private JButton claimButton;
	private JButton fillButton;
	private JButton passClaimButton;
	private JComboBox comboBox_1;
	private boolean isFirstTurn = true;
	private JTextArea textArea;
	private Timer passTimer;
	private Timer claimTimer;
	private long currentTime;
	private JLabel label;
	private static final int TIMEOUT = 30;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GridGUI window = new GridGUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GridGUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		buttons = new CellButton[20][20];
		frame.setSize(1000, 834);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (remoteClient != null && remoteClient.getUser() != null)
					remoteClient.logoff();
				System.exit(0);
			}
		});
		frame.getContentPane().setLayout(null);

		currentTime = System.currentTimeMillis();
		passTimer = new Timer(1000, new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				long delay = (System.currentTimeMillis() - currentTime) / 1000;
				System.out.println("Pass timer: " + delay);
				label.setText( "Time left: " + Long.toString(TIMEOUT - delay));
				if (delay > TIMEOUT) {
					passTimer.stop();
					label.setText("");
					textArea.setText("You have run out of time");
					disableControls();
					if (isFirstTurn) {
						disableGrid();
					}
					remoteClient.pass();

				}
			}
		});

		claimTimer = new Timer(1000, new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				long delay = (System.currentTimeMillis() - currentTime) / 1000;
				System.out.println("Claim timer: " + delay);
				label.setText( "Time left: " + Long.toString(TIMEOUT - delay));
				if (delay > TIMEOUT) {
					claimTimer.stop();
					label.setText("");
					textArea.setText("You have run out of time");
					disableControls();
					remoteClient.passClaim();
				}
			}
		});

		JPanel panel = new JPanel();
		panel.setBounds(0, 0, 800, 800);
		frame.getContentPane().add(panel);
		panel.setLayout(new GridLayout(20, 20));

		JPanel panel_1 = new JPanel();
		panel_1.setBounds(800, 0, 200, 400);
		frame.getContentPane().add(panel_1);
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[] { 0, 0 };
		gbl_panel_1.rowHeights = new int[] { 0, 0 };
		gbl_panel_1.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_panel_1.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		panel_1.setLayout(gbl_panel_1);

		list = new JList();
		GridBagConstraints gbc_list = new GridBagConstraints();
		gbc_list.fill = GridBagConstraints.BOTH;
		gbc_list.gridx = 0;
		gbc_list.gridy = 0;

		panel_1.add(list, gbc_list);

		JPanel panel_2 = new JPanel();
		panel_2.setBounds(800, 399, 200, 401);
		frame.getContentPane().add(panel_2);
		panel_2.setLayout(null);

		passButton = new JButton("Pass Turn");
		passButton.setBounds(12, 202, 101, 25);
		passButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				passTimer.stop();
				label.setText("");
				disableControls();
				if (isFirstTurn) {
					disableGrid();
				}
				remoteClient.pass();
			}

		});
		passButton.setEnabled(false);
		panel_2.add(passButton);

		JButton button_1 = new JButton("Logoff");
		button_1.setBounds(12, 346, 80, 25);
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				remoteClient.logoff();
				System.exit(0);
			}

		});
		panel_2.add(button_1);

		String[] alphabet = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R",
				"S", "T", "U", "V", "W", "X", "Y", "Z" };
		JComboBox comboBox = new JComboBox(alphabet);
		comboBox.setBounds(12, 236, 85, 24);
		panel_2.add(comboBox);

		comboBox_1 = new JComboBox();
		comboBox_1.setBounds(12, 272, 176, 24);
		panel_2.add(comboBox_1);

		claimButton = new JButton("Claim");
		claimButton.setBounds(12, 308, 79, 25);
		claimButton.setEnabled(false);
		claimButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				claimTimer.stop();
				label.setText("");
				int index = comboBox_1.getSelectedIndex();
				if (index < 2) {
					remoteClient.claim(selectedWord[index][0], selectedWord[index][1], selectedWord[index][2],
							selectedWord[index][3]);
				} else {
					for (int i = 0; i < 2; i++) {
						remoteClient.claim(selectedWord[i][0], selectedWord[i][1], selectedWord[i][2],
								selectedWord[i][3]);
					}
				}
				disableControls();
				remoteClient.passClaim();
				
			}

		});
		panel_2.add(claimButton);

		fillButton = new JButton("Fill");
		fillButton.setBounds(109, 236, 79, 25);
		fillButton.setEnabled(false);
		fillButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					if (selectedButton == null || selectedButton.getCharacter() != null) {
						textArea.setText("Plz select an empty cell before\nfilling the character");
					} else {
						passTimer.stop();
						label.setText("");
						String input = (String) comboBox.getSelectedItem();
						selectedButton.fill(input);
						if (isFirstTurn) {
							disableGrid();
							isFirstTurn = false;
						}
						remoteClient.placeChar(selectedButton.gethIndex(), selectedButton.getvIndex(),
								selectedButton.getCharacter());
						fillButton.setEnabled(false);
						claimWord(selectedButton);
						textArea.setText("Plz select word(s) to claim in 30s");
						currentTime = System.currentTimeMillis();
						claimTimer.start();
					}
				} catch (RemoteException | NotBoundException e) {
					e.printStackTrace();
				}
			}

		});
		panel_2.add(fillButton);

		passClaimButton = new JButton("Pass");
		passClaimButton.setEnabled(false);
		passClaimButton.setBounds(109, 308, 79, 25);
		passClaimButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				claimTimer.stop();
				label.setText("");
				disableControls();
				remoteClient.passClaim();
			}
		});
		panel_2.add(passClaimButton);

		textArea = new JTextArea();
		textArea.setBounds(0, 12, 200, 74);
		panel_2.add(textArea);

		label = new JLabel("");
		label.setBounds(12, 82, 176, 51);
		panel_2.add(label);

		for (int i = 0; i < 20; i++)
			for (int j = 0; j < 20; j++) {
				CellButton element = new CellButton(i, j);
				element.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						try {
							if (selectedButton != null) {
								selectedButton.setBackground(null);
							}
							selectedButton = element;
							selectedButton.setBackground(Color.gray);
						} catch (NullPointerException | IllegalArgumentException ex) {
							JOptionPane.showMessageDialog(frame, "Plz enter a character in A-Z");
							ex.printStackTrace();
						} catch (Exception ex) {
							JOptionPane.showMessageDialog(frame, ex.getMessage());
							ex.printStackTrace();
						}
					}
				});
				buttons[i][j] = element;
				panel.add(element);
			}

	}

	public void refresh(String[][] grid) {
		isFirstTurn = true;
		disableControls();
		label.setText("");
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[i].length; j++) {
				buttons[i][j].placeChar(grid[i][j]);
			}
		}
		frame.setVisible(true);
	}

	public void refreshCells(int x, int y, String c) {
		buttons[x][y].placeChar(c);

		if (x - 1 >= 0 && buttons[x - 1][y].getCharacter() == null) {
			buttons[x - 1][y].setEnabled(true);
		}
		if (x + 1 < 20 && buttons[x + 1][y].getCharacter() == null) {
			buttons[x + 1][y].setEnabled(true);
		}
		if (y - 1 >= 0 && buttons[x][y - 1].getCharacter() == null) {
			buttons[x][y - 1].setEnabled(true);
		}
		if (y + 1 < 20 && buttons[x][y + 1].getCharacter() == null) {
			buttons[x][y + 1].setEnabled(true);
		}
		isFirstTurn = false;

	}

	public void startNewTurn() {
		textArea.setText("This is Your Turn.\nPlz place a new character in 30s");
		if (isFirstTurn) {
			for (int i = 0; i < 20; i++)
				for (int j = 0; j < 20; j++) {
					buttons[i][j].setEnabled(true);
				}
		}
		fillButton.setEnabled(true);
		passButton.setEnabled(true);
		currentTime = System.currentTimeMillis();
		passTimer.start();
	}

	private void claimWord(CellButton button) throws AccessException, RemoteException, NotBoundException {
		String horizontalStr = "";
		String verticalStr = "";
		int x = button.gethIndex();
		int y = button.getvIndex();
		int leftBound = x;
		int rightBound = x;
		int topBound = y;
		int botBound = y;

		// Horizontally----------------------------
		for (; leftBound >= 0; leftBound--) {
			if (buttons[leftBound][y].getCharacter() == null) {
				break;
			}
		}
		leftBound++;

		for (; rightBound < 20; rightBound++) {
			if (buttons[rightBound][y].getCharacter() == null) {

				break;
			}
		}
		rightBound--;

		for (int i = leftBound; i <= rightBound; i++) {
			horizontalStr += buttons[i][y].getCharacter();
		}

		// Vertically------------------------------

		for (; topBound >= 0; topBound--) {
			if (buttons[x][topBound].getCharacter() == null) {
				break;
			}
		}
		topBound++;

		for (; botBound < 20; botBound++) {
			if (buttons[x][botBound].getCharacter() == null) {

				break;
			}
		}
		botBound--;

		for (int i = topBound; i <= botBound; i++) {
			verticalStr += buttons[x][i].getCharacter();
		}
		selectedWord[0] = new int[] { leftBound, y, rightBound, y };
		selectedWord[1] = new int[] { x, topBound, x, botBound };

		comboBox_1.removeAllItems();
		comboBox_1.addItem(horizontalStr);
		comboBox_1.addItem(verticalStr);
		comboBox_1.addItem(horizontalStr + " && " + verticalStr);
		claimButton.setEnabled(true);
		passClaimButton.setEnabled(true);
		passButton.setEnabled(false);
	}

	public boolean vote(int x1, int y1, int x2, int y2) {
		String word = "";
		for (int i = x1; i <= x2; i++)
			for (int j = y1; j <= y2; j++) {
				word += buttons[i][j].getCharacter();
				buttons[i][j].setEnabled(true);
			}

		boolean flag = JOptionPane.showConfirmDialog(frame, "Do you agree this is a word: " + word + " ?", "WARNING",
				JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
		for (int i = x1; i <= x2; i++)
			for (int j = y1; j <= y2; j++) {
				word += buttons[i][j].getCharacter();
				buttons[i][j].setEnabled(false);
			}

		if (flag) {
			return true;
		} else {
			return false;
		}

	}

	public void refreshScoreBoard(Map<User, Integer> gamers) {
		DefaultListModel dim = new DefaultListModel();

		for (Entry<User, Integer> gamer : gamers.entrySet()) {
			dim.addElement(gamer.getKey().getName() + ": " + gamer.getValue());
		}
		list.setModel(dim);
	}

	public void disableGrid() {
		for (int i = 0; i < 20; i++)
			for (int j = 0; j < 20; j++) {
				buttons[i][j].setEnabled(false);
			}
	}

	public void disableControls() {
		passTimer.stop();
		claimTimer.stop();
		fillButton.setEnabled(false);
		claimButton.setEnabled(false);
		passButton.setEnabled(false);
		passClaimButton.setEnabled(false);

	}

	public JFrame getFrame() {
		return frame;
	}

	public void setFrame(JFrame frame) {
		this.frame = frame;
	}

	public RemoteClient getRemoteClient() {
		return remoteClient;
	}

	public void setRemoteClient(RemoteClient remoteClient) {
		this.remoteClient = remoteClient;
	}
}
