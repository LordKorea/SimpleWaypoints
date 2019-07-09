package io.gitlab.lordkorea.simplewaypoints;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import nge.lk.mods.commonlib.util.DebugUtil;

import java.io.File;

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

    /**
     * The file in which waypoint data is stored.
     */
    private File storageFile;

    /**
     * The waypoint manager.
     */
    private WaypointManager waypointManager;

    /**
     * The waypoint renderer.
     */
    private WaypointRenderer waypointRenderer;

    @Mod.EventHandler
    public void onPreInit(final FMLPreInitializationEvent event) {
        DebugUtil.initializeLogger(MODID);
        storageFile = new File(event.getModConfigurationDirectory(), "simplewaypoints.dat");
    }

    @Mod.EventHandler
    public void onInit(final FMLInitializationEvent event) {
        waypointManager = new WaypointManager(storageFile);
        waypointManager.addWaypoint(new Waypoint("Test", 0.0, 0.0, 0.0, 0x00AA00));
        waypointRenderer = new WaypointRenderer();

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onRenderWorldLast(final RenderWorldLastEvent event) {
        final Entity viewer = Minecraft.getMinecraft().getRenderViewEntity();
        if (viewer == null) {
            return;
        }
        // TODO: This is not the camera position in other perspectives. Do we have a better approximation?
        final Vec3d cameraPos = viewer.getPositionEyes(event.getPartialTicks());
        final Vec2f cameraRotation = new Vec2f(viewer.rotationYaw, viewer.rotationPitch);

        for (final Waypoint waypoint : waypointManager.getWaypoints()) {
            waypointRenderer.render(waypoint, cameraPos, cameraRotation);
        }
    }
}
