package org.galax1y.music_back.services;

import org.galax1y.music_back.domain.music.dtos.CreateMusicDto;
import org.galax1y.music_back.domain.music.dtos.CreateMusicWithMidiDto;
import org.galax1y.music_back.domain.music.dtos.MusicDto;
import org.galax1y.music_back.helpers.DownloadParams;
import org.springframework.core.io.Resource;

import java.util.List;
import java.util.UUID;

public interface IMusicService {
    List<MusicDto> getAll();
    MusicDto create(CreateMusicDto request) throws Exception;
    MusicDto createWithMidi(CreateMusicWithMidiDto request);
    Resource download(UUID musicId, DownloadParams params);
    boolean delete(UUID musicId);
}
