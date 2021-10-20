package com.thegameratort.sneakutils.mixin;

import com.thegameratort.sneakutils.SneakUtils;
import com.thegameratort.sneakutils.config.SneakUtilsConfigManager;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.realms.RealmsConnection;
import net.minecraft.client.realms.dto.RealmsServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RealmsConnection.class)
public abstract class RealmsConnectionMixin {
	@Inject(method = "connect(Lnet/minecraft/client/realms/dto/RealmsServer;Lnet/minecraft/client/network/ServerAddress;)V", at = @At("HEAD"))
	private void connect_hook(RealmsServer server, ServerAddress address, CallbackInfo ci) {
		SneakUtilsConfigManager configManager = SneakUtils.getConfigManager();
		configManager.loadConfig("realms", Long.toString(server.id));
		configManager.setCurrentWorldName(server.name);
	}
}
