package club.sk1er.mods.thanos;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import org.lwjgl.opengl.GL11;

public class DustBox {
    private static int GL_LIST_ID = -1;
    private float particleRed, particleBlue, particleGreen, particleAlpha;
    private float initParticleRed, initParticleBlue, initParticleGreen, initParticleAlpha;
    private double posX, posY, posZ;
    private double prevPosX, prevPosY, prevPosZ;
    private double initialPosX, initialPosY, initialPosZ;
    private double origPosX;
    private double origPosY;
    private double origPosZ;
    private int age;
    private double randomValOne = Math.random();
    private double randomValTwo = Math.random();
    private double randomValueThree = Math.random();
    private float targetColorBrightness = 0;
    private float seed = 0;

    public DustBox(float particleRed, float particleGreen, float particleBlue, float particleAlpha, double posX, double posY, double posZ, double origPosX, double origPosY, double origPosZ, float seed) {
        this.particleRed = particleRed;
        this.particleBlue = particleBlue;
        this.particleGreen = particleGreen;
        this.particleAlpha = particleAlpha;

        this.initParticleRed = particleRed;
        this.initParticleBlue = particleBlue;
        this.initParticleGreen = particleGreen;
        this.initParticleAlpha = particleAlpha;

        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.initialPosX = posX;
        this.initialPosY = posY;
        this.initialPosZ = posZ;
        this.origPosX = origPosX;
        this.origPosY = origPosY;
        this.origPosZ = origPosZ;
        this.seed = seed;
        targetColorBrightness = (particleRed + particleBlue + particleGreen) / 5; //Gray and half brightness
    }

    public boolean onUpdate() {
        age++;
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        int wait = 20;
        if (age < wait)
            return false;

        float duration = 15;
        double state = 0;

        double percent = (age - wait) / (duration * 20);
        ThanosMod instance = ThanosMod.instance;
        int mode = instance.MODE;
        double a = percent * instance.speed - .2 + (origPosY / 48D);
        if (mode != 3) {
            a += origPosZ / 40D + origPosX / 40D;
        }

        state = -Math.cos(Math.toRadians(a * 180)) / 25D;
        if (state <= 0)
            return false;

        state *= 160;
        if (mode == 0) {
            if (state > 3) {
                double wiggleFactor = 50D / state;
                this.posX += (Math.random() * (randomValOne - .5)) / wiggleFactor;
                this.posY += (Math.random() * (randomValTwo - .5)) / wiggleFactor;
                this.posZ += (Math.random() * (randomValueThree - .5)) / wiggleFactor;
            }
        } else if (mode == 1) {
            double period = 2 * Math.pow(state, 3 / 2);
            this.posX = initialPosX + Math.cos(period) / 3 + Math.random() / 100;
            this.posY += .02 + Math.random() / 100;
            this.posZ = initialPosZ + Math.sin(period) / 3 + Math.random() / 100;
        } else if (mode == 2) {
            double xMult = 1;
            double zMult = 1;

            if (seed > .5 && seed < .75)
                zMult = -1;
            if (seed < .5 && seed > .25) {
                xMult = -1;
            }
            if (seed > .75) {
                zMult = -1;
                xMult = -1;
            }

            this.posX += (Math.random() / 200 * state * state) * (xMult);
            this.posY += Math.random() / 200 * state * state;
            this.posZ += Math.random() / 200 * state * state * (zMult);
        } else if (mode == 3) {
            double mag = 0;
            if (state < 1) {
                mag = -state;
            } else {
                mag = (Math.pow(state - 1, 2)) / 2 - 1;
            }
            double scale = 0.0311F;

            this.posX = initialPosX + origPosX * mag * scale;
            this.posZ = initialPosZ + origPosZ * mag * scale;
        }


        int thresholdOne = 0;
        double thresholdTwo = 5;
        if (state > thresholdOne) {
            if (state < 4) {
                //Initial = 150
                //Target = 100
                // 150 + (100 - 150) *
                particleRed = (float) (initParticleRed + ((targetColorBrightness - initParticleRed) * (state / 4D)));
                particleGreen = (float) (initParticleGreen + ((targetColorBrightness - initParticleGreen) * (state / 4D)));
                particleBlue = (float) (initParticleBlue + ((targetColorBrightness - initParticleBlue) * (state / 4D)));
            } else {
                particleRed = targetColorBrightness;
                particleBlue = targetColorBrightness;
                particleGreen = targetColorBrightness;
            }
            if (state < thresholdTwo)
                particleAlpha = (float) Math.max(((initParticleAlpha * (1F / state))), .70);
            else
                particleAlpha = (float) Math.min(.2, initParticleAlpha + (float) (0 - initParticleAlpha * (state - thresholdTwo)));

        } else {
            particleRed = initParticleRed;
            particleGreen = initParticleGreen;
            particleBlue = initParticleBlue;
            particleAlpha = initParticleAlpha;
        }
        if (!instance.blending && state > 4) {
            if (Math.random() > .95) { //5% chance
                return true;
            }
        }
        return state > 6;
    }

    public void render(WorldRenderer worldRendererIn, float partialTicks) {
        float f4 = 0.1F * 3;
        RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
        double f5 = ((float) (this.prevPosX + (this.posX - this.prevPosX) * partialTicks)) - renderManager.renderPosX;
        double f6 = ((float) (this.prevPosY + (this.posY - this.prevPosY) * partialTicks)) - renderManager.renderPosY;
        double f7 = ((float) (this.prevPosZ + (this.posZ - this.prevPosZ) * partialTicks)) - renderManager.renderPosZ;
        if (f5 * f5 + f6 * f6 + f7 * f7 > ThanosMod.instance.RENDER_DISTANCE)
            return;
        GlStateManager.pushMatrix();
        GlStateManager.color(particleRed, particleGreen, particleBlue, particleAlpha);
        GlStateManager.translate(f5, f6, f7);
        double scale = 0.0311F;
        GlStateManager.scale(scale, scale, scale);
        if (GL_LIST_ID != -1) {
            GlStateManager.callList(GL_LIST_ID);
            GlStateManager.popMatrix();
            return;
        }
        GL_LIST_ID = GLAllocation.generateDisplayLists(1);
        GL11.glNewList(GL_LIST_ID, GL11.GL_COMPILE_AND_EXECUTE);
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
        GL11.glEndList();
        GlStateManager.popMatrix();
    }

}
