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
import java.util.List;

public class CommandDust extends CommandBase {
    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public String getCommandName() {
        return "dust";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/dust <player>";
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        return getListOfStringsMatchingLastWord(args, this.getListOfPlayerUsernames());
    }


    /**
     * Returns String array containing all player usernames in the server.
     */
    protected String[] getListOfPlayerUsernames() {
        List playerEntities = Minecraft.getMinecraft().theWorld.playerEntities;
        List<EntityPlayer> players = new ArrayList<>();
        for (Object playerEntity : playerEntities) {
            players.add(((EntityPlayer) playerEntity));
        }
        return players.stream().map(entityPlayer -> entityPlayer.getGameProfile().getName()).toArray(String[]::new);
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 1) {
            WorldClient theWorld = Minecraft.getMinecraft().theWorld;
            String seed = args[0];
            for (Object o : theWorld.playerEntities) {
                EntityPlayer playerEntity = (EntityPlayer) o;
                if (playerEntity.getDisplayName().equalsIgnoreCase(seed)) {
                    double time = 15 * 20 / ThanosMod.instance.speed + 40;
                    ThanosMod.instance.renderBlacklist.put(playerEntity.getUniqueID(), (int) time);
                    boolean dust = ThanosMod.instance.dust(playerEntity);
                    if (dust)
                        sendMessage("Dusted " + playerEntity.getGameProfile().getName(), sender);
                    else sendMessage("Unable to dust: " + playerEntity.getGameProfile().getName(), sender);
                    return;
                }
            }
            sendMessage("Could not find player with name " + seed, sender);
        } else sendMessage(getCommandUsage(sender), sender);
    }

    private void sendMessage(String chat, ICommandSender sender) {
        String prefix = EnumChatFormatting.RED + "[" + EnumChatFormatting.AQUA + "Thanos Mod" + EnumChatFormatting.RED + "]" + EnumChatFormatting.YELLOW + ": ";
        sender.addChatMessage(new ChatComponentText(prefix + chat));
    }
}
