package acsse.csc3a.graph.algorithms;

import java.util.Iterator;
import acsse.csc3a.graph.Edge;
import acsse.csc3a.graph.Vertex;
import acsse.csc3a.imagegraph.ImageGraph;
import acsse.csc3a.imagegraph.Point;

/**
 * A class specialized to calculate the graph edit distance between two graphs
 * This graph uses specialized weighting tailored for water image classification
 * and matching
 * 
 */
public class GraphEditDistance {

	/**
	 * The cost to insert/ delete a edge is trivial Edge Deletion A connection
	 * present in one image but not the other
	 */
	private float EDGE_INSERTION_DELETION_COST = 0.05f;

	/**
	 * Vertex Deletion A pixel/region in one image does not exist in the other Might
	 * reflect small differences, occlusion, or non-critical background differences
	 */
	private float VERTEX_INSERTION_DELETION_COST = 0.09f;

	/**
	 * Vertex Substitution Changing the color or properties of a pixel Very
	 * important in detecting contamination (brown/green tints, cloudiness, etc.)
	 */
	private float VERTEX_SUBSTITUTION_COST = 5.0f;

	/**
	 * Vertex substitution cost, if the vertex point is not a water point
	 */
	private float VERTEX_SUBSTITUTION_COST_NA = 2.0f;

	/**
	 * Edge Substitution Changing the relationship (e.g. color difference) between
	 * pixels
	 */
	private float EDGE_SUBSTITUTION_COST = 2.0f;

	/**
	 * The maximum normal GED
	 */
	private static final float MAXIMUM_POSSIBLE_GED = 10_000_000;

	/**
	 * Constructs a GraphEditDistance with different cost weightings depending on
	 * the task being performed Which tailors the algorithms to task-specific
	 * priorities.
	 * 
	 * @param algorithmType
	 */
	public GraphEditDistance(ALGORITHM algorithmType) {

		if (algorithmType == ALGORITHM.CLASSIFY)
			ClassifyMode();
		else
			MatchMode();
	}

	/**
	 * Adapts this current GraphEditDistance instance to tailor the waitings for
	 * classification
	 */
	public void ClassifyMode() {
		EDGE_INSERTION_DELETION_COST = 2.0f;
		VERTEX_INSERTION_DELETION_COST = 5.0f;
		VERTEX_SUBSTITUTION_COST = 0.09f;
		VERTEX_SUBSTITUTION_COST_NA = 0.05f;
		EDGE_SUBSTITUTION_COST = 0.05f;
	}

	/**
	 * Adapts this current GraphEditDistance instance to tailor the waitings for
	 * matching
	 */
	public void MatchMode() {
		EDGE_INSERTION_DELETION_COST = 0.05f;
		VERTEX_INSERTION_DELETION_COST = 0.09f;
		VERTEX_SUBSTITUTION_COST = 5.0f;
		VERTEX_SUBSTITUTION_COST_NA = 2.0f;
		EDGE_SUBSTITUTION_COST = 2.0f;
	}

	/**
	 * This method calculates the "cost" or value of operations it would take to
	 * convert graphA to graphB Both graphs contain image data where each Vertex
	 * holds a point and each edge holds a weighting
	 * 
	 * @param graphA the graph being queried
	 * @param graphB the graph being compared with
	 * @return the value of the edit distance
	 */
	public float calculateGraphEditDistance(ImageGraph graphA, ImageGraph graphB) {
		float editDistance = 0.0f;

		// Vertex matching, matching vertices from graphA to graphB to compute costs
		Iterator<Vertex<Point>> graphAVertices = graphA.getGraph().vertices().iterator();
		// sort graph b vertices and edges to compare
		Iterator<Vertex<Point>> graphBVertices = graphB.getGraph().vertices().iterator();

		// Iterate through both to calculate vertex substitution cost
		while (graphAVertices.hasNext() && graphBVertices.hasNext()) {
			Point graphAPoint = graphAVertices.next().getElement();
			Point graphBPoint = graphBVertices.next().getElement();

			editDistance += vertexSubstitutionCost(graphAPoint, graphBPoint);
		}

		/*
		 * Add cost for all outstanding vertices that graphB does not have, or extra
		 * vertices that graphB has that graphA does not have They would need to be
		 * inserted or deleted from graphA
		 */
		editDistance += VERTEX_INSERTION_DELETION_COST
				* Math.abs(graphA.getVerticies().size() - graphB.getVerticies().size());

		// 2. Edge matching, matching edges from graphA to graphB to compute costs
		Iterator<Edge<Float>> graphAEdges = graphA.getGraph().edges().iterator();

		Iterator<Edge<Float>> graphBEdges = graphB.getGraph().edges().iterator();

		while (graphAEdges.hasNext() && graphBEdges.hasNext()) {
			Float graphAWeight = graphAEdges.next().getElement();
			Float graphBWeight = graphBEdges.next().getElement();

			editDistance += edgeSubstitutionCost(graphAWeight, graphBWeight);
		}

		/*
		 * Add cost for all outstanding edges that graphB does not have, or extra edges
		 * that graphB has that graphA does not have They would need to be inserted or
		 * deleted from graphA
		 */
		editDistance += EDGE_INSERTION_DELETION_COST
				* Math.abs(graphA.getGraph().numEdges() - graphB.getGraph().numEdges());

		/*
		 * linear scaling/ linear normalization which maps the values from one range (0
		 * to 10 000 000) to another range (0 to 1000) while maintaining the relative
		 * proportion if GED exceeds MAXIMUM_POSSIBLE_GED(10 000 000) (1000) then the
		 * distance is unreasonable
		 */
		return (editDistance / MAXIMUM_POSSIBLE_GED) * 1000;
	}

	/**
	 * This method determines how similar/dissimilar two points are
	 * 
	 * @param vertexA
	 * @param vertexB
	 * @return a float value representing similarity/ dissimilarity
	 */
	private float vertexSubstitutionCost(Point vertexA, Point vertexB) {
		float colourDifference = Point.colorDifference(vertexA.getColour(), vertexB.getColour());
		
		if (vertexA.isLikelyWater() && vertexB.isLikelyWater()) {
			// if both are likely water let the cost be less
			return colourDifference * VERTEX_SUBSTITUTION_COST_NA;
		} else {
			// if not likely water make the cost more
			return colourDifference * VERTEX_SUBSTITUTION_COST;
		}
	}

	/**
	 * This method determines how similar/dissimilar two edges are
	 * 
	 * @param edgeWeightA
	 * @param edgeWeightB
	 * @return a float value representing similarity/ dissimilarity
	 */
	private float edgeSubstitutionCost(Float edgeWeightA, Float edgeWeightB) {
		return (Math.abs(edgeWeightA - edgeWeightB)) * EDGE_SUBSTITUTION_COST;
	}

}
