import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public final class ImageModel {

    private final BufferedImage image;
    private final int width;
    private final int height;

    public ImageModel(BufferedImage image) {
        this.image = image;
        this.width = image.getWidth(null);
        this.height = image.getHeight(null);
    }

    public BufferedImage getImage() {
        return image;
    }

    public boolean checkDifferencesImage(BufferedImage image) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb1 = this.getImage().getRGB(x, y);
                int rgb2 = image.getRGB(x, y);
                if (rgb1 != rgb2) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void createScreenshot(Component component) {
        Robot robot = null;
        //            robot = new Robot();
//            BufferedImage screenshotImage = robot.createScreenCapture(component.getBounds());
        Rectangle componentRect = component.getBounds();
        BufferedImage bufferedImage = new BufferedImage(componentRect.width, componentRect.height, BufferedImage.TYPE_INT_ARGB);
        component.paint(bufferedImage.getGraphics());

        try {
            ImageIO.write(bufferedImage, "png", new File("expectedImage.png" ));
        } catch (IOException ex) {
            System.err.println("ImageIssues");
        }
    }
}