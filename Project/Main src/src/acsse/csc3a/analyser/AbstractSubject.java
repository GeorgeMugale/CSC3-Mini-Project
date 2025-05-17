package acsse.csc3a.analyser;

public interface AbstractSubject {
	void attach(AbstractObserver o);
	void detach();
	void notifyObserversCat(Result result);
	void notifyObserversMatch(Result result);
}
