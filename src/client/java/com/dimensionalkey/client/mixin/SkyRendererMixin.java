package com.dimensionalkey.client.mixin;

import com.dimensionalkey.PocketRealm;
import com.dimensionalkey.client.SkyVisuals;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SkyRenderer;
import net.minecraft.client.renderer.state.level.SkyRenderState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.DimensionType.Skybox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SkyRenderer.class)
public abstract class SkyRendererMixin {
	@Redirect(
		method = "extractRenderState",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/dimension/DimensionType;skybox()Lnet/minecraft/world/level/dimension/DimensionType$Skybox;")
	)
	private Skybox dimensionalkey$pickSkybox(DimensionType type, ClientLevel level, float partialTicks, Camera camera, SkyRenderState state) {
		return level.dimension() == PocketRealm.LEVEL ? SkyVisuals.skybox(type.skybox()) : type.skybox();
	}
}
