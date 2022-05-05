import org.testng.annotations.Test;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @test
 * @build UtilClass
 * @run testng FocusedWindowsWithDiffTimeVisibleTests
 */
public class FocusedWindowsWithDiffTimeVisibleTests {
    static ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private static final JTextField textFocused1 = new JTextField();
    private static final JTextField textFocused2 = new JTextField();
    private static volatile JFrame frameFocused;
    private static final Robot robot = UtilClass.createRobot();

    @Test
    public void runTest() throws InterruptedException {
        JFrame frameFocused1 = UtilClass.createEditor("Text editor with focus", true, textFocused1);
        frameFocused1.setExtendedState(frameFocused1.getExtendedState() | JFrame.MAXIMIZED_BOTH);
        frameFocused1.setLocationRelativeTo(null);
        frameFocused1.setVisible(true);
        Thread.sleep(1000);
        UtilClass.typeText("test1");
        Runnable r = () -> {
            frameFocused = UtilClass.createEditor ("Text editor without focus", true, textFocused2);
            frameFocused.setVisible(true);
            frameFocused.toFront();
        };
        executorService.schedule(r, 2, TimeUnit.SECONDS);
        int i = 0;
        while(i<10){
            UtilClass.typeText("test");
            i++;
        }
        isFocused(frameFocused, false);
        robot.waitForIdle();
        robot.delay(1000);

    }
    private static void isFocused(Component component, boolean deleteImage) {
        Objects.requireNonNull(component);
        ImageModel.createScreenshot(frameFocused);
        File img1 = new File(System.getProperty("test.src", "."), "screenShot.png");
        File img2 = new File(System.getProperty("test.src", "."), "expactedImage.png");
        try {
            ImageModel im1 = new ImageModel(ImageIO.read(img1));
            if (im1.checkDifferencesImage(ImageIO.read(img2))) {
                throw new RuntimeException("No focus, yes problem");
            }
        } catch (IOException e) {
            throw new RuntimeException("File not found: " + e.getMessage());
        } finally {
            if (deleteImage) {
                img1.delete();
            }
        }
    }
}
