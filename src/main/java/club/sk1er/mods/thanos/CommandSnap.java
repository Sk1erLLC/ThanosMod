package club.sk1er.mods.thanos;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommandSnap extends CommandBase {

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    @Override
    public String getName() {
        return "snap";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/snap";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        WorldClient theWorld = Minecraft.getMinecraft().world;
        List<EntityPlayer> list = new ArrayList<>(theWorld.playerEntities);
        Collections.shuffle(list);
        list.remove(Minecraft.getMinecraft().player);
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
