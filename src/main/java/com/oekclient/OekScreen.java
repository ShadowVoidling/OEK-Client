package com.oekclient;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** Vanilla widget-based menu. No external UI library required. */
public final class OekScreen extends Screen {
    private final Screen parent;
    private boolean orePage;

    public OekScreen(Screen parent) {
        super(Component.literal("OEK Client"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int middle = this.width / 2;
        int buttonWidth = Math.min(148, Math.max(95, (this.width - 32) / 2 - 5));
        int gap = 6;
        int left = middle - buttonWidth - gap/2;
        int right = middle + gap/2;
        int start = Math.max(42, this.height / 2 - 84);
        if (!orePage) {
            Module[] modules = Module.values();
            for (int i = 0; i < modules.length; i++) {
                Module module = modules[i];
                int x = i % 2 == 0 ? left : right;
                int y = start + (i / 2) * 25;
                button(x, y, buttonWidth, module.title + ": " + status(OekClient.config.active(module)), () -> {
                    OekClient.config.toggle(module);
                    refresh();
                });
            }
        } else {
            OreType[] types = OreType.values();
            for (int i = 0; i < types.length; i++) {
                OreType ore = types[i];
                int x = i % 2 == 0 ? left : right;
                int y = start + (i / 2) * 23;
                button(x, y, buttonWidth, ore.title + ": " + status(OekClient.config.oreEnabled(ore)), () -> {
                    OekClient.config.toggle(ore);
                    OekClient.SCANNER.clear();
                    refresh();
                });
            }
        }
        int bottom = Math.min(this.height - 26, start + 143);
        button(left, bottom, buttonWidth, orePage ? "< Modules" : "Ore Filters >", () -> { orePage = !orePage; refresh(); });
        button(right, bottom, buttonWidth, "Radius: " + OekClient.config.scanRadius + " (-/+)", () -> {
            OekClient.config.changeRadius(OekClient.config.scanRadius >= 32 ? -24 : 8);
            OekClient.SCANNER.clear(); refresh();
        });
        button(middle - 48, Math.min(this.height - 24, bottom + 24), 96, "Done", this::onClose);
    }

    private void button(int x, int y, int w, String text, Runnable action) {
        this.addRenderableWidget(Button.builder(Component.literal(text), btn -> action.run()).bounds(x, y, w, 20).build());
    }
    private String status(boolean enabled) { return enabled ? "ON" : "OFF"; }
    private void refresh() {
        if (minecraft != null) {
            this.clearWidgets();
            this.init();
        }
    }
    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float delta) {
        super.render(g, mouseX, mouseY, delta);
        int middle = width / 2;
        g.drawCenteredString(font, "OEK CLIENT", middle, 16, 0xFF55CBFF);
        g.drawCenteredString(font, orePage ? "Ore selection  |  v0.1" : "Client modules  |  v0.1", middle, 30, 0xFFE2EFFA);
        if (height >= 245) g.drawCenteredString(font, "Right Shift: menu   X: radar   Z: zoom   B: waypoint", middle, height - 8, 0xFFB3C7D8);
    }
    @Override
    public void onClose() {
        OekClient.config.save();
        if (minecraft != null) minecraft.setScreen(parent);
    }
}
