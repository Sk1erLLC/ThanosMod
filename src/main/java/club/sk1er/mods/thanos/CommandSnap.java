package club.sk1er.mods.thanos;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

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
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        WorldClient theWorld = Minecraft.getMinecraft().theWorld;
        List<EntityPlayer> list = new ArrayList<>(theWorld.playerEntities);
        Collections.shuffle(list);
        for (int i = 0; i < list.size() / 2; i++) {
            EntityPlayer playerEntity = list.get(i);
            if (ThanosMod.instance.dust(playerEntity)) {
                double time = 15 * 20 / ThanosMod.instance.speed;
                ThanosMod.instance.renderBlacklist.put(playerEntity.getUniqueID(), (int) time);
            }
        }
        ThanosMod.instance.snap();
    }

    private void sendMessage(String chat, ICommandSender sender) {
        String prefix = EnumChatFormatting.RED + "[" + EnumChatFormatting.AQUA + "Thanos Mod" + EnumChatFormatting.RED + "]" + EnumChatFormatting.YELLOW + ": ";
        sender.addChatMessage(new ChatComponentText(prefix + chat));
    }
}
