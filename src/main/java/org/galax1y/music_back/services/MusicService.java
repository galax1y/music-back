package org.galax1y.music_back.services;

import org.galax1y.music_back.domain.music.dtos.CreateMusicWithMidiDto;
import org.galax1y.music_back.domain.music.dtos.MusicDto;
import org.galax1y.music_back.domain.music.entities.Music;
import org.galax1y.music_back.domain.music.dtos.CreateMusicDto;
import org.galax1y.music_back.exceptions.ConflictException;
import org.galax1y.music_back.exceptions.NotFoundException;
import org.galax1y.music_back.exceptions.UnknownFormatException;
import org.galax1y.music_back.helpers.DownloadParams;
import org.galax1y.music_back.helpers.FileHandler;
import org.galax1y.music_back.repositories.InMemoryMusicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
public class MusicService implements IMusicService {
    @Autowired
    private InMemoryMusicRepository _musicRepository;

    @Override
    public List<MusicDto> getAll() {
        List<Music> items = _musicRepository.findAll();

        return items.stream()
                .map(Music::ToDto)
                .toList();
    }

    @Override
    public MusicDto create(CreateMusicDto request) throws ConflictException {
        String filename = FileHandler.generateSlug(request.title(), request.artist());

        if (FileHandler.fileAlreadyExists(filename)) {
            throw new ConflictException("Music with same name already exists");
        }

        Music music = new Music();

        music.setTitle(request.title());
        music.setArtist(request.artist());
        music.setRawText(request.rawText());

        _musicRepository.save(music);

        return Music.ToDto(music);
    }

    @Override
    public MusicDto createWithMidi(CreateMusicWithMidiDto request) {
        String filename = FileHandler.generateSlug(request.title(), request.artist());

        if (FileHandler.fileAlreadyExists(filename)) {
            throw new ConflictException("Music with same name already exists");
        }

        Music music = new Music();

        music.setTitle(request.title());
        music.setArtist(request.artist());

        var decodedFile = Base64.getDecoder().decode(request.midiInBase64());
        _musicRepository.saveMidi(music, decodedFile);

        return Music.ToDto(music);
    }

    @Override
    public Resource download(UUID musicId, DownloadParams params) {
        var music = _musicRepository.findById(musicId);

        if (music.isEmpty()) {
            throw new NotFoundException("Music not found.");
        }

        String extension = params.getExtension().toLowerCase();

        if (extension.equals(".mid")) {
            return FileHandler.getFileAsResource(music.get().getFileUrl());
        }

        if (extension.equals(".wav")) {
            return FileHandler.getFileAsResource(music.get().getWavUrl());
        }

        throw new UnknownFormatException("Unknown file format: " + extension);
    }

    @Override
    public boolean delete(UUID musicId) {
        return _musicRepository.deleteById(musicId);
    }
}
