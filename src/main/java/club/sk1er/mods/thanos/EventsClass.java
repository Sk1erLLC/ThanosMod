package club.sk1er.mods.thanos;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.WorldEvent;
import org.lwjgl.opengl.GL11;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class EventsClass {

    @SubscribeEvent
    public void switchWorld(WorldEvent.Unload event) {
        ThanosMod.instance.dustBoxes.clear();
        ThanosMod.instance.renderBlacklist.clear();
        ThanosMod.instance.seed = ThreadLocalRandom.current().nextFloat();

    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
//        generate();
//        if (Keyboard.isKeyDown(Keyboard.KEY_P)) {
//            dustBoxes.clear();
//            dust(Minecraft.getMinecraft().thePlayer);
//        }

        if (ThanosMod.instance.snapping)
            ThanosMod.instance.snapTime += .05;
        if (ThanosMod.instance.snapTime > 2.0) {
            ThanosMod.instance.snapping = false;
        }
        ThanosMod.instance.dustBoxes.removeIf(DustBox::onUpdate);
        Set<UUID> remove = new HashSet<>();
        for (UUID uuid : ThanosMod.instance.renderBlacklist.keySet()) {
            Integer integer = ThanosMod.instance.renderBlacklist.get(uuid);
            if (integer == 1)
                remove.add(uuid);
            else
                ThanosMod.instance.renderBlacklist.put(uuid, integer - 1);
        }
        for (UUID uuid : remove) {
            ThanosMod.instance.renderBlacklist.remove(uuid);
        }
        if (ThanosMod.instance.openGui) {
            ThanosMod.instance.openGui = false;
            Minecraft.getMinecraft().displayGuiScreen(new ThanosModGui());
        }

    }

    @SubscribeEvent
    public void renderPlayer(RenderPlayerEvent.Pre event) {
        Integer integer = ThanosMod.instance.renderBlacklist.get(event.entityPlayer.getUniqueID());
        if (integer != null && integer > 0)
            event.setCanceled(true);
    }

    @SubscribeEvent
    public void renderWorld(RenderWorldLastEvent event) {
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_CULL_FACE);
        if (ThanosMod.instance.blending) {
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GL11.glEnable(GL11.GL_BLEND);
        } else {
            GL11.glDisable(GL11.GL_ALPHA_TEST);
            GL11.glDisable(GL11.GL_BLEND);
        }
        GL11.glDisable(GL11.GL_TEXTURE_2D);

        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glBlendFunc(770, 771);
        for (DustBox dustBox : ThanosMod.instance.dustBoxes) {
            dustBox.render(event.partialTicks);
        }
        if (!ThanosMod.instance.blending) {
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GL11.glEnable(GL11.GL_BLEND);
        }
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glPopMatrix();
    }

    @SubscribeEvent
    public void renderTick(TickEvent.RenderTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        if (ThanosMod.instance.snapping) {
            double a = .5 * ThanosMod.instance.snapTime;
            int mag = (int) Math.min(255, 255 * Math.abs(Math.pow(2, -a)));
            ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft(), Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
            Gui.drawRect(0, 0, scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight(), new Color(255, 255, 255, mag).getRGB());
        }
    }
}
