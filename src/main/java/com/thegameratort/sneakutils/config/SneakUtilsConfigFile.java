package com.thegameratort.sneakutils.config;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class SneakUtilsConfigFile {
	private final SneakUtilsConfig config;
	private final File file;

	private static final Logger logger = LogManager.getLogger();
	private static final Gson gson = new GsonBuilder()
		.setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
		.setPrettyPrinting()
		//.excludeFieldsWithModifiers(Modifier.PRIVATE)
		.create();

	private SneakUtilsConfigFile(SneakUtilsConfig config, File file) {
		this.config = config;
		this.file = file;
	}

	public SneakUtilsConfig getConfig() {
		return this.config;
	}

	public static SneakUtilsConfigFile load(File file, SneakUtilsConfig defConfig, boolean saveIfMissing) {
		//System.out.println("[SneakUtils] Loaded config: " + file.getAbsolutePath());
		if (file.exists()) {
			SneakUtilsConfig config;
			try (FileReader reader = new FileReader(file)) {
				config = gson.fromJson(reader, SneakUtilsConfig.class);
			} catch (IOException ex) {
				throw new RuntimeException("Could not parse config", ex);
			}
			if (config.sneakMode == null) {
				config.sneakMode = SneakMode.LATEST;
				logger.warn("[Sneak Utils] Invalid sneak mode defaulted to LATEST!");
			}
			if (config.cameraLerpMode == null) {
				config.cameraLerpMode = CameraLerpMode.DEFAULT_STEP;
				logger.warn("[Sneak Utils] Invalid camera lerp mode defaulted to DEFAULT_STEP!");
			}
			return new SneakUtilsConfigFile(config, file);
		}
		SneakUtilsConfig config;
		SneakUtilsConfigFile configFile;
		if (defConfig == null) {
			config = new SneakUtilsConfig();
		} else {
			config = new SneakUtilsConfig(defConfig);
		}
		configFile = new SneakUtilsConfigFile(config, file);
		if (saveIfMissing) {
			configFile.save();
		}
		return configFile;
	}

	public void save() {
		//System.out.println("[SneakUtils] Saved config: " + this.file.getAbsolutePath());
		File dir = this.file.getParentFile();

		if (!dir.exists()) {
			if (!dir.mkdirs()) {
				throw new RuntimeException("Could not create parent directories");
			}
		} else if (!dir.isDirectory()) {
			throw new RuntimeException("The parent file is not a directory");
		}

		try (FileWriter writer = new FileWriter(this.file)) {
			gson.toJson(this.config, writer);
		} catch (IOException ex) {
			throw new RuntimeException("Could not save configuration file", ex);
		}
	}
}
