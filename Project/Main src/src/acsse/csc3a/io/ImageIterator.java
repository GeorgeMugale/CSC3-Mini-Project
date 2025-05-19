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
import acsse.csc3a.graph.algorithms.MATCH_TYPE;
import acsse.csc3a.graph.algorithms.MSTFeatures;
import acsse.csc3a.imagegraph.AbstractImageGraphProxy;
import acsse.csc3a.imagegraph.ImageGraph;
import acsse.csc3a.imagegraph.ImageGraphProxy;
import acsse.csc3a.lists.ArrayList;
import acsse.csc3a.map.AbstractMap;
import acsse.csc3a.map.AdjacencyMap;

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

	public ImageIterator() throws IOException {
		this(null, true);
	}

	public ImageIterator(CATEGORY_TYPE categoryType) throws IOException {
		this(categoryType, false);
	}

	private ImageIterator(CATEGORY_TYPE categoryType, boolean iterateAllData) throws IOException {
		this.categoryType = categoryType;
		this.iterateAllData = iterateAllData;
		File imageDir = determineImageDirectory();
		this.files = validateAndGetImageFiles(imageDir);
		initMSTFeatures();
		advanceToNextValid();
	}

	public void setSkipChance(float skipChance) {
		if (skipChance < 0f || skipChance > 1f) {
			throw new IllegalArgumentException("Skip chance must be between 0 and 1");
		}
		this.skipChance = skipChance;
	}
	
	
	public CATEGORY_TYPE getCategory() {
		return categoryType;
	}

	private File determineImageDirectory() {
		return categoryType == null ? new File("data/reference-data")
				: new File("data/reference-data/" + categoryType.toString());
	}

	private File[] directoryParser(FileFilter filter) {
		List<File> files = new ArrayList<>();
		File dir = determineImageDirectory();

		if (iterateAllData) {
			File[] subDirs = dir.listFiles(File::isDirectory);
			if (subDirs != null) {
				for (File subDir : subDirs) {
					Collections.addAll(files, subDir.listFiles(filter));
				}
			}
		} else {
			Collections.addAll(files, dir.listFiles(filter));
		}

		return files.toArray(new File[0]);
	}

	private void initMSTFeatures() {

		FileFilter datFilter = (File f) -> {
			String name = f.getName().toLowerCase();
			return name.endsWith(".dat");
		};

		File[] mstDatFiles = directoryParser(datFilter);

		for (File file : mstDatFiles) {
			try (FileInputStream fis = new FileInputStream(file);
					BufferedInputStream bis = new BufferedInputStream(fis);
					ObjectInputStream ois = new ObjectInputStream(bis);) {

				Object obj = ois.readObject();

				if (obj instanceof AbstractMap) {
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

		List<File> imageFiles = new ArrayList<>();
		FileFilter imageFilter = (File f) -> {
			String name = f.getName().toLowerCase();
			return name.endsWith(".jpg") || name.endsWith(".png") || name.endsWith(".jpeg");
		};

		if (iterateAllData) {
			File[] subDirs = imageDir.listFiles(File::isDirectory);
			if (subDirs != null) {
				for (File subDir : subDirs) {
					Collections.addAll(imageFiles, subDir.listFiles(imageFilter));
				}
			}
		} else {
			Collections.addAll(imageFiles, imageDir.listFiles(imageFilter));
		}

		if (imageFiles.isEmpty()) {
			System.out.println(imageDir.getAbsolutePath());
			System.out.println("Warning: No image files found in directory");
		}

		return imageFiles.toArray(new File[0]);
	}
	
	public int count() {
		return files.length;
	}

	@Override
	public boolean hasNext() {
		return !closed && hasNext;
	}

	@Override
	public AbstractImageGraphProxy next() {
		if (!hasNext()) {
			throw new NoSuchElementException("No more images to process");
		}

		try {
			File currentFile = files[currentIndex++];

//			System.out.println(currentFile.getName());

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

			// create a prooxy
			ImageGraphProxy proxy = new ImageGraphProxy(image, currentFile, mstFeatures.get(featureKeyString));

			// Prepare for the next call
			advanceToNextValid();

			return proxy;
		} catch (IOException e) {
			close();
			throw new RuntimeException("Error processing image file", e);
		}
	}

	
	public boolean hasNextMSTFeature() {
		return featureIndex < files.length - 1;
	}
	
	public MSTFeatures nextFeature() {
		File file = files[featureIndex++];
		return mstFeatures.get(new File(file.getParent()).getName() + file.getName());
	}

	private void advanceToNextValid() {
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

	@Override
	public void close() {
		if (!closed) {
			closed = true;
			hasNext = false;
			System.gc();
		}
	}
}