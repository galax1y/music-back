package org.galax1y.music_back.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class FileCleanup {
    private static final String TEMP_DIR_PATH = "src/files";

    @PostConstruct
    public void onInit() {
        System.out.println("[Initializer] Cleaning up temporary files.");

        System.out.println("[Initializer] Forcing garbage collection.");
        System.gc();

        File tempDir = new File(TEMP_DIR_PATH);

        File[] files = tempDir.listFiles();

        if (files != null) {
            for (File file : files) {
                System.out.println("[Initializer] File found: " + file.getName());
                if (file.isFile()) {
                    try {
                        if (file.delete()) {
                            System.out.println("[Initializer] Deleted: " + file.getName());
                        } else {
                            System.out.println("[Initializer] Failed to delete: " + file.getName());
                        }
                    } catch (SecurityException e) {
                        System.err.println("[Initializer] Permission denied to delete " + file.getName() + ": " + e.getMessage());
                    }
                }
            }
        }

        System.out.println("[Initializer] Cleanup complete.");
    }

    @PreDestroy
    public void onShutdown() {
        System.out.println("[Shutdown] Cleaning up temporary files.");

        System.out.println("[Shutdown] Forcing garbage collection." );
        System.gc();

        File tempDir = new File(TEMP_DIR_PATH);

        File[] files = tempDir.listFiles();

        if (files != null) {
            for (File file : files) {
                System.out.println("[Shutdown] File found: " + file.getName());
                if (file.isFile()) {
                    try {
                        if (file.delete()) {
                            System.out.println("[Shutdown] Deleted: " + file.getName());
                        } else {
                            System.out.println("[Shutdown] Failed to delete: " + file.getName());
                        }
                    } catch (SecurityException e) {
                        System.err.println("[Shutdown] Permission denied to delete " + file.getName() + ": " + e.getMessage());
                    }
                }
            }
        }

        System.out.println("[Shutdown] Cleanup complete.");
    }
}