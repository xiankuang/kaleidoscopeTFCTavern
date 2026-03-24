package net.xdpp.kaleidoscopetfctavern.init;

import net.xdpp.kaleidoscopetfctavern.block.plant.WildGrapevineType;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 野生葡萄藤类型注册表
 * <p>
 * 管理所有已注册的野生葡萄藤类型
 * 支持动态注册和随机选择，添加新类型时概率会自动平分
 */
public class WildGrapevineTypes {
    private static final List<WildGrapevineType> TYPES = new ArrayList<>();
    private static final Random RANDOM = new Random();

    public static void register(WildGrapevineType type) {
        TYPES.add(type);
    }

    public static WildGrapevineType getRandomType() {
        if (TYPES.isEmpty()) {
            return null;
        }
        return TYPES.get(RANDOM.nextInt(TYPES.size()));
    }

    public static List<WildGrapevineType> getAllTypes() {
        return new ArrayList<>(TYPES);
    }

    public static int getTypeCount() {
        return TYPES.size();
    }
}
