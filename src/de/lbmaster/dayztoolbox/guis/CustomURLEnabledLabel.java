package de.lbmaster.dayztoolbox.guis;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import de.lbmaster.dayztoolbox.Constants;

public class CustomURLEnabledLabel extends JEditorPane {

	private static final long serialVersionUID = 1L;

	public CustomURLEnabledLabel(String text) {
		super("text/html", "<html><font face=\"" + Constants.FONT + "\">" + text.replace("\n", "<br>") + "</font></html>");
		setEditable(false);
		setOpaque(false);
		addHyperlinkListener(new HyperlinkListener() {
			public void hyperlinkUpdate(HyperlinkEvent hle) {
				if (HyperlinkEvent.EventType.ACTIVATED.equals(hle.getEventType())) {
					open(hle.getURL().toString());
				}
			}
		});
	}

	private static void open(String uri) {
		if (Desktop.isDesktopSupported()) {
			try {
				Desktop.getDesktop().browse(new URI(uri));
			} catch (IOException e) {
				e.printStackTrace();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
	}
}
