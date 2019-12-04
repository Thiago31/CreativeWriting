package creativewriting.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * Help dialog window with a manual to show how to use this program.
 *
 * @author Thiago
 */
public class HelpManual extends JDialog {

    /**
     * Instance to this help manual window.
     */
    private static HelpManual helpManual = null;

    /**
     * Editor to show page content.
     */
    private JEditorPane jep;

    /**
     * Shows this dialog window.
     *
     * @param frame parent frame.
     * @param title title of this dialog window.
     * @param indexPage resource file name from help page to be displayed.
     */
    public static void showDialog(JFrame frame, String title, String indexPage) {
        if (helpManual == null) {
            HelpManual hm = new HelpManual(frame, title, indexPage);
            helpManual = hm;
            hm.setVisible(true);
        } else {
            helpManual.requestFocus();
        }

    }

    /**
     * Changes language that help page is displayed.
     *
     * @param rb ResourceBundle that defines resource file name from help page
     * to be displayed.
     */
    public static void changeLanguage(ResourceBundle rb) {
        if (helpManual == null) {
            return;
        }

        URL url = helpManual.getClass().getClassLoader().getResource(rb.getString("manualFile"));
        try {
            helpManual.jep.setPage(url);
            helpManual.setTitle(rb.getString("useTitle"));
        } catch (IOException ioe) {
            System.out.println(ioe);
        }
    }

    /**
     * Constructor. Makes a new help window.Can't be directly called. Use
     * {@code HelpManual.showDialog()} method instead.
     *
     * @param frame parent frame.
     * @param title title of this dialog window.
     * @param indexPage resource file name from help page to be displayed.
     * @see HelpManual.showDialog
     */
    private HelpManual(JFrame frame, String title, String indexPage) {
        super(frame, title, false);
        setSize(frame.getWidth() * 4 / 5, frame.getHeight() * 4 / 5);
        setLocationRelativeTo(frame);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evt) {
                helpManual = null;
            }
        });

        JPanel content = new JPanel(new BorderLayout());
        try {
            ClassLoader cl = getClass().getClassLoader();
            URL url = cl.getResource(indexPage);
            jep = new JEditorPane(url);
            jep.setEditable(false);
        } catch (IOException e) {
            jep = new JEditorPane();
            jep.setText("Couldn't open desired page!!!");
        }
        jep.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane jsc = new JScrollPane(jep);
        jsc.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10),
                BorderFactory.createLineBorder(Color.BLUE, 2)
        ));
        content.add(jsc, BorderLayout.CENTER);
        setContentPane(content);
    }

}
