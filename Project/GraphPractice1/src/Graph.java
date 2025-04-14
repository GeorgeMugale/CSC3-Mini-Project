public class Graph {
	//2D array for the matrix
	private int[][] matrix;
	private int numVertices;
	
	//constructor
	public Graph(int numVertices) {
		this.numVertices = numVertices;
		matrix = new int[numVertices][numVertices];
	}
	
	//add an edge -> undirected 
	public void addEdge(int i,int j) {
		matrix[i][j] = 1;
		matrix[j][i] = 1;
	}
	
	public void removeEdge(int i,int j) {
		matrix[i][j] = 0;
		matrix[j][i] = 0;
	}
	
	public void printMatrix() {
		for(int i = 0; i< numVertices;i++) {
			for(int j = 0;j < numVertices;j++) {
				System.out.print(matrix[i][j] + " ");
			}
			System.out.println();
		}
	}
}
