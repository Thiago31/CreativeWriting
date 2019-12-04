package creativewriting.textmodel;

import com.sun.org.apache.xml.internal.serializer.OutputPropertiesFactory;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Model to store data about text and images used in creative writing.
 * 
 * @author Thiago
 */
public class TextModel {

    /**
     * List with paths to images.
     */
    private ArrayList<String> imagesPaths;
    
    /**
     * Random number generator used to randomly choose a new image to display.
     */
    private Random random;
    
    /**
     * Document to store data content in xml format.
     */
    private Document document;
    
    /**
     * Number of images used in creative writing.
     */
    private int imageNumber;
    
    /**
     * Total number of images available in image library.
     */
    private int totalImages;
    
    /**
     * File used to read/write data in xml format.
     */
    private File file;
    
    /**
     * Static image used if an image previously loaded can't be found.
     */
    public static final BufferedImage BROKEN_IMAGE = new BufferedImage(100, 100, BufferedImage.TYPE_BYTE_BINARY);

    /**
     * Path for last image that couldn't be found.
     */
    private String brokenImagePath;
    
    /**
     * Constructor. Makes a text model from xml file.
     *
     * @param file xml file from which read data.
     * @throws javax.xml.parsers.ParserConfigurationException
     * @throws org.xml.sax.SAXException
     * @throws java.io.IOException
     */
    public TextModel(File file) throws ParserConfigurationException, SAXException, IOException, IllegalStateException {

        validateXmlFile(file);

        imagesPaths = new ArrayList<>();
        random = new Random();
        this.file = file;

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        document = builder.parse(file);

        Element root = document.getDocumentElement();
        boolean readSub = root.getAttribute("read_subdirectory").equals("yes");
        boolean mainLib = root.getAttribute("use_default_library").equals("yes");
        loadImages(readSub, mainLib);
        imageNumber = document.getElementsByTagName("img").getLength();

    }

    /**
     * Validates a xml file to be read by this class. 
     * @param file xml file to be validated.
     * @throws SAXException
     * @throws IOException 
     */
    private void validateXmlFile(File file) throws SAXException, IOException {
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        URL url = getClass().getClassLoader().getResource("resources/validator.xsd");
        Schema schema = factory.newSchema(url);
        Validator validator = schema.newValidator();
        validator.validate(new StreamSource(file));
    }

    /**
     * Constructor. Makes a new text model.
     *
     * @param file file to save this text model.
     * @param imageFonts source image directories list.
     * @param includeSubPaths defines if to read subdirectories also.
     * @param includeDefaultLibrary if true, read images from default library.
     */
    public TextModel(File file, String[] imageFonts, boolean includeSubPaths,
            boolean includeDefaultLibrary) {
        imagesPaths = new ArrayList<>();
        random = new Random();
        imageNumber = 0;
        this.file = file;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.newDocument();
            //        document.setXmlStandalone(true);

            Element root = document.createElement("creative_writing");
            document.appendChild(root);
            root.setAttribute("read_subdirectory", (includeSubPaths ? "yes" : "no"));
            root.setAttribute("use_default_library", (includeDefaultLibrary ? "yes" : "no"));

            for (String str : imageFonts) {
                Element fonte = document.createElement("image_source_directory");
                fonte.setAttribute("src", str);
                root.appendChild(fonte);
            }

            Element imagens = document.createElement("images");
            root.appendChild(imagens);

            Element texto = document.createElement("text");
            root.appendChild(texto);

            Element titulo = document.createElement("title");
            titulo.setTextContent("");
            texto.appendChild(titulo);
            document.normalize();

            loadImages(includeSubPaths, includeDefaultLibrary);

        } catch (ParserConfigurationException ex) {
            System.err.println(ex);
        }

        save();
    }

    /**
     * Returns an image from this model.
     *
     * @param i image index.
     * @return image with index <i>i</i>.
     */
    public BufferedImage getImage(int i) {
        if (i < 0) {
            throw new IllegalArgumentException("No image with index " + i);
        }
        NodeList list = document.getElementsByTagName("img");
        if (i > list.getLength()) {
            throw new IllegalArgumentException("Item position out of range: " + i
                    + ", list length: " + list.getLength());
        }
        if (i == list.getLength()) {
            return nextImage();
        }
        String path = ((Element) list.item(i)).getAttribute("src");
        BufferedImage image = null;
        try {
            if (path.startsWith("defaultLibrary")) {
                URL url = getClass().getClassLoader().getResource(path);
                image = ImageIO.read(url);
            } else {
                image = ImageIO.read(new File(path));
            }
        } catch (IOException e) {
            image = BROKEN_IMAGE;
            brokenImagePath = path;
        }
        return image;
    }

    /**
     * Returns path of an image that couldn't be found.
     * @return path of an image that couldn't be found.
     */
    public String getBrokenImagePath(){
        return brokenImagePath;
    }
    /**
     * Returns number of images used by this model.
     *
     * @return number of images used by this model.
     */
    public int getNumberOfImages() {
        return imageNumber;
    }

    /**
     * Randomly selects an image in image library, and returns it.
     *
     * @return selected image.
     */
    public BufferedImage nextImage() {
        int total = imagesPaths.size();

        if (total == 0) {
            URL url = getClass().getClassLoader().getResource("resources/noImages.png");
            BufferedImage image = null;
            try {
                image = ImageIO.read(url);
            } catch (IOException e) {

            }
            return image;
        }

        int choice = random.nextInt(total);
        String chosenImage = imagesPaths.remove(choice);
        Element imagens = (Element) document.getElementsByTagName("images").item(0);
        Element imagem = document.createElement("img");
        imagem.setAttribute("src", chosenImage);
        imagens.appendChild(imagem);
        imageNumber++;

        BufferedImage image = null;
        try {
            if (chosenImage.startsWith("defaultLibrary")) {
                URL url = getClass().getClassLoader().getResource(chosenImage);
                image = ImageIO.read(url);
            } else {
                image = ImageIO.read(new File(chosenImage));
            }
        } catch (IOException e) {

        }
        return image;
    }

    /**
     * Sets text in this model.
     *
     * @param text new text for this model.
     */
    public void setText(String text) {

        Element texto = (Element) document.getElementsByTagName("text").item(0);
        NodeList list = texto.getElementsByTagName("p");

        String[] lines = text.split("\n");
        
        int cLines = Math.min(list.getLength(), lines.length);
        
        for(int i = 0; i < cLines; i++){
            Node p = list.item(i);
            p.setTextContent(lines[i]);
        }
        
        if(list.getLength() > cLines ){
            int t = list.getLength() - cLines;
            for(int i = 0; i < t; i++){
                Node p = list.item(cLines);
                p.setTextContent("");
                cLines++;
            }
        } else if(lines.length > cLines){
            int t = lines.length - cLines;
            for(int i = 0; i < t; i++){
                Element paragrafo = document.createElement("p");
                paragrafo.setTextContent(lines[cLines]);
                cLines++;
                texto.appendChild(paragrafo);
            }
        }

    }

    /**
     * Returns text in this model.
     *
     * @return text in this model.
     */
    public String getText() {
        StringBuilder sb = new StringBuilder();
        NodeList list = document.getElementsByTagName("p");
        for (int i = 0; i < list.getLength(); i++) {
            sb.append(list.item(i).getTextContent()).append("\n");
        }
        return sb.toString();
    }

    /**
     * Sets text title.
     *
     * @param title new text title.
     */
    public void setTitle(String title) {
        Element titleNode = (Element) document.getElementsByTagName("title").item(0);
        titleNode.setTextContent(title);
    }

    /**
     * Returns text title.
     *
     * @return text title.
     */
    public String getTitle() {
        Element titleNode = (Element) document.getElementsByTagName("title").item(0);
        return titleNode.getTextContent();
    }

    /**
     * Saves a xml file wich data from this model.
     *
     * @param file file to write.
     */
    public void saveAs(File file) {
        try {
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputPropertiesFactory.S_KEY_INDENT_AMOUNT, "4");
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(file);
            transformer.transform(source, result);
            this.file = file;
        } catch (TransformerConfigurationException ex) {
            System.err.println(ex);
        } catch (TransformerException ex) {
            System.err.println(ex);
        }
    }

    /**
     * Saves a xml file with data from this model. It writes to file defined in
     * constructor.
     */
    public void save() {
        saveAs(file);
    }

    /**
     * Saves a txt file with text from this model.
     *
     * @param file file to save.
     * @throws java.io.IOException if some error occurs while writing file.
     */
    public void saveTxt(File file) throws IOException {

        try (FileWriter fw = new FileWriter(file);
                BufferedWriter bw = new BufferedWriter(fw)) {

            if (getTitle().length() > 0) {
                bw.write(getTitle());
                bw.newLine();
                bw.newLine();
            }
            NodeList paragrafos = document.getElementsByTagName("p");
            for (int i = 0; i < paragrafos.getLength(); i++) {
                bw.write(paragrafos.item(i).getTextContent());
                bw.newLine();
            }
        }
    }

    /**
     * Checks if a file name has valid extension for image files that can be read
     * by java applications.
     * 
     * @param fileName file name.
     * @return true if file name has valid extension.
     */
    private boolean isValidExtension(String fileName) {
        String[] extensions = {"png", "jpeg", "jpg", "gif", "bmp", "wbmp",
            "PNG", "JPEG", "JPG", "GIF", "BMP", "wbmp"};
        for (String str : extensions) {
            if (fileName.endsWith(str)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Stores into {@code imagePaths} variable the paths of image files.
     */
    private void loadImages(boolean includeSubPaths, boolean includeDefaultLibrary) {

        ArrayList<String> usedImages = new ArrayList<>();
        NodeList imageList = document.getElementsByTagName("img");
        for (int i = 0; i < imageList.getLength(); i++) {
            String path = ((Element) imageList.item(i)).getAttribute("src");
            usedImages.add(path);
        }
        totalImages = imageList.getLength();

        if (includeDefaultLibrary) {
            readDefaultLibrary(usedImages);
        }

        NodeList fontList = document.getElementsByTagName("image_source_directory");
        for (int i = 0; i < fontList.getLength(); i++) {
            String dirName = ((Element) fontList.item(i)).getAttribute("src");
            File dir = new File(dirName);
            if (!dir.isDirectory()) {
                IllegalStateException ise = new IllegalStateException(){
                    @Override
                    public String getLocalizedMessage(){
                        return dir.getAbsolutePath();
                    }
                    @Override
                    public String getMessage(){
                        return dir.getAbsolutePath() + " is not a valid directory.";
                    }
                };
                
                throw ise;
            }
            loadSubPath(dir, usedImages, includeSubPaths);
        }
        totalImages += imagesPaths.size();
    }

    /**
     * Help method to load image paths.
     * @param dir image directory to read.
     * @param skipImages list with images to be skipped (images already used).
     * @param includeSubPaths if true, read also subdirectories.
     */
    private void loadSubPath(File dir, ArrayList<String> skipImages, boolean includeSubPaths) {

        for (File file : dir.listFiles()) {
            if (file.isFile()) {
                String fileName = file.getAbsolutePath();
                if (!skipImages.contains(fileName) && isValidExtension(fileName)) {
                    imagesPaths.add(fileName);
                }
            }
            if (file.isDirectory() && includeSubPaths) {
                loadSubPath(file, skipImages, includeSubPaths);
            }
        }
    }

    /**
     * Reads default library.
     * @param skipedImages list with images to be skipped (images already used).
     */
    private void readDefaultLibrary(ArrayList<String> skipedImages) {
        URL url = getClass().getClassLoader().getResource("resources/libraryList.txt");
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(url.openStream()))) {
            
            String line;
            while(br.ready()){
                line = br.readLine();
                if(!skipedImages.contains(line)){
                    imagesPaths.add(line);
                }
            }

        } catch (IOException ioe) {

        }
    }

    /**
     * Returns total number of images in image library.
     * @return total number of images in image library.
     */
    public int getTotalImages() {
        return totalImages;
    }

    /**
     * Returns file name used by this model.
     * @return file name used by this model.
     */
    public String getFileName() {
        return file.getName();
    }
}
