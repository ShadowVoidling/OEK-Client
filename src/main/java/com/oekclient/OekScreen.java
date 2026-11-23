package com.oekclient;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** OEK's purple/magenta modular control panel. Keybind configurable in Minecraft Controls. */
public final class OekScreen extends Screen {
    private static final int BG = 0xF011071C;
    private static final int PANEL = 0xE5271039;
    private static final int MAGENTA = 0xFFFF43CE;
    private static final int PURPLE = 0xFF9952ED;
    private static final int TEXT = 0xFFF5E6FF;
    private static final int MUTED = 0xFFCC9DDC;
    private final Screen parent;
    private boolean orePage;

    public OekScreen(Screen parent) {
        super(Component.literal("OEK Client"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int middle = width / 2;
        int buttonWidth = Math.min(150, Math.max(95, (width - 42) / 2 - 6));
        int left = middle - buttonWidth - 4;
        int right = middle + 4;
        int start = Math.max(88, height / 2 - 26);

        // These global options do not overwrite each module's saved ON/OFF choice.
        button(left, start - 50, buttonWidth, "MODULES: " + state(OekClient.config.masterEnabled), () -> {
            OekClient.config.masterEnabled = !OekClient.config.masterEnabled;
            OekClient.config.save();
            refresh();
        });
        button(right, start - 50, buttonWidth, "HUD: " + (OekClient.config.showHud ? "VISIBLE" : "HIDDEN"), () -> {
            OekClient.config.showHud = !OekClient.config.showHud;
            OekClient.config.save();
            refresh();
        });

        if (!orePage) {
            Module[] modules = Module.values();
            for (int i = 0; i < modules.length; i++) {
                Module module = modules[i];
                int x = (i % 2 == 0) ? left : right;
                int y = start + (i / 2) * 24;
                button(x, y, buttonWidth, module.title + ": " + state(OekClient.config.selected(module)), () -> {
                    OekClient.config.toggle(module);
                    refresh();
                });
            }
        } else {
            OreType[] types = OreType.values();
            for (int i = 0; i < types.length; i++) {
                OreType ore = types[i];
                int x = (i % 2 == 0) ? left : right;
                int y = start + (i / 2) * 22;
                button(x, y, buttonWidth, ore.title + ": " + state(OekClient.config.oreEnabled(ore)), () -> {
                    OekClient.config.toggle(ore);
                    OekClient.SCANNER.clear();
                    refresh();
                });
            }
        }

        int bottom = Math.min(height - 49, start + 123);
        button(left, bottom, buttonWidth, orePage ? "< Modules" : "Ore Filters >", () -> {
            orePage = !orePage;
            refresh();
        });
        button(right, bottom, buttonWidth, "Scan radius: " + OekClient.config.scanRadius, () -> {
            OekClient.config.changeRadius(OekClient.config.scanRadius >= 32 ? -24 : 8);
            OekClient.SCANNER.clear();
            refresh();
        });
        button(middle - 48, Math.min(height - 23, bottom + 23), 96, "Close", this::onClose);
    }

    private void button(int x, int y, int w, String text, Runnable action) {
        addRenderableWidget(Button.builder(Component.literal(text), b -> action.run()).bounds(x, y, w, 20).build());
    }

    private String state(boolean enabled) { return enabled ? "ON" : "OFF"; }

    private void refresh() {
        if (minecraft != null) {
            clearWidgets();
            init();
        }
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float delta) {
        g.fill(0, 0, width, height, BG);
        int middle = width / 2;
        int panelWidth = Math.min(width - 12, 340);
        int panelLeft = middle - panelWidth / 2;
        g.fill(panelLeft, 10, panelLeft + panelWidth, height - 8, PANEL);
        g.fill(panelLeft, 10, panelLeft + panelWidth, 13, MAGENTA);
        g.fill(panelLeft, height - 11, panelLeft + panelWidth, height - 8, PURPLE);
        g.drawCenteredString(font, "OEK CLIENT", middle, 18, MAGENTA);
        g.drawCenteredString(font, orePage ? "ORE FILTERS  /  v0.2" : "MODULE CONTROL  /  v0.2", middle, 30, TEXT);
        super.render(g, mouseX, mouseY, delta);
        if (height >= 275)
            g.drawCenteredString(font, "Keys: Right Shift menu  |  P master  |  H HUD", middle, height - 15, MUTED);
    }

    @Override
    public void onClose() {
        OekClient.config.save();
        if (minecraft != null) minecraft.setScreen(parent);
    }
}
