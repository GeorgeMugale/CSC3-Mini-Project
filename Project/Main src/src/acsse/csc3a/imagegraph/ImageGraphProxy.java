package acsse.csc3a.imagegraph;

import java.awt.image.BufferedImage;
import java.io.File;

import acsse.csc3a.graph.algorithms.CATEGORY_TYPE;
import acsse.csc3a.graph.algorithms.MATCH_TYPE;
import acsse.csc3a.graph.algorithms.MSTFeatures;

/**
 * This class realizes the Lazy loading technique of the Proxy/ Surrogate design
 * pattern. 
 * It acts as a Gatekeeper and only does the memory and time consuming
 * job of creating a graph when the {@link #getGraph()} method is called,
 * ensuring it’s only instantiated and used when truly needed, however this can
 * only be done once
 */
public class ImageGraphProxy implements AbstractImageGraphProxy {

	private File file;
	private BufferedImage image;
	private MSTFeatures features;
	private boolean loaded = false;

	/**
	 * Creates an ImageGraphProxy which is ready to create a single ImageGraph
	 * instance
	 * 
	 * @param image    the buffered image
	 * @param file     the file associated with the image
	 * @param features the features of the image graph, to be assigned if the image
	 *                 graph is constructed
	 */
	public ImageGraphProxy(BufferedImage image, File file, MSTFeatures features) {

		this.image = image;
		this.file = file;
		this.features = features;

	}

	@Override
	public MSTFeatures getFeatures() {
		// TODO Auto-generated method stub
		return features;
	}

	/**
	 *
	 * @throws IllegalStateException    when the graph has already been loaded
	 * @throws IllegalArgumentException when the ImageGraph's directory could not
	 *                                  map the graph to a valid CATEGORY_TYPE
	 */
	@Override
	public ImageGraph getGraph() throws IllegalStateException, IllegalArgumentException {
		// TODO Auto-generated method stub

		// if the graph has already been loaded (created)
		if (loaded)
			throw new IllegalStateException(
					"Error: This image graph has already been loaded, image graphs can only be loaded once from this scource");

		// construct and initialize the graph
		ImageGraph graph = new ImageGraph(image);

		String name = file.getName().toLowerCase();
		MATCH_TYPE label = MATCH_TYPE.GREEN;

		// get match type
		if (name.contains("undrink")) {
			label = MATCH_TYPE.RED;
		} else if (name.contains("dirty") && !name.contains("undrink")) {
			label = MATCH_TYPE.ORANGE;
		} else if (name.contains("moderate")) {
			label = MATCH_TYPE.YELLOW;
		}

		graph.setLabel(label);

		boolean isCategorySet = false;

		// get category type
		String path = file.getAbsolutePath();
		for (CATEGORY_TYPE type : CATEGORY_TYPE.values()) {
			if (path.contains(type.toString())) {
				graph.setWaterImageType(type);
				isCategorySet = true;
				break;
			}
		}

		// if no category type was found
		if (!isCategorySet)
			throw new IllegalArgumentException("Invalid directory structure for file: " + path);

		// set features

		graph.setFeatures(features);

		return graph;
	}

}
