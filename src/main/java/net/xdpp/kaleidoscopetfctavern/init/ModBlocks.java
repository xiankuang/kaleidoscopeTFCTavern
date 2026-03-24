package net.xdpp.kaleidoscopetfctavern.init;

import net.xdpp.kaleidoscopetfctavern.Kaleidoscopetfctavern;
import net.xdpp.kaleidoscopetfctavern.block.plant.BaseWildGrapevineBlock;
import net.xdpp.kaleidoscopetfctavern.block.plant.BaseWildGrapevinePlantBlock;
import net.xdpp.kaleidoscopetfctavern.block.plant.GrapeCropBlockPurple;
import net.xdpp.kaleidoscopetfctavern.block.plant.GrapeCropBlockRed;
import net.xdpp.kaleidoscopetfctavern.block.plant.GrapeCropBlockWhite;
import net.xdpp.kaleidoscopetfctavern.block.plant.GrapeCropBlockGreen;
import net.xdpp.kaleidoscopetfctavern.block.plant.TFCGrapevineTrellisBlock;
import net.xdpp.kaleidoscopetfctavern.block.plant.WildGrapevineType;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * 方块注册类
 * <p>
 * 注册所有野生葡萄藤相关的方块
 * 使用 Holder 模式解决头部方块和主体方块之间的循环依赖问题
 */
public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Kaleidoscopetfctavern.MODID);

    /**
     * 方块持有者
     * <p>
     * 用于解决头部方块和主体方块之间的循环依赖
     * 通过 Supplier 延迟获取方块实例
     */
    private static class BlockHolder {
        RegistryObject<Block> head;
        RegistryObject<Block> body;

        Block getHead() {
            return head.get();
        }

        Block getBody() {
            return body.get();
        }
    }

    public static final RegistryObject<Block> WILD_GRAPEVINE_PURPLE;
    public static final RegistryObject<Block> WILD_GRAPEVINE_PLANT_PURPLE;

    public static final RegistryObject<Block> WILD_GRAPEVINE_RED;
    public static final RegistryObject<Block> WILD_GRAPEVINE_PLANT_RED;

    public static final RegistryObject<Block> WILD_GRAPEVINE_WHITE;
    public static final RegistryObject<Block> WILD_GRAPEVINE_PLANT_WHITE;

    public static final RegistryObject<Block> WILD_GRAPEVINE_GREEN;
    public static final RegistryObject<Block> WILD_GRAPEVINE_PLANT_GREEN;

    public static final RegistryObject<Block> GRAPE_CROP_PURPLE;
    public static final RegistryObject<Block> GRAPE_CROP_RED;
    public static final RegistryObject<Block> GRAPE_CROP_WHITE;
    public static final RegistryObject<Block> GRAPE_CROP_GREEN;
    public static final RegistryObject<Block> TFC_GRAPEVINE_TRELLIS;

    static {
        BlockHolder holderPurple = new BlockHolder();
        WILD_GRAPEVINE_PURPLE = BLOCKS.register("wild_grapevine_purple",
                () -> new BaseWildGrapevineBlock(holderPurple::getBody));
        WILD_GRAPEVINE_PLANT_PURPLE = BLOCKS.register("wild_grapevine_plant_purple",
                () -> new BaseWildGrapevinePlantBlock(holderPurple::getHead));
        holderPurple.head = WILD_GRAPEVINE_PURPLE;
        holderPurple.body = WILD_GRAPEVINE_PLANT_PURPLE;

        BlockHolder holderRed = new BlockHolder();
        WILD_GRAPEVINE_RED = BLOCKS.register("wild_grapevine_red",
                () -> new BaseWildGrapevineBlock(holderRed::getBody));
        WILD_GRAPEVINE_PLANT_RED = BLOCKS.register("wild_grapevine_plant_red",
                () -> new BaseWildGrapevinePlantBlock(holderRed::getHead));
        holderRed.head = WILD_GRAPEVINE_RED;
        holderRed.body = WILD_GRAPEVINE_PLANT_RED;

        BlockHolder holderWhite = new BlockHolder();
        WILD_GRAPEVINE_WHITE = BLOCKS.register("wild_grapevine_white",
                () -> new BaseWildGrapevineBlock(holderWhite::getBody));
        WILD_GRAPEVINE_PLANT_WHITE = BLOCKS.register("wild_grapevine_plant_white",
                () -> new BaseWildGrapevinePlantBlock(holderWhite::getHead));
        holderWhite.head = WILD_GRAPEVINE_WHITE;
        holderWhite.body = WILD_GRAPEVINE_PLANT_WHITE;

        BlockHolder holderGreen = new BlockHolder();
        WILD_GRAPEVINE_GREEN = BLOCKS.register("wild_grapevine_green",
                () -> new BaseWildGrapevineBlock(holderGreen::getBody));
        WILD_GRAPEVINE_PLANT_GREEN = BLOCKS.register("wild_grapevine_plant_green",
                () -> new BaseWildGrapevinePlantBlock(holderGreen::getHead));
        holderGreen.head = WILD_GRAPEVINE_GREEN;
        holderGreen.body = WILD_GRAPEVINE_PLANT_GREEN;

        GRAPE_CROP_PURPLE = BLOCKS.register("grape_crop_purple", GrapeCropBlockPurple::new);
        GRAPE_CROP_RED = BLOCKS.register("grape_crop_red", GrapeCropBlockRed::new);
        GRAPE_CROP_WHITE = BLOCKS.register("grape_crop_white", GrapeCropBlockWhite::new);
        GRAPE_CROP_GREEN = BLOCKS.register("grape_crop_green", GrapeCropBlockGreen::new);
        TFC_GRAPEVINE_TRELLIS = BLOCKS.register("tfc_grapevine_trellis", TFCGrapevineTrellisBlock::new);
    }

    /**
     * 注册葡萄藤类型
     * <p>
     * 将所有注册的葡萄藤方块注册到 WildGrapevineTypes 注册表中
     * 以便在世界生成时能够随机选择
     */
    public static void registerTypes() {
        WildGrapevineTypes.register(new WildGrapevineType("purple", WILD_GRAPEVINE_PURPLE, WILD_GRAPEVINE_PLANT_PURPLE));
        WildGrapevineTypes.register(new WildGrapevineType("red", WILD_GRAPEVINE_RED, WILD_GRAPEVINE_PLANT_RED));
        WildGrapevineTypes.register(new WildGrapevineType("white", WILD_GRAPEVINE_WHITE, WILD_GRAPEVINE_PLANT_WHITE));
        WildGrapevineTypes.register(new WildGrapevineType("green", WILD_GRAPEVINE_GREEN, WILD_GRAPEVINE_PLANT_GREEN));
    }
}
