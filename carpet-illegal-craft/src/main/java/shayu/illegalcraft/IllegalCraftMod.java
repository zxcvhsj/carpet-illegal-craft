package shayu.illegalcraft;

import carpet.CarpetServer;
import net.fabricmc.api.ModInitializer;

public class IllegalCraftMod implements ModInitializer {
	public static final String MOD_ID = "illegalcraft";
	public static final String MOD_NAME = "Carpet Illegal Craft";
	public static final String VERSION = "1.0.0";

	@Override
	public void onInitialize() {
		CarpetServer.manageExtension(new IllegalCraftExtension());
	}
}
