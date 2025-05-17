package acsse.csc3a.trainer;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

import javax.imageio.ImageIO;

import acsse.csc3a.graph.algorithms.CATEGORY_TYPE;
import acsse.csc3a.graph.algorithms.MATCH_TYPE;
import acsse.csc3a.graph.algorithms.MSTFeatures;
import acsse.csc3a.graph.algorithms.MSTFeatures.MSTNormalizer;
import acsse.csc3a.graph.algorithms.Prims_MST;
import acsse.csc3a.imagegraph.ImageGraph;
import acsse.csc3a.imagegraph.Point;
import acsse.csc3a.io.ImageIterator;
import acsse.csc3a.lists.ArrayList;
import acsse.csc3a.map.AdjacencyMap;
import acsse.csc3a.map.Map;

public class DataTrainer {

	/**
	 * This methods calculates the msts of all image graphs of that category and
	 * writes these msts to a dat file in that categories folder
	 * 
	 * @param category_TYPE - the category of reference data images being analyzed
	 */
	public void preComputeMSTs(CATEGORY_TYPE category_TYPE) {

		try {

			File imageDir = new File(
					"C:\\Users\\GEORGE MUGALE\\Desktop\\CS3 Mini Project\\Project\\Main src\\data\\reference-data"
							+ category_TYPE.toString());
			Map<String, MSTFeatures> mstMap = new AdjacencyMap<>();
			Prims_MST<Point> mstcalc = new Prims_MST<>();

			if (!imageDir.exists()) {
				System.out.println("Directory does not exist: " + imageDir.getAbsolutePath());
				return;
			}

			if (!imageDir.isDirectory()) {
				System.out.println("Path is not a directory: " + imageDir.getAbsolutePath());
				return;
			}

			if (!imageDir.canRead()) {
				System.out.println("Cannot read directory: " + imageDir.getAbsolutePath());
				return;
			}

			// 2. Get filtered files with null check
			File[] imageFiles = imageDir.listFiles((dir, name) -> {
				String lowerName = name.toLowerCase();
				return lowerName.endsWith(".jpg") || lowerName.endsWith(".png") || lowerName.endsWith(".jpeg");
			});

			if (imageFiles == null) {
				System.out.println("Error accessing directory contents");
				return;
			}

			if (imageFiles.length == 0) {
				System.out.println("No image files found in directory");
				return;
			}

			try {

				for (File file : imageFiles) {

					String fname = file.getName();
					System.out.println("printed " + file.getAbsolutePath());
					BufferedImage image = ImageIO.read(file);
					ImageGraph graph = new ImageGraph(image);

					if (fname.contains("dirty") && !fname.contains("undrink")) {
						graph.setLabel(MATCH_TYPE.ORANGE);
					} else if (fname.contains("undrink")) {
						graph.setLabel(MATCH_TYPE.RED);
					} else if (fname.contains("moderate")) {
						graph.setLabel(MATCH_TYPE.YELLOW);
					} else {
						graph.setLabel(MATCH_TYPE.GREEN);
					}

					graph.setWaterImageType(category_TYPE);

					MSTFeatures features = mstcalc.CalcMST(graph.getGraph());

					features.category_TYPE = graph.getWaterImageType();
					features.match_TYPE = graph.getLabel();
					features.degreeMap = null;

					graph = null;
					System.gc();

					mstMap.put(new File(file.getParent()).getName() + file.getName(), features);

				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}

			File outFile = new File(
					"C:\\Users\\GEORGE MUGALE\\Desktop\\CS3 Mini Project\\Project\\Main src\\data\\reference-data\\"
							+ category_TYPE.toString() + "\\precomputed-mst-features.dat");

			try (FileOutputStream fos = new FileOutputStream(outFile);
					BufferedOutputStream bos = new BufferedOutputStream(fos);
					ObjectOutputStream oos = new ObjectOutputStream(bos);) {

				oos.writeObject(mstMap);

			} catch (IOException e) {
				e.printStackTrace();
				System.out.println(e.getMessage());
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * This calculates mean and standard deviation of each mst feature across the
	 * entire feature data set, if the reference data set is changes significantly
	 * this will need to be recomputed because it affects how "normal" or "outlier"
	 * a new graph looks (mst features)
	 * 
	 * After this the static mean and standard deviation values of the MSTFeature
	 * class can be updated to reflect the new global state of mst features
	 */
	public void calcMeansAndStdDevs() {

		try (ImageIterator iterator = new ImageIterator();) {

			List<MSTFeatures> globalFeatureList = new ArrayList<>();

			while (iterator.hasNextMSTFeature()) {
				globalFeatureList.add(iterator.nextFeature());
			}

			MSTNormalizer.normalizeAll(globalFeatureList);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * This method normalizes precomputed mst features stored in a dat file of the
	 * specified category
	 * 
	 * @see #calcMeansAndStdDevs(), to calculate means and standard deviations and
	 *      manually add them to the class, so normalization can be relative to the
	 *      current reference data set
	 * @param category_TYPE specified category
	 */
	public void normalizePrecomputedMSTFeatures(CATEGORY_TYPE category_TYPE) {
		try (ImageIterator iterator = new ImageIterator(category_TYPE);) {

			for (String key : ImageIterator.mstFeatures.keySet()) {

				MSTFeatures feature = ImageIterator.mstFeatures.get(key);

				feature.normalize();

			}

			File outFile = new File(
					"C:\\Users\\GEORGE MUGALE\\Desktop\\CS3 Mini Project\\Project\\Main src\\data\\reference-data\\"
							+ category_TYPE.toString() + "\\precomputed-mst-features.dat");

			try (FileOutputStream fos = new FileOutputStream(outFile);
					BufferedOutputStream bos = new BufferedOutputStream(fos);
					ObjectOutputStream oos = new ObjectOutputStream(bos);) {

				oos.writeObject(ImageIterator.mstFeatures);

				System.out.println(ImageIterator.mstFeatures.size());

			} catch (IOException e) {
				e.printStackTrace();
				System.out.println(e.getMessage());
			}

			iterator.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
