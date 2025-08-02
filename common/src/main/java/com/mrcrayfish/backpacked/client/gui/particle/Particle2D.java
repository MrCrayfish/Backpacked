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
    private ResourceLocation texture;
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

    public Particle2D setLife(int life)
    {
        this.totalLife = life;
        this.life = life;
        return this;
    }

    public Particle2D setTexture(float u1, float v1, float u2, float v2, ResourceLocation texture)
    {
        this.u1 = u1;
        this.v1 = v1;
        this.u2 = u2;
        this.v2 = v2;
        this.texture = texture;
        return this;
    }

    public Particle2D setMotion(Vector2d motion)
    {
        this.motion = motion;
        return this;
    }

    public Particle2D setGravity(Vector2d gravity)
    {
        this.gravity = gravity;
        return this;
    }

    public Particle2D setFriction(double friction)
    {
        this.friction = friction;
        return this;
    }

    public Particle2D setRotation(double rotation)
    {
        this.prevRotation = rotation;
        this.rotation = rotation;
        return this;
    }

    public Particle2D setRotationSpeed(double rotationSpeed)
    {
        this.rotationSpeed = rotationSpeed;
        return this;
    }

    public Particle2D setStartScale(float scale)
    {
        return this.setStartScale(scale, 0);
    }

    public Particle2D setStartScale(float scale, int startAt)
    {
        this.startScale = scale;
        this.startScaleAtLife = startAt;
        this.scale = scale;
        this.prevScale = scale;
        return this;
    }

    public Particle2D setEndScale(float scale)
    {
        return this.setEndScale(scale, this.totalLife);
    }

    public Particle2D setEndScale(float scale, int endAt)
    {
        this.endScale = scale;
        this.endScaleAtLife = endAt;
        return this;
    }

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
        TextureAtlasSprite sprite = Minecraft.getInstance().getGuiSprites().getSprite(this.texture);
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
