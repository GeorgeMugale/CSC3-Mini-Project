package acsse.csc3a.graph.algorithms;

/**
 * Classifies the water image graph
 */
public enum WATER_IMAGE_TYPE {
	/**
	 * An image that only contains water from the top view
	 */
	ONLY_WATER_TOP_VIEW,

	/**
	 * An image that contains water from size view, so only the water and the
	 * atmosphere is visible
	 */
	ONLY_WATER_SIDE_VIEW,

	/**
	 * An image that contains water in a transparent container
	 */
	WATER_IN_TRANSPARENT,

	/**
	 * An image that contains water in an opaque container
	 */
	WATER_IN_OPAQUE,
}
