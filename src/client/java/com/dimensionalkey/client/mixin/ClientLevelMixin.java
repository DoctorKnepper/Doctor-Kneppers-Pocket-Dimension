package com.dimensionalkey.client.mixin;

import com.dimensionalkey.PocketRealm;
import com.dimensionalkey.client.SkyVisuals;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.attribute.EnvironmentAttributeSystem.Builder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin {
	@Inject(method = "addEnvironmentAttributeLayers", at = @At("RETURN"))
	private void dimensionalkey$addSkyLayers(Builder builder, CallbackInfoReturnable<Builder> cir) {
		if (((ClientLevel)(Object)this).dimension() == PocketRealm.LEVEL) {
			SkyVisuals.addLayers(builder);
		}
	}
}
