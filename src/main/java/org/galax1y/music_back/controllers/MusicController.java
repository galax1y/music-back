package org.galax1y.music_back.controllers;

import org.galax1y.music_back.domain.music.dtos.CreateMusicDto;
import org.galax1y.music_back.domain.music.dtos.CreateMusicWithMidiDto;
import org.galax1y.music_back.domain.music.dtos.MusicDto;
import org.galax1y.music_back.exceptions.ConflictException;
import org.galax1y.music_back.helpers.DownloadParams;
import org.galax1y.music_back.services.MusicService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/music")
public class MusicController {
    private final MusicService musicService;

    public MusicController(MusicService musicService) {
        this.musicService = musicService;
    }

    @PostMapping
    public ResponseEntity<MusicDto> create(@RequestBody CreateMusicDto dto) {
        try {
            var response = musicService.create(dto);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
        catch (ConflictException ex) {
            System.out.println("Conflict Exception: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/midi")
    public ResponseEntity<MusicDto> createWithMidi(@RequestBody CreateMusicWithMidiDto dto) {
        try {
            var response = musicService.createWithMidi(dto);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (ConflictException ex) {
            System.out.println("Conflict Exception: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{musicId}/download")
    public ResponseEntity<Resource> download(
            @PathVariable String musicId,
            @RequestParam(name="format") DownloadParams params) {
        try {
            Resource resource = musicService.download(UUID.fromString(musicId), params);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<MusicDto>> getAll() {
        try {
            var response = musicService.getAll();

            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        try {
            boolean deleted = musicService.delete(id);

            if (!deleted) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            return ResponseEntity.ok().build();
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
