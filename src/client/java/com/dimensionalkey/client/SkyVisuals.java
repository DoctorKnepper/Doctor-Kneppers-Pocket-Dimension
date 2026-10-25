package com.dimensionalkey.client;

import com.dimensionalkey.SkyPreset;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ARGB;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.attribute.EnvironmentAttributeSystem.Builder;
import net.minecraft.world.level.MoonPhase;
import net.minecraft.world.level.dimension.DimensionType.Skybox;
import org.joml.Vector3fc;
import org.joml.Vector4fc;

public final class SkyVisuals {
	private static final Vector4fc NO_GLOW = ARGB.vector4fFromARGB32(0x00FFE533);
	private static final Vector4fc NO_CLOUDS = ARGB.vector4fFromARGB32(0x00FFFFFF);
	private static final Vector3fc WHITE = ARGB.vector3fFromRGB24(0xFFFFFF);
	private static final Vector3fc NIGHT_LIGHT = ARGB.vector3fFromRGB24(0x7A7AFF);
	private static SkyPreset preset = SkyPreset.NATURAL;

	private SkyVisuals() {
	}

	public static SkyPreset preset() {
		return preset;
	}

	public static void setPreset(SkyPreset newPreset) {
		preset = newPreset;
		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.level != null) {
			minecraft.level.environmentAttributes().invalidateTickCache();
		}
	}

	public static Skybox skybox(Skybox original) {
		return switch (preset) {
			case THE_END -> Skybox.END;
			case VOID -> Skybox.NONE;
			default -> original;
		};
	}

	public static void addLayers(Builder builder) {
		layer(builder, EnvironmentAttributes.SUN_ANGLE, p -> {
			return switch (p) {
				case ETERNAL_DAY -> 0.0F;
				case STARRY_NIGHT, NEBULA -> 180.0F;
				case GOLDEN_SUNSET -> 80.0F;
				default -> null;
			};
		});
		layer(builder, EnvironmentAttributes.MOON_ANGLE, p -> {
			return switch (p) {
				case ETERNAL_DAY -> 180.0F;
				case STARRY_NIGHT, NEBULA -> 0.0F;
				case GOLDEN_SUNSET -> 260.0F;
				default -> null;
			};
		});
		layer(builder, EnvironmentAttributes.STAR_ANGLE, p -> {
			return switch (p) {
				case ETERNAL_DAY -> 0.0F;
				case STARRY_NIGHT, NEBULA -> 180.0F;
				case GOLDEN_SUNSET -> 80.0F;
				default -> null;
			};
		});
		layer(builder, EnvironmentAttributes.STAR_BRIGHTNESS, p -> {
			return switch (p) {
				case VOID, ETERNAL_DAY -> 0.0F;
				case STARRY_NIGHT, NEBULA -> 1.0F;
				case GOLDEN_SUNSET -> 0.08F;
				default -> null;
			};
		});
		layer(builder, EnvironmentAttributes.MOON_PHASE, p -> p == SkyPreset.STARRY_NIGHT || p == SkyPreset.NEBULA ? MoonPhase.FULL_MOON : null);
		layer(builder, EnvironmentAttributes.SKY_COLOR, p -> {
			return switch (p) {
				case ETERNAL_DAY -> rgb(0x78A7FF);
				case STARRY_NIGHT -> rgb(0x070B1E);
				case GOLDEN_SUNSET -> rgb(0xE89A5E);
				case NEBULA -> rgb(0x2A0E4A);
				case THE_END, VOID -> rgb(0x000000);
				default -> null;
			};
		});
		layer(builder, EnvironmentAttributes.FOG_COLOR, p -> {
			return switch (p) {
				case ETERNAL_DAY -> rgb(0xC0D8FF);
				case STARRY_NIGHT -> rgb(0x0F1226);
				case GOLDEN_SUNSET -> rgb(0xFFB27A);
				case NEBULA -> rgb(0x5A1E6E);
				case THE_END -> rgb(0x181318);
				case VOID -> rgb(0x000000);
				default -> null;
			};
		});
		layer(builder, EnvironmentAttributes.SUNRISE_SUNSET_COLOR, p -> {
			return switch (p) {
				case GOLDEN_SUNSET -> ARGB.vector4fFromARGB32(0xD8FF7A2E);
				case NATURAL -> null;
				default -> NO_GLOW;
			};
		});
		layer(builder, EnvironmentAttributes.CLOUD_COLOR, p -> {
			return switch (p) {
				case ETERNAL_DAY -> ARGB.vector4fFromARGB32(0xCCFFFFFF);
				case STARRY_NIGHT -> ARGB.vector4fFromARGB32(0x9919192A);
				case GOLDEN_SUNSET -> ARGB.vector4fFromARGB32(0xCCFFC9A0);
				case NEBULA -> ARGB.vector4fFromARGB32(0x88B060D0);
				case THE_END, VOID -> NO_CLOUDS;
				default -> null;
			};
		});
		layer(builder, EnvironmentAttributes.SKY_LIGHT_COLOR, p -> {
			return switch (p) {
				case VOID, ETERNAL_DAY -> WHITE;
				case STARRY_NIGHT -> NIGHT_LIGHT;
				case GOLDEN_SUNSET -> rgb(0xFFD2A0);
				case NEBULA -> rgb(0xC08CFF);
				case THE_END -> rgb(0xD0A8E8);
				default -> null;
			};
		});
		layer(builder, EnvironmentAttributes.SKY_LIGHT_FACTOR, p -> {
			return switch (p) {
				case ETERNAL_DAY -> 1.0F;
				case STARRY_NIGHT -> 0.35F;
				case GOLDEN_SUNSET -> 0.8F;
				case NEBULA -> 0.45F;
				case THE_END, VOID -> 0.6F;
				default -> null;
			};
		});
	}

	private static Vector3fc rgb(int color) {
		return ARGB.vector3fFromRGB24(color);
	}

	private static <V> void layer(Builder builder, EnvironmentAttribute<V> attribute, Function<SkyPreset, V> valueFor) {
		builder.addTimeBasedLayer(attribute, (base, cacheTickId) -> {
			V value = valueFor.apply(preset);
			return value != null ? value : base;
		});
	}
}
