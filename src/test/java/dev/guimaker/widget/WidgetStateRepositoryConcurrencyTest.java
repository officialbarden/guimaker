package dev.guimaker.widget;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;

class WidgetStateRepositoryConcurrencyTest {
    @BeforeEach
    @AfterEach
    void clearLocks() {
        WidgetStateRepository.clearHolderLocks();
    }

    @Test
    void onlyOneOwnerAcquiresSameWorldHolderConcurrently() throws Exception {
        String lockKey = "world:shared_holder";
        UUID firstOwner = UUID.randomUUID();
        UUID secondOwner = UUID.randomUUID();
        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);

        try (ExecutorService executor = Executors.newFixedThreadPool(2)) {
            Future<Boolean> first = executor.submit(() -> acquireTogether(lockKey, firstOwner, ready, start));
            Future<Boolean> second = executor.submit(() -> acquireTogether(lockKey, secondOwner, ready, start));
            ready.await();
            start.countDown();

            boolean firstResult = first.get();
            boolean secondResult = second.get();
            assertNotEquals(firstResult, secondResult,
                    "ConcurrentHashMap.putIfAbsent must allow exactly one distinct owner");

            UUID winner = firstResult ? firstOwner : secondOwner;
            UUID loser = firstResult ? secondOwner : firstOwner;
            assertFalse(WidgetStateRepository.tryAcquireHolderLock(lockKey, loser),
                    "loser must remain blocked while the winner owns the lock");

            WidgetStateRepository.releaseHolderLock(lockKey, winner);
            assertTrue(WidgetStateRepository.tryAcquireHolderLock(lockKey, loser),
                    "lock must become acquirable after the owner releases it");
        }
    }

    private static boolean acquireTogether(String key, UUID owner, CountDownLatch ready,
                                           CountDownLatch start) throws Exception {
        ready.countDown();
        start.await();
        return WidgetStateRepository.tryAcquireHolderLock(key, owner);
    }
}
