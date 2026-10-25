package com.dimensionalkey;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;

public record TravelFadePayload() implements CustomPacketPayload {
	public static final TravelFadePayload INSTANCE = new TravelFadePayload();
	public static final Type<TravelFadePayload> TYPE = new Type<>(DimensionalKeyMod.id("travel_fade"));
	public static final StreamCodec<RegistryFriendlyByteBuf, TravelFadePayload> STREAM_CODEC = StreamCodec.unit(INSTANCE);

	@Override
	public Type<TravelFadePayload> type() {
		return TYPE;
	}
}
