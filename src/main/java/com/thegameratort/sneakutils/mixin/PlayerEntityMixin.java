package com.thegameratort.sneakutils.mixin;

import com.thegameratort.sneakutils.SneakUtils;
import com.thegameratort.sneakutils.config.SneakMode;
import com.thegameratort.sneakutils.config.SneakUtilsConfig;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
	private static final float SNEAKING_HEIGHT_v1_8 = 1.8F;
	private static final float SNEAKING_HEIGHT_v1_13 = 1.65F;
	private static final float SNEAKING_EYEHEIGHT_LEGACY = 1.54F;

	@Inject(
		method = "getBaseDimensions(Lnet/minecraft/entity/EntityPose;)Lnet/minecraft/entity/EntityDimensions;",
		at = @At("TAIL"),
		cancellable = true
	)
	private void getBaseDimensions_hook(EntityPose pose, CallbackInfoReturnable<EntityDimensions> cir) {
		if (pose == EntityPose.CROUCHING) {
			EntityDimensions resultDimensions = cir.getReturnValue();
			SneakUtilsConfig config = SneakUtils.getConfig();

			float height;
			switch (config.sneakMode) {
				case v1_8 -> height = SNEAKING_HEIGHT_v1_8;
				case v1_13 -> height = SNEAKING_HEIGHT_v1_13;
				default -> height = resultDimensions.height();
			}

			float eyeHeight = config.sneakMode == SneakMode.LATEST ?
				resultDimensions.eyeHeight() : SNEAKING_EYEHEIGHT_LEGACY;

			EntityDimensions modifiedDimensions = new EntityDimensions(
				resultDimensions.width(), height, eyeHeight,
				resultDimensions.attachments(), resultDimensions.fixed());

			cir.setReturnValue(modifiedDimensions);
		}
	}

	@Inject(
		method = "clipAtLedge()Z",
		at = @At("HEAD"),
		cancellable = true
	)
	private void clipAtLedge_hook(CallbackInfoReturnable<Boolean> cir) {
		SneakUtilsConfig config = SneakUtils.getConfig();
		if (config.noLedgeClipping) {
			cir.setReturnValue(false);
		}
	}
}
