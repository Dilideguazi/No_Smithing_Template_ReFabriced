package org.dilideguazi.no_smithing_template_refabriced.mixin;

import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(RecipeManager.class)
public class RecipeManagerMixin {
    @Shadow
    private RecipeMap recipes;

    @Inject(method = "getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;)Ljava/util/Optional;",
            at = @At("HEAD"), cancellable = true)
    public <I extends RecipeInput, T extends Recipe<I>> void onGetFirstMatch(RecipeType<T> type, I input, Level level,
                                                                             CallbackInfoReturnable<Optional<RecipeHolder<T>>> cir) {
        if (type == RecipeType.SMITHING) {
            for (RecipeHolder<?> recipe : this.recipes.values()) {
                if (recipe.value() instanceof SmithingTransformRecipe
                        && input instanceof SmithingRecipeInput
                        && ((SmithingRecipeInput) input).template().isEmpty()
                        && ((SmithingTransformRecipeAccessor) recipe.value()).getBase()
                        .test(((SmithingRecipeInput) input).base())
                        && Ingredient.testOptionalIngredient(((SmithingTransformRecipeAccessor) recipe.value()).getAddition(),
                        ((SmithingRecipeInput) input).addition())) {
                    @SuppressWarnings("unchecked")
                    Optional<RecipeHolder<T>> result = Optional.of((RecipeHolder<T>) recipe);
                    cir.setReturnValue(result);
                    cir.cancel();
                    return;
                }
            }
        }
    }
}
