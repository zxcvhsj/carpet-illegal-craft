package shayu.illegalcraft.mixin;

import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import shayu.illegalcraft.IllegalCraftRules;

import java.util.Optional;

/**
 * 在配方查询处拦截被关闭的违禁配方：这样工作台的产物格会直接是空的，
 * 正常点击和 shift 连续合成都不会产出东西。
 */
@Mixin(RecipeManager.class)
public class RecipeManagerMixin {
	@Inject(
			method = "getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/crafting/RecipeHolder;)Ljava/util/Optional;",
			at = @At("RETURN"),
			cancellable = true
	)
	private void illegalcraft$filterDisabledRecipes(RecipeType<?> type, RecipeInput input, Level level,
			RecipeHolder<?> preferred, CallbackInfoReturnable<Optional<RecipeHolder<?>>> cir) {
		Optional<RecipeHolder<?>> found = cir.getReturnValue();
		if (found != null && found.isPresent() && !IllegalCraftRules.isRecipeAllowed(found.get())) {
			cir.setReturnValue(Optional.empty());
		}
	}
}
