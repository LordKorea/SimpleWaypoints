package io.gitlab.lordkorea.simplewaypoints;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

/**
 * Used for rendering waypoints in the world.
 */
public class WaypointRenderer {

    /**
     * The distance at which a projected position is used and alpha is interpolated.
     */
    private static final float FADE_DISTANCE = 10.0f;

    /**
     * The waypoint marker.
     */
    private static final ResourceLocation WAYPOINT_MARKER = new ResourceLocation("simplewaypoints",
            "textures/marker.png");

    /**
     * Renders a waypoint.
     *
     * @param waypoint  The waypoint to render.
     * @param cameraPos The camera position to render from.
     */
    public void render(final Waypoint waypoint, final Vec3d cameraPos, final Vec2f cameraRotation) {
        final FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
        final Vec3d waypointPos = new Vec3d(waypoint.getX() + 0.5, waypoint.getY() + 0.5,
                waypoint.getZ() + 0.5);

        // Setup GL state.
        GlStateManager.pushMatrix();
        GlStateManager.disableCull();
        GlStateManager.disableDepth();
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.enableTexture2D();

        // Determine the distance and the waypoint text.
        final float distance = (float) cameraPos.subtract(waypointPos).lengthVector();
        final String waypointText = waypoint.getName() + String.format(" (%dm)", (int) distance);

        // Determine the eye-to-waypoint vector.
        final Vec3d eyeToWaypoint = waypointPos.subtract(cameraPos).normalize()
                .scale(Math.min(FADE_DISTANCE, distance));

        // Translate to waypoint position. The 1.5f is magic, I'm unsure where it is coming from.
        GlStateManager.translate(eyeToWaypoint.x, eyeToWaypoint.y + 1.5f, eyeToWaypoint.z);

        // Align to camera.
        GlStateManager.rotate(-cameraRotation.x, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(cameraRotation.y, 1.0f, 0.0f, 0.0f);
        if (Minecraft.getMinecraft().gameSettings.thirdPersonView == 2) {
            GlStateManager.rotate(180.0f, 0.0f, 1.0f, 0.0f);
        }

        // Draw the marker.
        //  First pass: Draw the marker transparent, but disregarding depth.
        //  Second pass: Draw the marker solid, but regarding depth.
        // That way, the marker is transparent while obstructed.
        final int waypointColor = waypoint.getColorRGB();
        GlStateManager.color(((waypointColor >> 16) & 0xFF) / 255.0f,
                ((waypointColor >> 8) & 0xFF) / 255.0f, (waypointColor & 0xFF) / 255.0f);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.CONSTANT_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_CONSTANT_ALPHA);

        // Setup first pass: 50% opacity.
        final float distanceFactor = MathHelper.clamp(distance - FADE_DISTANCE, 0.0f, 64.0f) / 64.0f;
        final float finalAlpha = 1.0f - distanceFactor * 0.5f;
        GL14.glBlendColor(1.0f, 1.0f, 1.0f, 0.5f);
        for (int i = 0; i < 2; i++) {
            // Need to bind texture here, as font rendering overrides this before second pass.
            Minecraft.getMinecraft().getTextureManager().bindTexture(WAYPOINT_MARKER);
            final Tessellator tessellator = Tessellator.getInstance();
            final BufferBuilder builder = tessellator.getBuffer();
            builder.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX);
            builder.pos(0.0 - 0.5, 0.0 - 0.5, 0.0).tex(0.0, 0.0).endVertex();
            builder.pos(1.0 - 0.5, 1.0 - 0.5, 0.0).tex(1.0, 1.0).endVertex();
            builder.pos(1.0 - 0.5, 0.0 - 0.5, 0.0).tex(1.0, 0.0).endVertex();
            builder.pos(0.0 - 0.5, 0.0 - 0.5, 0.0).tex(0.0, 0.0).endVertex();
            builder.pos(0.0 - 0.5, 1.0 - 0.5, 0.0).tex(0.0, 1.0).endVertex();
            builder.pos(1.0 - 0.5, 1.0 - 0.5, 0.0).tex(1.0, 1.0).endVertex();
            tessellator.draw();

            // Text render. Downscaled 16x, also needs to be rolled 180°.
            GlStateManager.scale(1.0f / 16.0f, 1.0f / 16.0f, 1.0f / 16.0f);
            GlStateManager.rotate(180.0f, 0.0f, 0.0f, 1.0f);
            fontRenderer.drawString(waypointText, -fontRenderer.getStringWidth(waypointText) / 2.0f,
                    -16.0f, waypointColor, false);
            GlStateManager.rotate(180.0f, 0.0f, 0.0f, 1.0f);
            GlStateManager.scale(16.0f, 16.0f, 16.0f);

            // Setup second pass: 100% opacity, depth.
            GL14.glBlendColor(1.0f, 1.0f, 1.0f, finalAlpha);
            GlStateManager.enableDepth();
        }

        // Undo GL state setup.
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        GlStateManager.enableCull();
        GlStateManager.popMatrix();
    }
}
