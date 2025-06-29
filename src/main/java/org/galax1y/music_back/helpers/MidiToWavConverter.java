package org.galax1y.music_back.helpers;

import java.io.File;

public class MidiToWavConverter {
    public static File convertMidiToWav(String midiFilePath) throws Exception {
        File midiFile = new File(midiFilePath);
        if (!midiFile.exists()) {
            throw new IllegalArgumentException("MIDI file not found: " + midiFile.getAbsolutePath());
        }

        File soundfontFile = new File("src/synthesizer/FluidR3_GM.sf2");
        if (!soundfontFile.exists()) {
            throw new IllegalArgumentException("SoundFont file not found: " + soundfontFile.getAbsolutePath());
        }

        // Build the WAV file path in the same folder as MIDI file
        String wavPath = midiFilePath.replaceAll("(?i)\\.mid$", ".wav");
        File wavFile = new File(wavPath);

        ProcessBuilder pb = new ProcessBuilder(
                "fluidsynth",
                "-ni",
                "-F", wavFile.getAbsolutePath(),
                "-r", "44100",
                soundfontFile.getAbsolutePath(),
                midiFile.getAbsolutePath()
        );

/*  Debugging
        System.out.println("Command: " + String.join(" ", pb.command()));
        System.out.println("SoundFont exists? " + soundfontFile.exists());
        System.out.println("SoundFont readable? " + soundfontFile.canRead());
*/

        pb.inheritIO(); // inherit IO to show fluidsynth output/errors

        Process process = pb.start();
        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new RuntimeException("FluidSynth failed with exit code: " + exitCode);
        }

        System.out.println("Done.");

        return wavFile;
    }
}
