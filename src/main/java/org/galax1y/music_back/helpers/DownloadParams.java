package org.galax1y.music_back.helpers;

import lombok.Getter;

@Getter
public enum DownloadParams {
    WAV(".wav"),
    MIDI(".mid");

    private final String extension;

    DownloadParams(String extension) {
        this.extension = extension;
    }
}
