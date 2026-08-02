package dev.guimaker.gate;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * RateLimitGate — GATE #3: Button click rate limiting.
 *
 * WHY IT'S NEEDED:
 * Standing rule: Fabric mods must not drop server TPS (below 18-19). In the
 * original datapack, every button click triggered a chain of $data modify /
 * $function calls; uncontrolled rapid clicking (e.g. via an autoclicker
 * macro) could strain server tick time. This gate prevents the same player
 * from triggering GUI actions at too short an interval.
 *
 * HOW IT WORKS:
 * Keeps the last action timestamp per player (by UUID) in memory. When a
 * new request comes in, if not enough time has passed since the last
 * action, the request is rejected. ConcurrentHashMap is used because the
 * server may process packets across multiple threads; this data structure
 * is thread-safe.
 */
public final class RateLimitGate {

    // Minimum required interval between two GUI actions (milliseconds).
    // 150ms is below normal human reaction time, but tight enough to block
    // automated spam.
    private static final long MIN_INTERVAL_MS = 150L;

    // Player UUID -> timestamp of last action (System.currentTimeMillis() value).
    private static final ConcurrentHashMap<UUID, Long> lastActionTime = new ConcurrentHashMap<>();

    private RateLimitGate() {}

    /**
     * Checks whether a player is allowed to trigger a new GUI action.
     * If allowed, updates the timestamp.
     */
    public static boolean tryAcquire(UUID playerId) {
        long now = System.currentTimeMillis();
        Long previous = lastActionTime.get(playerId);

        if (previous != null && (now - previous) < MIN_INTERVAL_MS) {
            // Too rapid a consecutive request -> reject, don't update timestamp.
            return false;
        }

        lastActionTime.put(playerId, now);
        return true;
    }

    /**
     * Clears a player's record when they disconnect, to prevent a memory
     * leak. Should be called from the ServerPlayNetworkHandler disconnect event.
     */
    public static void clear(UUID playerId) {
        lastActionTime.remove(playerId);
    }
}
