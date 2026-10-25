package com.dimensionalkey;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.gamerules.GameRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DimensionalKeyMod implements ModInitializer {
	public static final String MOD_ID = "dimensionalkey";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final ResourceKey<Item> DIMENSIONAL_KEY_ID = ResourceKey.create(Registries.ITEM, id("dimensional_key"));
	public static final Item DIMENSIONAL_KEY = Registry.register(
		BuiltInRegistries.ITEM,
		DIMENSIONAL_KEY_ID,
		new DimensionalKeyItem(new Properties().setId(DIMENSIONAL_KEY_ID).stacksTo(1).rarity(Rarity.EPIC).fireResistant().useCooldown(1.0F))
	);

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}

	private static void returnKeysAfterDeath(ServerPlayer oldPlayer, ServerPlayer newPlayer, boolean alive) {
		if (alive || newPlayer.level().getGameRules().get(GameRules.KEEP_INVENTORY)) {
			return;
		}

		Inventory oldInventory = oldPlayer.getInventory();
		Inventory newInventory = newPlayer.getInventory();

		for (int slot = 0; slot < oldInventory.getNonEquipmentItems().size(); slot++) {
			ItemStack stack = oldInventory.getNonEquipmentItems().get(slot);
			if (stack.is(DIMENSIONAL_KEY)) {
				if (newInventory.getItem(slot).isEmpty()) {
					newInventory.setItem(slot, stack.copy());
				} else {
					newInventory.add(stack.copy());
				}
			}
		}
	}

	@Override
	public void onInitialize() {
		PayloadTypeRegistry.clientboundPlay().register(SkyPresetPayload.TYPE, SkyPresetPayload.STREAM_CODEC);
		PayloadTypeRegistry.clientboundPlay().register(TravelFadePayload.TYPE, TravelFadePayload.STREAM_CODEC);
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(output -> output.accept(DIMENSIONAL_KEY));
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> PocketRealm.syncSky(handler.player));
		ServerTickEvents.END_LEVEL_TICK.register(PocketRealm::tickVoidRescue);
		ServerPlayerEvents.COPY_FROM.register(DimensionalKeyMod::returnKeysAfterDeath);
		LOGGER.info("Dimensional Key loaded");
	}
}
