package com.thegameratort.sneakutils.mixin;

import com.thegameratort.sneakutils.SneakUtils;
import com.thegameratort.sneakutils.config.CameraLerpMode;
import com.thegameratort.sneakutils.config.SneakUtilsConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Making this Mixin was quite the challenge.
 *
 * The way Minecraft originally lerped the camera
 * was way different from what I expected, and it
 * required completely changing how it works.
 * */

@Mixin(Camera.class)
public abstract class CameraMixin {
	@Shadow private Entity focusedEntity;
	@Shadow private float cameraY;
	@Shadow private float lastCameraY;

	private RenderTickCounter renderTickCounter;
	private float lerpTime;
	private float lerpStartCameraY;
	private float lerpEndCameraY;
	private float lerpDuration;
	private boolean isLerping;

	@Inject(method = "<init>()V", at = @At("TAIL"))
	private void init_hook(CallbackInfo ci) {
		this.renderTickCounter = MinecraftClient.getInstance().getRenderTickCounter();
		this.isLerping = false;
	}

	@Redirect(
		method = "update(Lnet/minecraft/world/BlockView;Lnet/minecraft/entity/Entity;ZZF)V",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/util/math/MathHelper;lerp(FFF)F",
			ordinal = 0
		)
	)
	private float cameraHeightLerp_hook(float delta, float start, float end) {
		SneakUtilsConfig config = SneakUtils.getConfig();
		if (config.cameraLerpMode == CameraLerpMode.INSTANT) {
			return end;
		}
        /*if (config.cameraLerpMode == CameraLerpMode.DEFAULT) {
            return MathHelper.lerp(delta, start, end);
        }*/
		updateLerp(renderTickCounter.getLastFrameDuration());
		return this.cameraY;
	}

	/**
	 * @author TheGameratorT
	 * @reason Change camera sneak lerp speed.
	 */
	@Overwrite
	public void updateEyeHeight() {
		if (this.focusedEntity != null) {
			SneakUtilsConfig config = SneakUtils.getConfig();
			float eyeHeight = this.focusedEntity.getStandingEyeHeight();
			if (config.cameraLerpMode == CameraLerpMode.INSTANT) {
				this.cameraY = eyeHeight;
				this.lastCameraY = eyeHeight;
				return;
			}
			this.lastCameraY = this.cameraY;
            /*if (config.cameraLerpMode == CameraLerpMode.DEFAULT) {
                this.cameraY += (eyeHeight - this.cameraY) * 0.5F;
                return;
            }*/
			if (eyeHeight != this.lerpEndCameraY) {
				if (!this.isLerping) {
					this.lerpDuration = config.cameraLerpDuration;
					this.isLerping = true;
				}
				this.lerpStartCameraY = this.cameraY;
				this.lerpEndCameraY = eyeHeight;
				this.lerpTime = 0.0F;
			}
		}
	}

	private void updateLerp(float deltaTime) {
		if (this.isLerping) {
			SneakUtilsConfig config = SneakUtils.getConfig();
			if (this.lerpTime >= this.lerpDuration) {
				this.isLerping = false;
				this.cameraY = this.lerpEndCameraY;
				return;
			}
			float factor = resolveFactor(config.cameraLerpMode, this.lerpTime / this.lerpDuration);
			this.cameraY = MathHelper.lerp(factor, this.lerpStartCameraY, this.lerpEndCameraY);
			this.lerpTime += deltaTime;
		}
	}

	private float resolveFactor(CameraLerpMode lerpMode, float factor) {
		return switch (lerpMode) {
			default -> factor;
			case SMOOTH_STEP -> smoothStep(factor);
			case SMOOTHER_STEP -> smootherStep(factor);
			case DEFAULT_STEP -> defaultStep(factor);
		};
	}

	private static float smoothStep(float t) {
		return t * t * (3.0F - 2.0F * t);
	}

	private static float smootherStep(float t) {
		return t * t * t * (t * (t * 6.0F - 15.0F) + 10.0F);
	}

	// Thank you Arcayn
	private static float defaultStep(float x) {
		final double b = 16.0D;
		double n = -Math.exp(-(b * x)) + 1;
		double m = 1 - Math.exp(-b);
		return (float) (n / m);
	}
}
