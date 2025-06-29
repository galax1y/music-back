package org.galax1y.music_back.domain.music.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.galax1y.music_back.domain.music.dtos.MusicDto;
import org.galax1y.music_back.helpers.FileHandler;

import java.util.UUID;

@Table(name = "music")
@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Music {
    @Id
    @GeneratedValue
    private UUID id;

    private String title;
    private String artist;
    private String rawText;
    private String processedText;
    private String fileUrl;
    private String wavUrl;

    public static Music ToDomain(MusicDto dto) {
        Music music = new Music();

        music.setId(UUID.fromString(dto.id()));
        music.setArtist(dto.artist());
        music.setTitle(dto.title());
        music.setRawText(dto.rawText());
        music.setProcessedText(dto.processedText());
        music.setFileUrl(dto.fileUrl());
        music.setWavUrl(dto.fileUrl().replaceAll(".mid", ".wav"));

        return music;
    }

    public static MusicDto ToDto(Music music) {
        MusicDto musicDto;

        musicDto = new MusicDto(
                music.getId().toString(),
                music.getTitle(),
                music.getArtist(),
                music.getRawText(),
                music.getProcessedText(),
                music.getFileUrl(),
                FileHandler.toBase64(music.getWavUrl())
        );

        return musicDto;
    }
}
