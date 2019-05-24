package club.sk1er.mods.thanos;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import java.util.List;

public class CommandDust extends CommandBase {
    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    @Override
    public String getName() {
        return "dust";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/dust <player>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 1) {
            WorldClient theWorld = Minecraft.getMinecraft().world;
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
        } else sendMessage(getUsage(sender), sender);
    }


    /**
     * Returns String array containing all player usernames in the server.
     */
    protected String[] getListOfPlayerUsernames() {
        return Minecraft.getMinecraft().world.playerEntities.stream().map(EntityPlayer::getName).toArray(String[]::new);
    }


    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        return getListOfStringsMatchingLastWord(args, this.getListOfPlayerUsernames());
    }

    private void sendMessage(String chat, ICommandSender sender) {
        String prefix = TextFormatting.RED + "[" + TextFormatting.AQUA + "Thanos Mod" + TextFormatting.RED + "]" + TextFormatting.YELLOW + ": ";
        sender.sendMessage(new TextComponentString(prefix + chat));
    }
}
