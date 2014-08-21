package org.sigaim.csig.view;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BorderFactory;
import javax.swing.JTextArea;

public class MantainCaretFocusListener implements FocusListener {

	public void focusLost(FocusEvent e) {
		JTextArea source = (JTextArea) e.getSource();
		source.getCaret().setVisible(true);
		source.getCaret().setBlinkRate(0);
		source.setBorder(BorderFactory.createLineBorder(Color.gray, 1));
	}
	
	public void focusGained(FocusEvent e) {
		JTextArea source = (JTextArea) e.getSource();
		source.getCaret().setVisible(false);
		source.getCaret().setBlinkRate(400);
		source.getCaret().setVisible(true);
		source.setBorder(BorderFactory.createLineBorder(Color.blue, 1));
	}
	
	public void addTextArea(JTextArea txt) {
		txt.setBorder(BorderFactory.createLineBorder(Color.gray, 1));
		txt.addFocusListener(this);
	}
}
