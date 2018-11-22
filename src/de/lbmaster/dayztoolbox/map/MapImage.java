package de.lbmaster.dayztoolbox.map;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import de.lbmaster.dayztoolbox.MainClass;
import de.lbmaster.dayztoolbox.guis.mapcreatorgui.ErrorDialog;
import de.lbmaster.dayztoolbox.utils.ByteUtils;

public class MapImage extends MapObject {

	private static final int maxSliceSize = 512;

	private BufferedImage img;
	private int sourceWidth = 0, sourceHeight = 0;
	private byte[] imageBytes = null;
	private boolean hasFullyRead = false;
	private int readImages = 0;

	public void loadFromBytes(byte[] imageBytes) throws IOException {
		this.imageBytes = imageBytes;
		byte xSlices = imageBytes[0];
		byte ySlices = imageBytes[1];
		try {
		img = new BufferedImage(xSlices * maxSliceSize, ySlices * maxSliceSize, BufferedImage.TYPE_INT_RGB);
		} catch (Exception e) {
			e.printStackTrace();
			new ErrorDialog("Out of Memory error! " + (MainClass.is64BitJVM() ? "You are not using the 64bit Java Version. Download the 64bit Version to resolve this issue" : ""), true);
			return;
		}
		Graphics imgGraphics = img.createGraphics();

		int pos = 2;
		
		for (byte x = 0; x < xSlices; x++) {
			for (byte y = 0; y < ySlices; y++) {
				int size = ByteUtils.readInt(imageBytes, pos);
				pos += 4;
				final byte[] content = ByteUtils.substring(imageBytes, pos, size);
				pos += size;
				final ByteArrayInputStream contentStream = new ByteArrayInputStream(content);
//				System.out.println("Reading image... " + content.length);
				BufferedImage img2 = ImageIO.read(contentStream);
				synchronized (imgGraphics) {
					imgGraphics.drawImage(img2, x * maxSliceSize, y * maxSliceSize, null);
					readImages++;
				}
				img2 = null;
			}
			System.gc();
		}
		MainClass.checkMemory();
		hasFullyRead = true;
		imgGraphics.dispose();
		sourceWidth = img.getWidth();
		sourceHeight = img.getHeight();
		System.out.println("SourceHeight: " + sourceHeight + " SourceWidth: " + sourceWidth);
	}

	public boolean hasFullyReadContent() {
		return hasFullyRead;
	}
	
	public int getReadImagesCount() {
		return readImages;
	}
	
	@Override
	public byte[] toBytes() throws IOException {
		if (this.imageBytes != null)
			return this.imageBytes;
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		BufferedImage[][] imgSlices = fillSlices(img);
		bytes.write((byte) imgSlices.length);
		bytes.write((byte) imgSlices[0].length);
		for (byte x = 0; x < imgSlices.length; x++) {
			for (byte y = 0; y < imgSlices[0].length; y++) {
				ByteArrayOutputStream contentStream = new ByteArrayOutputStream();
				ImageIO.write(imgSlices[x][y], "JPEG", contentStream);
				int size = contentStream.size();
				System.out.println("ImageSize: " + size);
				bytes.write(ByteUtils.intToBytes(size));
				bytes.write(contentStream.toByteArray());

			}
		}
		return bytes.toByteArray();
	}

	public MapImage(BufferedImage img) {
		super(MapObjectType.MAP_IMAGE);
		if (img == null)
			return;
		this.sourceHeight = img.getHeight();
		this.sourceWidth = img.getWidth();
		this.img = img;
		this.hasFullyRead = true;
		this.readImages = 1;
	}

	private BufferedImage[][] fillSlices(BufferedImage img) {
		int ySlices = img.getHeight() / maxSliceSize;
		int xSlices = img.getWidth() / maxSliceSize;
		BufferedImage[][] imgSlices = new BufferedImage[xSlices][ySlices];
		for (int xI = 0; xI < imgSlices.length; xI++) {
			for (int yI = 0; yI < imgSlices[0].length; yI++) {
				int x = xI * maxSliceSize;
				int y = yI * maxSliceSize;
				BufferedImage img2 = img.getSubimage(x, y, maxSliceSize, maxSliceSize);
				imgSlices[xI][yI] = img2;
			}
		}
		return imgSlices;
	}
	
	public synchronized BufferedImage getImage() {
		return img;
	}
}
