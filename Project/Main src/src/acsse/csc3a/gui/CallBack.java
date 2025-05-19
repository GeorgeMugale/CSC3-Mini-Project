package acsse.csc3a.gui;

import java.util.function.BiConsumer;

@FunctionalInterface
public interface CallBack {
	void apply(BiConsumer<Double, Double> updateProgress);
}
