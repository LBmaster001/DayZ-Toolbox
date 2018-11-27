package de.lbmaster.dayztoolbox.guis.mapeditorgui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import de.lbmaster.dayztoolbox.map.MapFile;
import de.lbmaster.dayztoolbox.map.MapImage;
import de.lbmaster.dayztoolbox.map.MapPosition;
import de.lbmaster.dayztoolbox.map.MapPositions;

public class MapJPanel extends JPanel implements MouseWheelListener, MouseListener, MouseMotionListener, ComponentListener {

	private static final long serialVersionUID = 1L;
	private MapImage mapImage;
	private MapFile mapFile;
	private MapEditorGui gui;

	private double zoomFactor = 0.035;
	private double prevZoomFactor = 0.035;
	private boolean zoomer;
	private boolean dragger;
	private boolean released;
	private boolean moved = false;
	private boolean translate = false;
	private double xOffset = 0;
	private double yOffset = 0;
	private int xDiff;
	private int yDiff;
	private Point startPoint;

	public MapJPanel(MapFile mf, MapEditorGui gui) {
		this(mf, gui, false);
	}

	public MapJPanel(MapFile mf, MapEditorGui gui, boolean loadonlyImages) {
		loadImageAsync(mf, loadonlyImages);
		initComponent();
		this.gui = gui;
		this.zoomer = true;
	}

	private void loadImageAsync(final MapFile mf, final boolean loadonlyImages) {
		mapFile = mf;
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					if (loadonlyImages)
						mapFile.readImagesOnly();
					else
						mapFile.readContent();

					System.out.println("Finished reading Image");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					while (mapFile == null)
						Thread.sleep(10);

					System.out.println("MapFile created " + mapFile.getAllImages().size());
					int tries = 1000;
					int i = 0;
					while (mapFile.getAllImages().size() <= 0 && i < tries) {
						Thread.sleep(10);

						if (i % 100 == 0)
							System.out.println(mapFile.getAllImages().size());
						i++;
					}
					if (mapFile.getAllImages().size() <= 0) {
						System.out.println("No MapImage found in MapFile !");
						return;
					} else {
						System.out.println("Image in MapFile was found !");
					}
					mapImage = mapFile.getAllImages().get(0);
					int lastInt = mapImage.getReadImagesCount();
					while (!mapImage.hasFullyReadContent()) {
						int count = mapImage.getReadImagesCount();
						if (count != lastInt && getParent() != null && getParent().isVisible()) {
							onlyRepaint();
						}
						lastInt = count;
						Thread.sleep(100);
					}
					onlyRepaint();
					System.out.println("Image finished reading");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	private void initComponent() {
		addMouseWheelListener(this);
		addMouseMotionListener(this);
		addMouseListener(this);
	}

	public MapFile getMapFile() {
		return mapFile;
	}

	public MapEditorGui getMapEditorGui() {
		return gui;
	}

	private List<MapPositions> drawPositions = new ArrayList<MapPositions>();

	public void addPositionsDraw(MapPositions positions) {
		drawPositions.add(positions);
		onlyRepaint();
	}

	public void removePositionsDraw(MapPositions positions) {
		drawPositions.remove(positions);
		onlyRepaint();
	}

	public void clearPositionsDraw() {
		drawPositions.clear();
		onlyRepaint();
	}

	public void onlyRepaint() {
		translate = true;
		repaint();
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		Graphics2D g2 = (Graphics2D) g;

		if (zoomer) {
			AffineTransform at = new AffineTransform();

			double xRel = MouseInfo.getPointerInfo().getLocation().getX() - getLocationOnScreen().getX();
			double yRel = MouseInfo.getPointerInfo().getLocation().getY() - getLocationOnScreen().getY();

			double zoomDiv = zoomFactor / prevZoomFactor;

			xOffset = (zoomDiv) * (xOffset) + (1 - zoomDiv) * xRel;
			yOffset = (zoomDiv) * (yOffset) + (1 - zoomDiv) * yRel;

			at.translate(xOffset, yOffset);
			at.scale(zoomFactor, zoomFactor);
			prevZoomFactor = zoomFactor;
			g2.transform(at);
			zoomer = false;
		}
		if (dragger) {
			AffineTransform at = new AffineTransform();
			double offsetX = clampXOffset(this.xOffset + xDiff);
			double offsetY = clampYOffset(this.yOffset + yDiff);
			at.translate(offsetX, offsetY);
			at.scale(zoomFactor, zoomFactor);
			g2.transform(at);

			if (released) {
				xOffset += xDiff;
				yOffset += yDiff;
				dragger = false;
			}
		}
		if (translate && !dragger && !zoomer) {
			AffineTransform at = new AffineTransform();
			at.translate(this.xOffset, this.yOffset);
			at.scale(zoomFactor, zoomFactor);
			g2.transform(at);
			translate = false;
		}

		// All drawings go here

		if (mapImage != null && mapImage.getImage() != null)
			g2.drawImage(mapImage.getImage(), 0, 0, this);
		Color color2 = g2.getColor();
		g2.setColor(Color.PINK);
		for (MapPositions positions : drawPositions) {
			g2.setColor(positions.getColor());
			for (MapPosition pos : positions.getPositions()) {
				g2.drawRect((int) (pos.getX() - (4.0 / zoomFactor)), (int) getInvertedZPos(pos.getZ() + (4.0 / zoomFactor)), (int) (10.0 / zoomFactor), (int) (10.0 / zoomFactor));
			}
		}
		g2.setColor(color2);

	}

	private double getInvertedZPos(double y) {
		if (mapImage == null || mapImage.getImage() == null)
			return -1;
		return ((double) mapImage.getImage().getHeight()) - y;
	}

	private double clampXOffset(double offset) {
		return offset;
	}

	private double clampYOffset(double offset) {
		return offset;
	}

	private double clampZoom(double zoom) {
		return Math.min(Math.max(0.03, zoom), 5);
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {

		zoomer = true;

		// Zoom in
		if (e.getWheelRotation() < 0) {
			zoomFactor *= 1.1;
			zoomFactor = clampZoom(zoomFactor);
			repaint();
		}
		// Zoom out
		if (e.getWheelRotation() > 0) {
			zoomFactor /= 1.1;
			zoomFactor = clampZoom(zoomFactor);
			repaint();
		}
	}

	private void onDoubleClick() {
		zoomer = true;
		zoomFactor = 0.8;
		repaint();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (released || startPoint == null)
			return;
		moved = true;
		Point curPoint = e.getLocationOnScreen();
		xDiff = curPoint.x - startPoint.x;
		yDiff = curPoint.y - startPoint.y;

		dragger = true;
		repaint();

	}

	private long lastMouseClickButton1 = 0;
	private long doubleClickTimeMS = 300;

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			if (System.currentTimeMillis() - doubleClickTimeMS <= lastMouseClickButton1)
				onDoubleClick();
			lastMouseClickButton1 = System.currentTimeMillis();

			if (gui.isPositionsAddSelected()) {
				double xRel = MouseInfo.getPointerInfo().getLocation().getX() - getLocationOnScreen().getX();
				double yRel = MouseInfo.getPointerInfo().getLocation().getY() - getLocationOnScreen().getY();
				double mapPositionX = (-this.xOffset + xRel) / zoomFactor;
				double mapPositionZ = getInvertedZPos((-this.yOffset + yRel) / zoomFactor);

				MapPosition pos = new MapPosition(mapPositionX, mapPositionZ, null);
				gui.getRightPanel().addPosition(pos, this);
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			released = false;
			moved = false;
			startPoint = MouseInfo.getPointerInfo().getLocation();
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			released = true;
			if (moved)
				repaint();
		}
	}

	@Override
	public void componentResized(ComponentEvent e) {
		System.out.println("Resize");
		onlyRepaint();
	}

	@Override
	public void componentShown(ComponentEvent e) {
		onlyRepaint();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		double xRel = MouseInfo.getPointerInfo().getLocation().getX() - getLocationOnScreen().getX();
		double yRel = MouseInfo.getPointerInfo().getLocation().getY() - getLocationOnScreen().getY();
		double mapPositionX = (-this.xOffset + xRel) / zoomFactor;
		double mapPositionZ = getInvertedZPos((-this.yOffset + yRel) / zoomFactor);
		String posX = mapPositionX + "00000";
		posX = posX.substring(0, posX.indexOf(".") + 5);
		String posZ = mapPositionZ + "00000";
		posZ = posZ.substring(0, posZ.indexOf(".") + 5);
		gui.getInfoLabel().setText("X:" + posX + " Z:" + posZ);
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void componentHidden(ComponentEvent e) {
	}

	@Override
	public void componentMoved(ComponentEvent e) {
	}

}