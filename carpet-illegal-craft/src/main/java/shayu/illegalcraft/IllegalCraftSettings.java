package shayu.illegalcraft;

import carpet.api.settings.Rule;

/**
 * Carpet 规则定义。字段名即规则名，规则描述来自 lang 文件里的
 * {@code illegalcraft.rule.<规则名>.desc}。
 */
public final class IllegalCraftSettings {
	public static final String CATEGORY = "illegalcraft";

	private IllegalCraftSettings() {
	}

	/** 总开关：关掉时所有违禁方块都不能合成。 */
	@Rule(categories = {CATEGORY})
	public static boolean illegalBlockCraft = false;

	@Rule(categories = {CATEGORY})
	public static boolean craftCommandBlock = true;

	@Rule(categories = {CATEGORY})
	public static boolean craftChainCommandBlock = true;

	@Rule(categories = {CATEGORY})
	public static boolean craftRepeatingCommandBlock = true;

	@Rule(categories = {CATEGORY})
	public static boolean craftStructureBlock = true;

	@Rule(categories = {CATEGORY})
	public static boolean craftStructureVoid = true;

	@Rule(categories = {CATEGORY})
	public static boolean craftBarrier = true;

	@Rule(categories = {CATEGORY})
	public static boolean craftLightBlock = true;

	@Rule(categories = {CATEGORY})
	public static boolean craftJigsaw = true;

	@Rule(categories = {CATEGORY})
	public static boolean craftBedrock = true;

	@Rule(categories = {CATEGORY})
	public static boolean craftSpawner = true;

	@Rule(categories = {CATEGORY})
	public static boolean craftEndPortalFrame = true;

	@Rule(categories = {CATEGORY})
	public static boolean craftReinforcedDeepslate = true;
}
