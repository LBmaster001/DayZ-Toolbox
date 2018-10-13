package de.lbmaster.dayztoolbox.guis;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.lbmaster.dayztoolbox.guis.mapeditorgui.MapEditorGui;

public class CustomJButton extends JButton {

	private static final long serialVersionUID = 1L;

	private Color pressedColor = Color.GREEN;
	private Color rolloverColor = Color.RED;
	private Color normalColor = Color.BLUE;

	protected CustomDialog dialog;

	public CustomJButton(String text, final Class<? extends CustomDialog> class1) {
		super(text);

		// setBorder(null);

		setOpaque(true);
		setBackground(normalColor);
		setForeground(Color.BLACK);

		setFocusPainted(false);

		setFont(new Font("Monospaced", Font.PLAIN, 20));

		addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (dialog == null)
					try {
						dialog = class1.getDeclaredConstructor(String.class).newInstance(getText());
					} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e1) {
						e1.printStackTrace();
					}
				if (dialog != null) {
					dialog.setVisible(true);
					dialog.requestFocus();
					if (dialog != null && dialog instanceof MapEditorGui) {
						((MapEditorGui) dialog).loadMe();
					}
				}
			}
		});

		addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent evt) {
				if (getModel().isPressed()) {
					setBackground(pressedColor);
				} else if (getModel().isRollover()) {
					setBackground(rolloverColor);
				} else {
					setBackground(normalColor);
				}
			}
		});
	}
}
