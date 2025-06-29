package org.galax1y.music_back.repositories;

import org.galax1y.music_back.domain.music.entities.Music;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IMusicRepository {
    List<Music> findAll();
    Music save(Music music);
    Music saveMidi(Music music, byte[] fileInBase64);
    Optional<Music> findById(UUID id);
    boolean deleteById(UUID id);
}
