package net.xdpp.kaleidoscopetfctavern.block.properties;

import net.minecraft.util.StringRepresentable;

import java.util.Locale;

/**
 * 葡萄藤类型枚举
 * <p>
 * 用于区分不同的葡萄藤品种
 */
public enum GrapevineType implements StringRepresentable {
    PURPLE,
    RED,
    WHITE,
    GREEN;

    @Override
    public String getSerializedName() {
        return this.name().toLowerCase(Locale.ENGLISH);
    }

    @Override
    public String toString() {
        return this.getSerializedName();
    }
}
