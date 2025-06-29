package org.galax1y.music_back.helpers;

import org.jfugue.midi.MidiFileManager;
import org.jfugue.pattern.Pattern;
import org.jfugue.pattern.Token;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

public class PatternParser {
    private static final int MAX_TEMPO = 300;
    private static final int DEFAULT_TEMPO = 120;
    private static final int MIN_TEMPO = 1;

    private static final int MAX_VOLUME = 127;
    private static final int DEFAULT_VOLUME = 64;
    private static final int MIN_VOLUME = 1;

    private static final int MAX_OCTAVE = 8;
    private static final int DEFAULT_OCTAVE = 4;
    private static final int MIN_OCTAVE = 1;

    private static final int MAX_INSTRUMENT = 127;
    private static final int DEFAULT_INSTRUMENT = 0;
    private static final int MIN_INSTRUMENT = 0;

    private Pattern processed;
    private int octave;
    private int tempo;
    private int volume;
    private int instrument;
    private final Map<String, Runnable> commandsMapping = new HashMap<>();
    private final SecureRandom rng;

    public PatternParser() {
        volume = DEFAULT_VOLUME;
        tempo = DEFAULT_TEMPO;
        octave = DEFAULT_OCTAVE;
        instrument = DEFAULT_INSTRUMENT;
        processed = new Pattern(" ");
        rng = new SecureRandom();

        // Notes control
        commandsMapping.put("A", () -> this.InsertNote("A"));
        commandsMapping.put("a", () -> this.InsertNote("A"));
        commandsMapping.put("B", () -> this.InsertNote("B"));
        commandsMapping.put("b", () -> this.InsertNote("B"));
        commandsMapping.put("C", () -> this.InsertNote("C"));
        commandsMapping.put("c", () -> this.InsertNote("C"));
        commandsMapping.put("D", () -> this.InsertNote("D"));
        commandsMapping.put("d", () -> this.InsertNote("D"));
        commandsMapping.put("E", () -> this.InsertNote("E"));
        commandsMapping.put("e", () -> this.InsertNote("E"));
        commandsMapping.put("F", () -> this.InsertNote("F"));
        commandsMapping.put("f", () -> this.InsertNote("F"));
        commandsMapping.put("G", () -> this.InsertNote("G"));
        commandsMapping.put("g", () -> this.InsertNote("G"));
        commandsMapping.put(" ", this::InsertSilence);

        // Volume control
        commandsMapping.put("+", this::DoubleVolume);
        commandsMapping.put("-", this::DefaultVolume);

        // If last action was a note, repeats it, else plays 125 ringtone
        commandsMapping.put("O", this::Evaluate);
        commandsMapping.put("o", this::Evaluate);
        commandsMapping.put("I", this::Evaluate);
        commandsMapping.put("i", this::Evaluate);
        commandsMapping.put("U", this::Evaluate);
        commandsMapping.put("u", this::Evaluate);

        // Octave control
        commandsMapping.put("R+", this::IncrementOctave);
        commandsMapping.put("R-", this::DecrementOctave);

        commandsMapping.put("?", () -> this.InsertNote(GenerateRandomNote()));

        // Instrument control
        commandsMapping.put("\n", this::NextInstrument);

        // Tempo control
        commandsMapping.put("BPM+", () -> this.IncreaseTempo(80));
        commandsMapping.put(";", this::RandomizeTempo);
    }

    public Pattern Parse(String[] tokens) {
        Runnable handler;

        for (int i = 0; i < tokens.length; i++) {
            System.out.printf("[Debugging] tokens[%d] = %s\n", i, tokens[i]);
            System.out.printf("[Debugging] processed: %s\n", this.processed);
            handler = null;

            if (commandsMapping.containsKey(tokens[i])) {
                handler = commandsMapping.get(tokens[i]);
            }
            /* else {
                // Phase 3: Altered to NOP
                // String previousToken = (i > 0) ? tokens[i - 1] : "";
                // handler = previousToken.isEmpty() ? this::InsertSilence : () -> this.Repeat(previousToken);
                }
             */

            if (handler != null) {
                handler.run();
            }
        }
        System.out.println("Processed:" + this.processed);
        return new Pattern(processed);
    }

    private void IncrementOctave() {
        this.octave = this.octave >= 8 ?  MAX_OCTAVE : this.octave + 1;
    }

    private void DecrementOctave() {
        this.octave = this.octave <= 1 ? MIN_OCTAVE : this.octave - 1;
    }

    private void Repeat(String token) {
        this.processed.add(token);
    }

    private void Evaluate() {
        try {
            var lastToken = this.processed.getTokens().getLast();

            if (lastToken.getType().equals(Token.TokenType.NOTE)) {
                this.Repeat(lastToken.toString());
            }
        }
        catch (Exception e) {
            this.InsertRingtone();
        }
    }

    private void InsertRingtone() {
        var originalInstrument = this.instrument;
        Pattern changeInstrument = new Pattern();
        changeInstrument.setInstrument(124);
        this.processed.add(changeInstrument);
        this.InsertNote("C");
        changeInstrument.setInstrument(originalInstrument);
        this.processed.add(changeInstrument);
    }

    private void InsertSilence() {
        this.processed.add("Rw");
    }

    private void InsertNote(String note) {
        note += this.octave;
        this.processed.add(note);
    }

    private String GenerateRandomNote() {
        return "" + ((char) rng.nextInt(65, 72));
    }

    // Overflow at MAX_INSTRUMENT <=> 127 + 1 becomes 0
    private void NextInstrument() {
        this.instrument = this.instrument >= MAX_INSTRUMENT ? MIN_INSTRUMENT : this.instrument + 1;

        Pattern newInstrument = new Pattern();
        newInstrument.setInstrument(this.instrument);
        this.processed.add(newInstrument);
    }

    // Underflow at MIN_INSTRUMENT <=> 0 - 1 becomes 127
    private void PreviousInstrument() {
        this.instrument = this.instrument <= MIN_INSTRUMENT ? MAX_INSTRUMENT : this.instrument - 1;

        Pattern newInstrument = new Pattern();
        newInstrument.setInstrument(this.instrument);
        this.processed.add(newInstrument);
    }

    private void IncreaseTempo(int amount) {
        this.tempo += amount;

        this.processed.add(String.format("T%d", this.tempo));
    }

    private void RandomizeTempo() {
        SecureRandom rng = new SecureRandom();

        // Random int in interval [1, 300]
        String randomTempo = String.valueOf(rng.nextInt(MAX_TEMPO) + 1);

        this.processed.add(String.format("T%s", randomTempo));
    }

    private void DoubleVolume() {
        this.volume *= 2;

        this.volume = Math.min(this.volume, MAX_VOLUME);

        this.processed.add(String.format(":CON(7,%d)", this.volume));
    }

    private void DefaultVolume() {
        this.volume = DEFAULT_VOLUME;

        this.processed.add(String.format(":CON(7,%d)", this.volume));
    }

    private void IncreaseVolume(int amount) {
        this.volume += amount;

        this.volume = Math.min(this.volume, MAX_VOLUME);

        this.processed.add(String.format(":CON(7,%d)", this.volume));
    }

    private void DecreaseVolume(int amount) {
        this.volume -= amount;

        this.volume = Math.max(this.volume, MIN_VOLUME);

        this.processed.add(String.format(":CON(7,%d)", this.volume));
    }

    public String ExportToMidi(String filename) {
        try {
            String pathname = Paths.get("src", "files", filename + ".mid").toString();
            System.out.println("Exporting to midi processed:" + this.processed);
            MidiFileManager.savePatternToMidi(this.processed, new File(pathname));
            System.gc();
            return pathname;
        } catch (IOException e) {
            System.out.println("[Parser] something went wrong:"+ e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
