package org.dilideguazi.no_smithing_template_refabriced.mixin;

import net.minecraft.recipe.*;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.recipe.input.SmithingRecipeInput;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(ServerRecipeManager.class)
public class ServerRecipeManagerMixin {
    @Shadow
    private PreparedRecipes preparedRecipes;

    @Inject(method = "getFirstMatch(Lnet/minecraft/recipe/RecipeType;Lnet/minecraft/recipe/input/RecipeInput;Lnet/minecraft/world/World;)Ljava/util/Optional;",
            at = @At("HEAD"), cancellable = true)
    public <I extends RecipeInput, T extends Recipe<I>> void onGetFirstMatch(RecipeType<T> recipeType, I input, World world,
                                                                             CallbackInfoReturnable<Optional<RecipeEntry<T>>> cir) {
        if (recipeType == RecipeType.SMITHING) {
            for (RecipeEntry<?> recipe : this.preparedRecipes.recipes()) {
                if (recipe.value() instanceof SmithingTransformRecipe
                        && input instanceof SmithingRecipeInput
                        && ((SmithingTransformRecipe) recipe.value()).base()
                        .test(((SmithingRecipeInput) input).base())
                        && Ingredient.matches(((SmithingTransformRecipe) recipe.value()).addition(),
                        ((SmithingRecipeInput) input).addition())) {
                    @SuppressWarnings("unchecked")
                    Optional<RecipeEntry<T>> result = Optional.of((RecipeEntry<T>) recipe);
                    cir.setReturnValue(result);
                    cir.cancel();
                    return;
                }
            }
        }
    }
}
