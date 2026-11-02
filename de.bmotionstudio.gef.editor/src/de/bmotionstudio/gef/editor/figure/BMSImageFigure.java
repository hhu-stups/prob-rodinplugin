/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.figure;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.StackLayout;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Display;

public class BMSImageFigure extends AbstractBMotionFigure {

	private ImageFigure imageFigure;

	private int alpha;

	final ImageLoader loader = new ImageLoader();

	private Map<String, List<Image>> images = new HashMap<String, List<Image>>();
	private GIFThread currentGIFThread;

	public BMSImageFigure() {
		setLayoutManager(new StackLayout());
		imageFigure = new ImageFigure();
		add(imageFigure);
	}

	public void setLayout(Rectangle rect) {
		getParent().setConstraint(imageFigure, rect);
	}

	@Override
	public void paint(Graphics g) {
		g.setAlpha(alpha);
		super.paint(g);
	}

	public void setImage(String myPath) {

		if (currentGIFThread != null)
			currentGIFThread.interrupt();

		if (new File(myPath).exists()) {

			try {
				loader.load(myPath);
			} catch (SWTException e) {
				// not an image or a corrupt one
				imageFigure.setImage(null);
				return;
			}

			List<Image> imgList = images.get(myPath);
			if (imgList == null) {
				imgList = new ArrayList<Image>();
				for (ImageData imageData : loader.data)
					imgList.add(new Image(Display.getDefault(), imageData));
				images.put(myPath, imgList);
			}

			if (loader.data.length > 1) { // GIF file
				currentGIFThread = new GIFThread(this.imageFigure, loader.data,
						imgList);
				currentGIFThread.start();
			} else { // Non GIF file
				imageFigure.setImage(imgList.get(0));
			}

		}

	}

	@Override
	public void deactivateFigure() {
		if (currentGIFThread != null)
			currentGIFThread.interrupt();
		if (imageFigure.getImage() != null)
			imageFigure.getImage().dispose();
		for (List<Image> l : images.values())
			for (Image img : l)
				img.dispose();
	}

	class GIFThread extends Thread {

		/**
		 * Like web browsers, frames with a delay below 20ms (often 0, meaning
		 * "as fast as possible") are shown for 100ms. Otherwise the thread
		 * floods the UI thread with frame changes and Rodin hangs.
		 */
		private static final int MIN_DELAY_MS = 20;
		private static final int DEFAULT_DELAY_MS = 100;

		final ImageFigure imgFigure;
		final ImageData[] frames;
		final List<Image> imgList;
		// written by the UI thread
		volatile int imageNumber;
		volatile boolean stopped = false;

		public GIFThread(ImageFigure imgFigure, ImageData[] frames,
				List<Image> imgList) {
			super("BMotion Studio GIF animation");
			setDaemon(true);
			this.imgFigure = imgFigure;
			this.frames = frames;
			this.imgList = imgList;
		}

		@Override
		public void run() {

			while (!stopped) {

				int delay = frames[imageNumber].delayTime * 10;
				if (delay < MIN_DELAY_MS)
					delay = DEFAULT_DELAY_MS;

				try {
					Thread.sleep(delay);
				} catch (InterruptedException e) {
					return; // stopped
				}

				final Display display = Display.getDefault();
				if (stopped || display.isDisposed())
					return;
				// wait for the frame to be shown before scheduling the next
				// one
				display.syncExec(new Runnable() {
					@Override
					public void run() {
						// Increase the variable holding the frame
						// number
						imageNumber = imageNumber == frames.length - 1 ? 0
								: imageNumber + 1;
						Image image = imgList.get(imageNumber);
						if (image != null && !image.isDisposed() && !stopped) {
							imgFigure.setImage(image);
						}
					}
				});

			}

		}

		@Override
		public void interrupt() {
			stopped = true;
			super.interrupt();
		}

	}

	public void setAlpha(int alpha) {
		this.alpha = alpha;
		repaint();
	}

}
