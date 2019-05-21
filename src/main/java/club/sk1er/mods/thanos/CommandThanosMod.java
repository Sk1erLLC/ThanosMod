package club.sk1er.mods.thanos;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

public class CommandThanosMod extends CommandBase {
    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public String getCommandName() {
        return "thanosmod";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/thanosmod";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        ThanosMod.instance.openGui = true;
    }
}
