package client.agent;

import java.util.regex.Pattern;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public class CellButton extends JButton {
	private static final long serialVersionUID = 1L;
	private static final String FILEPATH = "resource/";
	private String character;
	private int hIndex;
	private int vIndex;
	private static final Pattern pattern = Pattern.compile("[A-Za-z]");

	public CellButton(int hIndex, int vIndex) {
		super();
		this.hIndex = hIndex;
		this.vIndex = vIndex;
	}

	public void placeChar(String c) {
		this.setBackground(null);
		character = c;
		if (c != null && !c.isEmpty()) {
			character = c.toUpperCase();
			String icon = FILEPATH + character + ".jpg";
			ImageIcon pic = new ImageIcon(getClass().getResource(icon), character);
			setIcon(pic);
			System.out.println(getIcon());
		}
		else {
			this.setIcon(null);
		}
		this.setEnabled(false);
	}

	public void fill(String c) {
		if(!pattern.matcher(c).matches()) {
			throw new IllegalArgumentException("Illegal input character");
		}
		character = c.toUpperCase();
		String icon = FILEPATH + character + ".jpg";
		System.out.println(icon);
		ImageIcon pic = new ImageIcon(getClass().getResource(icon), character);
		setIcon(pic);
		System.out.println(getIcon());
		this.setEnabled(false);
	}

	public String getCharacter() {
		return character;
	}

	public void setCharacter(String character) {
		this.character = character;
	}

	public int gethIndex() {
		return hIndex;
	}

	public void sethIndex(int hIndex) {
		this.hIndex = hIndex;
	}

	public int getvIndex() {
		return vIndex;
	}

	public void setvIndex(int vIndex) {
		this.vIndex = vIndex;
	}

}
