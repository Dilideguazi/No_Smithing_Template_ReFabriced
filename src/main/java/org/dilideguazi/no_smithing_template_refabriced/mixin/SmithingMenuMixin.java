package org.dilideguazi.no_smithing_template_refabriced.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.SmithingRecipe;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SmithingScreenHandler;
import net.minecraft.world.World;
import org.dilideguazi.no_smithing_template_refabriced.Config;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = SmithingScreenHandler.class)
public abstract class SmithingMenuMixin extends ForgingScreenHandler {

    @Shadow @Final private World world;
    @Shadow @Final private List<RecipeEntry<SmithingRecipe>> recipes;
    @Unique @Nullable private SmithingRecipe selectedRecipe;

    @SuppressWarnings("ConstantConditions")
    public SmithingMenuMixin() {
        super(null, 0, null, ScreenHandlerContext.EMPTY);
    }

    @Inject(method = "canTakeOutput", at = @At("HEAD"), cancellable = true)
    protected void onMayPickup(PlayerEntity pPlayer, boolean pHasStack, CallbackInfoReturnable<Boolean> cir) {
        if (this.selectedRecipe != null) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "updateResult", at = @At("HEAD"), cancellable = true)
    private void onCreateResult(CallbackInfo ci) {
        ItemStack template = this.input.getStack(0);
        ItemStack base = this.input.getStack(1);
        ItemStack addition = this.input.getStack(2);

        if (template.isEmpty() && !base.isEmpty() && !addition.isEmpty()) {

            if (Config.isTemplateRequired(base)) {
                return;
            }

            for (RecipeEntry<SmithingRecipe> recipe : this.recipes) {
                if (recipe.value().testBase(base) && recipe.value().testAddition(addition)) {
                    try {
                        ItemStack resultStack = recipe.value().craft(this.input, this.world.getRegistryManager());

                        if (!resultStack.isEmpty() && resultStack.isItemEnabled(this.world.getEnabledFeatures())) {

                            if (Config.isTemplateRequired(resultStack)) {
                                return;
                            }

                            this.selectedRecipe = recipe.value();
                            this.output.setLastRecipe(recipe);
                            this.output.setStack(0, resultStack);
                            ci.cancel();
                            return;
                        }
                    } catch (Exception ignored) {
                    }
                }
            }
        }
    }
}