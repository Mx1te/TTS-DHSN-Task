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

import java.io.*;

@RestController
public class TTSController {

    private static final String AUDIO_PATH = "wavedump/output.wav";

    @PostMapping("/api")
    public ResponseEntity<String> handleUpload(@RequestParam("file") MultipartFile file) {
        try {
            String text = extractText(file);
            if (text == null || text.isBlank()) {
                return ResponseEntity.badRequest().body("Datei konnte nicht gelesen werden oder war leer.");
            }

            // FreeTTS konfigurieren
            VoiceManager vm = VoiceManager.getInstance();
            Voice voice = vm.getVoice("kevin16");
            if (voice == null) return ResponseEntity.status(500).body("Stimme 'kevin16' nicht gefunden.");
            voice.allocate();

            // AudioPlayer zum WAV-Datei-Speichern
            AudioPlayer audioPlayer = new SingleFileAudioPlayer("wavedump/output", AudioFileFormat.Type.WAVE);
            voice.setAudioPlayer(audioPlayer);
            voice.speak(text);
            audioPlayer.close();
            voice.deallocate();

            return ResponseEntity.ok("Text verarbeitet. Du kannst dir die Audio-Datei unter /audio anhören.");
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
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"output.wav\"")
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
        }
        return null;
    }
}
