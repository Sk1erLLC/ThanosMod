package club.sk1er.mods.thanos;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.commons.io.FileUtils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Mod(modid = ThanosMod.MODID, version = ThanosMod.VERSION)
public class ThanosMod {
    public static final String MODID = "thanosmod";
    public static final String VERSION = "1.0";
    public static ThanosMod instance;
    public HashMap<UUID, Integer> renderBlacklist = new HashMap<>();
    public int DISTANCE = 16;
    public int MODE = 2;
    public boolean enabled = true;
    public double speed = 1.0D;
    public int RENDER_DISTANCE = 32;
    public boolean openGui;
    public boolean blending = true;
    private List<BodyPart> partList = new ArrayList<>();
    private List<DustBox> dustBoxes = new ArrayList<>();
    private HashMap<UUID, Long> cancel = new HashMap<>();
    private File configFile = null;

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
        partList.add(new BodyPart(8, 8, 8, 8, 0, 0, 0, 8, 8, 0)); //FrontOfFace
        partList.add(new BodyPart(24, 8, 8, 8, 0, 0, 7, 8, 8, 8)); //BackOfHead
        partList.add(new BodyPart(8, 0, 8, 6, 0, 0, 1, 8, 0, 7)); //Top of head
        partList.add(new BodyPart(16, 1, 8, 6, 0, 7, 1, 8, 7, 7)); //Neck
        partList.add(new BodyPart(16, 9, 6, 6, 0, 1, 1, 0, 7, 7)); //Left
        partList.add(new BodyPart(2, 9, 6, 6, 7, 1, 6, 7, 7, 0)); //Right


        //TORSO
        partList.add(new BodyPart(20, 20, 8, 12, 7, 8, 2, -1, 20, 2)); //Front
        partList.add(new BodyPart(32, 20, 8, 12, 0, 8, 5, 8, 20, 5)); //Back
        partList.add(new BodyPart(29, 21, 2, 10, 0, 9, 3, 0, 19, 5)); //Left
        partList.add(new BodyPart(17, 21, 2, 10, 7, 9, 4, 7, 19, 2)); //Right
        partList.add(new BodyPart(20, 16, 8, 2, 0, 8, 3, 8, 8, 5)); //Top
        partList.add(new BodyPart(28, 16, 8, 2, 0, 19, 3, 8, 19, 5)); //Bottom


        //RIGHT LEG
        partList.add(new BodyPart(4, 20, 4, 12, 7, 20, 2, 3, 32, 2)); //Front
        partList.add(new BodyPart(12, 20, 4, 12, 7, 20, 5, 3, 32, 5)); //Back
        partList.add(new BodyPart(8, 20, 2, 12, 4, 20, 3, 4, 32, 5)); //Left
        partList.add(new BodyPart(0, 20, 2, 12, 7, 20, 3, 7, 32, 5)); //Right
        partList.add(new BodyPart(8, 16, 2, 2, 5, 31, 3, 7, 31, 5)); //Bottom


        //LEFT LEG
        partList.add(new BodyPart(20, 52, 4, 12, 3, 20, 2, -1, 32, 2)); //Front
        partList.add(new BodyPart(28, 52, 4, 12, 3, 20, 5, -1, 32, 5)); //Back
        partList.add(new BodyPart(30, 52, 2, 12, 0, 20, 3, 0, 32, 5)); //Left
        partList.add(new BodyPart(22, 52, 2, 12, 3, 20, 3, 3, 32, 5)); //Right
        partList.add(new BodyPart(24, 50, 2, 2, 1, 31, 3, 3, 31, 5)); //Bottom


        //RIGHT ARM
        partList.add(new BodyPart(36 + 8, 52 - 12 - 16 - 4, 4, 12, 7 + 4, 8, 2, 3 + 4, 32 - 12, 2)); //Front
        partList.add(new BodyPart(44 + 8, 52 - 12 - 16 - 4, 4, 12, 7 + 4, 8, 5, 3 + 4, 32 - 12, 5)); //Back
        partList.add(new BodyPart(46 + 8, 52 - 12 - 16 - 4, 2, 12, 4 + 4, 8, 3, 4 + 4, 32 - 12, 5)); //Left
        partList.add(new BodyPart(38 + 8, 52 - 12 - 16 - 4, 2, 12, 7 + 4, 8, 3, 7 + 4, 32 - 12, 5)); //Right
        partList.add(new BodyPart(40 + 8, 50 - 12 - 16 - 4, 2, 2, 5 + 4, 19, 3, 7 + 4, 31 - 12, 5)); //Bottom
        partList.add(new BodyPart(36 + 8, 50 - 12 - 16 - 4, 2, 2, 5 + 4, 8, 3, 7 + 4, 8, 5)); //Top


        //LEFT ARM
        partList.add(new BodyPart(36, 52, 4, 12, 7 - 8, 8, 2, 3 - 8, 32 - 12, 2)); //Front
        partList.add(new BodyPart(44, 52, 4, 12, 7 - 8, 8, 5, 3 - 8, 32 - 12, 5)); //Back
        partList.add(new BodyPart(46, 52, 2, 12, 4 - 8, 8, 3, 4 - 8, 32 - 12, 5)); //Left
        partList.add(new BodyPart(38, 52, 2, 12, 7 - 8, 8, 3, 7 - 8, 32 - 12, 5)); //Right
        partList.add(new BodyPart(40, 50, 2, 2, 5 - 8, 19, 3, 7 - 8, 31 - 12, 5)); //Bottom
        partList.add(new BodyPart(36, 50, 2, 2, 5 - 8, 8, 3, 7 - 8, 8, 5)); //Top


    }

    public void remove(Entity entity) {
        if (entity instanceof EntityPlayer) {
            if (entity.getDistanceSqToEntity(Minecraft.getMinecraft().thePlayer) < DISTANCE * DISTANCE)
                dust(((EntityPlayer) entity));
        }
    }

    public void dust(EntityPlayer player) {
        if (!enabled)
            return;
        Long aLong = cancel.get(player.getUniqueID());
        if (aLong != null && System.currentTimeMillis() - aLong < 1000) {
            return;
        }
        cancel.put(player.getUniqueID(), System.currentTimeMillis());
        ResourceLocation defaultSkinLegacy = DefaultPlayerSkin.getDefaultSkinLegacy();
        InputStream inputstream = null;
        IResource iresource = null;
        try {
            SkinManager skinManager = Minecraft.getMinecraft().getSkinManager();
            Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> textures = skinManager.sessionService.getTextures(player.getGameProfile(), false);
            MinecraftProfileTexture minecraftProfileTexture = textures.get(MinecraftProfileTexture.Type.SKIN);
            File file2 = null;
            if (minecraftProfileTexture != null) {
                File file1 = new File(skinManager.skinCacheDir, minecraftProfileTexture.getHash().length() > 2 ? minecraftProfileTexture.getHash().substring(0, 2) : "xx");
                file2 = new File(file1, minecraftProfileTexture.getHash());
            }

            if (file2 == null || !file2.exists()) { //Default to steve
                iresource = Minecraft.getMinecraft().getResourceManager().getResource(defaultSkinLegacy);
                inputstream = iresource.getInputStream();
            } else {
                inputstream = new FileInputStream(file2);
            }
            BufferedImage bufferedimage = TextureUtil.readBufferedImage(inputstream);
            if (bufferedimage == null) return;
            if (bufferedimage.getWidth() != 64 || bufferedimage.getHeight() != 64)
                return;
            float seed = ThreadLocalRandom.current().nextFloat();
            for (BodyPart bodyPart : partList) {
                for (int j = 0; j < bodyPart.width; j++) {
                    for (int k = 0; k < bodyPart.height; k++) {
                        int rawColor = bufferedimage.getRGB(bodyPart.texX + j, bodyPart.texY + k);
                        int red = (rawColor >> 16) & 0xFF;
                        int green = (rawColor >> 8) & 0xFF;
                        int blue = (rawColor) & 0xFF;
                        double scale = 0.0625F;

                        Pos relCoords = bodyPart.getCoords(j, k);
                        //Adjust because our model system is centered around top left of head and we want to center around center of chest
                        relCoords.add(.22 * 1 / scale, 1.95 * 1 / scale, .22 * 1 / scale);
                        relCoords.rotate(0, (float) Math.toRadians(-player.rotationYaw), 0);

                        double xCoord = relCoords.x;
                        double yCoord = relCoords.y;
                        double zCoord = relCoords.z;


                        createPixel(
                                player.posX + xCoord * scale,
                                player.posY + yCoord * scale,
                                player.posZ + zCoord * scale,
                                red,
                                green,
                                blue,
                                255,
                                xCoord,
                                yCoord,
                                zCoord,
                                seed);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createPixel(double x, double y, double z, int red, int green, int blue, int alpha, double origPosX, double origPosY, double origPosZ, float seed) {
        dustBoxes.add(new DustBox(red / 255F, green / 255F, blue / 255F, alpha / 255F, x, y, z, origPosX, origPosY, origPosZ, seed));
    }

    @SubscribeEvent
    public void switchWorld(WorldEvent.Unload event) {
        dustBoxes.clear();
        renderBlacklist.clear();
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        dustBoxes.removeIf(DustBox::onUpdate);
        Set<UUID> remove = new HashSet<>();
        for (UUID uuid : renderBlacklist.keySet()) {
            Integer integer = renderBlacklist.get(uuid);
            if (integer == 1)
                remove.add(uuid);
            else
                renderBlacklist.put(uuid, integer - 1);
        }
        for (UUID uuid : remove) {
            renderBlacklist.remove(uuid);
        }
        if (openGui) {
            openGui = false;
            Minecraft.getMinecraft().displayGuiScreen(new ThanosModGui());
        }

    }

    @SubscribeEvent
    public void renderPlayer(RenderPlayerEvent.Pre event) {
        Integer integer = renderBlacklist.get(event.entityPlayer.getUniqueID());
        if (integer != null && integer > 0)
            event.setCanceled(true);
    }

    @SubscribeEvent
    public void renderWorld(RenderWorldLastEvent event) {
        GlStateManager.pushMatrix();
        Tessellator instance = Tessellator.getInstance();
        WorldRenderer worldRenderer = instance.getWorldRenderer();
        GlStateManager.disableCull();
        if (blending) {
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
        } else {
            GlStateManager.disableBlend();
            GlStateManager.disableAlpha();
        }
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.blendFunc(770, 771);
        for (DustBox dustBox : dustBoxes) {
            dustBox.render(event.partialTicks);
        }
        if (!blending) {
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
        }
        GlStateManager.enableCull();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        ClientCommandHandler.instance.registerCommand(new CommandThanosMod());
        ClientCommandHandler.instance.registerCommand(new CommandDust());
    }

    @EventHandler
    public void init(FMLPreInitializationEvent event) {
        configFile = event.getSuggestedConfigurationFile();
        loadConfig();
        Runtime.getRuntime().addShutdownHook(new Thread(this::saveConfig));
    }

    private void loadConfig() {
        try {
            JsonObject object = new JsonParser().parse(FileUtils.readFileToString(configFile)).getAsJsonObject();
            if (object.has("MODE"))
                MODE = object.get("MODE").getAsInt();
            if (object.has("spawn"))
                DISTANCE = object.get("spawn").getAsInt();
            if (object.has("render"))
                RENDER_DISTANCE = object.get("render").getAsInt();
            if (object.has("blending"))
                blending = object.get("blending").getAsBoolean();
            if (object.has("enabled"))
                enabled = object.get("enabled").getAsBoolean();
            if (object.has("speed"))
                speed = object.get("speed").getAsDouble();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveConfig() {
        try {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("MODE", MODE);
            jsonObject.addProperty("spawn", DISTANCE);
            jsonObject.addProperty("render", RENDER_DISTANCE);
            jsonObject.addProperty("blending", blending);
            jsonObject.addProperty("enabled", enabled);
            jsonObject.addProperty("speed", speed);
            FileUtils.writeStringToFile(this.configFile, jsonObject.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            //Negative because coords are are mult by -1 in Minecraft Model Rendering
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
