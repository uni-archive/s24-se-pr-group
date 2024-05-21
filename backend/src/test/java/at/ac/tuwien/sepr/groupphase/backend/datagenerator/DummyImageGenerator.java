package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;

public class DummyImageGenerator {

    public static byte[] createDummyImage() throws IOException {
        int width = 300;
        int height = 200;

        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D image = bufferedImage.createGraphics();

        image.setColor(getRandomColor());
        image.fillRect(0, 0, width, height);
        image.setColor(getRandomColor());
        image.fillOval(100, 50, 100, 100);
        image.setColor(Color.white);
        image.drawString("Dummy-Bild", 120, 100);
        image.setColor(Color.black);
        image.drawString("Dummy-Bild", 120, 120);

        image.dispose();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", stream);
        return stream.toByteArray();
    }

    private static Color getRandomColor() {
        Random random = new Random();
        int red = random.nextInt(256);
        int green = random.nextInt(256);
        int blue = random.nextInt(256);
        return new Color(red, green, blue);
    }
}
