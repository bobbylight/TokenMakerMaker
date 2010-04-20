package org.fife.tmm;

import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import javax.swing.JComponent;

class Divider extends JComponent {

	private String text;


	public Divider(String text) {
		this.text = text;
	}


	public Dimension getPreferredSize() {
		FontMetrics fm = getFontMetrics(getFont());
		int width = fm.stringWidth(getText());
		int height = 25;
		return new Dimension(width, height);
	}


	public String getText() {
		return text;
	}


	protected void paintComponent(Graphics g) {

		super.paintComponent(g);

		String text = getText();
		g.setColor(Color.BLUE);
		Font font = javax.swing.UIManager.getFont("Label.font");
		FontMetrics fm = getFontMetrics(font);
		int titleWidth = fm.stringWidth(text);
		int middleY = getHeight()/2;
		int titleY = middleY + fm.getHeight()/2;

		ComponentOrientation orientation = getComponentOrientation();
		if (orientation.isLeftToRight()) {
			g.drawString(text, 0,titleY);
			g.setColor(getBackground().darker());
			g.drawLine(titleWidth+5, middleY, getWidth(), middleY);
		}
		else {
			int titleX = getWidth()-titleWidth-1;
			g.drawString(text, titleX,titleY);
			g.setColor(getBackground().darker());
			g.drawLine(0,middleY, titleX-5,middleY);
		}

	}


	public void setText(String text) {
		this.text = text;
		revalidate();
	}


}