package com.thegameratort.sneakutils.config;

import net.fabricmc.loader.api.FabricLoader;

import java.io.File;

public class SneakUtilsConfigManager {
	private SneakUtilsConfigFile configFile = null;
	private SneakUtilsConfigFile defConfigFile = null;
	private String currentWorldName = null;
	private final String configDir;

	public SneakUtilsConfigManager() {
		this.configDir = FabricLoader.getInstance().getConfigDir().toString() + "/sneakutils";
	}

	public SneakUtilsConfig getConfig() {
		return this.configFile.getConfig();
	}

	public SneakUtilsConfigFile getConfigFile() {
		return this.configFile;
	}

	public void loadConfig(String folder, String name) {
		String path = this.configDir + "/" + folder + "/" + name + ".json";
		File file = new File(path);
		this.configFile = SneakUtilsConfigFile.load(file, this.getDefaultConfig(), false);
	}

    /*public void saveConfig() {
        this.configFile.save();
    }*/

	public void unloadConfig() {
		this.configFile = null;
	}

	public boolean isConfigLoaded() {
		return this.configFile != null;
	}

	// DEFAULT CONFIG

	public SneakUtilsConfig getDefaultConfig() {
		return this.defConfigFile.getConfig();
	}

	public SneakUtilsConfigFile getDefaultConfigFile() {
		return this.defConfigFile;
	}

	public void loadDefaultConfig() {
		String path = this.configDir + "/default.json";
		File file = new File(path);
		this.defConfigFile = SneakUtilsConfigFile.load(file, null, true);
	}

    /*public void saveDefaultConfig() {
        this.defConfigFile.save();
    }*/

	// CONFIG SCREEN

	public void setCurrentWorldName(String currentWorldName) {
		this.currentWorldName = currentWorldName;
	}

	public String getCurrentWorldName() {
		return this.currentWorldName;
	}
}
