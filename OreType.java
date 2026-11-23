package com.oekclient;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import java.util.Set;

public enum OreType {
    DIAMOND("Diamond", 0xFF53E5EE, Set.of(Blocks.DIAMOND_ORE, Blocks.DEEPSLATE_DIAMOND_ORE)),
    EMERALD("Emerald", 0xFF55E591, Set.of(Blocks.EMERALD_ORE, Blocks.DEEPSLATE_EMERALD_ORE)),
    GOLD("Gold", 0xFFF4CA56, Set.of(Blocks.GOLD_ORE, Blocks.DEEPSLATE_GOLD_ORE, Blocks.NETHER_GOLD_ORE)),
    IRON("Iron", 0xFFDAAB81, Set.of(Blocks.IRON_ORE, Blocks.DEEPSLATE_IRON_ORE)),
    REDSTONE("Redstone", 0xFFFF5555, Set.of(Blocks.REDSTONE_ORE, Blocks.DEEPSLATE_REDSTONE_ORE)),
    LAPIS("Lapis", 0xFF668DFF, Set.of(Blocks.LAPIS_ORE, Blocks.DEEPSLATE_LAPIS_ORE)),
    COAL("Coal", 0xFFAEB9C6, Set.of(Blocks.COAL_ORE, Blocks.DEEPSLATE_COAL_ORE)),
    COPPER("Copper", 0xFFE59763, Set.of(Blocks.COPPER_ORE, Blocks.DEEPSLATE_COPPER_ORE)),
    ANCIENT_DEBRIS("Ancient Debris", 0xFFC47D51, Set.of(Blocks.ANCIENT_DEBRIS));

    public final String title;
    public final int color;
    private final Set<Block> blocks;
    OreType(String title, int color, Set<Block> blocks) {
        this.title = title; this.color = color; this.blocks = blocks;
    }
    public static OreType from(BlockState state) {
        Block block = state.getBlock();
        for (OreType type : values()) if (type.blocks.contains(block)) return type;
        return null;
    }
}
