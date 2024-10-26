package dev.carrot_on_a_stick.mixin;

import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ItemSteerable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FoodOnAStickItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = FoodOnAStickItem.class)
public class FoodOnAStickItemMixin<T extends Entity & ItemSteerable> extends Item {

  @Mutable
  @Final
  @Shadow
  private final EntityType<T> canInteractWith;

  @Mutable
  @Final
  @Shadow
  private final int consumeItemDamage;

  public FoodOnAStickItemMixin(Item.Properties properties, EntityType<T> canInteractWith, int consumeItemDamage) {
    super(properties);
    this.canInteractWith = canInteractWith;
    this.consumeItemDamage = consumeItemDamage;
  }

  /**
   * @author STL
   * @reason cap consume damage amount to 1
   */
  @Overwrite
  public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand usedHand) {
    ItemStack itemStack = player.getItemInHand(usedHand);
    if (!level.isClientSide) {
      Entity entity = player.getControlledVehicle();
      if (player.isPassenger() && entity instanceof ItemSteerable itemSteerable && entity.getType() == this.canInteractWith && itemSteerable.boost()) {
        EquipmentSlot equipmentSlot = LivingEntity.getSlotForHand(usedHand);
        ItemStack itemStack2 = itemStack.hurtAndConvertOnBreak(1, Items.FISHING_ROD, player, equipmentSlot);
        return InteractionResultHolder.success(itemStack2);
      }

      player.awardStat(Stats.ITEM_USED.get(this));
    }
    return InteractionResultHolder.pass(itemStack);
  }
}