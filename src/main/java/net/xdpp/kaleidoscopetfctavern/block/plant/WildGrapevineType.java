package net.xdpp.kaleidoscopetfctavern.block.plant;

import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;

/**
 * 野生葡萄藤类型封装类
 * <p>
 * 用于封装一种野生葡萄藤的名称、头部方块和主体方块
 * 支持动态注册和随机选择
 */
public class WildGrapevineType {
    private final String name;
    private final RegistryObject<Block> headBlock;
    private final RegistryObject<Block> bodyBlock;

    public WildGrapevineType(String name, RegistryObject<Block> headBlock, RegistryObject<Block> bodyBlock) {
        this.name = name;
        this.headBlock = headBlock;
        this.bodyBlock = bodyBlock;
    }

    public String getName() {
        return name;
    }

    public RegistryObject<Block> getHeadBlock() {
        return headBlock;
    }

    public RegistryObject<Block> getBodyBlock() {
        return bodyBlock;
    }
}
