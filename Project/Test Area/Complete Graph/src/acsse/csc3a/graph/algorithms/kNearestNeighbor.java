package acsse.csc3a.graph.algorithms;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import acsse.csc3a.imagegraph.ImageGraph;
import acsse.csc3a.lists.ArrayList;
import acsse.csc3a.lists.PositionalList;
import acsse.csc3a.map.AdjacencyMap;
import acsse.csc3a.map.Map;

public class kNearestNeighbor {

	private static class KClass<A, B extends Comparable<B>> implements Comparable<KClass<A, B>> {
		public A label;
		public B distance;

		public KClass(A label, B distance) {
			this.label = label;
			this.distance = distance;
		}

		@Override
		public int compareTo(KClass<A, B> o) {
			return this.distance.compareTo(o.distance);
		}
	}

	/**
	 * Classify Image Type (Structure-focused)Identify the container context (water only, cup, bucket, etc.).
	 * @param inputGraph
	 * @param referenceGraphs
	 * @param K
	 * @return
	 */
	public static String classify(ImageGraph inputGraph, PositionalList<ImageGraph> referenceGraphs, int K) {
		
		return WATER_IMAGE_TYPE.ONLY_WATER_SIDE_VIEW.toString();
		
	}

	/**
	 * Determine Water Quality (Color-focused)Decide if the water is clean or dirty.
	 * @param inputGraph
	 * @param referenceGraphs
	 * @param K
	 * @return
	 */
	public static String match(ImageGraph inputGraph, PositionalList<ImageGraph> referenceGraphs, int K) {

		// first check if arguments are correct
		if (referenceGraphs == null || referenceGraphs.isEmpty() || K <= 0) {
			throw new IllegalArgumentException("Invalid input parameters");
		}

		List<KClass<String, Double>> classificationList = new ArrayList<>(referenceGraphs.size());
		GraphEditDistance GED = new GraphEditDistance(ALGORITHM.MATCH);

		// get GED of all graphs in loop and add them to a list of pairs that represent
		// the label and the GED
		for (ImageGraph imageGraph : referenceGraphs) {
			double distance = GED.calculateGraphEditDistance(inputGraph, imageGraph);
			classificationList.addLast(new KClass<>(imageGraph.getLabel(), distance));
		}

		// sort the list in ascending order (from biggest to smallest)
		Collections.sort(classificationList);

		// get only the first K pairs in the list (check that K is not more than the
		// size of the classfification lst)
		classificationList = classificationList.subList(0, Math.min(K, classificationList.size()));

		// create a map that will store the label and the frequency of occurrences and
		// the weight of occurrences
		// the list
		Map<String, Double> weightedVotes = new AdjacencyMap<>();

		// add the labels and the votes
		for (KClass<String, Double> kClass : classificationList) {
			Double currentVote = weightedVotes.get(kClass.label);
			/*
			 * weight the frequency so, best matches hold twice as much weight when
			 * weighting avoid division by zero
			 */
			double weight = 1.0 / (kClass.distance + Double.MIN_VALUE);

			// if it is a new entry
			if (currentVote == null) {
				weightedVotes.put(kClass.label, weight);
			} else {
				weightedVotes.put(kClass.label, currentVote + weight);
			}
		}

		// FindException the label with the most occurrences and least distance (highest
		// vote)
		String bestMatch = "";
		double highestVote = -1.0;

		Iterator<String> iterator = weightedVotes.keySet().iterator();
		// iterate through all keys and find check each vote
		while (iterator.hasNext()) {
			String currentMatch = iterator.next();
			double currentVote = weightedVotes.get(currentMatch);
			if (currentVote > highestVote) {
				highestVote = currentVote;
				bestMatch = currentMatch;
			}
		}

		return bestMatch;
	}

}
