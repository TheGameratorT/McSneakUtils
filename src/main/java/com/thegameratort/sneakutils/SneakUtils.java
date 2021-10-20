package com.thegameratort.sneakutils;

import com.thegameratort.sneakutils.config.SneakUtilsConfig;
import com.thegameratort.sneakutils.config.SneakUtilsConfigManager;
import net.fabricmc.api.ClientModInitializer;

public class SneakUtils implements ClientModInitializer {
	private static SneakUtilsConfigManager configManager;

	@Override
	public void onInitializeClient() {
		System.out.println("Sneak Utils started.");
		configManager = new SneakUtilsConfigManager();
		configManager.loadDefaultConfig();
	}

	public static SneakUtilsConfig getConfig() {
		return SneakUtils.configManager.getConfig();
	}

	public static SneakUtilsConfigManager getConfigManager() {
		return SneakUtils.configManager;
	}
}
