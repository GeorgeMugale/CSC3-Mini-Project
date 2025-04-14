package acsse.csc3a.graph;

import java.awt.image.BufferedImage;
import java.util.*;

public class ImageGraph {

    public static class PixelNode {
        public final int x;
        public final int y;
        public final int rgb;

        public PixelNode(int x, int y, int rgb) {
            this.x = x;
            this.y = y;
            this.rgb = rgb;
        }

        /*
         * %08X -> add zeros(0) to make 8 characters for the rgb and makes all alphabets be in upper case 
         */
        @Override
        public String toString() {
            return String.format("(%d,%d)[%08X]", x, y, rgb);
        }
    }

    public static Map<PixelNode, Set<PixelNode>> createImageGraph(BufferedImage image, double similarityThreshold) {
        int width = image.getWidth();
        int height = image.getHeight();
        /*
         * creates a graph where each pixel is connected to its neighbor
         */
        Map<PixelNode, Set<PixelNode>> graph = new HashMap<>();

        /*
         * creating the vertices of the graph
         */
        PixelNode[][] nodes = new PixelNode[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
            	/*
            	 * assigning the colour
            	 */
                int rgb = image.getRGB(x, y);
                nodes[y][x] = new PixelNode(x, y, rgb);
                graph.put(nodes[y][x], new HashSet<>());
            }
        }

        /*
         * create edges(mapping out neighbors)
         */
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                PixelNode current = nodes[y][x];
                
                /*
                 *  Check 4-connected neighbors
                 *  checks if the parts of the images are similar before they can be considered neightbors of a given pixel
                 *  pixel similarity should be 1 for this to be considered true
                 */
                if (x > 0 && isSimilar(current.rgb, nodes[y][x-1].rgb, similarityThreshold)) {
                    graph.get(current).add(nodes[y][x-1]);
                }
                if (x < width - 1 && isSimilar(current.rgb, nodes[y][x+1].rgb, similarityThreshold)) {
                    graph.get(current).add(nodes[y][x+1]);
                }
                if (y > 0 && isSimilar(current.rgb, nodes[y-1][x].rgb, similarityThreshold)) {
                    graph.get(current).add(nodes[y-1][x]);
                }
                if (y < height - 1 && isSimilar(current.rgb, nodes[y+1][x].rgb, similarityThreshold)) {
                    graph.get(current).add(nodes[y+1][x]);
                }
            }
        }


        return graph;
    }

    private static boolean isSimilar(int rgb1, int rgb2, double threshold) {
    	/*
    	 * shifts the bits 
    	 */
        int r1 = (rgb1 >> 16) & 0xFF;
        int g1 = (rgb1 >> 8) & 0xFF;
        int b1 = rgb1 & 0xFF;
        
        int r2 = (rgb2 >> 16) & 0xFF;
        int g2 = (rgb2 >> 8) & 0xFF;
        int b2 = rgb2 & 0xFF;
        
        double distance = Math.sqrt(Math.pow(r1 - r2, 2) + 
                         Math.pow(g1 - g2, 2) + 
                         Math.pow(b1 - b2, 2));
        return distance / 441.67 <= threshold;
    }

    public static void printGraph(Map<PixelNode, Set<PixelNode>> graph) {
        System.out.println("Image Graph Structure:");

        
        for (Map.Entry<PixelNode, Set<PixelNode>> entry : graph.entrySet()) {
            PixelNode node = entry.getKey();
            Set<PixelNode> neighbors = entry.getValue();
            
            System.out.printf("%s -> ", node);
            if (neighbors.isEmpty()) {
                System.out.println("[]");
            } else {
                System.out.print("[");
                Iterator<PixelNode> it = neighbors.iterator();
                while (it.hasNext()) {
                    System.out.print(it.next());
                    if (it.hasNext()) {
                        System.out.print(", ");
                    }
                }
                System.out.println("]");
            }
        }
    }

}