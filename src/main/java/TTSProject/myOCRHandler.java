package main.java.TTSProject;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class myOCRHandler {

    private final Tesseract tesseract;

    public myOCRHandler(String tessDataPath, String language) {
        tesseract = new Tesseract();
        tesseract.setDatapath(tessDataPath);
        tesseract.setLanguage(language);
    }

    /**
     * Runs OCR on the given image file.
     * @param imageFile the image file to process
     * @return the recognized text
     * @throws TesseractException if OCR fails
     */
    public String parseImage(File imageFile) throws TesseractException, IOException {
        BufferedImage image = ImageIO.read(imageFile);
        if (image == null) {
            throw new IOException("ImageIO konnte das Bild nicht lesen: " + imageFile.getAbsolutePath());
        }
        return tesseract.doOCR(image);
    }
}
