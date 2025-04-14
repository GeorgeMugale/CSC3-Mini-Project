import acsse.csc3a.map.AdjacencyMap;

public class Main {
	public static void main(String[] args) {

		AdjacencyMap<Integer, String> map = new AdjacencyMap<>(5, 10);

		map.put(0, "First");
		map.put(1, "Second");
		map.put(2, "third");
		map.put(3, "fourth");
		map.put(4, "fith");

		Iterable<Integer> iter = map.keyIterable();

		for (Integer key : iter) {
			System.out.println(map.get(key));
		}

		System.out.println("size: " + map.size());
	}

}
