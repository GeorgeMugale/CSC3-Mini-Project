package acsse.csc3a.observer;

public interface AbstractSubject {
	void attach(AbstractObserver o);
	void detach();
	void notifyObservers(Result result);
}
