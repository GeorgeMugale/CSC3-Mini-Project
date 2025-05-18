package acsse.csc3a.analyser;

/**
 * This interface manages how observers subscribe and unsubscribe, and how they
 * are notified of changes. It includes
 */
public interface AbstractSubject {
	
	
	/**
	 * Attaches an observer to this subject
	 * @param o the observer waiting subsribing to this subject
	 */
	void attach(AbstractObserver o);

	/**
	 * Unsubsrcibes the observer
	 */
	void detach();

	/**
	 * Notifies the observer of category information
	 * @param result the result containing the update
	 */
	void notifyObserversCat(Result result);

	/**
	 * Notifies the observer of match information
	 * @param result result the result containing the update
	 */
	void notifyObserversMatch(Result result);
	
	
	/**
	 * Notifies the observer of normal information
	 * @param result result the result containing the update
	 */
	void notify(String result);
	
}
