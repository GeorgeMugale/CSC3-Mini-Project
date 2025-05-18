package acsse.csc3a.imagegraph;

/**
 * This interface outlines the essential behavior if creating an ImageGraph
 * which each class that realizes it must support
 * Ensuring that an image graph can be returned
 */
public interface AbstractImageGraphProxy extends GraphFeatureSource {

	/**
	 * This method creates an image graph on demand
	 * @return an image graph
	 */
	public ImageGraph getGraph();

}
