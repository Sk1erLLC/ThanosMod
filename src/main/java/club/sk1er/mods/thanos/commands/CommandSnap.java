package club.sk1er.mods.thanos.commands;

import club.sk1er.mods.thanos.ThanosMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommandSnap extends CommandBase {
    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public String getCommandName() {
        return "snap";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/snap";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        WorldClient theWorld = Minecraft.getMinecraft().theWorld;
        List<EntityPlayer> list = new ArrayList<>(theWorld.playerEntities);
        Collections.shuffle(list);
        list.remove(Minecraft.getMinecraft().thePlayer);
        list.removeIf(Entity::isInvisible);
        for (int i = 0; i < list.size() / 2; i++) {
            EntityPlayer playerEntity = list.get(i);
            if (ThanosMod.instance.dust(playerEntity)) {
                double time = 15 * 20 / ThanosMod.instance.speed + 40;
                ThanosMod.instance.renderBlacklist.put(playerEntity.getUniqueID(), (int) time);
            }
        }
        ThanosMod.instance.snap();
    }
}
