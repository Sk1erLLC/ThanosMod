package club.sk1er.mods.thanos;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import org.lwjgl.opengl.GL11;

public class DustBox {
    private float particleRed, particleBlue, particleGreen, particleAlpha;
    private double posX, posY, posZ;
    private double prevPosX, prevPosY, prevPosZ;
    private double initialPosX, initialPosY, initialPosZ;
    private double origPosX;
    private double origPosY;
    private double origPosZ;
    private int age;

    public DustBox(float particleRed, float particleGreen, float particleBlue, float particleAlpha, double posX, double posY, double posZ, double origPosX, double origPosY, double origPosZ) {
        this.particleRed = particleRed;
        this.particleBlue = particleBlue;
        this.particleGreen = particleGreen;
        this.particleAlpha = particleAlpha;
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.initialPosX = posX;
        this.initialPosY = posY;
        this.initialPosZ = posZ;
        this.origPosX = origPosX;
        this.origPosY = origPosY;
        this.origPosZ = origPosZ;
    }

    public boolean onUpdate() {
        age++;
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        double mag = 0;

        long time = System.currentTimeMillis() % 1000;
        double percent = (double) time / 1000D;
        double a = percent + (origPosY / 24D);
        mag = Math.max(0, Math.sin(Math.toRadians(a * 360))) / 25D;
        this.posX = initialPosX + origPosX * mag;
//        this.posY += origPosY * mag;
        this.posZ = initialPosZ + origPosZ * mag;

        return false; //5 seocnds
    }

    public void render(WorldRenderer worldRendererIn, float partialTicks) {
        float f4 = 0.1F * 3;
        RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
        double f5 = ((float) (this.prevPosX + (this.posX - this.prevPosX))) - renderManager.renderPosX;
        double f6 = ((float) (this.prevPosY + (this.posY - this.prevPosY))) - renderManager.renderPosY;
        double f7 = ((float) (this.prevPosZ + (this.posZ - this.prevPosZ))) - renderManager.renderPosZ;
        GlStateManager.pushMatrix();
        GlStateManager.disableCull();
        GlStateManager.color(particleRed, particleGreen, particleBlue, 1);
        GlStateManager.disableBlend();
//        System.out.println("particleRed = " + particleRed);
//        System.out.println("particleGreen = " + particleGreen);
//        System.out.println("particleBlue = " + particleBlue);
        GlStateManager.translate(f5, f6, f7);
        double scale = 0.0311F;
        GlStateManager.disableAlpha();
        GlStateManager.disableTexture2D();
        GlStateManager.scale(scale, scale, scale);
        GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
        GL11.glVertex3d(-1.f, 1.f, 1.f);
        GL11.glVertex3d(1.f, 1.f, 1.f);
        GL11.glVertex3d(-1.f, -1.f, 1.f);
        GL11.glVertex3d(1.f, -1.f, 1.f);
        GL11.glVertex3d(1.f, -1.f, -1.f);
        GL11.glVertex3d(1.f, 1.f, 1.f);
        GL11.glVertex3d(1.f, 1.f, -1.f);
        GL11.glVertex3d(-1.f, 1.f, 1.f);
        GL11.glVertex3d(-1.f, 1.f, -1.F);
        GL11.glVertex3d(-1.f, -1.f, 1.f);
        GL11.glVertex3d(-1.f, -1.f, -1.);
        GL11.glVertex3d(1.f, -1.f, -1.f);
        GL11.glVertex3d(-1.f, 1.f, -1.f);
        GL11.glVertex3d(1.f, 1.f, -1.f);
        GL11.glEnd();
        GlStateManager.popMatrix();
        GlStateManager.enableCull();
        GlStateManager.enableTexture2D();

    }
}
