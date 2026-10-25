package com.dimensionalkey;

import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

public class DimensionalKeyItem extends Item {
	public DimensionalKeyItem(Item.Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		if (player instanceof ServerPlayer serverPlayer) {
			if (player.isShiftKeyDown()) {
				PocketRealm.cycleSky(serverPlayer);
			} else {
				PocketRealm.travel(serverPlayer);
			}
		}

		return InteractionResult.SUCCESS;
	}

	@Override
	public boolean isFoil(ItemStack itemStack) {
		return true;
	}

	@Override
	public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {
		builder.accept(Component.translatable("item.dimensionalkey.dimensional_key.tooltip.use").withStyle(ChatFormatting.GRAY));
		builder.accept(Component.translatable("item.dimensionalkey.dimensional_key.tooltip.sneak").withStyle(ChatFormatting.GRAY));
	}
}
