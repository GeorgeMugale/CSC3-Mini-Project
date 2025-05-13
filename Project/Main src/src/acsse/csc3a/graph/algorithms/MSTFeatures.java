package acsse.csc3a.graph.algorithms;

import java.io.Serializable;
import java.util.List;

import acsse.csc3a.graph.Vertex;
import acsse.csc3a.lists.ArrayList;
import acsse.csc3a.map.Map;

public class MSTFeatures implements Serializable {

	/*
	 * Training/ know dataset results
	 * 
	 * meanTotal: 5816869.5 stdTotal: 6845017.5 meanAvg: 132.75655 stdAvg: 26.181938
	 * meanVar: 6436.2017 stdVar: 3788.474 meanEdge: 44666.05 stdEdge: 52473.773
	 */

	public float totalWeight;
	public float averageWeight;
	public float variance;
	public int edgeCount;
	transient public Map<Vertex<?>, Integer> degreeMap;
	public MATCH_TYPE match_TYPE;
	public CATEGORY_TYPE category_TYPE;
	public static float meanTotal = 5816869.5f;
	public static float stdTotal = 6845017.5f;

	public static float meanAvg = 132.75655f;
	public static float stdAvg = 26.181938f;

	public static float meanVar = 6436.2017f;
	public static float stdVar = 3788.474f;

	public static float meanEdge = 44666.05f;
	public static float stdEdge = 52473.773f;

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

		public static List<MSTFeatures> normalizeAll(List<MSTFeatures> featuresList) {
			int n = featuresList.size();

			// Step 1: Collect feature arrays
			float[] totalWeights = new float[n];
			float[] averageWeights = new float[n];
			float[] variances = new float[n];
			int[] edgeCounts = new int[n];

			for (int i = 0; i < n; i++) {
				MSTFeatures f = featuresList.get(i);
				totalWeights[i] = f.totalWeight;
				averageWeights[i] = f.averageWeight;
				variances[i] = f.variance;
				edgeCounts[i] = f.edgeCount;
			}

			// Step 2: Compute means and std deviation
			float meanTotal = mean(totalWeights);
			float stdTotal = stdDev(totalWeights, meanTotal);

			float meanAvg = mean(averageWeights);
			float stdAvg = stdDev(averageWeights, meanAvg);

			float meanVar = mean(variances);
			float stdVar = stdDev(variances, meanVar);

			float meanEdge = mean(edgeCounts);
			float stdEdge = stdDev(edgeCounts, meanEdge);

			// Step 3: Normalize each vector
			List<MSTFeatures> normalized = new ArrayList<>();
			for (MSTFeatures f : featuresList) {
				MSTFeatures norm = new MSTFeatures();
				norm.totalWeight = (f.totalWeight - meanTotal) / stdTotal;
				norm.averageWeight = (f.averageWeight - meanAvg) / stdAvg;
				norm.variance = (f.variance - meanVar) / stdVar;
				norm.edgeCount = Math.round((f.edgeCount - meanEdge) / stdEdge); // You can keep as float if needed
				normalized.add(norm);
			}

			System.out.printf(
					"\nmeanTotal: %s \nstdTotal: %s \nmeanAvg: %s \nstdAvg: %s \nmeanVar: %s \nstdVar: %s \nmeanEdge: %s \nstdEdge: %s",
					meanTotal, stdTotal, meanAvg, stdAvg, meanVar, stdVar, meanEdge, stdEdge);

			return normalized;
		}

		public static MSTFeatures normalizeNew(MSTFeatures newMST, float meanTotal, float stdTotal, float meanAvg,
				float stdAvg, float meanVar, float stdVar, float meanEdge, float stdEdge) {
			MSTFeatures norm = new MSTFeatures();
			norm.totalWeight = (newMST.totalWeight - meanTotal) / stdTotal;
			norm.averageWeight = (newMST.averageWeight - meanAvg) / stdAvg;
			norm.variance = (newMST.variance - meanVar) / stdVar;
			norm.edgeCount = Math.round((newMST.edgeCount - meanEdge) / stdEdge);

			return norm;
		}

		// Utility methods
		public static float mean(float[] values) {
			double sum = 0;
			for (double v : values)
				sum += v;
			return (float) (sum / values.length);
		}

		public static float mean(int[] values) {
			double sum = 0;
			for (int v : values)
				sum += v;
			return (float) (sum / values.length);
		}

		public static float stdDev(float[] values, float mean) {
			double sum = 0;
			for (double v : values)
				sum += Math.pow(v - mean, 2);
			return (float) Math.sqrt(sum / values.length);
		}

		public static float stdDev(int[] values, float mean) {
			double sum = 0;
			for (int v : values)
				sum += Math.pow(v - mean, 2);
			return (float) Math.sqrt(sum / values.length);
		}
	}

}
