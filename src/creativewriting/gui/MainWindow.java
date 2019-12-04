package creativewriting.gui;

import creativewriting.textmodel.TextModel;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 * Main window for Creative Writing Software.
 *
 * @author Thiago
 */
public class MainWindow extends JFrame {

    /**
     * Panel to show images.
     */
    private final ImageViewer imageViewer;
    
    /**
     * Text editor to write text.
     */
    private final TextEditor textEditor;
    
    /**
     * TextModel used by this window.
     */
    private TextModel textModel;
    
    /**
     * File chooser to show open/save dialog window.
     */
    private final JFileChooser fc;
    
    /**
     * List containing available languages to display this program.
     */
    private HashMap<String, ResourceBundle> languages;
    
    /**
     * Current ResourceBundle used to define language to display this program.
     */
    private ResourceBundle rb;
    
    /**
     * Default language to use if a locale to user locale is not defined.
     */
    private String defaultLanguage = "";

    /**
     * Constructor. Makes a new main window.
     */
    public MainWindow() {
    
        makeResourceBundles();
        
        setWindowTitle();

        imageViewer = new ImageViewer(rb);
        textEditor = new TextEditor(rb);

        fc = new JFileChooser();
        
        setJMenuBar(new MyMenu());
  
        JPanel content = new JPanel(new GridLayout(1, 2));

        content.add(imageViewer);
        content.add(textEditor);
        setContentPane(content);

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

        setMinimumSize(new Dimension(dim.width / 2, dim.height / 2));
        setExtendedState(MAXIMIZED_BOTH);
        setLocationRelativeTo(null);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmExit();
            }
        });

        ArrayList<Image> images = new ArrayList<>();
        int[] s = {16, 32, 64, 128};
        for (int i = 0; i < s.length; i++) {
            URL url = getClass().getClassLoader().getResource("resources/icon" + s[i] + ".png");
            ImageIcon imi = new ImageIcon(url);
            images.add(imi.getImage());
        }
        setIconImages(images);

    }
    
    /**
     * Makes a list of available languages.
     */
    private void makeResourceBundles(){
        languages = new HashMap<>();
        String[] langCode = {"", "en-US", "pt-BR"};
        for(String str : langCode){
            ResourceBundle resB = ResourceBundle.getBundle("properties/menuTags", Locale.forLanguageTag(str));
            languages.put(str, resB);
            if(resB.getLocale().equals(Locale.getDefault())){
                defaultLanguage = str;
                rb = resB;
            }
        }
        if(rb == null){
            rb = languages.get("");
        }
    }
    
    /**
     * Sets this window title according current Locale.
     */
    private void setWindowTitle(){
        StringBuilder sb = new StringBuilder();
        sb.append(rb.getString("windowTitle"));
        
        if(textModel != null){
            sb.append(" - ").append(textModel.getFileName());
        }
        setTitle(sb.toString());
    }

    /**
     * Menu bar to this window.
     */
    private class MyMenu extends JMenuBar {
        
        JMenu fileMenu;
        JMenuItem _save;
        JMenuItem _saveAs;
        JMenuItem _savetxt;
        JMenuItem _new;
        JMenuItem _open;
        JMenuItem _exit;

        JMenu languageMenu;
        JMenuItem en_language;
        JMenuItem pt_language;
        
        JMenu helpMenu;
        JMenuItem _use;
        JMenuItem _about;
        
        /**
         * Constructor. Makes a menu bar.
         */
        private MyMenu() {
            fileMenu = new JMenu();

            _save = new JMenuItem();
            _save.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    textModel.setTitle(textEditor.getTitle());
                    textModel.setText(textEditor.getText());
                    textModel.save();
                }
            });
            _save.setEnabled(false);

            _saveAs = new JMenuItem();
            _saveAs.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    textModel.setTitle(textEditor.getTitle());
                    textModel.setText(textEditor.getText());
                    fc.setDialogTitle(rb.getString("SaveAs"));
                    int option = fc.showSaveDialog(fc);
                    if (option == JFileChooser.APPROVE_OPTION) {
                        File out = fc.getSelectedFile();
                        
                        String name = out.getAbsolutePath();
                        if(!name.endsWith(".xml")){
                            name += ".xml";
                            out = new File(name);
                        }
                        
                        if (out.exists()) {
                            Object[] args = {out.getName()};
                            MessageFormat formatter = new MessageFormat(rb.getString("newDialog.existMessage"));
                            int question = JOptionPane.showConfirmDialog(MainWindow.this,
                                    formatter.format(args),
                                    rb.getString("newDialog.fileExists"),
                                    JOptionPane.YES_NO_OPTION,
                                    JOptionPane.QUESTION_MESSAGE);
                            if (question != JOptionPane.YES_OPTION) {
                                return;
                            }
                        }
                        textModel.saveAs(out);
                        setWindowTitle();
                    }
                    
                }
            });
            _saveAs.setEnabled(false);
            
            _savetxt = new JMenuItem();
            _savetxt.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    textModel.setTitle(textEditor.getTitle());
                    textModel.setText(textEditor.getText());
                    fc.setDialogTitle(rb.getString("SaveTxt"));
                    int option = fc.showSaveDialog(fc);
                    if (option == JFileChooser.APPROVE_OPTION) {
                        File out = fc.getSelectedFile();
                        
                        String name = out.getAbsolutePath();
                        if(!name.endsWith(".txt")){
                            name += ".txt";
                            out = new File(name);
                        }
                        
                        if (out.exists()) {
                            Object[] args = {out.getName()};
                            MessageFormat formatter = new MessageFormat(rb.getString("newDialog.existMessage"));
                            int question = JOptionPane.showConfirmDialog(MainWindow.this,
                                    formatter.format(args),
                                    rb.getString("newDialog.fileExists"),
                                    JOptionPane.YES_NO_OPTION,
                                    JOptionPane.QUESTION_MESSAGE);
                            if (question != JOptionPane.YES_OPTION) {
                                return;
                            }
                        }
                        try {
                            textModel.saveTxt(out);
                        } catch (IOException ioe) {
                            Object[] args = {out.getName()};
                            MessageFormat formatter = new MessageFormat(rb.getString("save.error"));
                            JOptionPane.showMessageDialog(MainWindow.this,
                                    formatter.format(args),
                                    rb.getString("save.errorTitle"),
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }

                }
            });
            _savetxt.setEnabled(false);

            _new = new JMenuItem();
            _new.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    
                    if(textModel != null){
                        confirmSaveWork();
                    }
                    TextModel model = NewTextDialog.showDialog(MainWindow.this, rb);
                    if (model != null) {
                        textModel = model;
                        imageViewer.setTextModel(textModel);
                        textEditor.clear();
                        setWindowTitle();
                        _save.setEnabled(true);
                        _saveAs.setEnabled(true);
                        _savetxt.setEnabled(true);
                    }
                }
            });

            _open = new JMenuItem();
            _open.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    fc.setDialogTitle(rb.getString("open.title"));
                    int option = fc.showOpenDialog(MainWindow.this);
                    if(option == JFileChooser.APPROVE_OPTION){
                        File in = fc.getSelectedFile();
                        TextModel model = null;
                        try{
                            model = new TextModel(in);
                        } catch (ParserConfigurationException | SAXException ex) {
                            Object[] args = {in.getName()};
                            MessageFormat formatter = new MessageFormat(rb.getString("open.parseError"));
                            JOptionPane.showMessageDialog(MainWindow.this, 
                                    formatter.format(args),
                                    rb.getString("open.errorTitle"), 
                                    JOptionPane.ERROR_MESSAGE);
                            return;
                        } catch(IllegalStateException ise){
                            Object[] args = {in.getName(), ise.getLocalizedMessage()};
                            MessageFormat formatter = new MessageFormat(rb.getString("open.stateError"));
                            JOptionPane.showMessageDialog(MainWindow.this, 
                                    formatter.format(args),
                                    rb.getString("open.errorTitle"), 
                                    JOptionPane.ERROR_MESSAGE);
                            return;                            
                        } catch (IOException ex) {
                            Object[] args = {in.getName()};
                            MessageFormat formatter = new MessageFormat(rb.getString("open.ioError"));
                            JOptionPane.showMessageDialog(MainWindow.this, 
                                    formatter.format(args),
                                    rb.getString("open.errorTitle"), 
                                    JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        
                        if(textModel != null){
                            confirmSaveWork();
                        }
                        
                        textModel = model;
                        imageViewer.setTextModel(textModel);
                        textEditor.setTitle(textModel.getTitle());
                        textEditor.setText(textModel.getText());
                        setWindowTitle();
                        _save.setEnabled(true);
                        _saveAs.setEnabled(true);
                        _savetxt.setEnabled(true);
                        textModel.save();
                    }

                }
            });

            _exit = new JMenuItem();
            _exit.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    confirmExit();
                }
            });

            fileMenu.add(_new);
            fileMenu.add(_open);
            fileMenu.add(_save);
            fileMenu.add(_saveAs);
            fileMenu.add(_savetxt);
            fileMenu.add(_exit);

            languageMenu = new JMenu();
            
            en_language = new JMenuItem();
            en_language.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e){
                    changeLanguage("en-US");
                }
            });
            
            pt_language = new JMenuItem();
            pt_language.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e){
                    changeLanguage("pt-BR");
                }
            });
            
            languageMenu.add(en_language);
            languageMenu.add(pt_language);
        
            
            helpMenu = new JMenu();

            _use = new JMenuItem();
            _use.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    HelpManual.showDialog(MainWindow.this,
                            rb.getString("useTitle"),
                            rb.getString("manualFile"));
                }
            });

            _about = new JMenuItem();
            _about.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JOptionPane.showMessageDialog(MainWindow.this,
                            rb.getString("aboutContent"),
                            rb.getString("aboutTitle"),
                            JOptionPane.PLAIN_MESSAGE);
                }
            });

            helpMenu.add(_use);
            helpMenu.add(_about);

            add(fileMenu);
            add(languageMenu);
            add(helpMenu);
            
            changeLanguage(defaultLanguage);
        }

        /**
         * Changes language to display button labels.
         * @param language language id.
         */
        private void changeLanguage(String language){

            rb = languages.get(language);
            if(rb == null){
                rb = languages.get("");
            }
            
            setWindowTitle();
            fileMenu.setText(rb.getString("fileMenu"));
            _new.setText(rb.getString("new"));
            _open.setText(rb.getString("open"));
            _save.setText(rb.getString("save"));
            _saveAs.setText(rb.getString("saveAs"));
            _savetxt.setText(rb.getString("saveTxt"));
            _exit.setText(rb.getString("exit"));
            languageMenu.setText(rb.getString("languageMenu"));
            en_language.setText(rb.getString("en_language"));
            pt_language.setText(rb.getString("pt_language"));
            helpMenu.setText(rb.getString("helpMenu"));
            _use.setText(rb.getString("use"));
            _about.setText(rb.getString("about"));
            
            textEditor.setLabelTitle(rb);
            imageViewer.setButtonText(rb);
            HelpManual.changeLanguage(rb);
            
            JOptionPane.setDefaultLocale(rb.getLocale());
        }
    }

    /**
     * Confirms intention to close program.
     */
    private void confirmExit() {
        int op = JOptionPane.showConfirmDialog(this,
                rb.getString("exitMessage"),
                rb.getString("exitTitle"), JOptionPane.YES_NO_OPTION);
        if (op == JOptionPane.YES_OPTION) {
            confirmSaveWork();
            System.exit(0);
        }
    }
    
    /**
     * Confirms intention to save work.
     */
    private void confirmSaveWork(){
        if(textModel == null){
            return;
        }
        int op = JOptionPane.showConfirmDialog(this, 
                rb.getString("saveMessage"),
                rb.getString("saveTitle"), JOptionPane.YES_NO_OPTION);
        if(op == JOptionPane.YES_OPTION){
            textModel.setTitle(textEditor.getTitle());
            textModel.setText(textEditor.getText());
            textModel.save();
        }
    }
}
