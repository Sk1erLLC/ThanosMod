package club.sk1er.mods.thanos;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

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

    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        return getListOfStringsMatchingLastWord(args, this.getListOfPlayerUsernames());
    }

    /**
     * Returns String array containing all player usernames in the server.
     */
    protected String[] getListOfPlayerUsernames() {
        return Minecraft.getMinecraft().theWorld.playerEntities.stream().map(EntityPlayer::getName).toArray(String[]::new);
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 1) {
            WorldClient theWorld = Minecraft.getMinecraft().theWorld;
            String seed = args[0];
            for (EntityPlayer playerEntity : theWorld.playerEntities) {
                if (playerEntity.getName().equalsIgnoreCase(seed)) {
                    double time = 15 * 20 / ThanosMod.instance.speed + 40;
                    ThanosMod.instance.renderBlacklist.put(playerEntity.getUniqueID(), (int) time);
                    boolean dust = ThanosMod.instance.dust(playerEntity);
                    if (dust)
                        sendMessage("Dusted " + playerEntity.getName(), sender);
                    else sendMessage("Unable to dust: " + playerEntity.getName(), sender);
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
