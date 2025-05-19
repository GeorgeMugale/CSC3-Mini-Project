package acsse.csc3a.graph.algorithms;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;

import acsse.csc3a.analyser.ImageAnalyser;
import acsse.csc3a.graph.algorithms.MSTFeatures.Distance;
import acsse.csc3a.imagegraph.AbstractImageGraphProxy;
import acsse.csc3a.imagegraph.ImageGraph;
import acsse.csc3a.imagegraph.Point;
import acsse.csc3a.io.ImageIterator;
import acsse.csc3a.lists.ArrayList;
import acsse.csc3a.map.AdjacencyMap;
import acsse.csc3a.map.Map;

public class kNearestNeighbor {

	private BiConsumer<Double, Double> updateProgress;

	public kNearestNeighbor(BiConsumer<Double, Double> updateProgress) {
		this.updateProgress = updateProgress;
	}

	/**
	 * This inner class realizes the composite design pattern, which in a nutshell
	 * is a label distance composite known as a kclass because it is used for
	 * KNNearest calculation
	 * 
	 * @param <A> any label which will relate to a distance (similarity metric)
	 *            value
	 * @param <B> any comparable object which will be used to indicate the
	 *            similarity of graphs
	 */
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

	/**
	 * This method classifies an image into a category {@linkplain CATEGORY_TYPE}
	 * 
	 * @param inputGraph      the new graph being classified
	 * @param referenceGraphs the reference graphs used to classify
	 * @param K               the number of elements that will be left after this
	 *                        initial match list is pruned
	 * @return the most likely category which the image falls under
	 */
	public CATEGORY_TYPE classify(ImageGraph inputGraph, ImageIterator referenceGraphs, int K) {

		// first check if arguments are correct
		if (referenceGraphs == null || !referenceGraphs.hasNext() || K <= 0) {
			throw new IllegalArgumentException("Invalid input parameters");
		}

		// create a list to store msst features
		List<KClass<CATEGORY_TYPE, Distance>> classificationList = new ArrayList<>();

		// use Prim's MST to calculate the MST for the new graph image
		Prims_MST<Point> MST = new Prims_MST<>();
		inputGraph.setFeatures(MST.CalcMST(inputGraph.getGraph()));
		inputGraph.getFeatures().normalize();

		/*
		 * calculate the distance between the mst features of the reference graphs and
		 * the new image graph
		 */
		while (referenceGraphs.hasNextMSTFeature()) {
			MSTFeatures currentFeature = referenceGraphs.nextFeature();
			if (currentFeature != null) {
				classificationList.addLast(new KClass<CATEGORY_TYPE, Distance>(currentFeature.category_TYPE,
						MSTFeatures.calculateDistance(inputGraph.getFeatures(), currentFeature)));
			}
		}
		// close all references in the stream and for call garbage collector
		referenceGraphs.close();

		// sort the classified list
		Collections.sort(classificationList);

		/*
		 * get the first k element in the classified list(check that K is not more than
		 * the size of the classification list)
		 */
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

		/*
		 * initialize the best match, if no likely match it will by default be water
		 * from top view
		 */
		CATEGORY_TYPE bestMatch = CATEGORY_TYPE.ONLY_WATER_TOP_VIEW;
		int highestFrequency = Integer.MIN_VALUE;

		Iterator<CATEGORY_TYPE> iterator = frequencyMap.keySet().iterator();
		// iterate through all keys and find the one with the highest frequency
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
	 * This method finds the most similar match {@linkplain MATCH_TYPE} for a given
	 * graph
	 * 
	 * @apiNote This method also performs quick reject whereby If the feature
	 *          distance is large, we can immediately reject that reference as “too
	 *          different” without running full GED. This is don't by seeing if the
	 *          mst fingerprint falls bellow the average mst feature average
	 * @param inputGraph      the new graph being classified
	 * @param referenceGraphs the reference graphs used to classify
	 * @param K               the number of elements that will be left after this
	 *                        initial match list is pruned
	 * @param updateProgress  Represents a generic progress updater (which is a
	 *                        function that takes two doubles and returns nothing)
	 * @return the most likely similarity match which the image falls under
	 */
	public MATCH_TYPE match(ImageGraph inputGraph, ImageIterator referenceGraphs, int K) {
		// first check if arguments are correct
		if (referenceGraphs == null || !referenceGraphs.hasNext() || K <= 0) {
			throw new IllegalArgumentException("Invalid input parameters");
		}

		// create a list to store the composite required to store similarity quantifiers
		List<KClass<MATCH_TYPE, Double>> matchList = new ArrayList<>();

		GraphEditDistance GED = new GraphEditDistance(ALGORITHM.MATCH);
		Prims_MST<Point> mst = new Prims_MST<Point>();

		// calculate the mst features of the input graph is not set recalculate them
		if (inputGraph.getFeatures() == null) {
			inputGraph.setFeatures(mst.CalcMST(inputGraph.getGraph()));
			inputGraph.getFeatures().normalize();
		}

		// calculate the average features
		MSTFeatures avgFeatures = calcAverage(referenceGraphs.getCategory());

		// calculate average distance
		float average_d_appear = MSTFeatures.d_appear(inputGraph.getFeatures().getAppearanceFeatures(),
				avgFeatures.getAppearanceFeatures());
		float average_d_struct = MSTFeatures.d_struct(inputGraph.getFeatures().getStrcuturalFeatures(),
				avgFeatures.getStrcuturalFeatures());

		double count = 0.00;
		double numGraphs = (double) referenceGraphs.count();

		/*
		 * get GED of all graphs in loop and add them to a list of pairs that represent
		 * the label and the GED
		 */
		while (referenceGraphs.hasNext()) {
			// get the current image graph proxy from the reference data set
			AbstractImageGraphProxy proxy = referenceGraphs.next();
			
			progress(++count, numGraphs);

			// calculate the distance
			float current_d_appear = MSTFeatures.d_appear(inputGraph.getFeatures().getAppearanceFeatures(),
					proxy.getFeatures().getAppearanceFeatures());
			float current_d_strcut = MSTFeatures.d_struct(inputGraph.getFeatures().getStrcuturalFeatures(),
					proxy.getFeatures().getStrcuturalFeatures());

			// quick reject, Graph construction and GED calculation only performed on
			// relevant graphs
			if (current_d_appear < average_d_appear || current_d_strcut < average_d_struct) {
				// do resource heavy task of constructing ImageGraph
				ImageGraph imageGraph = proxy.getGraph();

				// calculate how different it is from the new image
				double distance = GED.calculateGraphEditDistance(inputGraph, imageGraph);
				// store the type of image and the difference
				matchList.addLast(new KClass<MATCH_TYPE, Double>(imageGraph.getLabel(), distance));
				// make the eligible for garbage collection
				imageGraph = null;
				System.gc();
			}

		}

		// close all references in the stream and for call garbage collector
		referenceGraphs.close();

		// sort the list in ascending order (from biggest to smallest)
		Collections.sort(matchList);

		/*
		 * get only the first K pairs in the list (check that K is not more than the
		 * size of the classification list)
		 */
		matchList = matchList.subList(0, Math.min(K, matchList.size()));

		/*
		 * create a map that will store the label and the frequency of occurrences and
		 * the weight of occurrences the list
		 */
		Map<MATCH_TYPE, Double> weightedVotes = new AdjacencyMap<>();

		// add the labels and the votes
		for (KClass<MATCH_TYPE, Double> kClass : matchList) {
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

		/*
		 * initialize the best match, if no likely match it will by default be water
		 * black meaning it cannot be matched
		 */
		MATCH_TYPE bestMatch = MATCH_TYPE.BLACK;
		double highestVote = Float.MIN_VALUE;

		/* Find the label with the most occurrences and least distance (highest vote) */
		Iterator<MATCH_TYPE> iterator = weightedVotes.keySet().iterator();
		// iterate through all keys and find the one with the highest vote
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
	
	private void progress(double count, double numGraphs) {
		double progress = ImageAnalyser.CURRENT_PROGRESS
				+ (((++count / numGraphs) * ImageAnalyser.TOTAL_PROGRESS) * 0.8);
		updateProgress.accept(progress, ImageAnalyser.TOTAL_PROGRESS);
	}

	/**
	 * This method calculates the average MSTFeatures for this current category of
	 * image graphs {@linkplain CATEGORY_TYPE}, used by {@link #classify()}
	 * 
	 * @param category_TYPE the category of the reference data set
	 * @return the average mst features
	 */
	public MSTFeatures calcAverage(CATEGORY_TYPE category_TYPE) {
		MSTFeatures averageFeatures = new MSTFeatures();
		/*
		 * initialize to smallest value, meaning if no reasonable average matches no GED
		 * will be computed
		 */
		averageFeatures.averageWeight = Float.MIN_VALUE;
		averageFeatures.edgeCount = Integer.MIN_VALUE;
		averageFeatures.totalWeight = Float.MIN_VALUE;
		averageFeatures.variance = Float.MIN_VALUE;

		try (ImageIterator iterator = new ImageIterator(category_TYPE);) {
			int count = 0;

			while (iterator.hasNextMSTFeature()) {
				count++;

				MSTFeatures currentFeature = iterator.nextFeature();

				if (currentFeature != null) {
					averageFeatures.averageWeight += currentFeature.averageWeight;
					averageFeatures.edgeCount += currentFeature.edgeCount;
					averageFeatures.totalWeight += currentFeature.totalWeight;
					averageFeatures.variance += currentFeature.variance;
				}
			}

			averageFeatures.averageWeight /= count;
			averageFeatures.edgeCount /= count;
			averageFeatures.totalWeight /= count;
			averageFeatures.variance /= count;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		averageFeatures.normalize();

		return averageFeatures;
	}

}
