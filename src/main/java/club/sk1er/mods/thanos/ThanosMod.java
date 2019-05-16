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
        partList.add(new BodyPart(8, 8, 8, 8, 0, 0, 0, 10, 10, 0)); //FrontOfFace

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
                        double scale = 0.0625F * .75F;

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
            return new Vec3(-newX, -newY, -newZ);
        }
    }

    class Pos {
        float x, y, z;

        public Pos(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public void rotate(float rotateAngleX, float rotateAngleY, float rotateAngleZ) {
            if (rotateAngleX != 0) {
                float tx = x;
                float ty = y;
                float tz = z;
                x = tx * MathHelper.cos(rotateAngleX) - ty * MathHelper.sin(rotateAngleX);
                y = tx * MathHelper.sin(rotateAngleX) + ty * MathHelper.cos(rotateAngleX);
                z = tz;
            }
            if (rotateAngleY != 0) {
                float tx = x;
                float ty = y;
                float tz = z;
                x = tx * MathHelper.cos(rotateAngleY) + tz * MathHelper.sin(rotateAngleY);
                y = ty;
                z = -tx * MathHelper.sin(rotateAngleY) + tz * MathHelper.cos(rotateAngleY);
            }
            if (rotateAngleZ != 0) {
                float tx = x;
                float ty = y;
                float tz = z;
                x = tx;
                y = ty * MathHelper.cos(rotateAngleX) - tz * MathHelper.sin(rotateAngleX);
                z = ty * MathHelper.sin(rotateAngleX) + tz * MathHelper.cos(rotateAngleX);
            }
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
