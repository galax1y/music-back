package org.galax1y.music_back.domain.music.dtos;

public record MusicDto(
        String id,
        String title,
        String artist,
        String rawText,
        String processedText,
        String fileUrl,
        String wavInBase64) { }
