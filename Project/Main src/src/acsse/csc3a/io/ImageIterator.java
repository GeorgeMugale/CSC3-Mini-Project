package acsse.csc3a.io;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

import javax.imageio.ImageIO;

import acsse.csc3a.graph.algorithms.CATEGORY_TYPE;
import acsse.csc3a.graph.algorithms.MSTFeatures;
import acsse.csc3a.imagegraph.AbstractImageGraphProxy;
import acsse.csc3a.imagegraph.ImageGraphProxy;
import acsse.csc3a.lists.ArrayList;
import acsse.csc3a.map.AbstractMap;
import acsse.csc3a.map.AdjacencyMap;

/**
 * A class that abstracts the process of traversing through a Image Graphs and
 * their associated MSTFeatures, using the Lazy Iterator approach
 */
public class ImageIterator implements Iterator<AbstractImageGraphProxy>, Closeable {
	private final File[] files;
	public static AbstractMap<String, MSTFeatures> mstFeatures = new AdjacencyMap<>();
	private int currentIndex = 0;
	private int featureIndex = 0;
	private final CATEGORY_TYPE categoryType;
	private final boolean iterateAllData;
	private float skipChance = 0f;
	private final Random random = new Random();
	private boolean closed = false;
	private boolean hasNext = true;

	/**
	 * Constructs a default iterator to traverse the entire data set
	 * 
	 * @throws IOException
	 */
	public ImageIterator() throws IOException {
		this(null, true);
	}

	/**
	 * Constructs an iterator for the specified category from the data set
	 * 
	 * @param categoryType the category of the data set being traversed
	 * @throws IOException
	 */
	public ImageIterator(CATEGORY_TYPE categoryType) throws IOException {
		this(categoryType, false);
	}

	/**
	 * Constructs an iterator for the specified category from the data set, and
	 * specifies traversal for the entire data set or a subset
	 * 
	 * @param categoryType   categoryType the category of the data set being
	 *                       traversed
	 * @param iterateAllData specifies iteration over entire data set or a sub set
	 * @throws IOException
	 */
	private ImageIterator(CATEGORY_TYPE categoryType, boolean iterateAllData) throws IOException {
		this.categoryType = categoryType;
		this.iterateAllData = iterateAllData;
		File imageDir = determineImageDirectory();
		// stores the relevant files readily available for traversal
		this.files = validateAndGetImageFiles(imageDir);
		// stores the relevant MSTFearures readily available for querying
		initMSTFeatures();
		// advances to the next valid image graph
		advanceToNextValid();
	}

	/**
	 * This method allows for skip traversal, which is a feature that allows to skip
	 * reference image graphs by a specified chance
	 * 
	 * @param skipChance the chance of an image graph being skipped, between 0 and 1
	 */
	public void setSkipChance(float skipChance) {
		if (skipChance < 0f || skipChance > 1f) {
			throw new IllegalArgumentException("Skip chance must be between 0 and 1");
		}
		this.skipChance = skipChance;
	}

	/**
	 * returns the category of the data set being traversed
	 * 
	 * @return the current category
	 */
	public CATEGORY_TYPE getCategory() {
		return categoryType;
	}

	/**
	 * This method determines the relevant directory of the category being traversed
	 * 
	 * @return the file (folder) containing all relevant images
	 */
	private File determineImageDirectory() {
		/*
		 * if the category is set the folder is the relevant folder of the category,
		 * otherwise it is the general data set folder
		 */
		return categoryType == null ? new File("data/reference-data")
				: new File("data/reference-data/" + categoryType.toString());
	}

	/**
	 * Parses the relevant directory for the relevant files
	 * 
	 * @param filter the filter which specifies which file types will be selected
	 * @return the files ready for traversal
	 */
	private File[] directoryParser(FileFilter filter) {
		// list to stores collected
		List<File> files = new ArrayList<>();
		// get the relevant directory
		File dir = determineImageDirectory();

		// if all data is being traversed
		if (iterateAllData) {
			// find sub directories
			File[] subDirs = dir.listFiles(File::isDirectory);
			if (subDirs != null) {
				// iterate files in the sub directories
				for (File subDir : subDirs) {
					Collections.addAll(files, subDir.listFiles(filter));
				}
			}
		} else {
			// add all files to the list
			Collections.addAll(files, dir.listFiles(filter));
		}

		// return the array
		return files.toArray(new File[0]);
	}

	/**
	 * Fetches relevant MSTFeatures for the data set being traversed
	 */
	private void initMSTFeatures() {

		// define a filter for the mst feature .dat files which contain mst features
		FileFilter datFilter = (File f) -> {
			String name = f.getName().toLowerCase();
			return name.endsWith(".dat");
		};

		// get the files
		File[] mstDatFiles = directoryParser(datFilter);

		for (File file : mstDatFiles) {
			try (FileInputStream fis = new FileInputStream(file);
					BufferedInputStream bis = new BufferedInputStream(fis);
					ObjectInputStream ois = new ObjectInputStream(bis);) {

				// read the object in each file
				Object obj = ois.readObject();

				// if the object is an AbstractMap store it and add everything in the map
				if (obj instanceof AbstractMap) {
					@SuppressWarnings("unchecked")
					AbstractMap<String, MSTFeatures> map = (AbstractMap<String, MSTFeatures>) obj;
					mstFeatures.putAll(map);
				}

			} catch (IOException e) {
				e.printStackTrace();
				System.out.println(e.getMessage());
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	/**
	 * Validates and gets all image files from a directory
	 * 
	 * @param imageDir The directory containing the relevant image files
	 * @return a list of File objects, where each file is an image file
	 * @throws IOException when the directory does not exist, it is not a directory,
	 *                     we cannot tread the directory
	 */
	private File[] validateAndGetImageFiles(File imageDir) throws IOException {
		if (!imageDir.exists()) {
			throw new IOException("Directory does not exist: " + imageDir.getAbsolutePath());
		}
		if (!imageDir.isDirectory()) {
			throw new IOException("Path is not a directory: " + imageDir.getAbsolutePath());
		}
		if (!imageDir.canRead()) {
			throw new IOException("Cannot read directory: " + imageDir.getAbsolutePath());
		}

		// create a filter for the to get relevant files
		FileFilter imageFilter = (File f) -> {
			String name = f.getName().toLowerCase();
			return name.endsWith(".jpg") || name.endsWith(".png") || name.endsWith(".jpeg");
		};

		// get relevant files
		File[] imageFiles = directoryParser(imageFilter);

		return imageFiles;
	}

	/**
	 * Counts the number of image graphs ready to be iterated over
	 * 
	 * @return the number of elements in the data set category
	 */
	public int count() {
		return files.length;
	}

	/**
	 * Determines if they are more abstract image graphs to iterated
	 */
	@Override
	public boolean hasNext() {
		return !closed && hasNext;
	}

	/**
	 * Gets the next iteration of an Abstract Image Graph
	 */
	@Override
	public AbstractImageGraphProxy next() {
		if (!hasNext()) {
			throw new NoSuchElementException("No more images to process");
		}

		try {
			File currentFile = files[currentIndex++];

			BufferedImage image = ImageIO.read(currentFile);

			/*
			 * if the image cannot be read by ImageIO.read and the image has no valid
			 * mstFeatures then it is not valid
			 */
			if (image == null || mstFeatures.isEmpty()
					|| mstFeatures.get(new File(currentFile.getParent()).getName() + currentFile.getName()) == null) {
				if (image == null)
					System.out.println("Warning: Could not read image - " + currentFile.getName());
				else
					System.out.println("Warning: Image has no corrosponding mst Features - " + currentFile.getName());

				return next(); // Skip to next file
			}

			// create the key which maps to an image graph's mst features
			String featureKeyString = new File(currentFile.getParent()).getName() + currentFile.getName();

			// create a proxy
			ImageGraphProxy proxy = new ImageGraphProxy(image, currentFile, mstFeatures.get(featureKeyString));

			// Prepare for the next call
			advanceToNextValid();

			return proxy;
		} catch (IOException e) {
			close();
			throw new RuntimeException("Error processing image file", e);
		}
	}

	/**
	 * Determines if there are more MSTFeatures to iterate over
	 * 
	 * @return true if the iteration has more elements
	 */
	public boolean hasNextMSTFeature() {
		return featureIndex < files.length - 1;
	}

	/**
	 * Gets the next MSTFeature
	 * 
	 * @return the next iteration of MSTFeatures
	 */
	public MSTFeatures nextFeature() {
		File file = files[featureIndex++];
		return mstFeatures.get(new File(file.getParent()).getName() + file.getName());
	}

	/**
	 * Utility method to advance to the next valid Image
	 */
	private void advanceToNextValid() {
		// while not at the end of the list
		while (currentIndex < files.length) {
			// Skip based on probability
			if (skipChance > 0 && random.nextFloat() < skipChance) {
				currentIndex++;
				continue;
			}
			hasNext = true;
			return;
		}
		hasNext = false;
		close();
	}

	/**
	 * Safely closes the iterator and releases all resources except for MSTFeatures
	 */
	@Override
	public void close() {
		if (!closed) {
			closed = true;
			hasNext = false;
			System.gc();
		}
	}
	
	/**
	 * Removal is not supported through this iterator
	 * 
	 * @F UnsupportedOperationException always because we are not allowed to remove
	 */
	@Override
	public void remove() throws UnsupportedOperationException {
		/*
		 * To maintain consistency with the underlying map structure we prevent
		 * modification
		 */
		throw new UnsupportedOperationException("Remove not supported via KeyIterator");
	}
}