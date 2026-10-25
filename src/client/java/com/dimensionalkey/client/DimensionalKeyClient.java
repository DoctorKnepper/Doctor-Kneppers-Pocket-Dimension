package com.dimensionalkey.client;

import com.dimensionalkey.SkyPreset;
import com.dimensionalkey.SkyPresetPayload;
import com.dimensionalkey.TravelFadePayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class DimensionalKeyClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(SkyPresetPayload.TYPE, (payload, context) -> SkyVisuals.setPreset(payload.preset()));
		ClientPlayNetworking.registerGlobalReceiver(TravelFadePayload.TYPE, (payload, context) -> TravelFade.start());
		TravelFade.register();
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> SkyVisuals.setPreset(SkyPreset.NATURAL));
	}
}
