package de.lbmaster.dayztoolbox.guis;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;
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

	public CustomJButton(final String text, final Class<? extends CustomDialog> class1) {
		this(text, class1, (Icon) null);
	}

	public CustomJButton(final String text, final Class<? extends CustomDialog> class1, String iconLocation) {
		this(text, class1, loadIcon(iconLocation));
	}

	private static Icon loadIcon(String name) {
		if (name == null)
			return null;
		System.out.println("Loading Image " + name);
		try {
			URL resource = CustomJButton.class.getResource(name);
			Icon icon = new ImageIcon(resource);
			return icon;
		} catch (Exception e) {
			System.out.println("Failed to load image " + name);
			return null;
		}
	}

	public CustomJButton(final String text, final Class<? extends CustomDialog> class1, Icon image) {
		super();
		if (image == null) {
			setText(text);
		} else {
			setIcon(image);
			setFocusable(false);
		}

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
						dialog = class1.getDeclaredConstructor(String.class).newInstance(text);
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
