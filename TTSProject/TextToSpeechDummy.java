package TTSProject;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import com.sun.speech.freetts.audio.AudioPlayer;
import com.sun.speech.freetts.audio.SingleFileAudioPlayer;

public class TextToSpeechDummy {

    public static void main(String[] args) {

        System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory"); //med quality free Voice
        // System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us.cmu_time_awb.AlanVoiceDirectory"); //high quality limited Voice

        Voice voice = VoiceManager.getInstance().getVoice("kevin16");
        // Voice voice = VoiceManager.getInstance().getVoice("alan");


        // Print all available voices
        Voice []voicelist = VoiceManager.getInstance().getVoices();

        for (int i = 0; i < voicelist.length; i++) {
            System.out.println("Voice " + i + ": " + voicelist[i].getName());
        }

        if (voice != null) {
            voice.allocate();

            System.out.println("Voice Rate: " + voice.getRate());
            System.out.println("Voice Pitch: " + voice.getPitch());
            System.out.println("Voice Volume: " + voice.getVolume());

            AudioPlayer aplayer = new SingleFileAudioPlayer("E:\\ProgrammingProjects\\TTS-DHSN-Task\\wavedump\\testwav", javax.sound.sampled.AudioFileFormat.Type.WAVE);

            voice.setAudioPlayer(aplayer);

            boolean status = voice.speak("Hello, this is a test of the FreeTTS text-to-speech system.");
            // boolean status = voice.speak("The time is now " + java.time.LocalTime.now() + "."); // Alan limited to time anouncement

            System.out.println("Status: " + status);

            aplayer.close();

            voice.deallocate();
        } else {
            System.out.println("No voice available.");
        }
    }
}
