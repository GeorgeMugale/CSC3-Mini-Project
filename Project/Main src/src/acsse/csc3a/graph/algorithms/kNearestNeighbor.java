package acsse.csc3a.graph.algorithms;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;

import acsse.csc3a.graph.algorithms.MSTFeatures.Distance;
import acsse.csc3a.graph.algorithms.MSTFeatures.MSTNormalizer;
import acsse.csc3a.imagegraph.ImageGraph;
import acsse.csc3a.imagegraph.Point;
import acsse.csc3a.io.ImageIterator;
import acsse.csc3a.lists.ArrayList;
import acsse.csc3a.map.AdjacencyMap;
import acsse.csc3a.map.Map;

public class kNearestNeighbor {

	public static class KClass<A, B extends Comparable<B>> implements Comparable<KClass<A, B>> {
		public A label;
		public B distance;

		public KClass(A label, B distance) {
			this.label = label;
			this.distance = distance;
		}

		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return String.format("KClass{label: %s distance: %s}", label, distance);
		}

		@Override
		public int compareTo(KClass<A, B> o) {
			return this.distance.compareTo(o.distance);
		}
	}

	public static CATEGORY_TYPE classify(ImageGraph inputGraph, ImageIterator referenceGraphs, int K) {

		if (referenceGraphs == null || !referenceGraphs.hasNext() || K <= 0) {
			throw new IllegalArgumentException("Invalid input parameters");
		}

		List<KClass<CATEGORY_TYPE, Distance>> classificationList = new ArrayList<>();
		Prims_MST<Point> MST = new Prims_MST<>();
		MSTFeatures inputGraphFeatures = MST.CalcMST(inputGraph.getGraph());

		while (referenceGraphs.hasNextMSTFeature()) {
			MSTFeatures currentFeature = referenceGraphs.nextFeature();

			if (currentFeature != null) {
				classificationList.addLast(new KClass<CATEGORY_TYPE, Distance>(currentFeature.category_TYPE,
						MSTFeatures.calculateDistance(inputGraphFeatures, currentFeature)));
			}
		}

		referenceGraphs = null;
		System.gc();

		Collections.sort(classificationList);

		classificationList = classificationList.subList(0, Math.min(K, classificationList.size()));

		// create a map that will store the label and the frequency of occurrences
		Map<CATEGORY_TYPE, Integer> frequencyMap = new AdjacencyMap<>();

		// add the labels and the votes
		for (KClass<CATEGORY_TYPE, Distance> kClass : classificationList) {
			Integer currentFrequency = frequencyMap.get(kClass.label);
			// if it is a new entry
			if (currentFrequency == null) {
				frequencyMap.put(kClass.label, 1);
			}
		}

		CATEGORY_TYPE bestMatch = CATEGORY_TYPE.ONLY_WATER_TOP_VIEW;
		int highestFrequency = Integer.MIN_VALUE;

		Iterator<CATEGORY_TYPE> iterator = frequencyMap.keySet().iterator();
		// iterate through all keys and find check each vote
		while (iterator.hasNext()) {
			CATEGORY_TYPE currentMatch = iterator.next();
			int currentFrequency = frequencyMap.get(currentMatch);
			if (currentFrequency > highestFrequency) {
				highestFrequency = currentFrequency;
				bestMatch = currentMatch;
			}
		}

		return bestMatch;
	}

	/**
	 * Determine Water Quality (Color-focused)Decide if the water is clean or dirty.
	 * 
	 * @param inputGraph
	 * @param referenceGraphs
	 * @param K
	 * @param spinner
	 * @return
	 */
	public static MATCH_TYPE match(ImageGraph inputGraph, Iterator<ImageGraph> referenceGraphs, int K) {
		// first check if arguments are correct
		if (referenceGraphs == null || !referenceGraphs.hasNext() || K <= 0) {
			throw new IllegalArgumentException("Invalid input parameters");
		}

		List<KClass<MATCH_TYPE, Double>> classificationList = new ArrayList<>();
		GraphEditDistance GED = new GraphEditDistance(ALGORITHM.MATCH);

		/*
		 * get GED of all graphs in loop and add them to a list of pairs that represent
		 * the label and the GED
		 */
		while (referenceGraphs.hasNext()) {
			ImageGraph imageGraph = referenceGraphs.next();
			double distance = GED.calculateGraphEditDistance(inputGraph, imageGraph);
			classificationList.addLast(new KClass<MATCH_TYPE, Double>(imageGraph.getLabel(), distance));
			imageGraph = null;
			System.gc();
		}

		referenceGraphs = null;
		System.gc();

		// sort the list in ascending order (from biggest to smallest)
		Collections.sort(classificationList);

		classificationList = classificationList.subList(0, Math.min(K, classificationList.size()));

		/*
		 * get only the first K pairs in the list (check that K is not more than the
		 * size of the classfification lst)
		 */
		classificationList = classificationList.subList(0, Math.min(K, classificationList.size()));

		// create a map that will store the label and the frequency of occurrences and
		// the weight of occurrences
		// the list
		Map<MATCH_TYPE, Double> weightedVotes = new AdjacencyMap<>();

		// add the labels and the votes
		for (KClass<MATCH_TYPE, Double> kClass : classificationList) {
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

		// Find the label with the most occurrences and least distance (highest
		// vote)
		MATCH_TYPE bestMatch = MATCH_TYPE.BLACK;
		double highestVote = -1.0;

		Iterator<MATCH_TYPE> iterator = weightedVotes.keySet().iterator();
		// iterate through all keys and find check each vote
		while (iterator.hasNext()) {
			MATCH_TYPE currentMatch = iterator.next();
			double currentVote = weightedVotes.get(currentMatch);
			if (currentVote > highestVote) {
				highestVote = currentVote;
				bestMatch = currentMatch;
			}
		}

		return bestMatch;
	}

}
