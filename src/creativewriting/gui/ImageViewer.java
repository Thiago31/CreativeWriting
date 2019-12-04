package creativewriting.gui;

import creativewriting.textmodel.TextModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * Panel to show images.
 *
 * @author Thiago
 */
public class ImageViewer extends JPanel {

    /**
     * Index of image that is displayed.
     */
    private int imageNumber;
    
    /**
     * TextModel from which to read image source.
     */
    private TextModel textModel;
    
    /**
     * Screen to show image.
     */
    private Tela tela;
    
    /**
     * PREVIOUS button.
     */
    private JButton previous;
    
    /**
     * NEXT button.
     */
    private JButton next;

    /**
     * Constructor. Makes a new Image Viewer.
     * @param rb ResourceBundle that defines language to show button text.
     */
    public ImageViewer(ResourceBundle rb) {
        super(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        tela = new Tela();
        add(tela, BorderLayout.CENTER);

        previous = new JButton();
        previous.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                imageNumber--;
                tela.setImage(textModel.getImage(imageNumber));
                if (imageNumber == 0) {
                    previous.setEnabled(false);
                }
                next.setEnabled(true);
            }
        });
        previous.setEnabled(false);

        next = new JButton();
        next.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                imageNumber++;

                tela.setImage(textModel.getImage(imageNumber));

                previous.setEnabled(true);
                if (imageNumber == textModel.getTotalImages()) {
                    next.setEnabled(false);
                }
            }
        });
        next.setEnabled(false);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        buttonPanel.add(previous);
        buttonPanel.add(next);

        add(buttonPanel, BorderLayout.SOUTH);
        setButtonText(rb);
    }

    /**
     * Sets TextModel from which images are read.
     * @param textModel TextModel to link with this ImageViewer.
     */
    public void setTextModel(TextModel textModel) {
        this.textModel = textModel;

        if (textModel == null) {
            tela.setImage(null);
            previous.setEnabled(false);
            next.setEnabled(false);
            return;
        }

        if (textModel.getTotalImages() == 0) {
            tela.setImage(textModel.nextImage());
            previous.setEnabled(false);
            next.setEnabled(false);
            return;
        }

        tela.setImage(textModel.nextImage());
        imageNumber = textModel.getNumberOfImages() - 1;
        next.setEnabled(true);
        if (imageNumber > 0) {
            previous.setEnabled(true);
        } else {
            previous.setEnabled(false);
        }
    }

    /**
     * Sets button title language.
     * @param rb ResourceBundle that defines language to show button text.
     */
    public void setButtonText(ResourceBundle rb) {
        previous.setText(rb.getString("previousButton"));
        next.setText(rb.getString("nextButton"));
    }

    /**
     * Class to draw and display image. Image is drawn centered and resized if
     * needed.
     */
    private class Tela extends JPanel {

        /**
         * Image to draw.
         */
        private Image image;

        /**
         * Constructor. Makes a new Tela.
         */
        Tela() {
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createLineBorder(Color.BLUE));
            image = null;
        }

        /**
         * Sets image to be displayed.
         * @param image new image to be displayed.
         */
        void setImage(Image image) {
            this.image = image;
            repaint();
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (image == null) {
                return;
            }

            if (image == TextModel.BROKEN_IMAGE) {

                g.setFont(new Font("Sans_Serif", Font.BOLD, 16));

                FontMetrics fm = g.getFontMetrics();
                String error = "Erro: arquivo de imagem não encontrado:";
                int x = (this.getWidth() - fm.stringWidth(error)) / 2;
                int y = this.getHeight() / 2;
                g.setColor(Color.RED);
                g.drawString(error, x, y);

                String path = textModel.getBrokenImagePath();
                x = (this.getWidth() - fm.stringWidth(path)) / 2;
                y += fm.getHeight();
                g.drawString(path, x, y);

                return;
            }

            int ws = this.getWidth();
            int hs = this.getHeight();

            int wi = image.getWidth(this);
            int hi = image.getHeight(this);

            if (wi > ws || hi > hs) {
                double scalaW = ((double) ws) / wi;
                double scalaH = ((double) hs) / hi;
                double scala = Math.min(scalaW, scalaH);
                wi = (int) (scala * wi);
                hi = (int) (scala * hi);
            }

            int x = (ws - wi) / 2;
            int y = (hs - hi) / 2;
            g.drawImage(image, x, y, wi, hi, this);
        }

    }

}
