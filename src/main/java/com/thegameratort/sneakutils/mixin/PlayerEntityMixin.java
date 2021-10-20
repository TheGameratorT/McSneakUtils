package com.thegameratort.sneakutils.mixin;

import com.thegameratort.sneakutils.SneakUtils;
import com.thegameratort.sneakutils.config.SneakUtilsConfig;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
	@Shadow @Final public static EntityDimensions STANDING_DIMENSIONS;

	@Inject(
		method = "getDimensions(Lnet/minecraft/entity/EntityPose;)Lnet/minecraft/entity/EntityDimensions;",
		at = @At("HEAD"),
		cancellable = true
	)
	private void getDimensions_hook(EntityPose pose, CallbackInfoReturnable<EntityDimensions> cir) {
		SneakUtilsConfig config = SneakUtils.getConfig();
		if (config.legacySneak && pose == EntityPose.CROUCHING) {
			cir.setReturnValue(STANDING_DIMENSIONS);
			cir.cancel();
		}
	}

	@Inject(
		method = "getActiveEyeHeight(Lnet/minecraft/entity/EntityPose;Lnet/minecraft/entity/EntityDimensions;)F",
		at = @At("HEAD"),
		cancellable = true
	)
	private void getActiveEyeHeight_hook(EntityPose pose, EntityDimensions dimensions, CallbackInfoReturnable<Float> cir) {
		SneakUtilsConfig config = SneakUtils.getConfig();
		if (config.legacySneak && pose == EntityPose.CROUCHING) {
			cir.setReturnValue(1.54F);
			cir.cancel();
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
			cir.cancel();
		}
	}
}
