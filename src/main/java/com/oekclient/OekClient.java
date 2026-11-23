package com.oekclient;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public final class OekClient implements ClientModInitializer {
    public static final String MOD_ID = "oekclient";
    public static OekConfig config;
    public static final OreScanner SCANNER = new OreScanner();
    private static KeyMapping menuKey, oreKey, waypointKey, zoomKey, masterKey, hudKey;
    private static int oldFov = -1;
    private static double oldGamma = Double.NaN;
    private static boolean boostApplied;

    @Override
    public void onInitializeClient() {
        config = OekConfig.load();
        KeyMapping.Category category = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(MOD_ID, "controls"));
        menuKey = key("key.oekclient.menu", GLFW.GLFW_KEY_RIGHT_SHIFT, category);
        oreKey = key("key.oekclient.ore", GLFW.GLFW_KEY_X, category);
        waypointKey = key("key.oekclient.waypoint", GLFW.GLFW_KEY_B, category);
        zoomKey = key("key.oekclient.zoom", GLFW.GLFW_KEY_Z, category);
        masterKey = key("key.oekclient.master", GLFW.GLFW_KEY_P, category);
        hudKey = key("key.oekclient.hud", GLFW.GLFW_KEY_H, category);
        ClientTickEvents.END_CLIENT_TICK.register(this::tick);
        HudElementRegistry.attachElementBefore(VanillaHudElements.CHAT,
                Identifier.fromNamespaceAndPath(MOD_ID, "hud"), OekHud::render);
    }

    private static KeyMapping key(String id, int code, KeyMapping.Category category) {
        return KeyBindingHelper.registerKeyBinding(new KeyMapping(id, InputConstants.Type.KEYSYM, code, category));
    }

    private void tick(Minecraft client) {
        while (menuKey.consumeClick()) {
            if (client.screen instanceof OekScreen) client.setScreen(null);
            else if (client.screen == null) client.setScreen(new OekScreen(null));
        }
        while (masterKey.consumeClick()) {
            config.masterEnabled = !config.masterEnabled;
            config.save();
            notify(client, "Modules: " + (config.masterEnabled ? "ON" : "OFF"));
        }
        while (hudKey.consumeClick()) {
            config.showHud = !config.showHud;
            config.save();
            notify(client, "OEK HUD: " + (config.showHud ? "VISIBLE" : "HIDDEN"));
        }
        while (oreKey.consumeClick()) {
            config.toggle(Module.ORE_RADAR);
            notify(client, "Ore Radar selected: " + (config.selected(Module.ORE_RADAR) ? "ON" : "OFF"));
        }
        while (waypointKey.consumeClick()) {
            if (client.player == null || client.level == null) continue;
            if (config.waypointX != null && config.waypointDimension != null
                    && config.waypointDimension.equals(client.level.dimension().identifier().toString())) {
                config.waypointX = config.waypointY = config.waypointZ = null;
                config.waypointDimension = null;
                notify(client, "Waypoint cleared");
            } else {
                BlockPos pos = client.player.blockPosition();
                config.waypointX = pos.getX(); config.waypointY = pos.getY(); config.waypointZ = pos.getZ();
                config.waypointDimension = client.level.dimension().identifier().toString();
                notify(client, "Waypoint saved at " + pos.toShortString());
            }
            config.save();
        }
        boolean zooming = client.player != null && client.screen == null
                && config.active(Module.ZOOM) && zoomKey.isDown();
        if (zooming) {
            if (oldFov < 0) oldFov = client.options.fov().get();
            if (client.options.fov().get() != 30) client.options.fov().set(30);
        } else if (oldFov >= 0) {
            client.options.fov().set(oldFov);
            oldFov = -1;
        }
        boolean brightness = client.player != null && config.active(Module.BRIGHTNESS);
        if (brightness && !boostApplied) {
            oldGamma = client.options.gamma().get();
            client.options.gamma().set(1.0);
            boostApplied = true;
        } else if (!brightness && boostApplied) {
            client.options.gamma().set(oldGamma);
            boostApplied = false;
            oldGamma = Double.NaN;
        }
        SCANNER.tick(client, config);
    }

    private static void notify(Minecraft client, String message) {
        if (client.player != null) client.player.displayClientMessage(Component.literal("[OEK] " + message), true);
    }
}
