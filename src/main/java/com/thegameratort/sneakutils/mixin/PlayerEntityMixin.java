package com.thegameratort.sneakutils.mixin;

import com.thegameratort.sneakutils.SneakUtils;
import com.thegameratort.sneakutils.config.SneakMode;
import com.thegameratort.sneakutils.config.SneakUtilsConfig;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PlayerEntity.class, priority = 1100)
public abstract class PlayerEntityMixin {
	private static final EntityDimensions SNEAKING_DIMENSIONS_v1_13 = EntityDimensions.changing(0.6F, 1.65F);

	@Inject(
		method = "getDimensions(Lnet/minecraft/entity/EntityPose;)Lnet/minecraft/entity/EntityDimensions;",
		at = @At("HEAD"),
		cancellable = true
	)
	private void getDimensions_hook(EntityPose pose, CallbackInfoReturnable<EntityDimensions> cir) {
		if (pose == EntityPose.CROUCHING) {
			SneakUtilsConfig config = SneakUtils.getConfig();
			if (config.sneakMode == SneakMode.v1_8) {
				cir.setReturnValue(PlayerEntity.STANDING_DIMENSIONS);
			} else if (config.sneakMode == SneakMode.v1_13) {
				cir.setReturnValue(SNEAKING_DIMENSIONS_v1_13);
			}
		}
	}

	@Inject(
		method = "getActiveEyeHeight(Lnet/minecraft/entity/EntityPose;Lnet/minecraft/entity/EntityDimensions;)F",
		at = @At("HEAD"),
		cancellable = true
	)
	private void getActiveEyeHeight_hook(EntityPose pose, EntityDimensions dimensions, CallbackInfoReturnable<Float> cir) {
		if (pose == EntityPose.CROUCHING) {
			SneakUtilsConfig config = SneakUtils.getConfig();
			if (config.sneakMode != SneakMode.LATEST) {
				cir.setReturnValue(1.54F);
			}
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
