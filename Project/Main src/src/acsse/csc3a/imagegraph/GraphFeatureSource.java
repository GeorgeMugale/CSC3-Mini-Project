package acsse.csc3a.imagegraph;

import acsse.csc3a.graph.algorithms.MSTFeatures;

/**
 * This interface serves as a contract for any object that represents an
 * ImageGraph which can provide its features, ensuring consistency in how
 * ImageGraphs are handled.
 */
public interface GraphFeatureSource {
	
	/**
	 * Returns an features for the current image graph
	 * @return the features of Minimum spanning tree
	 */
	public MSTFeatures getFeatures();
}
