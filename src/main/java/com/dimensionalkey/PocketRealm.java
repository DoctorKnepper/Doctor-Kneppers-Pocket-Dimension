package com.dimensionalkey;

import java.util.List;
import java.util.Optional;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.level.storage.LevelData.RespawnData;
import net.minecraft.world.phys.Vec3;

public final class PocketRealm {
	public static final ResourceKey<Level> LEVEL = ResourceKey.create(Registries.DIMENSION, DimensionalKeyMod.id("pocket"));
	private static final int PLOT_SPACING = 2048;
	private static final int PLATFORM_Y = 64;
	private static final int PLATFORM_SIZE = 10;
	private static final int FORCED_CHUNK_RADIUS = 2;

	private PocketRealm() {
	}

	public static void travel(ServerPlayer player) {
		MinecraftServer server = player.level().getServer();
		ServerLevel pocket = server.getLevel(LEVEL);
		if (pocket == null) {
			player.sendOverlayMessage(Component.translatable("message.dimensionalkey.missing"));
			return;
		}

		PocketData data = PocketData.get(server);
		PocketData.Entry entry = data.getOrCreate(player.getUUID());
		if (player.level().dimension() == LEVEL) {
			leave(player, server, entry);
		} else {
			entry.returnPoint = Optional.of(new PocketData.ReturnPoint(player.level().dimension(), player.position(), player.getYRot(), player.getXRot()));
			data.setDirty();
			preparePlot(pocket, entry, data);
			departEffects(player);
			Optional<PocketData.ReturnPoint> spot = entry.lastPocketSpot.filter(point -> isSafeSpot(pocket, BlockPos.containing(point.pos())));
			if (spot.isPresent()) {
				teleport(player, pocket, spot.get().pos(), spot.get().yRot(), spot.get().xRot());
			} else {
				teleport(player, pocket, arrivalPoint(pocket, entry), player.getYRot(), 0.0F);
			}

			arriveEffects(player);
			player.sendOverlayMessage(Component.translatable("message.dimensionalkey.enter"));
		}
	}

	private static void leave(ServerPlayer player, MinecraftServer server, PocketData.Entry entry) {
		entry.lastPocketSpot = Optional.of(new PocketData.ReturnPoint(LEVEL, player.position(), player.getYRot(), player.getXRot()));
		PocketData.get(server).setDirty();
		ServerLevel target = entry.returnPoint.<ServerLevel>map(point -> server.getLevel(point.dimension())).orElse(null);
		departEffects(player);
		if (target != null) {
			PocketData.ReturnPoint point = entry.returnPoint.get();
			teleport(player, target, point.pos(), point.yRot(), point.xRot());
			player.sendOverlayMessage(Component.translatable("message.dimensionalkey.leave"));
		} else {
			RespawnData spawn = server.getRespawnData();
			ServerLevel spawnLevel = server.getLevel(spawn.globalPos().dimension());
			if (spawnLevel == null) {
				spawnLevel = server.overworld();
			}

			teleport(player, spawnLevel, Vec3.atBottomCenterOf(spawn.pos()), spawn.yaw(), spawn.pitch());
			player.sendOverlayMessage(Component.translatable("message.dimensionalkey.no_return"));
		}

		arriveEffects(player);
	}

	public static void cycleSky(ServerPlayer player) {
		PocketData data = PocketData.get(player.level().getServer());
		PocketData.Entry entry = data.getOrCreate(player.getUUID());
		entry.sky = entry.sky.next();
		data.setDirty();
		syncSky(player);
		player.sendOverlayMessage(Component.translatable("message.dimensionalkey.sky", entry.sky.displayName()));
		player.level().playSound(null, player.blockPosition(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 1.0F, 1.2F);
	}

	public static void syncSky(ServerPlayer player) {
		if (ServerPlayNetworking.canSend(player, SkyPresetPayload.TYPE)) {
			SkyPreset sky = PocketData.get(player.level().getServer()).getOrCreate(player.getUUID()).sky;
			ServerPlayNetworking.send(player, new SkyPresetPayload(sky));
		}
	}

	public static void tickVoidRescue(ServerLevel level) {
		if (level.dimension() != LEVEL) {
			return;
		}

		for (ServerPlayer player : List.copyOf(level.players())) {
			if (player.getY() < level.getMinY() - 16 && !player.isSpectator()) {
				PocketData.Entry entry = PocketData.get(level.getServer()).getOrCreate(player.getUUID());
				teleport(player, level, arrivalPoint(level, entry), player.getYRot(), 0.0F);
				player.sendOverlayMessage(Component.translatable("message.dimensionalkey.void_rescue"));
			}
		}
	}

	private static BlockPos plotCenter(PocketData.Entry entry) {
		return new BlockPos(entry.plot * PLOT_SPACING, PLATFORM_Y, 0);
	}

	private static void preparePlot(ServerLevel pocket, PocketData.Entry entry, PocketData data) {
		BlockPos center = plotCenter(entry);
		int chunkX = center.getX() >> 4;
		int chunkZ = center.getZ() >> 4;

		for (int dx = -FORCED_CHUNK_RADIUS; dx <= FORCED_CHUNK_RADIUS; dx++) {
			for (int dz = -FORCED_CHUNK_RADIUS; dz <= FORCED_CHUNK_RADIUS; dz++) {
				pocket.setChunkForced(chunkX + dx, chunkZ + dz, true);
			}
		}

		if (!entry.built) {
			int half = PLATFORM_SIZE / 2;

			for (int x = -half; x < PLATFORM_SIZE - half; x++) {
				for (int z = -half; z < PLATFORM_SIZE - half; z++) {
					pocket.setBlockAndUpdate(center.offset(x, 0, z), Blocks.BEDROCK.defaultBlockState());
				}
			}

			entry.built = true;
			data.setDirty();
		}
	}

	private static Vec3 arrivalPoint(ServerLevel pocket, PocketData.Entry entry) {
		MutableBlockPos pos = plotCenter(entry).above().mutable();

		while (pos.getY() < pocket.getMaxY() - 1 && (!isOpen(pocket, pos) || !isOpen(pocket, pos.above()))) {
			pos.move(0, 1, 0);
		}

		return Vec3.atBottomCenterOf(pos);
	}

	private static boolean isSafeSpot(ServerLevel level, BlockPos feet) {
		if (!isOpen(level, feet) || !isOpen(level, feet.above())) {
			return false;
		}

		for (int down = 1; down <= 4; down++) {
			BlockPos below = feet.below(down);
			if (!level.getBlockState(below).getCollisionShape(level, below).isEmpty()) {
				return true;
			}
		}

		return false;
	}

	private static boolean isOpen(ServerLevel level, BlockPos pos) {
		return level.getBlockState(pos).getCollisionShape(level, pos).isEmpty() && level.getFluidState(pos).isEmpty();
	}

	private static void teleport(ServerPlayer player, ServerLevel level, Vec3 pos, float yRot, float xRot) {
		player.stopRiding();
		player.teleport(new TeleportTransition(level, pos, Vec3.ZERO, yRot, xRot, TeleportTransition.DO_NOTHING));
		player.resetFallDistance();
	}

	private static void departEffects(ServerPlayer player) {
		ServerLevel level = player.level();
		level.sendParticles(ParticleTypes.PORTAL, player.getX(), player.getY() + 1.0, player.getZ(), 80, 0.4, 0.8, 0.4, 0.6);
		level.playSound(null, player.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 0.8F);
	}

	private static void arriveEffects(ServerPlayer player) {
		ServerLevel level = player.level();
		level.sendParticles(ParticleTypes.REVERSE_PORTAL, player.getX(), player.getY() + 1.0, player.getZ(), 60, 0.5, 0.9, 0.5, 0.05);
		level.playSound(null, player.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.1F);
		if (ServerPlayNetworking.canSend(player, TravelFadePayload.TYPE)) {
			ServerPlayNetworking.send(player, TravelFadePayload.INSTANCE);
		}
	}
}
