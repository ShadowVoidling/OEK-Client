package com.oekclient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Set;

/** Human-editable settings stored in .minecraft/config/oekclient.json. */
public final class OekConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("oekclient.json");

    public Set<Module> enabled = EnumSet.noneOf(Module.class);
    public Set<OreType> selectedOres = EnumSet.allOf(OreType.class);
    public int scanRadius = 16;
    public int verticalRadius = 16;
    public int maxRadarDots = 100;
    public Integer waypointX;
    public Integer waypointY;
    public Integer waypointZ;
    public String waypointDimension;

    public OekConfig() {
        for (Module module : Module.values()) if (module.initialState) enabled.add(module);
    }

    public boolean active(Module module) { return enabled.contains(module); }
    public void toggle(Module module) {
        if (!enabled.remove(module)) enabled.add(module);
        save();
    }
    public void toggle(OreType ore) {
        if (!selectedOres.remove(ore)) selectedOres.add(ore);
        save();
    }
    public boolean oreEnabled(OreType ore) { return selectedOres.contains(ore); }
    public void changeRadius(int delta) {
        scanRadius = Math.max(8, Math.min(32, scanRadius + delta));
        verticalRadius = scanRadius;
        save();
    }
    public void save() {
        try {
            Files.createDirectories(FILE.getParent());
            try (Writer writer = Files.newBufferedWriter(FILE)) { GSON.toJson(this, writer); }
        } catch (IOException e) {
            System.err.println("[OEK Client] Failed to save config: " + e.getMessage());
        }
    }
    public static OekConfig load() {
        if (!Files.exists(FILE)) {
            OekConfig defaults = new OekConfig();
            defaults.save();
            return defaults;
        }
        try (Reader reader = Files.newBufferedReader(FILE)) {
            OekConfig data = GSON.fromJson(reader, OekConfig.class);
            if (data == null) return new OekConfig();
            if (data.enabled == null) data.enabled = EnumSet.noneOf(Module.class);
            if (data.selectedOres == null) data.selectedOres = EnumSet.allOf(OreType.class);
            data.scanRadius = Math.max(8, Math.min(32, data.scanRadius));
            data.verticalRadius = Math.max(8, Math.min(32, data.verticalRadius));
            data.maxRadarDots = Math.max(10, Math.min(250, data.maxRadarDots));
            return data;
        } catch (Exception e) {
            System.err.println("[OEK Client] Invalid config, resetting: " + e.getMessage());
            return new OekConfig();
        }
    }
}
