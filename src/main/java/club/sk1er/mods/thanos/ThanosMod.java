package club.sk1er.mods.thanos;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.IResource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Mod(modid = ThanosMod.MODID, version = ThanosMod.VERSION)
public class ThanosMod {
    public static final String MODID = "thanosmod";
    public static final String VERSION = "1.0";
    public static ThanosMod instance;
    private List<BodyPart> partList = new ArrayList<>();
    private int i = 0;
    private List<DustBox> dustBoxes = new ArrayList<>();

    public ThanosMod() {
        instance = this;
        generate();
    }

    public static void onEntityRemoved(Entity entity) {
        instance.remove(entity);
    }

    private void generate() {
        partList.clear();

        //These next two dozen lines of code represent hours of pain


        //HEAD
        partList.add(new BodyPart(8, 8, 8, 8, 0, 0, 0, 8, 8, 0, 0)); //FrontOfFace
        partList.add(new BodyPart(24, 8, 8, 8, 0, 0, 7, 8, 8, 8, 0)); //BackOfHead
        partList.add(new BodyPart(8, 0, 8, 6, 0, 0, 1, 8, 0, 7, 0)); //Top of head
        partList.add(new BodyPart(16, 1, 8, 6, 0, 7, 1, 8, 7, 7, 0)); //Neck
        partList.add(new BodyPart(16, 9, 6, 6, 0, 1, 1, 0, 7, 7, 0)); //Left
        partList.add(new BodyPart(2, 9, 6, 6, 7, 1, 6, 7, 7, 0, 0)); //Right


        //TORSO
        partList.add(new BodyPart(20, 20, 8, 12, 7, 8, 2, -1, 20, 2, 1)); //Front
        partList.add(new BodyPart(32, 20, 8, 12, 0, 8, 5, 8, 20, 5, 1)); //Back
        partList.add(new BodyPart(29, 21, 2, 10, 0, 9, 3, 0, 19, 5, 1)); //Left
        partList.add(new BodyPart(17, 21, 2, 10, 7, 9, 4, 7, 19, 2, 1)); //Right
        partList.add(new BodyPart(20, 16, 8, 2, 0, 8, 3, 8, 8, 5, 1)); //Top
        partList.add(new BodyPart(28, 16, 8, 2, 0, 19, 3, 8, 19, 5, 1)); //Bottom


        //RIGHT LEG
        partList.add(new BodyPart(4, 20, 4, 12, 7, 20, 2, 3, 32, 2, 5)); //Front
        partList.add(new BodyPart(12, 20, 4, 12, 7, 20, 5, 3, 32, 5, 5)); //Back
        partList.add(new BodyPart(8, 20, 2, 12, 4, 20, 3, 4, 32, 5, 5)); //Left
        partList.add(new BodyPart(0, 20, 2, 12, 7, 20, 3, 7, 32, 5, 5)); //Right
        partList.add(new BodyPart(8, 16, 2, 2, 5, 31, 3, 7, 31, 5, 5)); //Bottom
//


        //LEFT LEG
        partList.add(new BodyPart(20, 52, 4, 12, 3, 20, 2, -1, 32, 2, 4)); //Front
        partList.add(new BodyPart(28, 52, 4, 12, 3, 20, 5, -1, 32, 5, 4)); //Back
        partList.add(new BodyPart(30, 52, 2, 12, 0, 20, 3, 0, 32, 5, 4)); //Left
        partList.add(new BodyPart(22, 52, 2, 12, 3, 20, 3, 3, 32, 5, 4)); //Right
        partList.add(new BodyPart(24, 50, 2, 2, 1, 31, 3, 3, 31, 5, 4)); //Bottom


        //RIGHT ARM
        partList.add(new BodyPart(36 + 8, 52 - 12 - 16 - 4, 4, 12, 7 + 4, 8, 2, 3 + 4, 32 - 12, 2, 3)); //Front
        partList.add(new BodyPart(44 + 8, 52 - 12 - 16 - 4, 4, 12, 7 + 4, 8, 5, 3 + 4, 32 - 12, 5, 3)); //Back
        partList.add(new BodyPart(46 + 8, 52 - 12 - 16 - 4, 2, 12, 4 + 4, 8, 3, 4 + 4, 32 - 12, 5, 3)); //Left
        partList.add(new BodyPart(38 + 8, 52 - 12 - 16 - 4, 2, 12, 7 + 4, 8, 3, 7 + 4, 32 - 12, 5, 3)); //Right
        partList.add(new BodyPart(40 + 8, 50 - 12 - 16 - 4, 2, 2, 5 + 4, 19, 3, 7 + 4, 31 - 12, 5, 3)); //Bottom
        partList.add(new BodyPart(36 + 8, 50 - 12 - 16 - 4, 2, 2, 5 + 4, 8, 3, 7 + 4, 8, 5, 3)); //Top


        //LEFT ARM
        partList.add(new BodyPart(36, 52, 4, 12, 7 - 8, 8, 2, 3 - 8, 32 - 12, 2, 2)); //Front
        partList.add(new BodyPart(44, 52, 4, 12, 7 - 8, 8, 5, 3 - 8, 32 - 12, 5, 2)); //Back
        partList.add(new BodyPart(46, 52, 2, 12, 4 - 8, 8, 3, 4 - 8, 32 - 12, 5, 2)); //Left
        partList.add(new BodyPart(38, 52, 2, 12, 7 - 8, 8, 3, 7 - 8, 32 - 12, 5, 2)); //Right
        partList.add(new BodyPart(40, 50, 2, 2, 5 - 8, 19, 3, 7 - 8, 31 - 12, 5, 2)); //Bottom
        partList.add(new BodyPart(36, 50, 2, 2, 5 - 8, 8, 3, 7 - 8, 8, 5, 2)); //Top


    }

    public void remove(Entity entity) {
        //TODO implement
    }

    private void createPixel(double x, double y, double z, int red, int green, int blue, int alpha, double origPosX, double origPosY, double origPosZ) {
        dustBoxes.add(new DustBox(red / 255F, green / 255F, blue / 255F, alpha / 255F, x, y, z, origPosX, origPosY, origPosZ));
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        dustBoxes.removeIf(DustBox::onUpdate);
    }

    @SubscribeEvent
    public void onRender(RenderPlayerEvent.Post event) {
        if ((++i) % 300 != 0)
            return;
        dustBoxes.clear();
        Minecraft minecraft = Minecraft.getMinecraft();
        EntityPlayer thePlayer = event.entityPlayer;
        if (thePlayer == null) {
            return;
        }

        ResourceLocation defaultSkinLegacy = DefaultPlayerSkin.getDefaultSkinLegacy();
        InputStream inputstream = null;
        IResource iresource = null;
        try {
            iresource = Minecraft.getMinecraft().getResourceManager().getResource(defaultSkinLegacy);
            inputstream = iresource.getInputStream();
            BufferedImage bufferedimage = TextureUtil.readBufferedImage(inputstream);
            for (BodyPart bodyPart : partList) {
                for (int j = 0; j < bodyPart.width; j++) {
                    for (int k = 0; k < bodyPart.height; k++) {
                        int rawColor = bufferedimage.getRGB(bodyPart.texX + j, bodyPart.texY + k);
                        int red = (rawColor >> 16) & 0xFF;
                        int green = (rawColor >> 8) & 0xFF;
                        int blue = (rawColor) & 0xFF;
                        double scale = 0.0625F;

                        Pos relCoords = bodyPart.getCoords(j, k);
                        relCoords.add(.22 * 1 / scale, 1.95 * 1 / scale, .22 * 1 / scale);
                        relCoords.rotate(0, (float) Math.toRadians(-thePlayer.rotationYaw ), 0);

                        double xCoord = relCoords.x;
                        double yCoord = relCoords.y;
                        double zCoord = relCoords.z;


                        //Adjust because our model system is centered around top left of head and we want to center around center of chest
                        //Negative because coords are are mult by *-1 cause MC
                        createPixel(
                                thePlayer.posX + xCoord * scale,
                                thePlayer.posY + yCoord * scale,
                                thePlayer.posZ + zCoord * scale,
                                red,
                                green,
                                blue,
                                255,
                                xCoord,
                                yCoord,
                                zCoord);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @SubscribeEvent
    public void renderWorld(RenderWorldLastEvent event) {
        GlStateManager.pushMatrix();
        Tessellator instance = Tessellator.getInstance();
        WorldRenderer worldRenderer = instance.getWorldRenderer();
        for (DustBox dustBox : dustBoxes) {
            dustBox.render(worldRenderer, event.partialTicks);
        }
        GlStateManager.popMatrix();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }

    enum BodyPartLocation {
        HEAD,
        BODY,
        LEFT_ARM,
        RIGHT_ARM,
        LEFT_FOOT,
        RIGHT_FOOT;
    }

    class BodyPart {
        private int texX, texY, width, height; //Texture locations in MC skin

        private double startX;
        private double startY;
        private double startZ;
        private double endX;
        private double endY;
        private double endZ;
        private float rotX = 0;
        private float rotY = 0;
        private float rotZ = 0;
        private BodyPartLocation location;

        public BodyPart(int texX, int texY, int width, int height, double startX, double startY, double startZ, double endX, double endY, double endZ, int part) {
            this.texX = texX;
            this.texY = texY;
            this.width = width;
            this.height = height;
            this.startX = startX;
            this.startY = startY;
            this.startZ = startZ;
            this.endX = endX;
            this.endY = endY;
            this.endZ = endZ;
            location = BodyPartLocation.values()[part];
        }


        //Translate x and y texture coords into real 3d coords
        public Pos getCoords(double texOne, double texTwo) {
            double newX;
            double newY;
            double newZ;
            if (startX == endX) {
                newX = startX;
                newY = startY + (endY - startY) * (texTwo / height);
                newZ = startZ + (endZ - startZ) * (texOne / width);
            } else if (startY == endY) {
                newX = startX + (endX - startX) * (texOne / width);
                newY = startY;
                newZ = startZ + (endZ - startZ) * (texTwo / height);
            } else {
                newX = startX + (endX - startX) * (texOne / width);
                newY = startY + (endY - startY) * (texTwo / height);
                newZ = startZ;
            }
            Pos pos = new Pos(newX, newY, newZ);
            pos.rotate((float) Math.toRadians(rotX), (float) Math.toRadians(rotY), (float) Math.toRadians(rotZ));
            pos.multiply(-1, -1, -1);
            return pos;
        }
    }

    class Pos {
        double x, y, z;

        public Pos(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public void rotate(float rotateAngleX, float rotateAngleY, float rotateAngleZ) {
            if (rotateAngleX != 0) {
                double tx = x;
                double ty = y;
                double tz = z;
                x = tx * MathHelper.cos(rotateAngleX) - ty * MathHelper.sin(rotateAngleX);
                y = tx * MathHelper.sin(rotateAngleX) + ty * MathHelper.cos(rotateAngleX);
                z = tz;
            }
            if (rotateAngleY != 0) {
                double tx = x;
                double ty = y;
                double tz = z;
                x = tx * MathHelper.cos(rotateAngleY) + tz * MathHelper.sin(rotateAngleY);
                y = ty;
                z = -tx * MathHelper.sin(rotateAngleY) + tz * MathHelper.cos(rotateAngleY);
            }
            if (rotateAngleZ != 0) {
                double tx = x;
                double ty = y;
                double tz = z;
                x = tx;
                y = ty * MathHelper.cos(rotateAngleZ) - tz * MathHelper.sin(rotateAngleZ);
                z = ty * MathHelper.sin(rotateAngleZ) + tz * MathHelper.cos(rotateAngleZ);
            }
        }

        @Override
        public String toString() {
            return "Pos{" +
                    "x=" + x +
                    ", y=" + y +
                    ", z=" + z +
                    '}';
        }

        public void add(double xCoord, double yCoord, double zCoord) {
            x += xCoord;
            y += yCoord;
            z += zCoord;
        }

        public void invert() {
            multiply(-1, -1, -1);
        }

        public void multiply(double xMult, double yMult, double zMult) {
            this.x *= xMult;
            this.y *= yMult;
            this.z *= zMult;
        }
    }
}
