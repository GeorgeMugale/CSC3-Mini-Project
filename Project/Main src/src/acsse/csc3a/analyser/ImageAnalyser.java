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
		if (observer != null) observer.update(result);
	}
	
	@Override
	public void notifyObserversCat(Result result) {
		// TODO Auto-generated method stub
		if (observer != null) observer.update(result);
	}

	public void analyze(BufferedImage image /*, ProgressIndicator spinner*/) {
		// TODO Auto-generated method stub
		
		imageGraph = new ImageGraph(image);

		Result result = new Result();
		
		try {
			result.category_TYPE = kNearestNeighbor.classify(imageGraph, new ImageIterator(), 5);
			this.notifyObserversCat(result);
			result.match_TYPE = kNearestNeighbor.match(imageGraph, new ImageIterator(result.category_TYPE), 3);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.notifyObserversMatch(result);
	}

}
