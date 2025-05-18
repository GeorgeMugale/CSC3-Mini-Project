package acsse.csc3a.analyser;

import java.awt.image.BufferedImage;
import java.io.IOException;

import acsse.csc3a.graph.algorithms.kNearestNeighbor;
import acsse.csc3a.imagegraph.ImageGraph;
import acsse.csc3a.io.ImageIterator;

public class ImageAnalyser implements AbstractSubject {

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

	public void analyze(BufferedImage image /* , ProgressIndicator spinner */) {
		// TODO Auto-generated method stub

		imageGraph = new ImageGraph(image);

		notify("INFO---Graph Construction---Image has been converted to a graph successfully!");

		Result result = new Result();

		try {
			result.category_TYPE = kNearestNeighbor.classify(imageGraph, new ImageIterator(), 5);
			this.notifyObserversCat(result);
			result.match_TYPE = kNearestNeighbor.match(imageGraph, new ImageIterator(result.category_TYPE), 3);
			this.notifyObserversMatch(result);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			notify("ERR---Classficatio or Similarity detection error---" + e.getMessage());
			e.printStackTrace();
		}

		;
	}

}
