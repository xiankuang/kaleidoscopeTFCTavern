package net.xdpp.kaleidoscopetfctavern;

import com.mojang.logging.LogUtils;
import net.xdpp.kaleidoscopetfctavern.init.ModBlocks;
import net.xdpp.kaleidoscopetfctavern.init.ModItems;
import net.xdpp.kaleidoscopetfctavern.init.WildGrapevineTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

/**
 * Kaleidoscope TFC Tavern 主模组类
 * <p>
 * 负责模组的初始化和组件注册
 * 将 Kaleidoscope Tavern 的葡萄系统与 TerraFirmaCraft 兼容
 */
@Mod(Kaleidoscopetfctavern.MODID)
public class Kaleidoscopetfctavern {

    public static final String MODID = "kaleidoscopetfctavern";
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public Kaleidoscopetfctavern() {
        var modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);

        registerCreativeTabs();

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void registerCreativeTabs() {
        CREATIVE_MODE_TABS.register("main", () -> CreativeModeTab.builder()
                .icon(() -> ModItems.WILD_GRAPEVINE_PURPLE.get().getDefaultInstance())
                .displayItems((parameters, output) -> {
                    output.accept(ModItems.WILD_GRAPEVINE_PURPLE.get());
                    output.accept(ModItems.WILD_GRAPEVINE_RED.get());
                    output.accept(ModItems.WILD_GRAPEVINE_WHITE.get());
                    output.accept(ModItems.WILD_GRAPEVINE_GREEN.get());
                    output.accept(ModItems.GRAPE_PURPLE.get());
                    output.accept(ModItems.GRAPE_RED.get());
                    output.accept(ModItems.GRAPE_WHITE.get());
                    output.accept(ModItems.GRAPE_GREEN.get());
                    output.accept(ModItems.GRAPEVINE_LOCATOR.get());
                })
                .build());
    }

    /**
     * 通用初始化
     * <p>
     * 在模组加载时执行，注册所有葡萄藤类型
     */
    private void commonSetup(final FMLCommonSetupEvent event) {
        ModBlocks.registerTypes();
        LOGGER.info("Registered {} wild grapevine types", WildGrapevineTypes.getTypeCount());
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("Kaleidoscope TFC Tavern loaded successfully!");
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            LOGGER.info("你好，群峦！");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }
}
