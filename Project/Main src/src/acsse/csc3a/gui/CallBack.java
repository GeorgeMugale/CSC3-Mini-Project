package acsse.csc3a.gui;

import java.util.function.BiConsumer;

/**
 * An interface which defines a method with some functionality that is able to
 * accept a {@link #BiConsumer}
 */
@FunctionalInterface
public interface CallBack {
	
	/**
	 * The method that contains the functionality
	 * @param updateProgress exposed functionality per the purpose of onitoring progress
	 */
	void apply(BiConsumer<Double, Double> updateProgress);
}
