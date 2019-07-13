package io.gitlab.lordkorea.simplewaypoints;

import io.gitlab.lordkorea.simplewaypoints.gui.WaypointManagerGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import nge.lk.mods.commonlib.util.DebugUtil;
import org.lwjgl.input.Keyboard;

import java.io.File;
import java.util.Random;

/**
 * The main class of the mod.
 */
@Mod(modid = SimpleWaypointsMod.MODID, name = SimpleWaypointsMod.MODNAME, version = SimpleWaypointsMod.VERSION,
        certificateFingerprint = SimpleWaypointsMod.FINGERPRINT)
public final class SimpleWaypointsMod {

    /**
     * A random number generator.
     */
    private static final Random RANDOM = new Random();

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

    /**
     * The key binding for the waypoint manager GUI.
     */
    private KeyBinding managerKey;

    /**
     * The key binding used for quickly creating a waypoint.
     */
    private KeyBinding quickWaypointKey;

    /**
     * The key binding used for cycling the active waypoint group.
     */
    private KeyBinding cycleGroupKey;

    @Mod.EventHandler
    public void onPreInit(final FMLPreInitializationEvent event) {
        DebugUtil.initializeLogger(MODID);
        storageFile = new File(event.getModConfigurationDirectory(), "simplewaypoints.dat");
    }

    @Mod.EventHandler
    public void onInit(final FMLInitializationEvent event) {
        managerKey = new KeyBinding("Waypoint Manager", KeyConflictContext.IN_GAME, Keyboard.KEY_F10,
                "Simple Waypoints");
        quickWaypointKey = new KeyBinding("Quick Waypoint", KeyConflictContext.IN_GAME, KeyModifier.CONTROL,
                Keyboard.KEY_RETURN, "Simple Waypoints");
        cycleGroupKey = new KeyBinding("Cycle Waypoint Group", KeyConflictContext.IN_GAME,
                Keyboard.KEY_RBRACKET, "Simple Waypoints");
        ClientRegistry.registerKeyBinding(managerKey);
        ClientRegistry.registerKeyBinding(quickWaypointKey);
        ClientRegistry.registerKeyBinding(cycleGroupKey);

        waypointManager = new WaypointManager(storageFile, quickWaypointKey);
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

        for (final Waypoint waypoint : waypointManager.getActiveWaypoints()) {
            waypointRenderer.render(waypoint, cameraPos, cameraRotation);
        }
    }

    @SubscribeEvent
    public void onKeyPress(final KeyInputEvent event) {
        final Minecraft mc = Minecraft.getMinecraft();

        if (managerKey.isPressed()) {
            mc.displayGuiScreen(new WaypointManagerGui(waypointManager));
        }

        if (quickWaypointKey.isPressed()) {
            final Waypoint quickPoint = new Waypoint("[+]", waypointManager.getActiveGroup(),
                    (int) Math.floor(mc.thePlayer.posX), (int) Math.floor(mc.thePlayer.posY + mc.thePlayer.eyeHeight),
                    (int) Math.floor(mc.thePlayer.posZ), RANDOM.nextInt(0xFFFFFF + 1));
            waypointManager.addWaypoint(quickPoint);
            waypointManager.save();
        }

        if (cycleGroupKey.isPressed()) {
            waypointManager.cycleActiveGroup();
            Minecraft.getMinecraft().thePlayer.addChatMessage(
                    new TextComponentString(String.format("Waypoint Group: %s", waypointManager.getActiveGroup())));
        }
    }
}
