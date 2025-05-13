package acsse.csc3a.imagegraph;

import java.io.IOException;

import acsse.csc3a.graph.algorithms.CATEGORY_TYPE;
import acsse.csc3a.graph.algorithms.kNearestNeighbor;
import acsse.csc3a.io.ImageIterator;
import acsse.csc3a.observer.Result;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressIndicator;

public class WaterQualityDetectorVisitor implements AbstractVisitor {

	public WaterQualityDetectorVisitor() {

	}

	@Override
	public Result visit(ImageGraph visitable) {
		// TODO Auto-generated method stub
		
		Result result = new Result();
	
		try {
			result.category_TYPE = kNearestNeighbor.classify(visitable, new ImageIterator(), 5);
			result.match_TYPE = kNearestNeighbor.match(visitable, new ImageIterator(result.category_TYPE), 3);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

}
