package de.lbmaster.dayztoolbox.guis.mapeditorgui;

import java.awt.BorderLayout;
import java.awt.Choice;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import de.lbmaster.dayztoolbox.guis.CustomDialog;
import de.lbmaster.dayztoolbox.map.MapPosition;
import de.lbmaster.dayztoolbox.map.MapPositions;

public class MapCreateCategoryGui extends CustomDialog {

	private static final long serialVersionUID = -4747248565874182303L;
	private final JPanel contentPanel = new JPanel();
	private JTextField textField;

	public MapCreateCategoryGui(final MapJPanel parent, final MapPosition pos) {
		super("Create Category");
		setBounds(100, 100, 350, 184);
		if (parent != null)
			setLocationRelativeTo(parent);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);


		textField = new JTextField();
		textField.setBounds(110, 62, 215, 20);
		textField.setEditable(false);
		textField.setText(MapPositions.MAP_POSITIONS_NAME_PLAYER_SPAWNPOINTS);
		contentPanel.add(textField);
		textField.setColumns(10);

		final Choice choice = new Choice();
		choice.add("Player Spawnpoints");
		choice.add("Event Spawnpoints");
		choice.setBounds(10, 36, 314, 20);
		contentPanel.add(choice);
		choice.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getItem().equals(choice.getItem(0))) {
					System.out.println("Player Spawnpoint selected");
					textField.setEditable(false);
					textField.setText(MapPositions.MAP_POSITIONS_NAME_PLAYER_SPAWNPOINTS);
				} else {
					textField.setEditable(true);
					textField.setText("");
				}
			}
		});

		JLabel label = new JLabel("Event Name:");
		label.setBounds(10, 62, 90, 20);
		contentPanel.add(label);
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);

		JButton okButton = new JButton("OK");
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);
		
		okButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String catname = textField.getText();
				if (catname.length() <= 0) {
					catname = "default";
				}
				catname.replace(" ", "_");
				MapPositions positions = new MapPositions();
				positions.setName(catname);
				pos.setParent(positions);
				positions.addPosition(pos);
				parent.getMapFile().addMapObject(positions);
				parent.getMapEditorGui().getRightPanel().initPositions();
				setVisible(false);
			}
		});

		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(getDefaultCloseListener());
		buttonPane.add(cancelButton);

	}

}
