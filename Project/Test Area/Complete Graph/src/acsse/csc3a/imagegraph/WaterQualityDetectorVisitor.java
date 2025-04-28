package acsse.csc3a.imagegraph;

import acsse.csc3a.graph.algorithms.kNearestNeighbor;
import acsse.csc3a.lists.LinkedPositionalList;

public class WaterQualityDetectorVisitor implements AbstractVisitor {

	@Override
	public void visit(ImageGraph visitable) {
		// TODO Auto-generated method stub
		
		String fName = kNearestNeighbor.classify(visitable, new LinkedPositionalList<ImageGraph>(), 10);
		
		// read appropriate file
		// get linked positional list of image graphs
		
		String match = kNearestNeighbor.match(visitable, new LinkedPositionalList<ImageGraph>(), 5);

	}

}
