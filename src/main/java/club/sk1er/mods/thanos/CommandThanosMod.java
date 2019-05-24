package club.sk1er.mods.thanos;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class CommandThanosMod extends CommandBase {
    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    @Override
    public String getName() {
        return "thanosmod";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/thanosmod";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        ThanosMod.instance.openGui = true;

    }

}
