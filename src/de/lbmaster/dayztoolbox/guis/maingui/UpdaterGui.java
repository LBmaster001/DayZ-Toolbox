package de.lbmaster.dayztoolbox.guis.maingui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import de.lbmaster.dayztoolbox.Constants;
import de.lbmaster.dayztoolbox.MainClass;
import de.lbmaster.dayztoolbox.utils.Config;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class UpdaterGui extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();

	public UpdaterGui() {
		setSize(450, 150);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		JLabel lblUpdateAvailable = new JLabel("Update available !");
		lblUpdateAvailable.setHorizontalAlignment(SwingConstants.CENTER);
		lblUpdateAvailable.setFont(new Font(Constants.FONT, Font.BOLD, 25));
		lblUpdateAvailable.setBounds(10, 11, 414, 62);
		contentPanel.add(lblUpdateAvailable);

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		
		JButton button = new JButton("Update now !");
		button.setActionCommand("update");
		buttonPane.add(button);
		getRootPane().setDefaultButton(button);

		JButton okButton = new JButton("Remind me Later");
		okButton.setActionCommand("later");
		buttonPane.add(okButton);

		JButton cancelButton = new JButton("Skip this update");
		cancelButton.setActionCommand("skip");
		buttonPane.add(cancelButton);
		
		ActionListener listener = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equals("update")) {
					MainClass.update();
				} else if (e.getActionCommand().equals("later")) {
					long later = System.currentTimeMillis() + 1000 * 60 * 60 * 24; // 24H
					Config.getConfig().setString(Constants.CONFIG_REMINDER_AT, "" + later);
					setVisible(false);
				}
			}
		};
		button.addActionListener(listener);
		okButton.addActionListener(listener);
		cancelButton.addActionListener(listener);

	}
}
