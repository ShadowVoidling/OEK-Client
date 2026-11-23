package com.oekclient;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Only scans chunks already sent to the client. No worldgen seed guesses or server data requests. */
public final class OreScanner {
    public record OreHit(BlockPos pos, OreType type, long distanceSq) {}
    private final List<OreHit> hits = new ArrayList<>();
    private int ticks;
    private int lastX = Integer.MIN_VALUE, lastY = Integer.MIN_VALUE, lastZ = Integer.MIN_VALUE;

    public List<OreHit> hits() { return List.copyOf(hits); }
    public void clear() { hits.clear(); ticks = 0; }
    public void tick(Minecraft minecraft, OekConfig config) {
        if (minecraft.level == null || minecraft.player == null || !config.active(Module.ORE_RADAR)) {
            clear(); return;
        }
        BlockPos center = minecraft.player.blockPosition();
        if (++ticks < 30 && Math.abs(center.getX() - lastX) < 4
                && Math.abs(center.getY() - lastY) < 4 && Math.abs(center.getZ() - lastZ) < 4) return;
        ticks = 0;
        lastX = center.getX(); lastY = center.getY(); lastZ = center.getZ();
        hits.clear();
        Level world = minecraft.level;
        int r = config.scanRadius;
        int vr = config.verticalRadius;
        BlockPos.MutableBlockPos test = new BlockPos.MutableBlockPos();
        for (int x = center.getX() - r; x <= center.getX() + r; x++) {
            for (int z = center.getZ() - r; z <= center.getZ() + r; z++) {
                // Avoid scanning chunks that were not sent to the client.
                if (!world.hasChunkAt(test.set(x, center.getY(), z))) continue;
                for (int y = Math.max(world.getMinY(), center.getY() - vr);
                     y <= Math.min(world.getMaxY() - 1, center.getY() + vr); y++) {
                    test.set(x, y, z);
                    OreType ore = OreType.from(world.getBlockState(test));
                    if (ore == null || !config.oreEnabled(ore)) continue;
                    long dx = x - center.getX(), dy = y - center.getY(), dz = z - center.getZ();
                    hits.add(new OreHit(test.immutable(), ore, dx*dx + dy*dy + dz*dz));
                }
            }
        }
        hits.sort(Comparator.comparingLong(OreHit::distanceSq));
        if (hits.size() > config.maxRadarDots) hits.subList(config.maxRadarDots, hits.size()).clear();
    }
}
