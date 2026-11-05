package shayu.illegalcraft;

import carpet.CarpetExtension;
import carpet.CarpetServer;
import carpet.api.settings.SettingsManager;
import carpet.utils.Translations;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;

public class IllegalCraftExtension implements CarpetExtension {
	private static final Logger LOGGER = LoggerFactory.getLogger("illegalcraft");
	private boolean registered;

	@Override
	public String version() {
		return IllegalCraftMod.VERSION;
	}

	/**
	 * 规则直接注册进 Carpet 自己的规则表，这样和原版 Carpet 规则一起出现在
	 * {@code /carpet list} 里，用 {@code /carpet illegalBlockCraft true} 开关。
	 */
	@Override
	public void onGameStarted() {
		registerRules();
	}

	private void registerRules() {
		if (registered) {
			return;
		}
		SettingsManager manager = CarpetServer.settingsManager;
		if (manager == null) {
			return;
		}
		manager.parseSettingsClass(IllegalCraftSettings.class);
		registered = true;
	}

	@Override
	public void onServerLoadedWorlds(MinecraftServer server) {
		if (!registered) {
			registerRules();
			if (!registered) {
				LOGGER.warn("carpet settings manager was not available, rules were not registered");
			}
		}
		reportRecipeStatus(server);
	}

	@Override
	public void onReload(MinecraftServer server) {
		reportRecipeStatus(server);
	}

	private void reportRecipeStatus(MinecraftServer server) {
		int total = 0;
		int enabled = 0;
		for (RecipeHolder<?> holder : server.getRecipeManager().getRecipes()) {
			if (!IllegalCraftMod.MOD_ID.equals(holder.id().identifier().getNamespace())) {
				continue;
			}
			total++;
			if (IllegalCraftRules.isRecipeAllowed(holder)) {
				enabled++;
			}
		}
		LOGGER.info("loaded {} recipes, {} enabled (illegalBlockCraft={})", total, enabled,
				IllegalCraftSettings.illegalBlockCraft);
	}

	@Override
	public void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext) {
		IllegalCraftCommand.register(dispatcher);
		SelfTestCommand.register(dispatcher);
	}

	/** 规则文本固定走中文，避免服务器 Carpet language 是 en_us 时显示成英文。 */
	@Override
	public Map<String, String> canHasTranslations(String lang) {
		Map<String, String> translations = Translations.getTranslationFromResourcePath(
				"assets/" + IllegalCraftMod.MOD_ID + "/lang/zh_cn.json");
		if (translations == null || translations.isEmpty()) {
			translations = Translations.getTranslationFromResourcePath(
					"assets/" + IllegalCraftMod.MOD_ID + "/lang/en_us.json");
		}
		return translations == null ? Collections.emptyMap() : translations;
	}
}
