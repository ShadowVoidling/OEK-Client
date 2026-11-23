package com.oekclient;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import java.util.List;
import java.util.Locale;

public final class OekHud {
    private static final int WHITE = 0xFFF3F7FF;
    private static final int MUTED = 0xFFB3C7D8;
    private static final int ACCENT = 0xFF55CBFF;
    private static final int PANEL = 0xBB101D2B;
    private OekHud() {}

    public static void render(GuiGraphics draw, DeltaTracker delta) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null || mc.options.hideGui || mc.screen instanceof OekScreen) return;
        OekConfig conf = OekClient.config;
        int x = 8, y = 8;
        int lines = 1;
        if (conf.active(Module.COORDINATES)) lines++;
        if (conf.active(Module.COMPASS)) lines++;
        if (conf.active(Module.FPS)) lines++;
        if (conf.active(Module.WAYPOINT) && conf.waypointX != null) lines++;
        draw.fill(x - 4, y - 4, x + 175, y + lines * 12 + 3, PANEL);
        draw.drawString(mc.font, "OEK CLIENT  v0.1", x, y, ACCENT, true); y += 12;
        BlockPos pos = mc.player.blockPosition();
        if (conf.active(Module.COORDINATES)) {
            draw.drawString(mc.font, "XYZ  " + pos.getX() + " / " + pos.getY() + " / " + pos.getZ(), x, y, WHITE, true);
            y += 12;
        }
        if (conf.active(Module.COMPASS)) {
            draw.drawString(mc.font, "Facing  " + mc.player.getDirection().getName().toUpperCase(Locale.ROOT), x, y, WHITE, true);
            y += 12;
        }
        if (conf.active(Module.FPS)) {
            draw.drawString(mc.font, "FPS  " + mc.getFps(), x, y, WHITE, true);
            y += 12;
        }
        if (conf.active(Module.WAYPOINT) && conf.waypointX != null) {
            boolean sameWorld = mc.level.dimension().identifier().toString().equals(conf.waypointDimension);
            if (sameWorld) {
                double d = Math.sqrt(Math.pow(pos.getX() - conf.waypointX, 2)
                        + Math.pow(pos.getZ() - conf.waypointZ, 2));
                draw.drawString(mc.font, "Waypoint " + Math.round(d) + "m", x, y, ACCENT, true);
            } else {
                draw.drawString(mc.font, "Waypoint: other dimension", x, y, MUTED, true);
            }
        }
        if (conf.active(Module.DURABILITY)) {
            ItemStack held = mc.player.getMainHandItem();
            if (held.isDamageableItem()) {
                int remaining = held.getMaxDamage() - held.getDamageValue();
                int percent = (int)(100.0 * remaining / Math.max(1, held.getMaxDamage()));
                if (percent <= 15) {
                    String message = "LOW TOOL DURABILITY: " + percent + "%";
                    int cx = draw.guiWidth() / 2 - mc.font.width(message) / 2;
                    draw.drawString(mc.font, message, cx, draw.guiHeight() - 60, 0xFFFF7777, true);
                }
            }
        }
        if (conf.active(Module.ORE_RADAR)) drawRadar(draw, mc, conf);
    }

    private static void drawRadar(GuiGraphics draw, Minecraft mc, OekConfig conf) {
        int size = 112;
        int left = draw.guiWidth() - size - 10;
        int top = 10;
        int cx = left + size / 2, cy = top + 64;
        draw.fill(left, top, left + size, top + 138, PANEL);
        draw.drawString(mc.font, "ORE RADAR", left + 7, top + 6, ACCENT, true);
        draw.drawString(mc.font, "N", cx - 3, top + 19, MUTED, false);
        draw.fill(left + 9, top + 27, left + size - 9, top + 119, 0x6023394B);
        draw.fill(cx, top + 28, cx + 1, top + 118, 0x5062829A);
        draw.fill(left + 10, cy, left + size - 10, cy + 1, 0x5062829A);
        List<OreScanner.OreHit> hits = OekClient.SCANNER.hits();
        BlockPos player = mc.player.blockPosition();
        for (OreScanner.OreHit hit : hits) {
            int dx = hit.pos().getX() - player.getX();
            int dz = hit.pos().getZ() - player.getZ();
            int px = cx + Math.round(dx * 42.0f / conf.scanRadius);
            int py = cy + Math.round(dz * 42.0f / conf.scanRadius);
            if (px < left + 12 || px > left + size - 13 || py < top + 30 || py > top + 116) continue;
            draw.fill(px - 1, py - 1, px + 2, py + 2, hit.type().color);
        }
        draw.fill(cx - 2, cy - 2, cx + 3, cy + 3, WHITE);
        String footer = hits.isEmpty() ? "No loaded ore found" : hits.size() + " ore blocks detected";
        draw.drawString(mc.font, footer, left + 7, top + 125, MUTED, false);
    }
}
