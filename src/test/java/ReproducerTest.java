import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @test
 * @build UtilClass
 * @run testng ReproducerTest
 */
public class ReproducerTest {
    static JFrame f;
    static JPanel p;
    static JButton b;
    static JButton b2;
    static JFrame childFrame;
    private static JTextArea text;
    private static Process process;
    static ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    @BeforeTest
    public void init() throws IOException, InterruptedException {
        List<String> command = new ArrayList<>();
        //TODO: Переделать на вызов редактора FocusedWindow.jar
        command.add("gedit");
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(command);
        process = processBuilder.start();
        Thread.sleep(1000);
    }

    @Test
    public void testFocusChild(){
        Robot robot = UtilClass.createRobot();
        testParent();
        UtilClass.moveMouseAndClick(b2);
        robot.waitForIdle();
        robot.mouseMove(100, 100);
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.delay(200);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        robot.waitForIdle();
        UtilClass.typeText("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        robot.delay(10000);

        try { if (KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner() == childFrame
                || KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner() == text
                || !text.getText().isEmpty()) {
            throw new RuntimeException("Focus is not here ");
        }
        }
        finally {
            if(process.isAlive()){
                process.destroy();
            }
            executorService.shutdown();
        }
    }

    public static void testParent(){

        f = new JFrame("parent");
        p = new JPanel();
        b = new JButton("Open non-focused window");
        b.addActionListener(e -> {
            Runnable runnable = () -> new OpenNewWindow(false).run();
            executorService.schedule(runnable,4, TimeUnit.SECONDS);
        });
        p.add(b);
        b2 = new JButton("Open focused window");
        b2.addActionListener(e -> {
            Runnable runnable2 = () -> new OpenNewWindow(true).run();
            executorService.schedule(runnable2,4, TimeUnit.SECONDS);
        });
        p.add(b2);
        f.add(p);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f.requestFocus();
        f.setVisible(true);
    }

    static class OpenNewWindow implements Runnable {
        private final boolean requestFocus;

        OpenNewWindow(boolean requestFocus) {
            this.requestFocus = requestFocus;
        }

        @Override
        public void run() {
            childFrame = new JFrame("child");
            text = new JTextArea();
            childFrame.setAutoRequestFocus(requestFocus);
            childFrame.setSize(200, 50);
            childFrame.getContentPane().add(text);
            childFrame.setLocationRelativeTo(null);
            childFrame.setVisible(true);
        }
    }
}
