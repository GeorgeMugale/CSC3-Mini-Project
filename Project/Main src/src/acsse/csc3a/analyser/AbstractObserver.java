package acsse.csc3a.analyser;

/**
 * This interface defines essential functionality to update this object with
 * current
 */
public interface AbstractObserver {

	/**
	 * Updates this current object with match information
	 * 
	 * @param result the result containing the update
	 */
	public void updateMatch(Result result);

	/**
	 * Updates this current object with category information
	 * 
	 * @param result the result containing the update
	 */
	public void updateCat(Result result);

	/**
	 * Updates this current object with normal information
	 * 
	 * @param result the result containing the update
	 */
	public void update(String result);
}
