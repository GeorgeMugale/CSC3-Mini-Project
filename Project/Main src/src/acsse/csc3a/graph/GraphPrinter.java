package acsse.csc3a.graph;

import javax.swing.*;
import java.awt.*;
import acsse.csc3a.imagegraph.Point;

public class GraphPrinter extends JPanel {
    private Graph<Point, Double> graph;

    public GraphPrinter(Graph<Point, Double> graph) {
        this.graph = graph;
     
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLACK);

        // Draw edges
        for (Edge<Double> edge : graph.edges()) {
            Vertex<Point>[] endpoints = graph.endVertices(edge);
            Point p1 = endpoints[0].getElement();
            Point p2 = endpoints[0].getElement();
            if (p1 != null && p2 != null) {
                g.drawLine(p1.getX(), p1.getY(), p2.getX(), p2.getY());
            }
        }

        // Draw vertices
        
        for (Vertex<Point> v : graph.vertices()) {
            Point p = v.getElement();
            g.setColor(p.getColour());
            if (p != null) {
                g.fillOval(p.getX() - 5, p.getY() - 5, 10, 10);
                g.drawString(v.toString(), p.getX() + 8, p.getY());
            }
        }
    }

    public static <V, E> void showGraph(Graph<Point, Double> graph) {
        JFrame frame = new JFrame("Graph Visualization");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 1000);
        frame.add(new GraphPrinter(graph));
        frame.setVisible(true);
    }
}