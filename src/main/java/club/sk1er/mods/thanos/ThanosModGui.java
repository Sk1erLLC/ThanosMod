package club.sk1er.mods.thanos;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.client.config.GuiSlider;

import java.util.HashMap;
import java.util.function.Consumer;

import static net.minecraft.util.EnumChatFormatting.AQUA;
import static net.minecraft.util.EnumChatFormatting.RED;
import static net.minecraft.util.EnumChatFormatting.YELLOW;

public class ThanosModGui extends GuiScreen {

    private HashMap<GuiButton, Consumer<GuiButton>> clicks = new HashMap<>();
    private HashMap<GuiButton, Consumer<GuiButton>> update = new HashMap<>();
    private int id;

    @Override
    public void initGui() {
        super.initGui();
        ThanosMod instance = ThanosMod.instance;

        reg(new GuiButton(++id, width / 2 - 100, 3, "TOGGLE"), guiButton -> {
            StringBuilder append = new StringBuilder().append(EnumChatFormatting.YELLOW).append("Mod Status: ");
            if (instance.enabled)
                append.append(EnumChatFormatting.GREEN).append("Enabled");
            else append.append(RED).append("Disabled");
            guiButton.displayString = append.toString();
        }, guiButton -> {
            instance.enabled = !instance.enabled;
        });

        reg(new GuiButton(++id, width / 2 - 100, 25, "MODE"), guiButton -> {
            StringBuilder append = new StringBuilder().append(EnumChatFormatting.YELLOW).append("Mode: ");
            int mode = instance.MODE;
            append.append(AQUA);
            if (mode == 0) {
                append.append("Static");
            } else if (mode == 1) {
                append.append("Twirl");
            } else if (mode == 2) {
                append.append("Scatter");
            }
            guiButton.displayString = append.toString();
        }, guiButton -> {
            instance.MODE++;
            if (instance.MODE > 2)
                instance.MODE = 0;
        });
        regSlider(new GuiSlider(++id, width / 2 - 100, 47, 200, 20, YELLOW + "Dust Render Distance: " + AQUA, "", 1, 64, instance.RENDER_DISTANCE, false, true, slider -> {
            instance.RENDER_DISTANCE = slider.getValueInt();
        }));
        regSlider(new GuiSlider(++id, width / 2 - 100, 69, 200, 20, YELLOW + "Animation Start Distance: " + AQUA, "", 1, 64, instance.DISTANCE, false, true, slider -> {
            instance.DISTANCE = slider.getValueInt();
        }));
        reg(new GuiButton(++id, width / 2 - 100, 69 + 22, "BLENDING"), guiButton -> {
            StringBuilder append = new StringBuilder().append(EnumChatFormatting.YELLOW).append("Blending: ");
            append.append(AQUA);
            if (instance.blending) {
                append.append("Higher Quality");
            } else append.append("Higher Performance");
            guiButton.displayString = append.toString();
        }, guiButton -> {
            instance.blending = !instance.blending;
        });
        regSlider(new GuiSlider(++id, width / 2 - 100, 69 + 44, 200, 20, YELLOW + "Speed: " + AQUA, "", .5D, 2D, instance.speed, true, true, slider -> {
            instance.speed = slider.getValue();
        }));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        Consumer<GuiButton> guiButtonConsumer = clicks.get(button);
        if (guiButtonConsumer != null) {
            guiButtonConsumer.accept(button);
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        for (GuiButton guiButton : update.keySet()) {
            update.get(guiButton).accept(guiButton);
        }
    }

    private void regSlider(GuiSlider slider) {
        reg(slider, null, null);
    }

    private void reg(GuiButton button, Consumer<GuiButton> onUpdate, Consumer<GuiButton> onClick) {
        this.buttonList.removeIf(button1 -> button1.id == button.id);
        this.buttonList.add(button);
        this.clicks.keySet().removeIf(button1 -> button1.id == button.id);
        if (onClick != null) {
            this.clicks.put(button, onClick);
        }
        this.update.keySet().removeIf(button1 -> button1.id == button.id);
        if (onUpdate != null) {
            this.update.put(button, onUpdate);
        }
    }
}
