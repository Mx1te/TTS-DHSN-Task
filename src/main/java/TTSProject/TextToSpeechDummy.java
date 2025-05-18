package TTSProject;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import com.sun.speech.freetts.audio.AudioPlayer;
import com.sun.speech.freetts.audio.SingleFileAudioPlayer;

import javax.sound.sampled.AudioFileFormat;

public class TextToSpeechDummy {

    public static void main(String[] args) {

        // Setzt die Voice Directory für FreeTTS (Pflicht!)
        System.setProperty("freetts.voices",
                "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");

        // Listet verfügbare Stimmen auf
        System.out.println("Verfügbare Stimmen:");
        Voice[] voiceList = VoiceManager.getInstance().getVoices();
        for (int i = 0; i < voiceList.length; i++) {
            System.out.println("Voice " + i + ": " + voiceList[i].getName());
        }

        // Lädt die Stimme "kevin16"
        Voice voice = VoiceManager.getInstance().getVoice("kevin16");

        if (voice == null) {
            System.err.println("Die Stimme 'kevin16' konnte nicht geladen werden.");
            return;
        }

        // Initialisiere Stimme
        voice.allocate();

        try {
            // Speicherort für WAV-Datei (nur Pfad ohne Endung!)
            String basePath = "/home/kai/TTS_project/TTS-DHSN-Task/wavedump/testwav";

            // Erzeuge AudioPlayer zum Schreiben in Datei
            AudioPlayer audioPlayer = new SingleFileAudioPlayer(basePath, AudioFileFormat.Type.WAVE);
            voice.setAudioPlayer(audioPlayer);

            // Text sprechen
            String text = "Hallo! Dies ist ein Test des Free TTS Systems mit der Stimme Kevin sechzehn.";
            boolean status = voice.speak(text);
            System.out.println("Sprachsynthese erfolgreich: " + status);

            // Wichtig: Datei schließen!
            audioPlayer.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            voice.deallocate();
        }
    }
}
