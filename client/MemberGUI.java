package client.agent;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.server.ExportException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;

import entity.User;

public class MemberGUI {

	private JFrame frame;
	private JTextField serverHost;
	private JTextField serverPort;
	private JTextField clientHost;
	private JTextField clientPort;
	private JTextField userName;
	private RemoteClient client;
	private JButton btnLogin;
	private JList userJList;

	/**
	 * Create the application.
	 */
	public MemberGUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();

		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
					if(!btnLogin.isEnabled())
					client.logoff();
					System.exit(0);
			}
		});

		frame.setBounds(100, 100, 741, 441);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				0.0, 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		frame.getContentPane().setLayout(gridBagLayout);

		JPanel panel_2 = new JPanel();
		GridBagConstraints gbc_panel_2 = new GridBagConstraints();
		gbc_panel_2.gridwidth = 6;
		gbc_panel_2.insets = new Insets(0, 0, 0, 5);
		gbc_panel_2.fill = GridBagConstraints.BOTH;
		gbc_panel_2.gridx = 0;
		gbc_panel_2.gridy = 0;
		frame.getContentPane().add(panel_2, gbc_panel_2);
		GridBagLayout gbl_panel_2 = new GridBagLayout();
		gbl_panel_2.columnWidths = new int[] { 0, 0, 0, 0, 0 };
		gbl_panel_2.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_panel_2.columnWeights = new double[] { 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE };
		gbl_panel_2.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				Double.MIN_VALUE };
		panel_2.setLayout(gbl_panel_2);

		JLabel lblServer = new JLabel("Server IP");
		GridBagConstraints gbc_lblServer = new GridBagConstraints();
		gbc_lblServer.insets = new Insets(0, 0, 5, 5);
		gbc_lblServer.gridx = 1;
		gbc_lblServer.gridy = 2;
		panel_2.add(lblServer, gbc_lblServer);

		serverHost = new JTextField();
		serverHost.setText("localhost");
		GridBagConstraints gbc_txtLocalhost = new GridBagConstraints();
		gbc_txtLocalhost.insets = new Insets(0, 0, 5, 0);
		gbc_txtLocalhost.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtLocalhost.gridx = 3;
		gbc_txtLocalhost.gridy = 2;
		panel_2.add(serverHost, gbc_txtLocalhost);
		serverHost.setColumns(10);

		JLabel lblServerPort = new JLabel("Server Port");
		GridBagConstraints gbc_lblServerPort = new GridBagConstraints();
		gbc_lblServerPort.insets = new Insets(0, 0, 5, 5);
		gbc_lblServerPort.gridx = 1;
		gbc_lblServerPort.gridy = 3;
		panel_2.add(lblServerPort, gbc_lblServerPort);

		serverPort = new JTextField();
		serverPort.setText("19140");
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.insets = new Insets(0, 0, 5, 0);
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.gridx = 3;
		gbc_textField.gridy = 3;
		panel_2.add(serverPort, gbc_textField);
		serverPort.setColumns(10);

		JLabel lblClientIp = new JLabel("Client IP");
		GridBagConstraints gbc_lblClientIp = new GridBagConstraints();
		gbc_lblClientIp.insets = new Insets(0, 0, 5, 5);
		gbc_lblClientIp.gridx = 1;
		gbc_lblClientIp.gridy = 4;
		panel_2.add(lblClientIp, gbc_lblClientIp);

		clientHost = new JTextField();
		clientHost.setText("localhost");
		GridBagConstraints gbc_txtLocalhost_1 = new GridBagConstraints();
		gbc_txtLocalhost_1.insets = new Insets(0, 0, 5, 0);
		gbc_txtLocalhost_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtLocalhost_1.gridx = 3;
		gbc_txtLocalhost_1.gridy = 4;
		panel_2.add(clientHost, gbc_txtLocalhost_1);
		clientHost.setColumns(10);

		JLabel lblClientport = new JLabel("ClientPort");
		GridBagConstraints gbc_lblClientport = new GridBagConstraints();
		gbc_lblClientport.insets = new Insets(0, 0, 5, 5);
		gbc_lblClientport.gridx = 1;
		gbc_lblClientport.gridy = 5;
		panel_2.add(lblClientport, gbc_lblClientport);

		clientPort = new JTextField();
		clientPort.setText("8709");
		GridBagConstraints gbc_textField_1 = new GridBagConstraints();
		gbc_textField_1.insets = new Insets(0, 0, 5, 0);
		gbc_textField_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_1.gridx = 3;
		gbc_textField_1.gridy = 5;
		panel_2.add(clientPort, gbc_textField_1);
		clientPort.setColumns(10);

		JLabel lblUsername = new JLabel("UserName");
		GridBagConstraints gbc_lblUsername = new GridBagConstraints();
		gbc_lblUsername.insets = new Insets(0, 0, 5, 5);
		gbc_lblUsername.gridx = 1;
		gbc_lblUsername.gridy = 6;
		panel_2.add(lblUsername, gbc_lblUsername);

		userName = new JTextField();
		userName.setText("David");
		GridBagConstraints gbc_txtDavid = new GridBagConstraints();
		gbc_txtDavid.insets = new Insets(0, 0, 5, 0);
		gbc_txtDavid.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtDavid.gridx = 3;
		gbc_txtDavid.gridy = 6;
		panel_2.add(userName, gbc_txtDavid);
		userName.setColumns(10);

		JPanel panel_1 = new JPanel();
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.gridwidth = 9;
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 6;
		gbc_panel_1.gridy = 0;
		frame.getContentPane().add(panel_1, gbc_panel_1);
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[] { 0, 0 };
		gbl_panel_1.rowHeights = new int[] { 0, 0 };
		gbl_panel_1.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_panel_1.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		panel_1.setLayout(gbl_panel_1);

		userJList = new JList();
		GridBagConstraints gbc_list = new GridBagConstraints();
		gbc_list.fill = GridBagConstraints.BOTH;
		gbc_list.gridx = 0;
		gbc_list.gridy = 0;
		userJList.setCellRenderer(new CheckListRenderer());
		userJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		userJList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				JList list = (JList) event.getSource();
				int index = list.locationToIndex(event.getPoint());// Get index of item
																	// clicked
				CheckListItem item = (CheckListItem) list.getModel().getElementAt(index);
				item.setSelected(!item.isSelected()); // Toggle selected state
				list.repaint(list.getCellBounds(index, index));// Repaint cell
			}
		});
		panel_1.add(userJList, gbc_list);
		
		JButton btnLogoff = new JButton("Logoff");
		GridBagConstraints gbc_btnLogoff = new GridBagConstraints();
		gbc_btnLogoff.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnLogoff.insets = new Insets(0, 0, 5, 5);
		gbc_btnLogoff.gridx = 2;
		gbc_btnLogoff.gridy = 9;
		btnLogoff.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				client.logoff();
				System.exit(0);

			}
		});
		btnLogoff.setEnabled(false);
		panel_2.add(btnLogoff, gbc_btnLogoff);
		
		JButton btnInvite = new JButton("Invite");
		GridBagConstraints gbc_btnInvite = new GridBagConstraints();
		gbc_btnInvite.insets = new Insets(0, 0, 5, 5);
		gbc_btnInvite.gridx = 2;
		gbc_btnInvite.gridy = 10;
		btnInvite.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				List<User> gamers = new ArrayList<User>();
				gamers.add(client.getUser());
				for (int i = 0; i < userJList.getModel().getSize(); i++) {
					CheckListItem item = (CheckListItem) userJList.getModel().getElementAt(i);
					if (item.isSelected) {
						gamers.add(item.getUser());
					}
				}
				switch (client.startGame(gamers)) {
				case SERVERNOTALIVE:
					JOptionPane.showMessageDialog(frame, "Server is down");
					break;
				case GAMESTARTED:
					JOptionPane.showMessageDialog(frame, "Invitation failed, as game already started");
					break;
				case INVITATIONFAILED:
					JOptionPane.showMessageDialog(frame,
							"Invitation failed, as not all the players accepted the invitaion");
					break;
				case PLAYERDISCONNECTED:
					JOptionPane.showMessageDialog(frame, "Some player disconnected after accepting the invitation");
					break;
				default:
					break;

				}
			}
		});
		btnInvite.setEnabled(false);
		panel_2.add(btnInvite, gbc_btnInvite);
		

		btnLogin = new JButton("Login");
		GridBagConstraints gbc_btnLogin = new GridBagConstraints();
		gbc_btnLogin.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnLogin.insets = new Insets(0, 0, 5, 5);
		gbc_btnLogin.gridx = 2;
		gbc_btnLogin.gridy = 8;

		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				try {
					client.initialize(serverHost.getText(), Integer.parseInt(serverPort.getText()),
							clientHost.getText(), Integer.parseInt(clientPort.getText()), userName.getText());
					client.login();
					btnLogoff.setEnabled(true);
					btnInvite.setEnabled(true);
					btnLogin.setEnabled(false);
				} catch (NullPointerException e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(frame, "Object initialization failed");
				} catch (NumberFormatException e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(frame, "Plz type in the configuration in right format ");
				} catch(ExportException | AlreadyBoundException e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(frame, "IP and port alread in use");
				}
				catch (RemoteException  e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(frame, "Remote server is not alive ");
				} catch (Exception e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(frame, e1.getMessage());
				}
			}
		});

		panel_2.add(btnLogin, gbc_btnLogin);

		

		

	}

	public RemoteClient getClient() {
		return client;
	}

	public void setClient(RemoteClient client) {
		this.client = client;
	}

	public JFrame getFrame() {
		return frame;
	}

	public void setFrame(JFrame frame) {
		this.frame = frame;
	}

	public void showActiveUsers(Set<User> users, User client) {
		DefaultListModel<CheckListItem> dim = new DefaultListModel<CheckListItem>();
		for (User user : users) {
			if (!user.equals(client)) {
				dim.addElement(new CheckListItem(user));
			}
		}
		userJList.setModel(dim);
	}

	private class CheckListItem {

		private User user;
		private boolean isSelected = false;

		public CheckListItem(User user) {
			super();
			this.user = user;
		}

		public boolean isSelected() {
			return isSelected;
		}

		public void setSelected(boolean isSelected) {
			this.isSelected = isSelected;
		}

		public User getUser() {
			return user;
		}

		public void setUser(User user) {
			this.user = user;
		}

		@Override
		public String toString() {
			return user.getName() + "@" + user.getIp();
		}
	}

	private class CheckListRenderer extends JCheckBox implements ListCellRenderer {
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
				boolean hasFocus) {
			setEnabled(list.isEnabled());
			setSelected(((CheckListItem) value).isSelected());
			setFont(list.getFont());
			setBackground(list.getBackground());
			setForeground(list.getForeground());
			setText(value.toString());
			return this;
		}

	}
}
