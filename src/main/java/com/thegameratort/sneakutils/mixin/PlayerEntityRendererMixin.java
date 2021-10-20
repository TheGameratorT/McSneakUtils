package com.thegameratort.sneakutils.mixin;

import com.thegameratort.sneakutils.SneakUtils;
import com.thegameratort.sneakutils.config.SneakUtilsConfig;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin {
	@Redirect(
		method = "getPositionOffset(Lnet/minecraft/client/network/AbstractClientPlayerEntity;F)Lnet/minecraft/util/math/Vec3d;",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;isInSneakingPose()Z"
		)
	)
	private boolean getPositionOffset_isInSneakingPose_hook(AbstractClientPlayerEntity player) {
		SneakUtilsConfig config = SneakUtils.getConfig();
		return !config.legacySneakPosOff && player.isInSneakingPose();
	}
}
