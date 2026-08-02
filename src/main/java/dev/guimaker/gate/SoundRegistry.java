package dev.guimaker.gate;

import java.util.Set;

/**
 * SoundRegistry — Fixed list of sounds GUI buttons are allowed to play.
 *
 * IN THE ORIGINAL DATAPACK:
 * data/gm/function/zprivate/execution/playsound/macro.mcfunction:
 *   $playsound $(sound)
 * Any string typed by a player was passed directly to the /playsound
 * command. This alone isn't RCE, but it's still the same "pass unvalidated
 * player input directly as a command parameter" pattern — inconsistent
 * with a sound security architecture.
 *
 * WHAT'S DIFFERENT HERE:
 * Only sound IDs listed in this Set are accepted. A developer wanting to
 * add a new sound must add it to the code and recompile the mod — it
 * cannot be extended at runtime via player input.
 */
public final class SoundRegistry {

    private static final Set<String> ALLOWED_SOUNDS = Set.of(
            "block.note_block.pling",
            "ui.button.click",
            "entity.experience_orb.pickup",
            "block.chest.open",
            "block.chest.close"
    );

    private SoundRegistry() {}

    public static boolean isRegistered(String soundId) {
        return soundId != null && ALLOWED_SOUNDS.contains(soundId);
    }

    public static java.util.Set<String> ids() {
        return java.util.Collections.unmodifiableSet(ALLOWED_SOUNDS);
    }
}
