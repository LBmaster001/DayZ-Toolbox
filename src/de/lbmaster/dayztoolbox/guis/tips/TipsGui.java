package de.lbmaster.dayztoolbox.guis.tips;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

import de.lbmaster.dayztoolbox.guis.CustomDialog;
import de.lbmaster.dayztoolbox.guis.CustomURLEnabledLabel;

public class TipsGui extends CustomDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();

	public TipsGui(String title) {
		super(title);
		setBounds(100, 100, 600, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		RowSpec[] rows = new RowSpec[Tips.tips.size()];
		for (int i = 0; i < rows.length; i++)
			rows[i] = FormSpecs.PREF_ROWSPEC;
		contentPanel.setLayout(new FormLayout(new ColumnSpec[] { ColumnSpec.decode("120px"), ColumnSpec.decode("default:grow"), }, rows));

		int index = 1;
		for (Entry<String, String> entry : Tips.tips.entrySet()) {
			JLabel lblDownloads = new JLabel("<html><body>" + entry.getKey() + "</body></html>");
			contentPanel.add(lblDownloads, "1, " + index);

			CustomURLEnabledLabel infotext = new CustomURLEnabledLabel(entry.getValue());
			contentPanel.add(infotext, "2, " + index);

			index++;
		}

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);

		JButton okButton = new JButton("OK");
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);

		okButton.addActionListener(getDefaultCloseListener());

	}
}
