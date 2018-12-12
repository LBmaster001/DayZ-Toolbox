package de.lbmaster.dayztoolbox.guis;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class ErrorDialog extends CustomDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();

	private ErrorDialog(String errorMessage, boolean isError) {
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

	public static void displayError(String message) {
		new ErrorDialog(message, true).setVisible(true);
		StackTraceElement[] stack = Thread.currentThread().getStackTrace();
		StringBuilder sb = new StringBuilder();
		sb.append("Displayed Error Message: " + message + "\n");
		if (stack.length > 2) {
			sb.append("\tat " + stackElementToString(stack[2]) + "\n");
		}
		for (int i = 3; i < stack.length; i++) {
			if (i > 2+3)
				break;
			sb.append("\t   " + stackElementToString(stack[i]) + "\n");
		}
		System.out.println(sb.toString());
		System.err.println(sb.toString());
	}

	public static String stackElementToString(StackTraceElement elm) {
		if (elm == null)
			return "";
		return elm.getClassName() + "." + elm.getMethodName() + "(" + (elm.getFileName() == null ? "Unknown Source" : elm.getFileName() + ":" + elm.getLineNumber()) + ")";
	}
	
	public static void displayInfo(String message) {
		new ErrorDialog(message, false).setVisible(true);
	}

}
