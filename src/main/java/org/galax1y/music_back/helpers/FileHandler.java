package org.galax1y.music_back.helpers;

import org.galax1y.music_back.exceptions.ConflictException;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;
import java.util.stream.Collectors;

public class FileHandler {
    public static String toBase64(String fileUrl) {
        try {
            byte[] fileBytes = Files.readAllBytes(Paths.get(fileUrl));
            return Base64.getEncoder().encodeToString(fileBytes);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read file: " + fileUrl, e);
        }
    }

    public static String generateSlug(String... parts) {
        return Arrays.stream(parts)
            .filter(Objects::nonNull)
            .map(part -> part.toLowerCase()
            .replaceAll("[^a-z0-9\\s]", "") // remove non-alphanum
            .replaceAll("\\s+", "-"))       // replace spaces with hyphens
            .collect(Collectors.joining("-"));
    }

    public static boolean fileAlreadyExists(String fileName) {
        // Ensure the filename ends with .mid
        if (!fileName.toLowerCase().endsWith(".mid")) {
            fileName += ".mid";
        }

        // Build the path to the file
        Path path = Paths.get("src", "files", fileName);
        return Files.exists(path);
    }

    public static Resource getFileAsResource(String filePath) {
        Path path = Paths.get(filePath);
        File file = path.toFile();

        if (!Files.exists(path) || !Files.isReadable(path)) {
            throw new RuntimeException("File not found or not readable.");
        }

        return new FileSystemResource(file);
    }

    public static String insertFile(String fileName, byte[] decodedFile) {
        if (fileAlreadyExists(fileName)) {
            throw new ConflictException("File with this name already exists");
        }

        if (!fileName.toLowerCase().endsWith(".mid")) {
            fileName += ".mid";
        }

        try {
            Path path = Paths.get("src", "files", fileName);
            Files.write(path, decodedFile);
            return path.toString();
        } catch (IOException e) {
            System.err.println("Failed to create file");
        }

        throw new RuntimeException("Something went wrong while creating a file");
    }

    public static boolean removeFile(String fileUrl) {
        try {
            System.gc();
            Path midiPath = Paths.get(fileUrl);
            Path wavPath = Paths.get(fileUrl.replaceAll(".mid", ".wav"));
            boolean isMidiDeleted = Files.deleteIfExists(midiPath);
            boolean isWavDeleted = Files.deleteIfExists(wavPath);

            if (!isMidiDeleted) {
                System.out.println("[FileHandler] Midi file was not deleted.");
                return false;
            }

            if (!isWavDeleted) {
                System.out.println("[FileHandler] Wav file was not deleted");
            }

            System.out.println("[FileHandler] File at '" + midiPath + "' was deleted successfully.");
            System.out.println("[FileHandler] File at '" + wavPath + "' was deleted successfully.");
            return true;
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("[FileHandler] Error deleting file: " + e.getMessage());
            return false;
        }
    }

}
