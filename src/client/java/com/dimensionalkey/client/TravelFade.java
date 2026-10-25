package com.dimensionalkey.client;

import com.dimensionalkey.DimensionalKeyMod;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;

public final class TravelFade {
	private static final long DURATION_NANOS = 1200000000L;
	private static final int COLOR = 5905052;
	private static boolean pending;
	private static long startNanos = -1L;

	private TravelFade() {
	}

	public static void register() {
		HudElementRegistry.addLast(DimensionalKeyMod.id("travel_fade"), TravelFade::extract);
	}

	public static void start() {
		pending = true;
		startNanos = -1L;
	}

	private static void extract(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
		if (pending && Minecraft.getInstance().gui.screen() == null) {
			pending = false;
			startNanos = System.nanoTime();
		}

		if (startNanos < 0L) {
			return;
		}

		float progress = (float)(System.nanoTime() - startNanos) / DURATION_NANOS;
		if (progress >= 1.0F) {
			startNanos = -1L;
			return;
		}

		float strength = progress < 0.15F ? 1.0F : Mth.square(1.0F - (progress - 0.15F) / 0.85F);
		int alpha = Mth.floor(220.0F * strength);
		graphics.fill(0, 0, graphics.guiWidth(), graphics.guiHeight(), ARGB.color(alpha, COLOR));
	}
}
