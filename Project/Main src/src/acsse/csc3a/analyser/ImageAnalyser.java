package acsse.csc3a.analyser;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.function.BiConsumer;

import acsse.csc3a.graph.algorithms.kNearestNeighbor;
import acsse.csc3a.imagegraph.ImageGraph;
import acsse.csc3a.io.ImageIterator;

/**
 * This class supports analyze water functionality which can be monitored
 */
public class ImageAnalyser implements AbstractSubject {

	public static Double TOTAL_PROGRESS = 10.0;
	public static Double CURRENT_PROGRESS = 0.0;

	private AbstractObserver observer;
	private ImageGraph imageGraph;

	@Override
	public void attach(AbstractObserver o) {
		// TODO Auto-generated method stub
		observer = o;
	}

	@Override
	public void detach() {
		// TODO Auto-generated method stub
		observer = null;
	}

	@Override
	public void notifyObserversMatch(Result result) {
		// TODO Auto-generated method stub
		if (observer != null)
			observer.updateMatch(result);
	}

	@Override
	public void notifyObserversCat(Result result) {
		// TODO Auto-generated method stub
		if (observer != null)
			observer.updateCat(result);
	}

	@Override
	public void notify(String result) {
		// TODO Auto-generated method stub
		if (observer != null)
			observer.update(result);
	}

	/**
	 * This method analyzes an image to perform classification and similarity
	 * detection
	 * 
	 * @param image          the image being analyzed
	 * @param updateProgress Represents a functional interface which accepts a
	 *                       method with a signature like the updateProdress method
	 *                       of the {@link #Task} to allow monitoring of progress,
	 *                       when the analyzing of water
	 */
	public void analyze(BufferedImage image, BiConsumer<Double, Double> updateProgress) {
		// TODO Auto-generated method stub

		if (image != null) {
			imageGraph = new ImageGraph(image);

			updateProgress.accept(++CURRENT_PROGRESS, TOTAL_PROGRESS);

			notify("INFO---Graph Construction---Image has been converted to a graph successfully, \nclassifying and similiarity detection has begun!");

			Result result = new Result();

			kNearestNeighbor knn = new kNearestNeighbor(updateProgress);

			try {
				result.category_TYPE = knn.classify(imageGraph, new ImageIterator(), 5);
				this.notifyObserversCat(result);
				updateProgress.accept(++CURRENT_PROGRESS, TOTAL_PROGRESS);
				result.match_TYPE = knn.match(imageGraph, new ImageIterator(result.category_TYPE), 3);
				this.notifyObserversMatch(result);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				notify("ERR---Classficatio or Similarity detection error---" + e.getMessage());
				e.printStackTrace();
			}

		}
	}

}
