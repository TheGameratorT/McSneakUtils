package com.thegameratort.sneakutils.mixin.compat;

import com.thegameratort.sneakutils.SneakUtils;
import com.thegameratort.sneakutils.config.SneakUtilsConfigManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Pseudo
@Mixin(targets = "com.replaymod.replay.ReplayHandler", remap = false)
public class ReplayModMixin {
	private static final Logger logger = LogManager.getLogger();

	@Dynamic
	@Inject(method = "setup", at = @At("HEAD"))
	private void setup_hook(CallbackInfo ci) {
		boolean isSingleplayer;
		String serverName;
		try {
			Object replayMetadata = getReplayMetadata(getReplayFile());
			isSingleplayer = getReplayMetadataField(replayMetadata, "isSingleplayer");
			serverName = getReplayMetadataField(replayMetadata, "getServerName");
		} catch (Exception ex) {
			return;
		}
		String folder = isSingleplayer ? "worlds" : "servers";

		SneakUtilsConfigManager configManager = SneakUtils.getConfigManager();
		configManager.loadConfig(folder, serverName);
		configManager.setCurrentWorldName(serverName);
	}

	private Object getReplayFile() throws Exception {
		Field replayFileField;
		Object replayFile;
		try {
			replayFileField = this.getClass().getDeclaredField("replayFile");
			replayFile = replayFileField.get(this);
		} catch (NoSuchFieldException | IllegalAccessException ex) {
			logger.error("Could not get the replay file", ex);
			throw new Exception();
		}
		return replayFile;
	}

	private Object getReplayMetadata(Object replayFile) throws Exception {
		Method getMetadataMethod;
		Object replayMetadata;
		try {
			getMetadataMethod = replayFile.getClass().getMethod("getMetaData");
			replayMetadata = getMetadataMethod.invoke(replayFile);
		} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ex) {
			logger.error("Could not get the replay file metadata", ex);
			throw new Exception();
		}
		return replayMetadata;
	}

	@SuppressWarnings("unchecked")
	private <T> T getReplayMetadataField(Object replayMetadata, String methodName) throws Exception {
		Method method;
		try {
			method = replayMetadata.getClass().getMethod(methodName);
		} catch (NoSuchMethodException ex) {
			logger.error("Could not find method ReplayMetaData." + methodName + "()", ex);
			throw new Exception();
		}

		T value;
		try {
			value = (T) method.invoke(replayMetadata);
		} catch (InvocationTargetException | IllegalAccessException ex) {
			logger.error("Could not invoke method ReplayMetaData." + methodName + "()", ex);
			throw new Exception();
		}
		return value;
	}
}
