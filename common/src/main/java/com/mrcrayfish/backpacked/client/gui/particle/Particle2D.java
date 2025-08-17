package com.mrcrayfish.backpacked.client.gui.particle;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.joml.Vector2d;

public class Particle2D
{
    private int life = 10;
    private int totalLife = 10;
    private double prevX;
    private double prevY;
    private double x;
    private double y;
    private final double width;
    private final double height;
    private float u1;
    private float v1;
    private float u2;
    private float v2;
    private ResourceLocation sprite;
    private Vector2d motion = new Vector2d();
    private Vector2d gravity = new Vector2d();
    private double friction;
    private double prevRotation;
    private double rotation;
    private double rotationSpeed;
    private float startScale = 1F;
    private float endScale = 1F;
    private float prevScale = this.startScale;
    private float scale = this.startScale;
    private int startScaleAtLife = 0;
    private int endScaleAtLife = this.totalLife;

    public Particle2D(double x, double y, double width, double height)
    {
        this.prevX = x;
        this.prevY = y;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Sets the life (the amount of ticks to exist for) of the particle. The value must be positive,
     * or it will simply be removed.
     *
     * @param life a positive integer representing the amount of ticks to exist for
     * @return this Particle2D instance
     */
    public Particle2D setLife(int life)
    {
        this.totalLife = life;
        this.life = life;
        return this;
    }

    /**
     * Sets the sprite to display when rendering the particle. This must be a texture placed in
     * "assets/&lt;mod_id&gt;/textures/gui/sprites", otherwise it will not be loaded correctly.
     * When defining the ResourceLocation, the base directory is the "sprites" directory. So the
     * defined path in the ResourceLocation should be relative to that directory.
     *
     * @param u1     the start u of the texture
     * @param v1     the start v of the texture
     * @param u2     the end u of the texture
     * @param v2     the end v of the texture
     * @param sprite the location to the texture
     * @return this Particle2D instance
     */
    public Particle2D setSprite(float u1, float v1, float u2, float v2, ResourceLocation sprite)
    {
        this.u1 = u1;
        this.v1 = v1;
        this.u2 = u2;
        this.v2 = v2;
        this.sprite = sprite;
        return this;
    }

    /**
     * Sets the initial motion vector of the particle. This motion will decay over time if the
     * friction of the particle is greater than zero. The motion is also affected by the set gravity,
     * by default, no gravity is set.
     *
     * @param motion the initial motion vector
     * @return this Particle2D instance
     */
    public Particle2D setMotion(Vector2d motion)
    {
        this.motion = motion;
        return this;
    }

    /**
     * Sets the constant gravity force for this particle. Gravity will be applied to the motion
     * vector every tick, but spread across one second. If gravity vector is (0, -8), then the
     * particle will move 8 pixels down over one second.
     *
     * @param gravity the gravity vector
     * @return this Particle2D instance
     */
    public Particle2D setGravity(Vector2d gravity)
    {
        this.gravity = gravity;
        return this;
    }

    /**
     * Sets the friction for the particle. This will be applied to the motion vector every tick,
     * allowing the motion to decay, or increase if friction is negative.
     *
     * @param friction the friction value for this particle
     * @return this Particle2D instance
     */
    public Particle2D setFriction(double friction)
    {
        this.friction = friction;
        return this;
    }

    /**
     * Sets the initial rotation of this particle. The particle is rotated from the center of the
     * particle when rendering. This value should be in degrees.
     *
     * @param rotation a rotation value in degrees.
     * @return this Particle2D instance
     */
    public Particle2D setRotation(double rotation)
    {
        this.prevRotation = rotation;
        this.rotation = rotation;
        return this;
    }

    /**
     * Sets the rotation speed of the rotational motion of this particle. If the rotation speed is
     * set to 30, the particle will rotation 30 degrees clockwise over the course of one second. The
     * rotation speed will decay over time based on the friction of the particle.
     *
     * @param rotationSpeed the rotation speed in degrees per second
     * @return this Particle2D instance
     */
    public Particle2D setRotationSpeed(double rotationSpeed)
    {
        this.rotationSpeed = rotationSpeed;
        return this;
    }

    /**
     * Sets the initial scale of the particle at the start of its life, starting at zero.
     *
     * @param scale the scale to display at the start
     * @return this Particle2D instance
     */
    public Particle2D setStartScale(float scale)
    {
        return this.setStartScale(scale, 0);
    }

    /**
     * Sets the initial scale of the particle, but allows a custom time before the scale starts
     * transitioning to the end scale. This essentially allows a delayed effect.
     *
     * @param scale   the initial scale of the particle
     * @param startAt the time in ticks before it can start transitioning to the end scale
     * @return this Particle2D instance
     */
    public Particle2D setStartScale(float scale, int startAt)
    {
        this.startScale = scale;
        this.startScaleAtLife = startAt;
        this.scale = scale;
        this.prevScale = scale;
        return this;
    }

    /**
     * Sets the final scale of the particle at the end of its life. The time at which this particle
     * will display at this scale is determined by the {@link #setLife(int)}.
     *
     * @param scale the scale to display at the end of the particle's life
     * @return this Particle2D instance
     */
    public Particle2D setEndScale(float scale)
    {
        return this.setEndScale(scale, this.totalLife);
    }

    /**
     * Sets the final scale of the particle, but allows a custom time so the final scale can occur
     * earlier rather than right at the end of its life. If the particle's life is 30 ticks, the
     * final scale can occur at 20 ticks.
     *
     * @param scale the scale to display at the end of the particle's life
     * @return this Particle2D instance
     */
    public Particle2D setEndScale(float scale, int endAt)
    {
        this.endScale = scale;
        this.endScaleAtLife = endAt;
        return this;
    }

    /**
     * Ticks this particle, updating life, motion, scale, etc.
     *
     * @return True if the particle is dead, and should be removed
     */
    public boolean tick()
    {
        if (this.life <= 0)
            return true;
        this.prevX = this.x;
        this.prevY = this.y;
        this.prevRotation = this.rotation;
        this.prevScale = this.scale;
        // Divide by 20 because it's easier to conceptualise per second instead of per tick
        this.motion.add(this.gravity);
        this.x += this.motion.x / 20.0;
        this.y += this.motion.y / 20.0;
        this.x += this.gravity.x / 20.0;
        this.y += this.gravity.y / 20.0;
        this.motion.sub(this.motion.x * this.friction, this.motion.y * this.friction);
        this.rotation += this.rotationSpeed / 20.0;
        this.rotationSpeed -= this.rotationSpeed * this.friction;
        int maxScaleLife = Math.max(this.endScaleAtLife - this.startScaleAtLife, 1);
        this.scale = Mth.clamp((this.totalLife - this.life) - this.startScaleAtLife, 0, maxScaleLife) / (float) maxScaleLife;
        this.scale = Mth.lerp(this.scale, this.startScale, this.endScale);
        this.life--;
        return false;
    }

    /**
     * Renders the particle onto the screen
     *
     * @param graphics the current GuiGraphics instance
     * @param partialTick the current partial tick time
     */
    public void render(GuiGraphics graphics, float partialTick)
    {
        if(this.life <= 0)
            return;
        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate(Mth.lerp(partialTick, this.prevX, this.x), Mth.lerp(partialTick, this.prevY, this.y), 300);
        pose.translate(this.width / 2, this.height / 2, 0);
        pose.mulPose(Axis.ZP.rotationDegrees((float) Mth.lerp(partialTick, this.prevRotation, this.rotation)));
        float scale = Mth.lerp(partialTick, this.prevScale, this.scale);
        pose.scale(scale, scale, scale);
        pose.translate(-this.width / 2, -this.height / 2, 0);
        TextureAtlasSprite sprite = Minecraft.getInstance().getGuiSprites().getSprite(this.sprite);
        RenderSystem.setShaderTexture(0, sprite.atlasLocation());
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        Matrix4f matrix = pose.last().pose();
        BufferBuilder builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        builder.addVertex(matrix, 0, 0, 0).setUv(sprite.getU(this.u1), sprite.getV(this.v1));
        builder.addVertex(matrix, 0, (float) this.height, 0).setUv(sprite.getU(this.u1), sprite.getV(this.v2));
        builder.addVertex(matrix, (float) this.width, (float) this.height, 0).setUv(sprite.getU(this.u2), sprite.getV(this.v2));
        builder.addVertex(matrix, (float) this.width, 0, 0).setUv(sprite.getU(this.u2), sprite.getV(this.v1));
        BufferUploader.drawWithShader(builder.buildOrThrow());
        pose.popPose();
    }
}
