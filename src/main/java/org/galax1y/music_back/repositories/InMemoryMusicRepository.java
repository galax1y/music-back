package org.galax1y.music_back.repositories;

import org.galax1y.music_back.domain.music.entities.Music;
import org.galax1y.music_back.helpers.FileHandler;
import org.galax1y.music_back.helpers.MidiToWavConverter;
import org.galax1y.music_back.helpers.PatternParser;
import org.galax1y.music_back.helpers.Tokenizer;
import org.jfugue.pattern.Pattern;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryMusicRepository implements IMusicRepository {
    private final Map<UUID, Music> storage = new ConcurrentHashMap<>();

    public List<Music> findAll() {
        return new ArrayList<>(storage.values());
    }

    public Optional<Music> findById(UUID id) {
        return Optional.ofNullable(storage.get(id));
    }

    public Music save(Music music) {
        UUID id = music.getId() != null ? music.getId() : UUID.randomUUID();
        music.setId(id);

        String[] tokens = Tokenizer.Tokenize(music.getRawText());
        PatternParser parser = new PatternParser();
        Pattern processed = parser.Parse(tokens);
        music.setProcessedText(processed.toString());

        String filename = FileHandler.generateSlug(music.getTitle(), music.getArtist());
        try {
            String fileUrl = parser.ExportToMidi(filename);
            MidiToWavConverter.convertMidiToWav(fileUrl);
            music.setFileUrl(fileUrl);
            music.setWavUrl(fileUrl.replaceAll(".mid", ".wav"));
        } catch (Exception e) {
            System.out.println("File parsing error." + e.getMessage());
        }

        storage.put(id, music);
        return music;
    }

    public Music saveMidi(Music music, byte[] decodedFile) {
        UUID id = music.getId() != null ? music.getId() : UUID.randomUUID();
        music.setId(id);

        String filename = FileHandler.generateSlug(music.getTitle(), music.getArtist());
        try {
            String fileUrl = FileHandler.insertFile(filename, decodedFile);
            MidiToWavConverter.convertMidiToWav(fileUrl);
            music.setFileUrl(fileUrl);
            music.setWavUrl(fileUrl.replaceAll(".mid", ".wav"));
        } catch (Exception e) {
            System.out.println("File parsing error." + e.getMessage());
        }

        storage.put(id, music);
        return music;
    }

    public boolean deleteById(UUID id) {
        var musicToDelete = this.findById(id);

        if (musicToDelete.isEmpty()) {
            return false;
        }

        boolean isFileRemoved = FileHandler.removeFile(musicToDelete.get().getFileUrl());

        if (!isFileRemoved) {
            return false;
        }

        var result = storage.remove(id);
        return result != null;
    }
}