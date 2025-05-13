package acsse.csc3a.observer;

import java.awt.image.BufferedImage;

import acsse.csc3a.imagegraph.ImageGraph;
import acsse.csc3a.imagegraph.WaterQualityDetectorVisitor;

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
	public void notifyObservers(Result result) {
		// TODO Auto-generated method stub
		if (observer != null) observer.update(result);
	}

	public void analyze(BufferedImage image /*, ProgressIndicator spinner*/) {
		// TODO Auto-generated method stub
		
		imageGraph = new ImageGraph(image);
		
		Result result = imageGraph.accept(new WaterQualityDetectorVisitor());
		
		this.notifyObservers(result);
	}

}
