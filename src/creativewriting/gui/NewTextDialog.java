package creativewriting.gui;

import creativewriting.textmodel.TextModel;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Dialog window to start a new creative writing work. User must define a new
 * xml file to save his/her work, what directories to use as image source, 
 * including subdirectories or not, or to use default library.
 * 
 * @author Thiago
 */
public class NewTextDialog extends JDialog {

    /**
     * Shows this dialog window.
     * @param parent parent frame.
     * @param rb ResourceBundle that defines language to display this dialog.
     * @return TextModel created by this dialog window.
     */
    public static TextModel showDialog(Frame parent, ResourceBundle rb) {
        NewTextDialog dialog = new NewTextDialog(parent, rb);
        dialog.setVisible(true);
        return dialog.getTextModel();
    }

    /**
     * File to store data in xml format.
     */
    private File file;

    /**
     * TextModel returned by this dialog window.
     */
    private TextModel textModel;

    /**
     * Constructor. Makes a new dialog window. Can't be directly called, use
     * {@code NewTextDialog.showDialog()} instead.
     * @param parent parent frame.
     * @param rb ResourceBundle that defines language to display this dialog.
     */
    private NewTextDialog(Frame parent, ResourceBundle rb) {
        super(parent, rb.getString("newDialog.title"), true);

        JFileChooser fc = new JFileChooser();
        JButton okButton = new JButton(rb.getString("newDialog.ok"));
        okButton.setEnabled(false);
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JPanel workName = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel label = new JLabel(rb.getString("newDialog.name"));
        JTextField textField = new JTextField(40);
        textField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                enableButton();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                enableButton();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                enableButton();
            }
            
            private void enableButton(){
                int size = textField.getText().trim().length();
                if(size > 0){
                    okButton.setEnabled(true);
                } else{
                    okButton.setEnabled(false);
                }
            }

        });
        label.setLabelFor(textField);
        JButton browser = new JButton(rb.getString("newDialog.browser"));
        browser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fc.setDialogTitle(rb.getString("newDialog.fcTitle"));
                fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fc.setSelectedFile(null);
                int option = fc.showSaveDialog(rootPane);
                if (option == JFileChooser.APPROVE_OPTION) {
                    file = fc.getSelectedFile();
                    String name = file.getAbsolutePath();

                    if (!name.endsWith(".xml")) {
                        name += ".xml";
                        file = new File(name);
                    }

                    if (file.exists()) {
                        Object[] args = {file.getName()};
                        MessageFormat formatter = new MessageFormat(rb.getString("newDialog.existMessage"));
                        int question = JOptionPane.showConfirmDialog(parent,
                                formatter.format(args),
                                rb.getString("newDialog.fileExists"),
                                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                        if (question != JOptionPane.YES_OPTION) {
                            return;
                        }
                    }

                    textField.setText(name);
                }
            }
        });
        workName.add(label);
        workName.add(textField);
        workName.add(browser);

        JPanel imageFont = new JPanel();
        imageFont.setLayout(new BoxLayout(imageFont, BoxLayout.Y_AXIS));
        imageFont.setBorder(BorderFactory.createTitledBorder(rb.getString("newDialog.source")));
        JCheckBox mainLibrary = new JCheckBox(rb.getString("newDialog.defaultLibrary"));
        mainLibrary.setSelected(true);
        JPanel panelBox = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBox.add(mainLibrary);

        DefaultListModel listModel = new DefaultListModel();
        JList list = new JList(listModel);
        list.setVisibleRowCount(4);

        JPanel addLibrary = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton(rb.getString("newDialog.add"));
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fc.setDialogTitle(rb.getString("newDialog.addTitle"));
                fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                fc.setSelectedFile(null);
                int option = fc.showOpenDialog(parent);
                if (option == JFileChooser.APPROVE_OPTION) {
                    File selFile = fc.getSelectedFile();
                    if (selFile.isFile()) {
                        if (listModel.contains(selFile.getParent())) {
                            return;
                        }
                        listModel.addElement(selFile.getParent());
                    } else {
                        if (listModel.contains(selFile.getAbsolutePath())) {
                            return;
                        }
                        listModel.addElement(selFile.getAbsolutePath());
                    }
                }
            }
        });

        JButton removeButton = new JButton(rb.getString("newDialog.remove"));
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int[] sel = list.getSelectedIndices();
                for (int i = sel.length - 1; i > -1; i--) {
                    listModel.remove(sel[i]);
                }
            }
        });
        removeButton.setEnabled(false);

        list.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (list.getSelectedIndex() == -1) {
                    removeButton.setEnabled(false);
                } else {
                    removeButton.setEnabled(true);
                }
            }

        });

        JCheckBox includeSubPaths = new JCheckBox(rb.getString("newDialog.include"));
        includeSubPaths.setSelected(true);

        addLibrary.add(addButton);
        addLibrary.add(removeButton);
        addLibrary.add(includeSubPaths);

        JScrollPane scroller = new JScrollPane(list);

        imageFont.add(panelBox);
        imageFont.add(addLibrary);
        imageFont.add(scroller);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton cancelButton = new JButton(rb.getString("newDialog.cancel"));
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textModel = null;
                dispose();
            }
        });

        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (file == null) {
                    String name = textField.getText().trim();
                    if (!name.endsWith(".xml")) {
                        name += ".xml";
                    }
                    file = new File(name);
                }
                String[] paths = new String[listModel.getSize()];
                for (int i = 0; i < listModel.getSize(); i++) {
                    paths[i] = listModel.get(i).toString();
                }
                textModel = new TextModel(file, paths, includeSubPaths.isSelected(),
                        mainLibrary.isSelected());
                dispose();
            }
        });

        buttonPanel.add(cancelButton);
        buttonPanel.add(okButton);

        content.add(workName);
        content.add(imageFont);
        content.add(buttonPanel);

        setContentPane(content);
        pack();
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setResizable(false);
    }

    /**
     * Returns text model created by this dialog window.
     * @return text model.
     */
    private TextModel getTextModel() {
        return textModel;
    }
}
