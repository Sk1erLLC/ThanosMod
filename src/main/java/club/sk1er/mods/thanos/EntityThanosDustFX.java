package club.sk1er.mods.thanos;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

import java.awt.Color;

public class EntityThanosDustFX extends EntityFX {
    private Color color;

    public EntityThanosDustFX(World worldIn, double posXIn, double posYIn, double posZIn, Color color) {
        super(worldIn, posXIn, posYIn, posZIn);
        this.color = color;
        particleRed = color.getRed() / 255F;
        particleBlue = color.getBlue() / 255F;
        particleGreen = color.getGreen() / 255F;
        particleAlpha = color.getAlpha() / 255F;
        if (particleAlpha == 1.0)
            particleAlpha -= .00001;
        this.particleMaxAge = 20 * 3 + 500;
        this.particleScale = 5;

    }

    @Override
    public void onUpdate() {

        if (particleAge > 20 * 3) {
            particleAlpha -= .05 / 255F;
            particleRed -= .05 / 255F;
            particleGreen -= .05 / 255F;
            particleBlue -= .05 / 255F;
            if (particleRed < 0)
                particleRed = 0;
            if (particleGreen < 0)
                particleGreen = 0;
            if (particleBlue < 0)
                particleBlue = 0;

        }
        if (particleAlpha <= 0) {
            particleAlpha = 0;
            setDead();
        }

    }

    @Override
    public void renderParticle(WorldRenderer worldRendererIn, Entity entityIn, float partialTicks, float p_180434_4_, float p_180434_5_, float p_180434_6_, float p_180434_7_, float p_180434_8_) {
        float f = (float) this.particleTextureIndexX / 16.0F;
        float f1 = f + 0.0624375F;
        float f2 = (float) this.particleTextureIndexY / 16.0F;
        float f3 = f2 + 0.0624375F;
        float f4 = 0.1F * this.particleScale;

        if (this.particleIcon != null) {
            f = this.particleIcon.getMinU();
            f1 = this.particleIcon.getMaxU();
            f2 = this.particleIcon.getMinV();
            f3 = this.particleIcon.getMaxV();
        }

        float f5 = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) partialTicks - interpPosX);
        float f6 = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) partialTicks - interpPosY);
        float f7 = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) partialTicks - interpPosZ);
        int i = this.getBrightnessForRender(partialTicks);
        int j = i >> 16 & 65535;
        int k = i & 65535;
        worldRendererIn.pos((double) (f5 - p_180434_4_ * f4 - p_180434_7_ * f4), (double) (f6 - p_180434_5_ * f4), (double) (f7 - p_180434_6_ * f4 - p_180434_8_ * f4)).tex((double) f1, (double) f3).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
        worldRendererIn.pos((double) (f5 - p_180434_4_ * f4 + p_180434_7_ * f4), (double) (f6 + p_180434_5_ * f4), (double) (f7 - p_180434_6_ * f4 + p_180434_8_ * f4)).tex((double) f1, (double) f2).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
        worldRendererIn.pos((double) (f5 + p_180434_4_ * f4 + p_180434_7_ * f4), (double) (f6 + p_180434_5_ * f4), (double) (f7 + p_180434_6_ * f4 + p_180434_8_ * f4)).tex((double) f, (double) f2).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
        worldRendererIn.pos((double) (f5 + p_180434_4_ * f4 - p_180434_7_ * f4), (double) (f6 - p_180434_5_ * f4), (double) (f7 + p_180434_6_ * f4 - p_180434_8_ * f4)).tex((double) f, (double) f3).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
    }
}
