package acsse.csc3a.graph.algorithms;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import acsse.csc3a.graph.Vertex;
import acsse.csc3a.lists.ArrayList;
import acsse.csc3a.map.Map;

public class MSTFeatures implements Serializable {

	/*
	 * Training/ know dataset results 
	 * meanTotal: 4516127.5 
	 * stdTotal: 5961088.5
	 * meanAvg: 133.4319 
	 * stdAvg: 26.10389 
	 * meanVar: 6503.956 
	 * stdVar: 3798.0818
	 * meanEdge: 34397.035 
	 * stdEdge: 45944.926
	 */
	public static float meanTotal = 4516127.5f;
	public static float stdTotal = 5961088.5f;

	public static float meanAvg = 133.4319f;
	public static float stdAvg = 26.10389f;

	public static float meanVar = 6503.956f;
	public static float stdVar = 3798.0818f;

	public static float meanEdge = 34397.035f;
	public static float stdEdge = 45944.926f;

	public float totalWeight;
	public float averageWeight;
	public float variance;
	public int edgeCount;
	transient public Map<Vertex<?>, Integer> degreeMap;
	public MATCH_TYPE match_TYPE;
	public CATEGORY_TYPE category_TYPE;

	@Override
	public String toString() {
		return "Total Weight: " + totalWeight + "\nAverage Weight: " + averageWeight + "\nVariance: " + variance
				+ "\nEdge Count: " + edgeCount + "\nDegree Map: " + degreeMap;
	}

	public f_struct getStrcuturalFeatures() {

		f_struct feature = new f_struct();

		feature.edgeCount = edgeCount;
		feature.variance = variance;

		return feature;
	}

	public f_appear getAppearanceFeatures() {
		f_appear feature = new f_appear();

		feature.averageWeight = averageWeight;
		feature.totalWeight = totalWeight;

		return feature;
	}

	public static float euclideanDistance(float x1, float x2, float y1, float y2) {
		return (float) Math.sqrt(Math.pow(x1 - x2, 2.00) + Math.pow(y1 - y2, 2.00));
	}

	public static float d_appear(f_appear f_new, f_appear f_ref) {
		return euclideanDistance(f_new.totalWeight, f_ref.totalWeight, f_new.averageWeight, f_ref.averageWeight);
	}

	public static float d_struct(f_struct f_new, f_struct f_ref) {
		return euclideanDistance(f_new.variance, f_ref.variance, f_new.edgeCount, f_ref.edgeCount);
	}

	public static Distance calculateDistance(MSTFeatures f_new, MSTFeatures f_ref) {

		f_appear f_appear_new = f_new.getAppearanceFeatures();
		f_appear f_appear_ref = f_ref.getAppearanceFeatures();

		f_struct f_struct_new = f_new.getStrcuturalFeatures();
		f_struct f_struct_ref = f_ref.getStrcuturalFeatures();

		float d_appear = d_appear(f_appear_new, f_appear_ref);
		float d_struct = d_struct(f_struct_new, f_struct_ref);

		return new Distance(d_appear, d_struct);
	}

	protected static class f_struct {
		public float variance;
		public float edgeCount;
	}

	protected static class f_appear {
		public float totalWeight;
		public float averageWeight;
	}

	public void normalize() {
		totalWeight = (totalWeight - meanTotal) / stdTotal;
		averageWeight = (averageWeight - meanAvg) / stdAvg;
		variance = (variance - meanVar) / stdVar;
		edgeCount = Math.round((edgeCount - meanEdge) / stdEdge);
	}

	protected static class Distance implements Comparable<Distance> {
		public float d_appear;
		public float d_struct;

		public Distance(float d_appear, float d_struct) {
			this.d_appear = d_appear;
			this.d_struct = d_struct;
		}

		@Override
		public int compareTo(Distance o) {
			// TODO Auto-generated method stub
			float thisWeighted = 0.7f * this.d_struct + 0.3f * this.d_appear;
			float otherWeighted = 0.7f * o.d_struct + 0.3f * o.d_appear;
			return Float.compare(thisWeighted, otherWeighted);

		}
	}

	public static class MSTNormalizer {

		public static String getStardDeveations(List<MSTFeatures> featuresList) {
			int n = featuresList.size();

			// Step 1: Collect feature arrays
			Float[] totalWeights = new Float[n];
			Float[] averageWeights = new Float[n];
			Float[] variances = new Float[n];
			Integer[] edgeCounts = new Integer[n];

			for (int i = 0; i < n; i++) {
				MSTFeatures f = featuresList.get(i);
				if (f != null) {
					totalWeights[i] = f.totalWeight;
					averageWeights[i] = f.averageWeight;
					variances[i] = f.variance;
					edgeCounts[i] = f.edgeCount;
				} 
			}

			float meanTotal = calcMean(totalWeights);
			float stdTotal = calcStandardDeviation(totalWeights, meanTotal);

			float meanAvg = calcMean(averageWeights);
			float stdAvg = calcStandardDeviation(averageWeights, meanAvg);

			float meanVar = calcMean(variances);
			float stdVar = calcStandardDeviation(variances, meanVar);

			float meanEdge = calcMean(edgeCounts);
			float stdEdge = calcStandardDeviation(edgeCounts, meanEdge);

			return String.format(
					"\nmeanTotal: %s \nstdTotal: %s \nmeanAvg: %s \nstdAvg: %s \nmeanVar: %s \nstdVar: %s \nmeanEdge: %s \nstdEdge: %s",
					meanTotal, stdTotal, meanAvg, stdAvg, meanVar, stdVar, meanEdge, stdEdge);

		}

		/**
		 * This method calculates the means and standard deveations normalizes a list of
		 * MSTFeatures and normalizes them
		 * 
		 * @param featuresList
		 * @return
		 */
		public static List<MSTFeatures> normalizeAll(List<MSTFeatures> featuresList) {
			int n = featuresList.size();

			// Step 1: Collect feature arrays
			Float[] totalWeights = new Float[n];
			Float[] averageWeights = new Float[n];
			Float[] variances = new Float[n];
			Integer[] edgeCounts = new Integer[n];

			for (int i = 0; i < n; i++) {
				MSTFeatures f = featuresList.get(i);
				if (f != null) {
					totalWeights[i] = f.totalWeight;
					averageWeights[i] = f.averageWeight;
					variances[i] = f.variance;
					edgeCounts[i] = f.edgeCount;
				}
			}

			// Step 2: Compute means and std deviation
			float meanTotal = calcMean(totalWeights);
			float stdTotal = calcStandardDeviation(totalWeights, meanTotal);

			float meanAvg = calcMean(averageWeights);
			float stdAvg = calcStandardDeviation(averageWeights, meanAvg);

			float meanVar = calcMean(variances);
			float stdVar = calcStandardDeviation(variances, meanVar);

			float meanEdge = calcMean(edgeCounts);
			float stdEdge = calcStandardDeviation(edgeCounts, meanEdge);

			// Step 3: Normalize each feature
			List<MSTFeatures> normalized = new ArrayList<>();
			for (MSTFeatures f : featuresList) {
				MSTFeatures norm = new MSTFeatures();
				norm.totalWeight = (f.totalWeight - meanTotal) / stdTotal;
				norm.averageWeight = (f.averageWeight - meanAvg) / stdAvg;
				norm.variance = (f.variance - meanVar) / stdVar;
				norm.edgeCount = Math.round((f.edgeCount - meanEdge) / stdEdge);

				normalized.add(norm);
			}

			System.out.printf(
					"\nmeanTotal: %s \nstdTotal: %s \nmeanAvg: %s \nstdAvg: %s \nmeanVar: %s \nstdVar: %s \nmeanEdge: %s \nstdEdge: %s",
					meanTotal, stdTotal, meanAvg, stdAvg, meanVar, stdVar, meanEdge, stdEdge);

			return normalized;
		}

		/**
		 * This method takes a new MSTFeature an normalizes its values so it can be used
		 * for subsequent calculations with the trained data (whihc is also normalized)
		 * 
		 * @param newMST new mts feature being normalized
		 * @return a normalized mstFeature
		 */
		public static MSTFeatures normalizeNew(MSTFeatures newMST) {
			MSTFeatures norm = new MSTFeatures();
			norm.totalWeight = (newMST.totalWeight - meanTotal) / stdTotal;
			norm.averageWeight = (newMST.averageWeight - meanAvg) / stdAvg;
			norm.variance = (newMST.variance - meanVar) / stdVar;
			norm.edgeCount = Math.round((newMST.edgeCount - meanEdge) / stdEdge);

			return norm;
		}

		/**
		 * This method calculates the mean of an array of values
		 * 
		 * @param <T>    any number
		 * @param values an array of values
		 * @return the mean
		 */
		public static <T extends Number> float calcMean(T[] values) {
			if (values == null || values.length == 0) {
				throw new IllegalArgumentException("Input array must not be null or empty");
			}

			double tot = 0.0;
			for (T num : values) {
				if (num != null) {
					tot += num.doubleValue(); // Convert any Number to double
				}
			}

			return (float) (tot / values.length);
		}

		/**
		 * This method calculates the standard deviation of an array of values
		 * 
		 * @param <T>    any number
		 * @param values an array of values
		 * @return the mean
		 */
		public static <T extends Number> float calcStandardDeviation(T[] values, float mean) {
			if (values == null || values.length == 0) {
				throw new IllegalArgumentException("Input array must not be null or empty");
			}

			double sum = 0.0;
			for (T num : values) {
				sum += Math.pow(num.doubleValue() - mean, 2);
			}
			return (float) Math.sqrt(sum / values.length);
		}
	}

}
