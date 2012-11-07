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

			loader.load(myPath);

			List<Image> imgList = images.get(myPath);
			if (imgList == null) {
				imgList = new ArrayList<Image>();
				for (ImageData imageData : loader.data)
					imgList.add(new Image(Display.getDefault(), imageData));
				images.put(myPath, imgList);
			}

			if (loader.data.length > 1) { // GIF file
				currentGIFThread = new GIFThread(this.imageFigure, myPath,
						imgList);
				currentGIFThread.start();
			} else { // Non GIF file
				imageFigure.setImage(imgList.get(0));
			}

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.bmotionstudio.gef.editor.figure.IBMotionFigure#deactivateFigure()
	 */
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

		ImageFigure imgFigure;
		int imageNumber;
		final ImageLoader loader = new ImageLoader();
		boolean stopped = false;
		List<Image> imgList;

		public GIFThread(ImageFigure imgFigure, String imgPath,
				List<Image> imgList) {
			this.imgFigure = imgFigure;
			this.imgList = imgList;
			loader.load(imgPath);
		}

		@Override
		public void run() {

			stopped = false;

			while (!stopped) {

				int delayTime = loader.data[imageNumber].delayTime;

				try {
					Thread.sleep(delayTime * 10);
				} catch (InterruptedException e) {
					// e.printStackTrace();
				}

				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						// Increase the variable holding the frame
						// number
						imageNumber = imageNumber == loader.data.length - 1 ? 0
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
