package TTSProject;
import com.sun.speech.freetts.*;
import com.sun.speech.freetts.audio.AudioPlayer;
import com.sun.speech.freetts.audio.SingleFileAudioPlayer;
import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import javax.sound.sampled.AudioFileFormat;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import main.java.TTSProject.myOCRHandler;
import net.sourceforge.tess4j.Tesseract;

import java.io.*;
import java.net.http.HttpHeaders;

@RestController
public class TTSController {

    private static final String AUDIO_PATH = "wavedump/output.wav";
    private String parseSSML(String input) {
        return input.replaceAll("(?i)<pause\\s*/?>", " ... ");  // 3 Punkte als Pause
    }
    /**
     * Diese Methode verarbeitet entweder den eingegebenen Text oder den Inhalt der hochgeladenen Datei.
     * Sie wandelt den Text in Sprache um und speichert die Audiodatei im WAV-Format.
     *
     * @param file    Die hochgeladene Datei (optional).
     * @param rawText Der eingegebene Text (optional).
     * @return Eine ResponseEntity mit dem Ergebnis der Verarbeitung.
     */    
    @PostMapping("/api")
public ResponseEntity<String> handleUpload(
        @RequestParam(value = "file", required = false) MultipartFile file,
        @RequestParam(value = "text", required = false) String rawText) {

    try {
        String text;

        if (rawText != null && !rawText.isBlank()) {
            text = parseSSML(rawText); // Text aus Eingabefeld inkl. SSML
        } else if (file != null && !file.isEmpty()) {
            text = extractText(file);  // Text aus Datei
        } else {
            return ResponseEntity.badRequest().body("Bitte geben Sie Text ein oder laden Sie eine Datei hoch.");
        }

        if (text == null || text.isBlank()) {
            return ResponseEntity.badRequest().body("Kein gültiger Text gefunden.");
        }

        // FreeTTS vorbereiten
        System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");
        VoiceManager vm = VoiceManager.getInstance();
        Voice voice = vm.getVoice("kevin16");

        if (voice == null) return ResponseEntity.status(500).body("Stimme 'kevin16' nicht gefunden.");
        voice.allocate();

        voice.setPitch(120);
        voice.setRate(140);
        voice.setVolume(1.0f);

        AudioPlayer audioPlayer = new SingleFileAudioPlayer("wavedump/output", AudioFileFormat.Type.WAVE);
        voice.setAudioPlayer(audioPlayer);

        voice.speak(text);

        audioPlayer.close();
        voice.deallocate();

        return ResponseEntity.ok("Text verarbeitet. Du kannst dir die Audio-Datei unter /wavedump anhören oder direkt hier der Stimme lauschen.");

    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(500).body("Fehler: " + e.getMessage());
    }
}


    @GetMapping("/audio")
    public ResponseEntity<InputStreamResource> getAudio() throws IOException {
        File file = new File(AUDIO_PATH);
        if (!file.exists()) {
            return ResponseEntity.status(404).body(null);
        }

        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("audio/wav"))
                .contentLength(file.length())
                .header("inline; filename=\"output.wav\"")
                .body(resource);
    }

    private String extractText(MultipartFile file) throws IOException {
        String filename = file.getOriginalFilename();
        if (filename == null) return null;

        if (filename.endsWith(".txt")) {
            return new String(file.getBytes(), java.nio.charset.StandardCharsets.UTF_8);
        } else if (filename.endsWith(".pdf")) {
            try (PDDocument document = PDDocument.load(file.getInputStream())) {
                PDFTextStripper stripper = new PDFTextStripper();
                return stripper.getText(document);
            }
        } else if (filename.endsWith(".png") || filename.endsWith(".jpg") || filename.endsWith(".jpeg")) {
            File tempFile = File.createTempFile("upload_", "_" + file.getOriginalFilename());
            file.transferTo(tempFile);
        
            // Optional: Ausgabe zur Kontrolle
            System.out.println("Temporäre Datei: " + tempFile.getAbsolutePath());

            myOCRHandler ocrHandler = new myOCRHandler("tessdata", "eng");
            try {
                return ocrHandler.parseImage(tempFile);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            finally {tempFile.deleteOnExit();}
        }
        return null;
    }
}
