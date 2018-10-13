package de.lbmaster.dayztoolbox.guis.upnp;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import de.lbmaster.dayztoolbox.guis.CustomDialog;
import javax.swing.JLabel;

public class UPnPGui extends CustomDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();

	public UPnPGui(String title) {
		super(title);
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		JLabel lblWip = new JLabel("WIP");
		lblWip.setBounds(175, 101, 46, 14);
		contentPanel.add(lblWip);

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);

		JButton okButton = new JButton("Close");
		okButton.setActionCommand("OK");
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);
		
		okButton.addActionListener(getDefaultCloseListener());

	}

}
