package dev.guimaker.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;

class WorldDataPathsConcurrencyTest {
    @TempDir
    Path directory;

    @Test
    void concurrentMigrationCopyCreatesTargetExactlyOnce() throws Exception {
        Path source = directory.resolve("legacy.json");
        Path target = directory.resolve("world.json");
        Files.writeString(source, "{\"source\":true}");
        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);

        try (ExecutorService executor = Executors.newFixedThreadPool(2)) {
            Future<Boolean> first = executor.submit(() -> copyTogether(source, target, ready, start));
            Future<Boolean> second = executor.submit(() -> copyTogether(source, target, ready, start));
            ready.await();
            start.countDown();

            assertNotEquals(first.get(), second.get(),
                    "CREATE_NEW copy must report exactly one successful creator");
        }
        assertEquals("{\"source\":true}", Files.readString(target));
    }

    private static boolean copyTogether(Path source, Path target, CountDownLatch ready,
                                        CountDownLatch start) throws Exception {
        ready.countDown();
        start.await();
        return WorldDataPaths.copyLegacyFileIfAbsent(source, target);
    }
}
