package club.sk1er.mods.thanos;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.PositionTextureVertex;
import net.minecraft.client.model.TexturedQuad;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.awt.Color;
import java.util.List;

@Mod(modid = ThanosMod.MODID, version = ThanosMod.VERSION)
public class ThanosMod {
    public static final String MODID = "thanosmod";
    public static final String VERSION = "1.0";


    public static ThanosMod instance;
    private int i = 0;

    public ThanosMod() {
        instance = this;
    }

    public static void onEntityRemoved(Entity entity) {
        instance.remove(entity);
    }

    public void remove(Entity entity) {
        World entityWorld = entity.getEntityWorld();

    }

    private void spawnDustAtWithColor(World world, double x, double y, double z, int red, int green, int blue, int alpha) {
        EffectRenderer effectRenderer = Minecraft.getMinecraft().effectRenderer;
        effectRenderer.addEffect(new EntityThanosDustFX(world, x, y, z, new Color(red, green, blue, alpha)));
    }

    @SubscribeEvent
    public void onTick(TickEvent event) {

    }

    @SubscribeEvent
    public void onRender(RenderPlayerEvent.Post event) {
//        if (++i % 60 *5 != 0)
//            return;

        Minecraft minecraft = Minecraft.getMinecraft();
        EntityPlayerSP thePlayer = (EntityPlayerSP) event.entityPlayer;
        if (thePlayer != null) {
            Minecraft.getMinecraft().getTextureManager().bindTexture(thePlayer.getLocationSkin());
            Render<Entity> entityRenderObject = minecraft.getRenderManager().getEntityRenderObject(thePlayer);
            if (entityRenderObject instanceof RendererLivingEntity) {
                ModelBase mainModel = ((RendererLivingEntity) entityRenderObject).getMainModel();
                for (ModelRenderer modelRenderer : mainModel.boxList) {
                    List<ModelBox> cubeList = modelRenderer.cubeList;
                    for (ModelBox modelBox : cubeList) {
                        for (TexturedQuad texturedQuad : modelBox.quadList) {

                            float scale = 0.0625F;

                            PositionTextureVertex[] vertexPositions = texturedQuad.vertexPositions;

                            PositionTextureVertex startVertex = vertexPositions[2];
                            PositionTextureVertex endVertex = vertexPositions[0];
                            Vec3 start = startVertex.vector3D;
                            Vec3 end = endVertex.vector3D;


                            Vec3 planeVectorOne = end.subtract(start).normalize();
                            Vec3 planeVectorTwo = vertexPositions[1].vector3D.subtract(vertexPositions[3].vector3D).normalize();


                            for (float j = startVertex.texturePositionX; j < endVertex.texturePositionX; j += 1 / 128F) {
                                for (float k = startVertex.texturePositionY; k < endVertex.texturePositionY; k += 1 / 128F) {
                                    double baseX = thePlayer.posX;
                                    double baseY = thePlayer.posY+thePlayer.getEyeHeight();
                                    double baseZ = thePlayer.posZ;
                                    float workingPosX = modelRenderer.offsetX + modelRenderer.rotationPointX * scale;
                                    float workingPosY = modelRenderer.offsetY + modelRenderer.rotationPointY * scale;
                                    float workingPosZ = modelRenderer.offsetZ + modelRenderer.rotationPointZ * scale;

                                    Pos pos = new Pos(workingPosX, workingPosY, workingPosZ);
                                    float xInter = j / endVertex.texturePositionX;
                                    float yInter = k / endVertex.texturePositionY;
                                    pos.rotate(modelRenderer.rotateAngleX, modelRenderer.rotateAngleY, modelRenderer.rotateAngleZ);

                                    pos.add(planeVectorOne.xCoord * xInter + planeVectorTwo.xCoord * yInter,
                                            planeVectorOne.yCoord * xInter + planeVectorTwo.yCoord * yInter,
                                            planeVectorOne.zCoord * xInter + planeVectorTwo.zCoord * yInter);

                                    pos.invert();
                                    spawnDustAtWithColor(thePlayer.worldObj, baseX + pos.x, baseY + pos.y, baseZ + pos.z, 255, 255, 255, 255);
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    @EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
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
