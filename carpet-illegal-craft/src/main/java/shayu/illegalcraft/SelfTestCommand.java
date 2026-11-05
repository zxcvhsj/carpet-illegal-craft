package shayu.illegalcraft;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.List;
import java.util.Optional;

/** 自检：拿两条代表性配方去查一次，看拦截有没有生效。 */
public final class SelfTestCommand {
	private SelfTestCommand() {
	}

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("illegalcraft-test")
				.requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
				.executes(context -> run(context.getSource())));
	}

	public static int run(CommandSourceStack source) {
		check(source, "command_block", Items.OBSIDIAN, Items.REDSTONE_BLOCK, Items.GOLD_INGOT);
		check(source, "end_portal_frame", Items.END_STONE_BRICKS, Items.ENDER_EYE, Items.END_STONE_BRICKS);
		return 1;
	}

	private static void check(CommandSourceStack source, String label, Item corner, Item center, Item edge) {
		CraftingInput input = CraftingInput.of(3, 3, List.of(
				new ItemStack(corner), new ItemStack(edge), new ItemStack(corner),
				new ItemStack(edge), new ItemStack(center), new ItemStack(edge),
				new ItemStack(corner), new ItemStack(edge), new ItemStack(corner)));

		Optional<RecipeHolder<CraftingRecipe>> found = source.getServer().getRecipeManager()
				.getRecipeFor(RecipeType.CRAFTING, input, source.getLevel(), (RecipeHolder<CraftingRecipe>) null);

		String recipeId = found.map(holder -> holder.id().identifier().toString()).orElse("(none)");
		String gate = found.map(holder -> String.valueOf(IllegalCraftRules.isRecipeAllowed(holder))).orElse("n/a");
		String enabled = String.valueOf(IllegalCraftSettings.illegalBlockCraft);

		source.sendSuccess(() -> Component.literal(
				"[" + label + "] lookup=" + recipeId + " gate=" + gate + " illegalBlockCraft=" + enabled), false);
	}
}
