package com.dimensionalkey.mixin;

import com.dimensionalkey.DimensionalKeyMod;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Inventory.class)
public abstract class InventoryMixin {
	@Shadow
	@Final
	private NonNullList<ItemStack> items;
	@Shadow
	@Final
	public Player player;

	@Inject(method = "dropAll", at = @At("HEAD"))
	private void dimensionalkey$stowOffhandKey(CallbackInfo ci) {
		ItemStack offhand = this.player.getItemBySlot(EquipmentSlot.OFFHAND);
		if (offhand.is(DimensionalKeyMod.DIMENSIONAL_KEY)) {
			int free = this.items.indexOf(ItemStack.EMPTY);
			if (free >= 0) {
				this.items.set(free, offhand);
				this.player.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
			}
		}
	}

	@ModifyExpressionValue(method = "dropAll", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/NonNullList;get(I)Ljava/lang/Object;"))
	private Object dimensionalkey$keepKeys(Object stack) {
		return ((ItemStack)stack).is(DimensionalKeyMod.DIMENSIONAL_KEY) ? ItemStack.EMPTY : stack;
	}
}
