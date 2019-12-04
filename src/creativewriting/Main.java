package creativewriting;

import creativewriting.gui.MainWindow;

/**
 * Creative Writing is a software to train writer's creativity. Exercise is
 * simple: at screen left side a randomly chosen image is shown, at right side
 * there is a text editor to user to digit text based on ideas suggested by
 * image. User writes a small text, max a paragraph, then, user clicks NEXT
 * button to show a new image. User continues writing text using ideas from this
 * new image. User repeats this step until concludes his/her work.
 *
 * @author Thiago
 */
public class Main {

    /**
     * Main method. Creates and shows main window.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        MainWindow window = new MainWindow();
        window.setVisible(true);
    }
}
