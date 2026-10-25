package com.dimensionalkey;

import net.minecraft.network.chat.Component;

public enum SkyPreset {
	NATURAL("natural"),
	ETERNAL_DAY("eternal_day"),
	STARRY_NIGHT("starry_night"),
	GOLDEN_SUNSET("golden_sunset"),
	NEBULA("nebula"),
	THE_END("the_end"),
	VOID("void");

	private final String name;

	SkyPreset(String name) {
		this.name = name;
	}

	public String getSerializedName() {
		return this.name;
	}

	public Component displayName() {
		return Component.translatable("sky.dimensionalkey." + this.name);
	}

	public SkyPreset next() {
		SkyPreset[] values = values();
		return values[(this.ordinal() + 1) % values.length];
	}

	public static SkyPreset byName(String name) {
		for (SkyPreset preset : values()) {
			if (preset.name.equals(name)) {
				return preset;
			}
		}

		return NATURAL;
	}

	public static SkyPreset byId(int id) {
		SkyPreset[] values = values();
		return id >= 0 && id < values.length ? values[id] : NATURAL;
	}
}
