package org.dilideguazi.no_smithing_template_refabriced.mixin;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.input.SmithingRecipeInput;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.SmithingScreenHandler;
import net.minecraft.screen.slot.ForgingSlotsManager;
import net.minecraft.world.World;
import org.dilideguazi.no_smithing_template_refabriced.Config;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SmithingScreenHandler.class)
public abstract class SmithingScreenHandlerMixin extends ForgingScreenHandler {
    @Shadow
    protected abstract SmithingRecipeInput createRecipeInput();

    @Shadow
    @Final
    private World world;

    public SmithingScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context, ForgingSlotsManager forgingSlotsManager) {
        super(type, syncId, playerInventory, context, forgingSlotsManager);
    }

    @Inject(method = "updateResult", at = @At("TAIL"))
    public void onUpdateResult(CallbackInfo ci) {
        ItemStack template = this.createRecipeInput().template();
        ItemStack base = this.createRecipeInput().base();
        ItemStack resultStack = this.output.getStack(0);

        if (template.isEmpty()
                && (Config.isTemplateRequired(base) || Config.isTemplateRequired(resultStack))
                || !resultStack.isItemEnabled(this.world.getEnabledFeatures())) {
            this.output.setLastRecipe(null);
            this.output.setStack(0, ItemStack.EMPTY);
        }
    }
}
