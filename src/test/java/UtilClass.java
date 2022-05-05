import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Objects;

public final class UtilClass {

    private static Robot robot;

    private UtilClass() {
        throw new RuntimeException("");
    }

    static void moveMouseAndClick(JButton button) {
        robot = createRobot();
        Point point = button.getLocationOnScreen();
        robot.waitForIdle();
        robot.mouseMove(point.x + button.getWidth() / 2, point.y + button.getHeight() / 2);
        robot.waitForIdle();
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.delay(200);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }

    static boolean isFocused(Component component) {
        Objects.requireNonNull(component);
        waitComponent(component, 1000);
        return KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner() == component;
    }

    static void waitComponent(Component component, int delay) {
        int i = 0;
        while (Objects.isNull(component) && i <= delay) {
            i++;
            if (i == 1000) {
                throw new RuntimeException("Component should be there, but it's not");
            }
        }
    }

    static void typeText(String input, int delay) {
        robot = createRobot();
        char[] strs = input.toCharArray();
        robot.keyPress(KeyEvent.VK_ENTER);
        for (char str : strs) {
            robot.keyPress(KeyEvent.getExtendedKeyCodeForChar(str));
            robot.keyRelease(KeyEvent.getExtendedKeyCodeForChar(str));
            robot.delay(delay);
        }
    }

    static void typeText(String input) {
        typeText(input, 200);
    }

    static Robot createRobot() {
        try {
            if (robot == null) {
                robot = new Robot();
            }
        } catch (AWTException e) {
            throw new RuntimeException("Error: ooops", e);
        }
        return robot;
    }

    static JFrame createEditor(String nameEditor, boolean requestFocus, JTextField text) {
        JFrame frame = new JFrame();
        frame.setName(nameEditor);
        frame.getContentPane().add(text);
        frame.setAutoRequestFocus(requestFocus);
        frame.setSize(300, 200);
        return frame;
    }
}
