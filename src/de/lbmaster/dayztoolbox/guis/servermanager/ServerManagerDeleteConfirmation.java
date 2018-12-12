package de.lbmaster.dayztoolbox.guis.servermanager;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import de.lbmaster.dayztoolbox.guis.CustomDialog;
import de.lbmaster.dayztoolbox.guis.ErrorDialog;

import javax.swing.JLabel;

public class ServerManagerDeleteConfirmation extends CustomDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();

	public ServerManagerDeleteConfirmation(final List<File> files, ServerPanel serverPanel) {
		super("");
		setBounds(100, 100, 1000, 390);
		setLocationRelativeTo(serverPanel);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setLayout(new FlowLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{
			JLabel label = new JLabel("");
			StringBuilder sb = new StringBuilder();
			sb.append("<html>Are you sure you want to delete the following files:<br>");
			for (File f : files) {
				sb.append(f.getAbsolutePath()+"<br>");
			}
			sb.append("</html>");
			label.setText(sb.toString());
			contentPanel.add(label);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
				okButton.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						boolean ok = true;
						for (File f : files) {
							if (!f.delete()) {
								ok = false;
							}
						}
						setVisible(false);
						if (ok) {
							ErrorDialog.displayInfo("Succesfully deleted persistance Files");
						} else {
							ErrorDialog.displayError("Some files could not be deleted ! Maybe the Server is blocking the files ?");
						}
					}
				});
			}
			{
				JButton cancelButton = new JButton("Cancel");
				buttonPane.add(cancelButton);
				cancelButton.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						setVisible(false);
					}
				});
			}
		}
	}

}
