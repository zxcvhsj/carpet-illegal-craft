package shayu.illegalcraft;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeHolder;

/**
 * 判断某个配方当前是否允许被合成。
 * <p>
 * 只有命名空间是本模组的配方会被拦截，原版和其它模组的配方一律放行。
 */
public final class IllegalCraftRules {
	private IllegalCraftRules() {
	}

	public static boolean isRecipeAllowed(RecipeHolder<?> holder) {
		if (holder == null) {
			return true;
		}

		Identifier id = holder.id().identifier();
		if (!IllegalCraftMod.MOD_ID.equals(id.getNamespace())) {
			return true;
		}

		if (!IllegalCraftSettings.illegalBlockCraft) {
			return false;
		}

		return switch (id.getPath()) {
			case "command_block" -> IllegalCraftSettings.craftCommandBlock;
			case "chain_command_block" -> IllegalCraftSettings.craftChainCommandBlock;
			case "repeating_command_block" -> IllegalCraftSettings.craftRepeatingCommandBlock;
			case "structure_block" -> IllegalCraftSettings.craftStructureBlock;
			case "structure_void" -> IllegalCraftSettings.craftStructureVoid;
			case "barrier" -> IllegalCraftSettings.craftBarrier;
			case "light" -> IllegalCraftSettings.craftLightBlock;
			case "jigsaw" -> IllegalCraftSettings.craftJigsaw;
			case "bedrock" -> IllegalCraftSettings.craftBedrock;
			case "spawner" -> IllegalCraftSettings.craftSpawner;
			case "end_portal_frame" -> IllegalCraftSettings.craftEndPortalFrame;
			case "reinforced_deepslate" -> IllegalCraftSettings.craftReinforcedDeepslate;
			default -> true;
		};
	}
}
