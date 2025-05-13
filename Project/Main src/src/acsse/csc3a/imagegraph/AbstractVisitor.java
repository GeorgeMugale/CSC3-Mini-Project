package acsse.csc3a.imagegraph;

import acsse.csc3a.observer.Result;

public interface AbstractVisitor {
	public Result visit(ImageGraph visitable);
}
