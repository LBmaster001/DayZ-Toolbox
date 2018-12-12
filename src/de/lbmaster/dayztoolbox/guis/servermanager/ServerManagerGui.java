package de.lbmaster.dayztoolbox.guis.servermanager;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;

import javax.swing.JButton;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import de.lbmaster.dayztoolbox.guis.CustomDialog;
import de.lbmaster.dayztoolbox.utils.Config;
import de.lbmaster.dayztoolbox.utils.PathFinder;

public class ServerManagerGui extends CustomDialog {

	private static final long serialVersionUID = 1L;

	private int currentPanelIndex = 1;

	public ServerManagerGui(String title) {
		super(title);
		setSize(660, 500);
		setResizable(false);
		
		getContentPane().setLayout(new FormLayout(new ColumnSpec[] { ColumnSpec.decode("130px"), ColumnSpec.decode("default:grow"), }, new RowSpec[] { RowSpec.decode("30px"), }));

		JButton btnCreateServer = new JButton("Create Server");
		getContentPane().add(btnCreateServer, "1, 1");
		btnCreateServer.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				new ServerCreatorGui(ServerManagerGui.this).setVisible(true);
			}
		});
		
		loadAllServerPanels();
		setFocusable(true);
		addWindowListener(new WindowListener() {
			
			@Override
			public void windowOpened(WindowEvent e) {}
			
			@Override
			public void windowIconified(WindowEvent e) {}
			
			@Override
			public void windowDeiconified(WindowEvent e) {}
			
			@Override
			public void windowDeactivated(WindowEvent e) {}
			
			@Override
			public void windowClosing(WindowEvent e) {}
			
			@Override
			public void windowClosed(WindowEvent e) {}
			
			@Override
			public void windowActivated(WindowEvent e) {
				for (Component com : getContentPane().getComponents()) {
					if (com instanceof ServerPanel) {
						((ServerPanel) com).onFocusGained();
					}
				}
			}
		});
	}

	public void createNewServerPanel(String config, String serverConfigLocation, ServerCreatorGui serverCreatorGui) {
		Config cfg = ServerPanel.createNewConfig(config, serverConfigLocation, serverCreatorGui);
		createServerPanel(cfg);
	}

	public void createServerPanel(Config config) {
		addConfigRow();
		ServerPanel panel = new ServerPanel(config, this);
		getContentPane().add(panel, "1, " + ++currentPanelIndex + ", 2, 1, fill, fill");
		revalidate();
	}

	public void createServerPanel(String config) {
		addConfigRow();
		ServerPanel panel = new ServerPanel(config, this);
		getContentPane().add(panel, "1, " + ++currentPanelIndex + ", 2, 1, fill, fill");
		revalidate();
	}
	
	private void addConfigRow() {

		FormLayout layout = (FormLayout) getContentPane().getLayout();
		if (layout.getRowCount() <= currentPanelIndex)
			layout.appendRow(RowSpec.decode("120px"));
	}

	public void loadAllServerPanels() {
		File serverConfigs = new File(PathFinder.findDayZToolBoxFolder() + "/serverconfigs");
		if (serverConfigs.exists() && serverConfigs.isDirectory()) {
			for (File f : serverConfigs.listFiles()) {
				if (f.getName().endsWith(".cfg")) {
					createServerPanel(f.getName());
				}
			}
		}
		updateSize();
	}

	public void removePanel(String configLocation) {
		int index = 1;
		for (Component com : getContentPane().getComponents()) {
			if (com instanceof ServerPanel) {
				System.out.println("remove : " + com.getClass().getSimpleName());
				getContentPane().remove(com);
				if (!configLocation.equals(((ServerPanel) com).getConfigLocation())) {
					getContentPane().add(com, "1, " + ++index + ", 2, 1, fill, fill");
				}
			}
		}
		getContentPane().revalidate();
		getContentPane().repaint();
		revalidate();
		repaint();
		this.currentPanelIndex = index;
		updateSize();
	}
	
	public void updateSize() {
		int height = 38 + 30;
		height += (this.currentPanelIndex-1) * 120;
		height = Math.max(188, height);
		int width = getWidth();
		setSize(width, height);
	}

}
