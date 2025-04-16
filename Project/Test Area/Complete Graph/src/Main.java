import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import acsse.csc3a.imagegraph.ImageGraph;
import acsse.csc3a.imagegraph.Point;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		BufferedImage img;
		try {
			img = ImageIO.read(new File("IMG_6145.jpg"));
			ImageGraph graph = new ImageGraph(img);

		}catch(IOException e) {
			e.printStackTrace();
		}
		
	}

}
