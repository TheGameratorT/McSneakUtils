package com.thegameratort.sneakutils.config;

import com.thegameratort.sneakutils.gui.ConfigFloatBounds;

public class SneakUtilsConfig {
	public SneakMode sneakMode = SneakMode.v1_17;
	public boolean legacySneakPosOff = false;
	public boolean noLedgeClipping = false;
	public CameraLerpMode cameraLerpMode = CameraLerpMode.DEFAULT_STEP;
	@ConfigFloatBounds(min=0.0F, max=100.0F)
	public float cameraLerpDuration = 20.0F;

	public SneakUtilsConfig() {}

	public SneakUtilsConfig(SneakUtilsConfig other) {
		this.sneakMode = other.sneakMode;
		this.legacySneakPosOff = other.legacySneakPosOff;
		this.noLedgeClipping = other.noLedgeClipping;
		this.cameraLerpMode = other.cameraLerpMode;
		this.cameraLerpDuration = other.cameraLerpDuration;
	}
}
