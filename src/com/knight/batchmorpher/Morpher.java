package com.knight.batchmorpher;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;

public class Morpher {

	private String path;

	public ArrayList<BufferedImage> frames = new ArrayList<BufferedImage>();
	ArrayList<BufferedImage> pictures = new ArrayList<BufferedImage>(); 
	BufferedImage first;
	BufferedImage second;
	BufferedImage third;
	int frameCount = 0;
	int framesPerTransition = 30;
	int pause = 30;
	int targWidth = 0;
	int targHeight = 0;
	String name;

	public Morpher(String path, int frames, int pause) {
		this.path = path;
		this.framesPerTransition = frames;
		this.pause = pause;
		File folder = new File(path);
		name = folder.getName();
		for(File f : folder.listFiles()) {
			if(!f.getName().contains(".gif")) {
				try {
					pictures.add(ImageIO.read(f));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public Morpher(String path, int frames, int pause, int width, int height) {
		this(path, frames, pause);
		targWidth = width;
		targHeight = height;
	}
	
	public void morph() {
		File f = new File(path);
		frameCount = f.listFiles().length * framesPerTransition;
		int width = 0;
		int height = 0;
		if(targWidth != 0) {
			width = targWidth;
			height = targHeight;
		} else {
			width = pictures.get(0).getWidth();
			height = pictures.get(0).getHeight();
		}
		BufferedImage last = null;//pictures.get(0);
		for(BufferedImage next : pictures) {
			if(next == null) continue;
			if(next.getWidth() != width || next.getHeight() != height) {
				next = resize(next, width, height);
			}
			if(last == null) {
				last = next;
				continue;
			}
			for(int i = 1; i < pause; i++) frames.add(last);
			boolean[][] switched = new boolean[width][height];
			int totalPixels = width * height;
			int changedPixels = 0;
			Random rand = new Random();
			int pixelsToChangePerFrame = totalPixels / framesPerTransition;
			while(changedPixels < totalPixels) {
				BufferedImage bi = copy(last);//new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
				for(int tot = 0; tot < pixelsToChangePerFrame; tot++) {
					int x = 0;
					int y = 0;
					while(switched[x][y] && changedPixels < totalPixels) {
						x = rand.nextInt(width);
						y = rand.nextInt(height);
					}
					switched[x][y] = true;
					bi.setRGB(x, y, next.getRGB(x, y));
					changedPixels++;
				}
				frames.add(bi);
				last = bi;
			}
		}
	}

	public void morph1() {
		File f = new File(path);
		frameCount = f.listFiles().length * framesPerTransition;
		int width = 0;
		int height = 0;
		try {
			first = ImageIO.read(f.listFiles()[0]);
			second = ImageIO.read(f.listFiles()[1]);
			third = ImageIO.read(f.listFiles()[2]);
			if(targWidth != 0) {
				width = targWidth;
				height = targHeight;
			} else {
				width = first.getWidth();
				height = first.getHeight();
			}
			first = resize(first, width, height);
			second = resize(second, width, height);
			third = resize(third, width, height);
		} catch (IOException e) {
			e.printStackTrace();
		}
		for(int i = 1; i < pause; i++) frames.add(first);
		boolean[][] switched = new boolean[width][height];
		int totalPixels = width * height;
		int changedPixels = 0;
		Random rand = new Random();
		BufferedImage last = first;
		while(changedPixels < totalPixels) {
			int pixelsToChangePerFrame = totalPixels / framesPerTransition;
			BufferedImage bi = copy(last);//new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			for(int tot = 0; tot < pixelsToChangePerFrame; tot++) {
				int x = 0;
				int y = 0;
				while(switched[x][y] && changedPixels < totalPixels) {
					x = rand.nextInt(width);
					y = rand.nextInt(height);
				}
				switched[x][y] = true;
				bi.setRGB(x, y, second.getRGB(x, y));
				changedPixels++;
			}
			frames.add(bi);
			last = bi;
		}
		for(int i = 1; i < pause; i++) frames.add(second);
		last = second;
		changedPixels = 0;
		switched = new boolean[width][height];
		while(changedPixels < totalPixels) {
			int pixelsToChangePerFrame = totalPixels / framesPerTransition;
			BufferedImage bi = copy(last);//new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			for(int tot = 0; tot < pixelsToChangePerFrame; tot++) {
				int x = 0;
				int y = 0;
				while(switched[x][y] && changedPixels < totalPixels) {
					x = rand.nextInt(width);
					y = rand.nextInt(height);
				}
				switched[x][y] = true;
				bi.setRGB(x, y, third.getRGB(x, y));
				changedPixels++;
			}
			frames.add(bi);
			last = bi;
		}
		for(int i = 0; i < pause * 2; i++) frames.add(third);
	}

	public void save() {
		try {
			System.out.println("Saving to: " + path + "\\output.gif, frames: " + frames.size());
			//ImageOutputStream output = new FileImageOutputStream(new File(path + "\\output.gif"));
			//GifSequenceWriter writer = new GifSequenceWriter(output, first.getType(), (1/30) * 1000, false);
			AnimatedGifEncoder encoder = new AnimatedGifEncoder();
			encoder.start(path + "\\output.gif");
			encoder.setDelay(framesPerTransition);
			int wrote = 0;
			for(BufferedImage b : frames) {
				//writer.writeToSequence(b);
				encoder.addFrame(b);
				wrote++;
			}
			//writer.close();
			encoder.finish();
			//output.close();
			System.out.println("Wrote " + wrote + " frames. done");
		} catch (Exception e) {
			e.printStackTrace();
		}
		frames = null;
	}

	static BufferedImage copy(BufferedImage bi) {
		ColorModel cm = bi.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = bi.copyData(null);
		return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}

	BufferedImage resize(BufferedImage img, int width, int height) {
		/*BufferedImage out = null;
		width = second.getWidth();
		height = second.getHeight();*/
		System.out.print("Resizing image to " + width + "x" + height + "\n");
		BufferedImage out = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		AffineTransform at = new AffineTransform();
		at.scale(((double)width) / ((double)img.getWidth()), ((double)height) / ((double)img.getHeight()));
		//System.out.print("Scale ratios: " + (width / first.getWidth()) + ", " + height / first.getHeight());
		AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
		//first = scaleOp.filter(first, after);
		out = scaleOp.filter(img, out);
		//System.out.println(", final size: " + first.getWidth() + "x" + first.getHeight());
		return out;
	}

}
