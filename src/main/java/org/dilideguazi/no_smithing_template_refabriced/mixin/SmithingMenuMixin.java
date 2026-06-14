package org.dilideguazi.no_smithing_template_refabriced.mixin;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.level.Level;
import org.dilideguazi.no_smithing_template_refabriced.Config;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SmithingMenu.class)
public abstract class SmithingMenuMixin extends ItemCombinerMenu {
    @Shadow
    protected abstract SmithingRecipeInput createRecipeInput();

    @Shadow
    @Final
    private Level level;

    public SmithingMenuMixin(@Nullable MenuType<?> menuType, int containerId, Inventory inventory, ContainerLevelAccess access, ItemCombinerMenuSlotDefinition itemInputSlots) {
        super(menuType, containerId, inventory, access, itemInputSlots);
    }

    @Inject(method = "createResult", at = @At("TAIL"))
    public void onCreateResult(CallbackInfo ci) {
        ItemStack template = this.createRecipeInput().template();
        ItemStack base = this.createRecipeInput().base();
        ItemStack resultStack = this.resultSlots.getItem(0);

        if (template.isEmpty()
                && (Config.isTemplateRequired(base) || Config.isTemplateRequired(resultStack))
                || !resultStack.isItemEnabled(this.level.enabledFeatures())) {
            this.resultSlots.setRecipeUsed(null);
            this.resultSlots.setItem(0, ItemStack.EMPTY);
        }
    }
}
