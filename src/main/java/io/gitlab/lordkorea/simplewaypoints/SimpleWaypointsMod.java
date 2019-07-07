package io.gitlab.lordkorea.simplewaypoints;

import net.minecraftforge.fml.common.Mod;

/**
 * The main class of the mod.
 */
@Mod(modid = SimpleWaypointsMod.MODID, name = SimpleWaypointsMod.MODNAME, version = SimpleWaypointsMod.VERSION,
        certificateFingerprint = SimpleWaypointsMod.FINGERPRINT)
public final class SimpleWaypointsMod {

    /**
     * The mod ID of this mod.
     */
    protected static final String MODID = "simplewaypoints";

    /**
     * The name of this mod.
     */
    protected static final String MODNAME = "SimpleWaypoints";

    /**
     * The version of this mod.
     */
    protected static final String VERSION = "@VERSION@";

    /**
     * The signing fingerprint of this mod.
     */
    protected static final String FINGERPRINT = "@FINGERPRINT@";
}
