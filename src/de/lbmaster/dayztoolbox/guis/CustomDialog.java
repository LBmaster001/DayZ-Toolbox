package de.lbmaster.dayztoolbox.guis;

import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;

public abstract class CustomDialog extends JFrame {

	private static final long serialVersionUID = 1L;

	public CustomDialog(String title) {
		setTitle(title);
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
			public boolean dispatchKeyEvent(KeyEvent e) {
				if (e.getID() == KeyEvent.KEY_PRESSED) {
					if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
						close();
					}
				}
				return false;
			}
		});
		System.out.println("Added KeyListener");
	}
	
	public void close() {
		setVisible(false);
	}
	
	protected ActionListener getDefaultCloseListener() {
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				close();
			}
		};
	}
}
