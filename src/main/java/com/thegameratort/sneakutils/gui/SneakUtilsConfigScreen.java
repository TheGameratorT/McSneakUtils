package com.thegameratort.sneakutils.gui;

import com.thegameratort.sneakutils.SneakUtils;
import com.thegameratort.sneakutils.config.SneakUtilsConfig;
import com.thegameratort.sneakutils.config.SneakUtilsConfigFile;
import com.thegameratort.sneakutils.config.SneakUtilsConfigManager;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Language;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public class SneakUtilsConfigScreen {
	private static final Logger logger = LogManager.getLogger();

	public static Screen get(Screen parent) {
		SneakUtilsConfigManager configManager = SneakUtils.getConfigManager();
		return configManager.isConfigLoaded() ? getCurrent(parent) : getDefault(parent);
	}

	public static Screen getCurrent(Screen parent) {
		SneakUtilsConfigManager configManager = SneakUtils.getConfigManager();
		return createScreen(parent, configManager.getConfigFile(), configManager.getDefaultConfig(), true);
	}

	public static Screen getDefault(Screen parent) {
		SneakUtilsConfigManager configManager = SneakUtils.getConfigManager();
		return createScreen(parent, configManager.getDefaultConfigFile(), new SneakUtilsConfig(), false);
	}

	private static Screen createScreen(Screen parent, SneakUtilsConfigFile configFile, SneakUtilsConfig defConfig, boolean isCurrent) {
		SneakUtilsConfig config = configFile.getConfig();
		SneakUtilsConfigManager configManager = SneakUtils.getConfigManager();

		ConfigBuilder builder = ConfigBuilder.create()
			.setParentScreen(parent)
			.setTitle(new TranslatableText("text.sneakutils.title"));

		ConfigEntryBuilder entryBuilder = builder.entryBuilder();

		ConfigCategory options = builder.getOrCreateCategory(LiteralText.EMPTY);

		if (isCurrent) {
			ButtonListEntry switchConfigBtnEntry = new ButtonListEntry(
				new TranslatableText("option.sneakutils.defaultConfig"),
				new TranslatableText("text.sneakutils.edit"),
				null/*() -> Optional.of(new Text[]{new TranslatableText("option.sneakutils.tooltip.defaultConfig")})*/,
				() -> {
					MinecraftClient client = MinecraftClient.getInstance();
					client.openScreen(getDefault(parent));
				}
			);
			options.addEntry(switchConfigBtnEntry);

			String entryText = Language.getInstance()
				.get("option.sneakutils.currentConfigSeparator")
				.formatted(configManager.getCurrentWorldName());
			options.addEntry(new SeparatorListEntry(new LiteralText(entryText)));
		} else {
			if (configManager.isConfigLoaded()) {
				String entryText = Language.getInstance()
					.get("option.sneakutils.currentConfig")
					.formatted(configManager.getCurrentWorldName());
				ButtonListEntry switchConfigBtnEntry = new ButtonListEntry(
					new LiteralText(entryText),
					new TranslatableText("text.sneakutils.edit"),
					null/*() -> Optional.of(new Text[]{new TranslatableText("option.sneakutils.tooltip.currentConfig")})*/,
					() -> {
						MinecraftClient client = MinecraftClient.getInstance();
						client.openScreen(getCurrent(parent));
					}
				);
				options.addEntry(switchConfigBtnEntry);
			}
			options.addEntry(new SeparatorListEntry(new TranslatableText("option.sneakutils.defaultConfigSeparator")));
		}

		Field[] fields = SneakUtilsConfig.class.getFields();
		for (Field field : fields) {
			Class<?> type = field.getType();
			if (type.isAssignableFrom(boolean.class)) {
				addBooleanField(config, defConfig, field, options, entryBuilder);
			} else if (type.isAssignableFrom(float.class)) {
				addFloatField(config, defConfig, field, options, entryBuilder);
			} else if (type.isAssignableFrom(int.class)) {
				addIntField(config, defConfig, field, options, entryBuilder);
			} else if (type.isAssignableFrom(long.class)) {
				addLongField(config, defConfig, field, options, entryBuilder);
			} else if (type.isEnum()) {
				addEnumField(config, defConfig, field, options, entryBuilder);
			}
		}

		builder.setSavingRunnable(configFile::save);

		return builder.build();
	}

	private static void addBooleanField(SneakUtilsConfig config, SneakUtilsConfig defConfig, Field field, ConfigCategory options, ConfigEntryBuilder entryBuilder) {
		boolean value, defValue;
		try {
			value = field.getBoolean(config);
			defValue = field.getBoolean(defConfig);
		} catch (Exception ex) {
			logger.error("Could not get boolean from config class.", ex);
			return;
		}
		String key = field.getName();
		BooleanToggleBuilder btb = entryBuilder.startBooleanToggle(new TranslatableText("option.sneakutils." + key), value);
		btb.setTooltip(new TranslatableText("option.sneakutils.tooltip." + key));
		btb.setDefaultValue(defValue);
		btb.setSaveConsumer(newValue -> {
			try {
				field.setBoolean(config, newValue);
			} catch (Exception ex) {
				logger.error("Could not set boolean to config class.", ex);
			}
		});
		options.addEntry(btb.build());
	}

	private static void addFloatField(SneakUtilsConfig config, SneakUtilsConfig defConfig, Field field, ConfigCategory options, ConfigEntryBuilder entryBuilder) {
		float value, defValue;
		try {
			value = field.getFloat(config);
			defValue = field.getFloat(defConfig);
		} catch (Exception ex) {
			logger.error("Could not get float from config class.", ex);
			return;
		}
		String key = field.getName();
		FloatFieldBuilder ffb = entryBuilder.startFloatField(new TranslatableText("option.sneakutils." + key), value);
		ffb.setTooltip(new TranslatableText("option.sneakutils.tooltip." + key));
		ffb.setDefaultValue(defValue);
		ffb.setSaveConsumer(newValue -> {
			try {
				field.setFloat(config, newValue);
			} catch (Exception ex) {
				logger.error("Could not set float to config class.", ex);
			}
		});
		if (field.isAnnotationPresent(ConfigFloatBounds.class)) {
			ConfigFloatBounds annotation = field.getAnnotation(ConfigFloatBounds.class);
			ffb.setMin(annotation.min());
			ffb.setMax(annotation.max());
		}
		options.addEntry(ffb.build());
	}

	private static void addIntField(SneakUtilsConfig config, SneakUtilsConfig defConfig, Field field, ConfigCategory options, ConfigEntryBuilder entryBuilder) {
		int value, defValue;
		try {
			value = field.getInt(config);
			defValue = field.getInt(defConfig);
		} catch (Exception ex) {
			logger.error("Could not get int from config class.", ex);
			return;
		}
		String key = field.getName();
		IntFieldBuilder ifb = entryBuilder.startIntField(new TranslatableText("option.sneakutils." + key), value);
		ifb.setTooltip(new TranslatableText("option.sneakutils.tooltip." + key));
		ifb.setDefaultValue(defValue);
		ifb.setSaveConsumer(newValue -> {
			try {
				field.setInt(config, newValue);
			} catch (Exception ex) {
				logger.error("Could not set int to config class.", ex);
			}
		});
		if (field.isAnnotationPresent(ConfigIntBounds.class)) {
			ConfigIntBounds annotation = field.getAnnotation(ConfigIntBounds.class);
			ifb.setMin((int) annotation.min());
			ifb.setMax((int) annotation.max());
		}
		options.addEntry(ifb.build());
	}

	private static void addLongField(SneakUtilsConfig config, SneakUtilsConfig defConfig, Field field, ConfigCategory options, ConfigEntryBuilder entryBuilder) {
		long value, defValue;
		try {
			value = field.getLong(config);
			defValue = field.getLong(defConfig);
		} catch (Exception ex) {
			logger.error("Could not get long from config class.", ex);
			return;
		}
		String key = field.getName();
		LongFieldBuilder lfb = entryBuilder.startLongField(new TranslatableText("option.sneakutils." + key), value);
		lfb.setTooltip(new TranslatableText("option.sneakutils.tooltip." + key));
		lfb.setDefaultValue(defValue);
		lfb.setSaveConsumer(newValue -> {
			try {
				field.setLong(config, newValue);
			} catch (Exception ex) {
				logger.error("Could not set long to config class.", ex);
			}
		});
		if (field.isAnnotationPresent(ConfigIntBounds.class)) {
			ConfigIntBounds annotation = field.getAnnotation(ConfigIntBounds.class);
			lfb.setMin(annotation.min());
			lfb.setMax(annotation.max());
		}
		options.addEntry(lfb.build());
	}

	private static void addEnumField(SneakUtilsConfig config, SneakUtilsConfig defConfig, Field field, ConfigCategory options, ConfigEntryBuilder entryBuilder) {
		Enum<?> value, defValue;
		try {
			value = (Enum<?>) field.get(config);
			defValue = (Enum<?>) field.get(defConfig);
		} catch (Exception ex) {
			logger.error("Could not get enum from config class.", ex);
			return;
		}

		String key = field.getName();
		String enumClassName = value.getClass().getSimpleName();
		String fieldKeyPrefix = "enum.sneakutils." + enumClassName + ".";

		@SuppressWarnings("unchecked")
		List<Enum<?>> constants = Arrays.asList(((Class<? extends Enum<?>>) field.getType()).getEnumConstants());
		int constantCount = constants.size();
		List<String> enumDisplayNames = Arrays.asList(new String[constantCount]);
		Language language = Language.getInstance();

		for (int i = 0; i < constantCount; i++) {
			Enum<?> constant = constants.get(i);
			String constantName = constant.toString();
			String constantDisplayName = language.get(fieldKeyPrefix + constantName);
			if (constantDisplayName.equals(key)) {
				constantDisplayName = constantName;
			}
			enumDisplayNames.set(i, constantDisplayName);
		}

		DropdownMenuBuilder<String> dmb = entryBuilder.startDropdownMenu(
			new TranslatableText("option.sneakutils." + key),
			DropdownMenuBuilder.TopCellElementBuilder.of(
				enumDisplayNames.get(constants.indexOf(value)),
				str -> enumDisplayNames.contains(str) ? str : null
			)
		);
		dmb.setTooltip(new TranslatableText("option.sneakutils.tooltip." + key));
		dmb.setDefaultValue(() -> enumDisplayNames.get(constants.indexOf(defValue)));
		dmb.setSelections(enumDisplayNames);
		dmb.setSaveConsumer(newValue -> {
			try {
				field.set(config, constants.get(enumDisplayNames.indexOf(newValue)));
			} catch (Exception ex) {
				logger.error("Could not set enum to config class.", ex);
			}
		});
		options.addEntry(dmb.build());
	}
}
