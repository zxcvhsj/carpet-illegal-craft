# Carpet Illegal Craft

> 让「违禁方块」也能合成，并且每一条配方都能用 Carpet 规则单独开关。

一个 [Fabric](https://fabricmc.net/) 的 Carpet 附属模组，给命令方块、屏障、结构方块、
基岩、刷怪笼这些平时拿不到的方块加上合成配方。默认全部关闭，用 Carpet 规则按需
打开；关掉之后配方会被真正拦截，工作台产物格是空的，连 shift 连点也刷不出来。

* Minecraft：26.2（Java 25）
* 前置：[Fabric Loader](https://fabricmc.net/use/installer/) ≥ 0.19.4、[Fabric API](https://modrinth.com/mod/fabric-api)、[Carpet](https://modrinth.com/mod/carpet) ≥ 26.2
* 许可：MIT

## 安装

1. 装好 Fabric Loader 26.2 和 [Fabric API](https://modrinth.com/mod/fabric-api)。
2. 把 [Carpet](https://modrinth.com/mod/carpet) 和本模组的 `carpet-illegal-craft-1.0.0.jar`
   一起放进 `mods/`。
3. 启动游戏，进存档后执行 `/illegalcraft list` 就能看到开关列表。

## 用法

规则直接注册进 Carpet 的规则表，所以有两种等价写法：

```mcfunction
/illegalcraft list                      # 列出本模组所有规则，带可点的 [true]/[false]
/illegalcraft illegalBlockCraft true    # 打开总开关
/carpet illegalBlockCraft true          # 效果相同
/carpet craftBarrier true               # 只放开屏障
/illegalcraft-test                      # 自检：查两条代表性配方有没有被正确拦截
```

> 总开关 `illegalBlockCraft` 默认是 `false`：不开它，下面所有配方都合不出来。
> 打开之后，再由每条规则单独决定哪个方块能合成。

需要权限等级 2（Game Master）以上。

## 规则与配方

| 规则 | 产物 | 配方（3×3） | 数量 |
| --- | --- | --- | --- |
| `illegalBlockCraft` | — | 总开关，必须先开 | — |
| `craftCommandBlock` | 命令方块 | 黑曜石×4 + 金锭×4 + 红石块×1 | 1 |
| `craftChainCommandBlock` | 连锁命令方块 | 黑曜石×4 + 紫水晶碎片×4 + 红石块×1 | 1 |
| `craftRepeatingCommandBlock` | 循环命令方块 | 黑曜石×4 + 石英×4 + 红石块×1 | 1 |
| `craftStructureBlock` | 结构方块 | 铁块×4 + 钻石×4 + 末影之眼×1 | 1 |
| `craftStructureVoid` | 结构空位 | 玻璃×4 + 钻石×1（无序） | 8 |
| `craftBarrier` | 屏障 | 黑曜石×4 + 铁块×4 + 钻石×1 | 4 |
| `craftLightBlock` | 光源方块 | 荧石×4 + 玻璃×4 + 钻石×1 | 8 |
| `craftJigsaw` | 拼图方块 | 深板岩瓦×4 + 末影珍珠×4 + 钻石×1 | 1 |
| `craftBedrock` | 基岩 | 黑曜石×8 + 下界合金碎片×1 | 1 |
| `craftSpawner` | 刷怪笼 | 铁块×4 + 铁栏杆×4 + 灵魂沙×1 | 1 |
| `craftEndPortalFrame` | 末地传送门框架 | 末地石砖×8 + 末影之眼×1 | 1 |
| `craftReinforcedDeepslate` | 强化深板岩 | 深板岩×8 + 钻石×1 | 1 |

配方定义在 [`src/main/resources/data/illegalcraft/recipe/`](src/main/resources/data/illegalcraft/recipe)，
每加一个方块就是「一个 json + 一条 `@Rule` + `IllegalCraftRules` 里一个 `case`」。

## 实现方式

* `IllegalCraftSettings` 用 Carpet 的 `@Rule` 声明规则，`IllegalCraftExtension#onGameStarted`
  把它们注册进 Carpet 的 `SettingsManager`，所以 `/carpet list` 里也能看到。
* `RecipeManagerMixin` 注入 `RecipeManager#getRecipeFor`，在返回处过滤掉被关闭的配方，
  工作台查不到配方，产物格自然为空。
* `IllegalCraftRules` 是唯一的判定入口：只拦命名空间为 `illegalcraft` 的配方，
  原版和其它模组的配方一律放行。

## 从源码构建

需要 JDK 25。

```bash
./gradlew build
```

产物在 `build/libs/`：`carpet-illegal-craft-1.0.0.jar` 和 `carpet-illegal-craft-1.0.0-sources.jar`。

开发时可以用 `./gradlew runClient` / `runServer`（需要先在 `gradle.properties` 里确认版本号）。

### 国内构建慢？

Gradle 默认从 `services.gradle.org` 下载分发包，可以换成镜像：把
`gradle/wrapper/gradle-wrapper.properties` 里的 `distributionUrl` 改成

```properties
distributionUrl=https\://mirrors.cloud.tencent.com/gradle/gradle-9.8.2-bin.zip
```

## 目录结构

```
src/main/java/shayu/illegalcraft/
├── IllegalCraftMod.java          # 入口，注册 Carpet extension
├── IllegalCraftExtension.java    # 规则注册、命令注册、语言文件
├── IllegalCraftSettings.java     # @Rule 规则定义
├── IllegalCraftRules.java        # 配方是否放行的判定
├── IllegalCraftCommand.java      # /illegalcraft 命令
├── SelfTestCommand.java          # /illegalcraft-test 自检
└── mixin/RecipeManagerMixin.java # 配方查询拦截
```

## 许可

[MIT](LICENSE) © 2026 shayu
