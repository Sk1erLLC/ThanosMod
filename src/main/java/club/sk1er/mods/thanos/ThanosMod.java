package club.sk1er.mods.thanos;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.IResource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
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
    }

    public static void onEntityRemoved(Entity entity) {
        instance.remove(entity);
    }

    private void generate() {
        partList.clear();
        partList.add(new BodyPart(8, 8, 8, 8, 0, 0, 0, 8, 8, 0)); //FrontOfFace
        partList.add(new BodyPart(24, 8, 8, 8, 0, 0, 8, 8, 8, 8)); //BackOfHead
        partList.add(new BodyPart(16, 0, 8, 7, 0, 7, 1, 8, 7, 8)); //Neck (shifted up and back 1)
        partList.add(new BodyPart(8, 0, 8, 8, 0, 0, 0, 8, 0, 8)); //Top of head
        partList.add(new BodyPart(16, 9, 6, 7, 7, 7, 1, 7, 1, 7, 0, 0, 90)); //Left

        //        partList.add(new BodyPart(16, 9, 6, 7, 0, 1, 7, 0, 7, 1)); //Left
        //1,7 -> 7,0
//        partList.add(new BodyPart(0, 8, 8, 8, 0, 0, 0, 10, 0, 10)); //Right


    }

    public void remove(Entity entity) {
        //TODO implement
    }

    private void createPixel(double x, double y, double z, int red, int green, int blue, int alpha) {
        dustBoxes.add(new DustBox(red / 255F, green / 255F, blue / 255F, alpha / 255F, x, y, z));
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        dustBoxes.removeIf(DustBox::onUpdate);
    }

    @SubscribeEvent
    public void onRender(RenderPlayerEvent.Post event) {
        if ((++i) % 50 != 0)
            return;
        Minecraft minecraft = Minecraft.getMinecraft();
        EntityPlayer thePlayer = event.entityPlayer;
        if (thePlayer == null) {
            return;
        }

        generate(); //TODO remove
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

                        Vec3 relCoords = bodyPart.getCoords(j, k);
                        createPixel(
                                thePlayer.posX + relCoords.xCoord * scale,
                                thePlayer.posY + relCoords.yCoord * scale,
                                thePlayer.posZ + relCoords.zCoord * scale,
                                red,
                                green,
                                blue,
                                255);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @SubscribeEvent
    public void renderWorld(RenderWorldLastEvent event) {
        Tessellator instance = Tessellator.getInstance();
        WorldRenderer worldRenderer = instance.getWorldRenderer();
        for (DustBox dustBox : dustBoxes) {
            dustBox.render(worldRenderer, event.partialTicks);
        }
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
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

        public BodyPart(int texX, int texY, int width, int height, double startX, double startY, double startZ, double endX, double endY, double endZ) {
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
        }

        public BodyPart(int texX, int texY, int width, int height, double startX, double startY, double startZ, double endX, double endY, double endZ, float rotx, float roty, float rotz) {
            this(texX, texY, width, height, startX, startY, startZ, endX, endY, endZ);
            this.rotX = rotx;
            this.rotY = roty;
            this.rotZ = rotz;
        }

        //Translate x and y texture coords into real 3d coords
        public Vec3 getCoords(double texOne, double texTwo) {
            double newX;
            double newY;
            double newZ;
            if (startX == endX) {
                newX = startX;
                newY = startY + (endY - startY) * (texOne / width);
                newZ = startZ + (endZ - startZ) * (texTwo / height);
            } else if (startY == endY) {
                newX = startX + (endX - startX) * (texOne / width);
                newY = startY;
                newZ = startZ + (endZ - startZ) * (texTwo / height);
            } else {
                newX = startX + (endX - startX) * (texOne / width);
                newY = startY + (endY - startY) * (texTwo / width);
                newZ = startZ;
            }
            Pos pos = new Pos(newX, newY, newZ);
            System.out.println(pos.toString());
            pos.rotate((float) Math.toRadians(rotX), (float) Math.toRadians(rotY), (float) Math.toRadians(rotZ));
            System.out.println(pos.toString());
            return new Vec3(-pos.x, -pos.y, -pos.z);
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
            System.out.println("ROT Z" + ": " + rotateAngleZ);
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
            this.x = -x;
            this.y = -y;
            this.z = -z;
        }
    }
}
