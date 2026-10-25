package com.dimensionalkey;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;

public record SkyPresetPayload(SkyPreset preset) implements CustomPacketPayload {
	public static final Type<SkyPresetPayload> TYPE = new Type<>(DimensionalKeyMod.id("sky_preset"));
	public static final StreamCodec<RegistryFriendlyByteBuf, SkyPresetPayload> STREAM_CODEC = ByteBufCodecs.VAR_INT
		.<SkyPresetPayload>map(id -> new SkyPresetPayload(SkyPreset.byId(id)), payload -> payload.preset().ordinal())
		.cast();

	@Override
	public Type<SkyPresetPayload> type() {
		return TYPE;
	}
}
