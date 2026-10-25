package com.dimensionalkey;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.phys.Vec3;

public class PocketData extends SavedData {
	public static final Codec<PocketData> CODEC = RecordCodecBuilder.create(
		i -> i.group(
				Codec.INT.optionalFieldOf("next_plot", 0).forGetter(data -> data.nextPlot),
				Codec.unboundedMap(UUIDUtil.STRING_CODEC, PocketData.Entry.CODEC).optionalFieldOf("players", Map.of()).forGetter(data -> data.players)
			)
			.apply(i, PocketData::new)
	);
	public static final SavedDataType<PocketData> TYPE = new SavedDataType<>(
		DimensionalKeyMod.id("pocket_realm"), PocketData::new, CODEC, DataFixTypes.SAVED_DATA_WANDERING_TRADER
	);
	private int nextPlot;
	private final Map<UUID, PocketData.Entry> players;

	public PocketData() {
		this(0, Map.of());
	}

	private PocketData(int nextPlot, Map<UUID, PocketData.Entry> players) {
		this.nextPlot = nextPlot;
		this.players = new HashMap<>(players);
	}

	public static PocketData get(MinecraftServer server) {
		return server.getDataStorage().computeIfAbsent(TYPE);
	}

	public PocketData.Entry getOrCreate(UUID player) {
		return this.players.computeIfAbsent(player, uuid -> {
			this.setDirty();
			return new PocketData.Entry(this.nextPlot++, false, Optional.empty(), Optional.empty(), SkyPreset.NATURAL.getSerializedName());
		});
	}

	public static class Entry {
		static final Codec<PocketData.Entry> CODEC = RecordCodecBuilder.create(
			i -> i.group(
					Codec.INT.fieldOf("plot").forGetter(e -> e.plot),
					Codec.BOOL.optionalFieldOf("built", false).forGetter(e -> e.built),
					PocketData.ReturnPoint.CODEC.optionalFieldOf("return").forGetter(e -> e.returnPoint),
					PocketData.ReturnPoint.CODEC.optionalFieldOf("last_pocket_spot").forGetter(e -> e.lastPocketSpot),
					Codec.STRING.optionalFieldOf("sky", SkyPreset.NATURAL.getSerializedName()).forGetter(e -> e.sky.getSerializedName())
				)
				.apply(i, PocketData.Entry::new)
		);
		public final int plot;
		public boolean built;
		public Optional<PocketData.ReturnPoint> lastPocketSpot;
		public Optional<PocketData.ReturnPoint> returnPoint;
		public SkyPreset sky;

		private Entry(int plot, boolean built, Optional<PocketData.ReturnPoint> returnPoint, Optional<PocketData.ReturnPoint> lastPocketSpot, String sky) {
			this.plot = plot;
			this.built = built;
			this.returnPoint = returnPoint;
			this.lastPocketSpot = lastPocketSpot;
			this.sky = SkyPreset.byName(sky);
		}
	}

	public record ReturnPoint(ResourceKey<Level> dimension, Vec3 pos, float yRot, float xRot) {
		static final Codec<PocketData.ReturnPoint> CODEC = RecordCodecBuilder.create(
			i -> i.group(
					Level.RESOURCE_KEY_CODEC.fieldOf("dimension").forGetter(PocketData.ReturnPoint::dimension),
					Vec3.CODEC.fieldOf("pos").forGetter(PocketData.ReturnPoint::pos),
					Codec.FLOAT.fieldOf("y_rot").forGetter(PocketData.ReturnPoint::yRot),
					Codec.FLOAT.fieldOf("x_rot").forGetter(PocketData.ReturnPoint::xRot)
				)
				.apply(i, PocketData.ReturnPoint::new)
		);
	}
}
