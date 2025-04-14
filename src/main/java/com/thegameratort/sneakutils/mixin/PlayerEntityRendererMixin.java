package com.thegameratort.sneakutils.mixin;

import com.thegameratort.sneakutils.SneakUtils;
import com.thegameratort.sneakutils.config.SneakUtilsConfig;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin {
	@Unique private boolean isInSneakingPoseBackup;

	@Inject(method = "getPositionOffset(Lnet/minecraft/client/render/entity/state/PlayerEntityRenderState;)Lnet/minecraft/util/math/Vec3d;", at = @At("HEAD"))
	private void getPositionOffset_preHook(PlayerEntityRenderState playerEntityRenderState, CallbackInfoReturnable<Vec3d> cir) {
		isInSneakingPoseBackup = playerEntityRenderState.isInSneakingPose;

		SneakUtilsConfig config = SneakUtils.getConfig();
		playerEntityRenderState.isInSneakingPose = !config.legacySneakPosOff && isInSneakingPoseBackup;
	}

	@Inject(method = "getPositionOffset(Lnet/minecraft/client/render/entity/state/PlayerEntityRenderState;)Lnet/minecraft/util/math/Vec3d;", at = @At("TAIL"))
	private void getPositionOffset_postHook(PlayerEntityRenderState playerEntityRenderState, CallbackInfoReturnable<Vec3d> cir) {
		playerEntityRenderState.isInSneakingPose = isInSneakingPoseBackup;
	}
}
