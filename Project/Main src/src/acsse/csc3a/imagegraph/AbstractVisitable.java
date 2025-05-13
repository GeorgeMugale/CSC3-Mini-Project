package acsse.csc3a.imagegraph;

import acsse.csc3a.observer.Result;

public interface AbstractVisitable {

	public Result accept(AbstractVisitor visitor);

}
