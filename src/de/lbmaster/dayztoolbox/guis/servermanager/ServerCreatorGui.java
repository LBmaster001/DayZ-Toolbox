package de.lbmaster.dayztoolbox.guis.servermanager;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;

import de.lbmaster.dayztoolbox.Constants;
import de.lbmaster.dayztoolbox.guis.CustomDialog;
import de.lbmaster.dayztoolbox.utils.Config;
import de.lbmaster.dayztoolbox.utils.UIDGenerator;

import java.awt.Choice;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.JCheckBox;
import javax.swing.JSpinner;

public class ServerCreatorGui extends CustomDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JTextField textField;
	private JTextField textField_rconpass;
	private JSpinner serverPort;
	private JCheckBox chckbxUpnp, chckbxRcon;

	public ServerCreatorGui(final ServerManagerGui serverManager) {
		super("Create new Server");
		setSize(300, 203);
		setLocationRelativeTo(serverManager);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("80px"),
				ColumnSpec.decode("default:grow"),
				ColumnSpec.decode("80px"),},
			new RowSpec[] {
				RowSpec.decode("30px"),
				RowSpec.decode("30px"),
				RowSpec.decode("30px"),
				RowSpec.decode("30px"),}));

		textField = new JTextField("newDZconfig.cfg");
		contentPanel.add(textField, "1, 2, 3, 1, fill, default");
		textField.setColumns(10);

		final Choice choice = new Choice();
		contentPanel.add(choice, "1, 1, 3, 1");
		
		chckbxRcon = new JCheckBox("Rcon");
		contentPanel.add(chckbxRcon, "1, 3");
		
		textField_rconpass = new JTextField(UIDGenerator.generateUID(16));
		textField_rconpass.setEditable(chckbxRcon.isSelected());
		contentPanel.add(textField_rconpass, "2, 3, 2, 1, fill, default");
		textField_rconpass.setColumns(10);
		
		chckbxUpnp = new JCheckBox("UPnP");
		contentPanel.add(chckbxUpnp, "1, 4");
		
		serverPort = new JSpinner();
		serverPort.setModel(new SpinnerNumberModel(2302, 1, 65535, 1));
		serverPort.setEditor(new JSpinner.NumberEditor(serverPort, "#"));
		contentPanel.add(serverPort, "3, 4");
		
		choice.add("NEW CONFIG");
		for (File f : new File(Config.getConfig().getString(Constants.CONFIG_LAST_DAYZ_SERVER_FOLDER)).listFiles()) {
			if (f.isFile() && f.getName().endsWith(".cfg")) {
				choice.add(f.getName());
			}
		}
		choice.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (choice.getSelectedIndex() == 0) {
					textField.setEditable(true);
					textField.setText("newDZconfig.cfg");
				} else {
					textField.setEditable(false);
					textField.setText(choice.getSelectedItem());
				}
			}
		});

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);

		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String file = textField.getText();
				System.out.println("file: " + file);
				if (!file.endsWith(".cfg"))
					file += ".cfg";
				if (serverManager != null) {
					int nextUID = Config.getConfig().getInt(Constants.CONFIG_SERVERMANAGER_NEXTUID, 0);
					serverManager.createNewServerPanel((nextUID++) + "_" + UIDGenerator.generateUID(8) + ".cfg", file, ServerCreatorGui.this);
					serverManager.updateSize();
					Config.getConfig().setInt(Constants.CONFIG_SERVERMANAGER_NEXTUID, nextUID);
				}
				setVisible(false);
			}
		});
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);

		JButton cancelButton = new JButton("Cancel");
		buttonPane.add(cancelButton);
		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		
		chckbxRcon.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				textField_rconpass.setEditable(chckbxRcon.isSelected());
			}
		});
	}

	public boolean useUPnP() {
		return chckbxUpnp.isSelected();
	}
	
	public boolean useRcon() {
		return chckbxRcon.isSelected();
	}
	
	public String getRconPass() {
		return textField_rconpass.getText();
	}
	
	public int getPort() {
		return Math.min(Math.max(1, (int) serverPort.getValue()), 65535);
	}
	
}
