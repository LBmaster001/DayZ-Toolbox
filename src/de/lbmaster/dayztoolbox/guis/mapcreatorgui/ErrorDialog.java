package de.lbmaster.dayztoolbox.guis.mapcreatorgui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import de.lbmaster.dayztoolbox.guis.CustomDialog;
import de.lbmaster.dayztoolbox.guis.CustomURLEnabledLabel;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class ErrorDialog extends CustomDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();

	public ErrorDialog(String errorMessage, boolean isError) {
		super(isError ? "Error" : "Info");
		setBounds(170, 160, 400, 200);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);

		CustomURLEnabledLabel lbltest = new CustomURLEnabledLabel("<html><p style=\"width:270px\">" + errorMessage + "</p></html>");
		lbltest.setBounds(10, 10, 364, 120);
		contentPanel.add(lbltest);

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);

		JButton okButton = new JButton("OK");
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);
		okButton.addActionListener(getDefaultCloseListener());

	}

}
