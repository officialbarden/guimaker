package dev.guimaker.gate;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** Prevents duplicate interaction callbacks from opening the same GUI repeatedly. */
public final class ItemOpenRateLimitGate {
    private static final long MIN_INTERVAL_MS = 250L;
    private static final ConcurrentHashMap<UUID, Long> LAST_OPEN = new ConcurrentHashMap<>();

    private ItemOpenRateLimitGate() {}

    public static boolean tryAcquire(UUID playerId) {
        long now = System.currentTimeMillis();
        boolean[] acquired = {false};
        LAST_OPEN.compute(playerId, (ignored, previous) -> {
            if (previous == null || now - previous >= MIN_INTERVAL_MS) {
                acquired[0] = true;
                return now;
            }
            return previous;
        });
        return acquired[0];
    }

    public static void clear(UUID playerId) {
        LAST_OPEN.remove(playerId);
    }
}
