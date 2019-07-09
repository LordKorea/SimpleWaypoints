package io.gitlab.lordkorea.simplewaypoints;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

/**
 * Used for rendering waypoints in the world.
 */
public class WaypointRenderer {

    /**
     * The waypoint marker.
     */
    private static final ResourceLocation WAYPOINT_MARKER = new ResourceLocation("simplewaypoints",
            "textures/marker.png");

    /**
     * Renders a waypoint.
     *
     * @param waypoint The waypoint to render.
     * @param cameraPos The camera position to render from.
     */
    public void render(final Waypoint waypoint, final Vec3d cameraPos, final Vec2f cameraRotation) {
        final FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;

        // Setup GL state.
        GlStateManager.pushMatrix();
        GlStateManager.disableCull();
        GlStateManager.disableDepth();
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.enableTexture2D();

        // Determine the distance and the waypoint text.
        final double distance = cameraPos.subtract(new Vec3d(waypoint.getX(), waypoint.getY(), waypoint.getZ()))
                .lengthVector();
        final String waypointText = waypoint.getName() + String.format(" (%dm)", (int) distance);

        // Determine the eye-to-waypoint vector.
        final Vec3d eyeToWaypoint = new Vec3d(waypoint.getX(), waypoint.getY(), waypoint.getZ()).subtract(cameraPos)
                .normalize().scale(Math.min(10.0, distance));

        // Translate to waypoint position.
        GlStateManager.translate(eyeToWaypoint.x, eyeToWaypoint.y, eyeToWaypoint.z);

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
        GL14.glBlendColor(1.0f, 1.0f, 1.0f, 0.5f);
        for (int i = 0; i < 2; i++) {
            // Need to bind texture here, as font rendering overrides this before second pass.
            Minecraft.getMinecraft().getTextureManager().bindTexture(WAYPOINT_MARKER);
            final Tessellator tessellator = Tessellator.getInstance();
            final BufferBuilder builder = tessellator.getBuffer();
            builder.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX);
            builder.pos(0.0, 0.0, 0.0).tex(0.0, 0.0).endVertex();
            builder.pos(1.0, 1.0, 0.0).tex(1.0, 1.0).endVertex();
            builder.pos(1.0, 0.0, 0.0).tex(1.0, 0.0).endVertex();
            builder.pos(0.0, 0.0, 0.0).tex(0.0, 0.0).endVertex();
            builder.pos(0.0, 1.0, 0.0).tex(0.0, 1.0).endVertex();
            builder.pos(1.0, 1.0, 0.0).tex(1.0, 1.0).endVertex();
            tessellator.draw();

            // Text render. Downscaled 16x, also needs to be rolled 180°.
            GlStateManager.scale(1.0f / 16.0f, 1.0f / 16.0f, 1.0f / 16.0f);
            GlStateManager.rotate(180.0f, 0.0f, 0.0f, 1.0f);
            fontRenderer.drawString(waypointText, -fontRenderer.getStringWidth(waypointText) / 2.0f - 8.0f,
                    -24.0f, waypointColor, false);
            GlStateManager.rotate(180.0f, 0.0f, 0.0f, 1.0f);
            GlStateManager.scale(16.0f, 16.0f, 16.0f);

            // Setup second pass: 100% opacity, depth.
            GL14.glBlendColor(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.enableDepth();
        }

        // Undo GL state setup.
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.enableCull();
        GlStateManager.popMatrix();
    }
}
