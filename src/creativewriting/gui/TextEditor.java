package creativewriting.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * A text editor.
 *
 * @author Thiago
 */
public class TextEditor extends JPanel {

    /**
     * Text field to write text's title.
     */
    private final JTextField textField;
    
    /**
     * Text area to write text.
     */
    private final JTextArea textArea;

    /**
     * Scroller to scroll text area.
     */
    private final JScrollPane scroller;
    
    /**
     * Label for text field with work's title.
     */
    private final JLabel label;

    /**
     * Constructor. Makes a new text editor.
     * @param rb ResourceBundle that defines language to display label title.
     */
    public TextEditor(ResourceBundle rb) {
        super(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        label = new JLabel("Título:");
        setLabelTitle(rb);
        
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 6));
        textField = new JTextField();

        label.setLabelFor(textField);

        JPanel titlePane = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePane.setLayout(new BoxLayout(titlePane, BoxLayout.X_AXIS));
        titlePane.add(label);
        titlePane.add(textField);

        textArea = new JTextArea();
        textArea.setMargin(new Insets(4, 4, 4, 4));
        textArea.setLineWrap(true);

        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        scroller = new JScrollPane(textArea);
        scroller.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(6, 0, 0, 0),
                BorderFactory.createLineBorder(Color.BLACK)));

        textField.setEnabled(false);
        textArea.setEnabled(false);
        add(titlePane, BorderLayout.NORTH);
        add(scroller, BorderLayout.CENTER);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {

                int space = titlePane.getWidth() - label.getWidth();
                Dimension d = new Dimension(space, textField.getHeight());
                textField.setPreferredSize(d);
            }

            @Override
            public void componentMoved(ComponentEvent e) {

                int space = titlePane.getWidth() - label.getWidth();
                Dimension d = new Dimension(space, textField.getHeight());
                textField.setPreferredSize(d);
            }

        });

    }

    /**
     * Returns text title.
     * @return text title.
     */
    public String getTitle() {
        return textField.getText();
    }

    /**
     * Returns text content.
     * @return text content.
     */
    public String getText() {
        return textArea.getText();
    }

    /**
     * Sets text title.
     * @param title new text title.
     */
    public void setTitle(String title) {
        textField.setEnabled(true);
        textField.setText(title);
    }

    /**
     * Sets text content.
     * @param text new text content.
     */
    public void setText(String text) {
        textArea.setEnabled(true);
        textArea.setText(text);
    }

    /**
     * Clears text title and text content.
     */
    public void clear() {
        textField.setEnabled(true);
        textArea.setEnabled(true);
        textField.setText("");
        textArea.setText("");
    }
    
    /**
     * Sets label title.
     * @param rb ResourceBundle that defines language to display label title.
     */
    public void setLabelTitle(ResourceBundle rb){
        label.setText(rb.getString("labelTitle"));
    }

}
