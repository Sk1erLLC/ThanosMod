package club.sk1er.mods.thanos.commands;

import club.sk1er.mods.thanos.ThanosMod;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

import java.util.Collections;
import java.util.List;

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
    public void processCommand(ICommandSender sender, String[] args) {
        ThanosMod.instance.openGui = true;
    }

    @Override
    public List<String> getCommandAliases() {
        return Collections.singletonList("thanos");
    }
}
