package shayu.illegalcraft;

import carpet.CarpetServer;
import carpet.api.settings.CarpetRule;
import carpet.api.settings.RuleHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * {@code /illegalcraft}：只列本模组的规则，带可点的 [true] / [false] 按钮。
 * 规则本体住在 Carpet 的规则表里，所以 {@code /carpet <规则> <值>} 一样能用。
 */
public final class IllegalCraftCommand {
	private IllegalCraftCommand() {
	}

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("illegalcraft")
				.requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
				.executes(context -> list(context.getSource()))
				.then(Commands.literal("list").executes(context -> list(context.getSource())))
				.then(Commands.literal("test").executes(context -> SelfTestCommand.run(context.getSource())))
				.then(Commands.argument("rule", StringArgumentType.word())
						.suggests((context, builder) -> {
							for (String name : ruleNames()) {
								builder.suggest(name);
							}
							return builder.buildFuture();
						})
						.executes(context -> forward(context.getSource(), StringArgumentType.getString(context, "rule")))
						.then(Commands.argument("value", StringArgumentType.greedyString())
								.executes(context -> forward(context.getSource(),
										StringArgumentType.getString(context, "rule") + " "
												+ StringArgumentType.getString(context, "value"))))));
	}

	private static List<CarpetRule<?>> ourRules() {
		List<CarpetRule<?>> rules = new ArrayList<>();
		if (CarpetServer.settingsManager == null) {
			return rules;
		}
		for (CarpetRule<?> rule : CarpetServer.settingsManager.getCarpetRules()) {
			if (rule.categories().contains(IllegalCraftSettings.CATEGORY)) {
				rules.add(rule);
			}
		}
		rules.sort(Comparator.comparing((CarpetRule<?> rule) -> !rule.name().equals("illegalBlockCraft"))
				.thenComparing(CarpetRule::name));
		return rules;
	}

	private static List<String> ruleNames() {
		return ourRules().stream().map(CarpetRule::name).toList();
	}

	private static int list(CommandSourceStack source) {
		List<CarpetRule<?>> rules = ourRules();
		if (rules.isEmpty()) {
			source.sendSuccess(() -> Component.literal("违禁合成：规则还没注册好，稍后再试").withStyle(ChatFormatting.RED), false);
			return 0;
		}

		Component header = Component.literal("违禁方块合成（点方块名看配方，点 [true]/[false] 切换）")
				.withStyle(ChatFormatting.GOLD);
		source.sendSuccess(() -> header, false);

		if (!IllegalCraftSettings.illegalBlockCraft) {
			Component warn = Component.literal("⚠ 总开关关着：现在所有违禁配方都合不出来，先点下面第一行的 [true]")
					.withStyle(ChatFormatting.RED);
			source.sendSuccess(() -> warn, false);
		}

		for (CarpetRule<?> rule : rules) {
			String current = RuleHelper.toRuleString(rule.value());

			MutableComponent line = Component.literal("- ")
					.append(Component.literal(RuleHelper.translatedName(rule))
							.withStyle(Style.EMPTY.withColor(ChatFormatting.WHITE)
									.withClickEvent(new ClickEvent.RunCommand("/carpet " + rule.name()))
									.withHoverEvent(new HoverEvent.ShowText(
											Component.literal(RuleHelper.translatedDescription(rule))))))
					.append(Component.literal(" "));

			for (String option : rule.suggestions()) {
				boolean active = option.equalsIgnoreCase(current);
				Style style = Style.EMPTY
						.withColor(active ? ChatFormatting.GREEN : ChatFormatting.DARK_GRAY)
						.withClickEvent(new ClickEvent.RunCommand("/carpet " + rule.name() + " " + option))
						.withHoverEvent(new HoverEvent.ShowText(Component.literal("把 "
								+ rule.name() + " 设为 " + option)));
				if (active) {
					style = style.withUnderlined(true);
				}
				line.append(Component.literal("[" + option + "]").withStyle(style)).append(Component.literal(" "));
			}

			Component message = line;
			source.sendSuccess(() -> message, false);
		}
		return rules.size();
	}

	private static int forward(CommandSourceStack source, String rest) {
		source.getServer().getCommands().performPrefixedCommand(source, "carpet " + rest);
		return 1;
	}
}
