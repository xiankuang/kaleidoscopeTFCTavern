package net.xdpp.kaleidoscopetfctavern.item;

import com.github.ysbbbbbb.kaleidoscopetavern.api.blockentity.IBarrel;
import com.github.ysbbbbbb.kaleidoscopetavern.block.brew.DrinkBlock;
import com.github.ysbbbbbb.kaleidoscopetavern.blockentity.brew.DrinkBlockEntity;
import com.github.ysbbbbbb.kaleidoscopetavern.datamap.data.DrinkEffectData;
import com.github.ysbbbbbb.kaleidoscopetavern.datamap.resources.DrinkEffectDataReloadListener;
import com.github.ysbbbbbb.kaleidoscopetavern.item.BottleBlockItem;
import com.github.ysbbbbbb.kaleidoscopetavern.item.IHasContainer;
import com.google.common.collect.Lists;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 自定义饮品方块物品类
 * <p>
 * 扩展KT原版的BottleBlockItem，添加TFC食物系统支持
 * 确保饮用葡萄酒时能正确恢复饱食度、口渴度和获得buff
 */
public class CustomDrinkBlockItem extends BottleBlockItem implements IHasContainer {
    
    /**
     * 构造函数
     * 
     * @param block 关联的饮品方块
     */
    public CustomDrinkBlockItem(Block block) {
        super(block, new Properties().stacksTo(16));
    }
    
    /**
     * 获取默认物品实例
     * <p>
     * 设置默认酿造等级为7
     * 
     * @return 默认物品栈
     */
    @Override
    public ItemStack getDefaultInstance() {
        ItemStack stack = super.getDefaultInstance();
        BottleBlockItem.setBrewLevel(stack, 7);
        return stack;
    }

    /**
     * 获取使用持续时间
     * 
     * @param stack 物品栈
     * @return 使用持续时间（刻）
     */
    @Override
    public int getUseDuration(ItemStack stack) {
        return 32;
    }

    /**
     * 获取使用动画
     * 
     * @param stack 物品栈
     * @return 使用动画类型
     */
    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    /**
     * 在方块上使用物品
     * <p>
     * 处理饮品放置和饮用逻辑
     * 
     * @param context 使用上下文
     * @return 交互结果
     */
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        BlockPos pos = context.getClickedPos();
        ItemStack stack = context.getItemInHand();

        BlockState state = level.getBlockState(pos);
        Block self = this.getBlock();

        if (player == null || player.isShiftKeyDown()) {
            if (player != null && tryIncreaseCount(self, state, level, pos, stack, player)) {
                return InteractionResult.SUCCESS;
            }
            return this.place(new BlockPlaceContext(context));
        }

        InteractionResult result = this.use(level, player, context.getHand()).getResult();
        return result == InteractionResult.CONSUME ? InteractionResult.CONSUME_PARTIAL : result;
    }

    /**
     * 尝试增加方块上的饮品数量
     * 
     * @param self 当前方块
     * @param state 方块状态
     * @param level 世界
     * @param pos 方块位置
     * @param stack 物品栈
     * @param player 玩家
     * @return 是否成功增加
     */
    private boolean tryIncreaseCount(Block self, BlockState state, Level level, BlockPos pos, ItemStack stack, Player player) {
        if (self instanceof DrinkBlock drink && state.is(self) && drink.tryIncreaseCount(level, pos, state, stack)) {
            SoundType soundType = state.getSoundType(level, pos, player);
            SoundEvent sound = this.getPlaceSound(state, level, pos, player);
            level.playSound(
                    player, pos, sound, SoundSource.BLOCKS,
                    (soundType.getVolume() + 1) / 2f,
                    soundType.getPitch() * 0.8f
            );
            if (!player.isCreative()) {
                stack.shrink(1);
            }
            return true;
        }
        return false;
    }

    /**
     * 更新自定义方块实体标签
     * 
     * @param pos 方块位置
     * @param level 世界
     * @param player 玩家
     * @param stack 物品栈
     * @param state 方块状态
     * @return 是否成功更新
     */
    @Override
    protected boolean updateCustomBlockEntityTag(BlockPos pos, Level level, @Nullable Player player, ItemStack stack, BlockState state) {
        if (level.getBlockEntity(pos) instanceof DrinkBlockEntity be && be.addItem(stack)) {
            be.refresh();
        }
        return super.updateCustomBlockEntityTag(pos, level, player, stack, state);
    }

    /**
     * 使用物品
     * 
     * @param level 世界
     * @param player 玩家
     * @param hand 使用的手
     * @return 交互结果持有器
     */
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return ItemUtils.startUsingInstantly(level, player, hand);
    }

    /**
     * 完成物品使用
     * <p>
     * 处理饮用逻辑：调用TFC食物系统、添加饮品效果、返回空瓶
     * 
     * @param stack 物品栈
     * @param level 世界
     * @param entity 使用物品的实体
     * @return 使用后的物品栈
     */
    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (entity instanceof ServerPlayer serverPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, stack);
            serverPlayer.awardStat(Stats.ITEM_USED.get(this));
        }
        
        if (entity instanceof Player player) {
            if (BottleBlockItem.getBrewLevel(stack) <= 0) {
                BottleBlockItem.setBrewLevel(stack, 7);
            }
            
            player.getFoodData().eat(this, stack, player);
        }
        
        this.addDrinkEffect(stack, level, entity);
        
        if (entity instanceof Player player && !player.isCreative()) {
            stack.shrink(1);
        }
        return returnContainerToEntity(stack, level, entity);
    }

    /**
     * 添加饮品效果
     * <p>
     * 根据酿造等级和概率应用效果
     * 
     * @param drink 饮品物品栈
     * @param level 世界
     * @param entity 实体
     */
    protected void addDrinkEffect(ItemStack drink, Level level, LivingEntity entity) {
        DrinkEffectData effectData = DrinkEffectDataReloadListener.INSTANCE.get(drink.getItem());
        if (effectData == null) {
            return;
        }
        var effects = effectData.effects();
        int brewLevel = BottleBlockItem.getBrewLevel(drink);
        if (brewLevel < IBarrel.BREWING_STARTED || brewLevel > effects.size()) {
            return;
        }
        for (DrinkEffectData.Entry entry : effects.get(brewLevel - 1)) {
            if (!level.isClientSide && level.random.nextFloat() < entry.probability()) {
                MobEffect effect = entry.effect();
                int duration = entry.duration() * 20;
                int amplifier = entry.amplifier();
                MobEffectInstance instance = new MobEffectInstance(effect, duration, amplifier);
                entity.addEffect(instance);
            }
        }
    }

    /**
     * 制作投掷药水
     * 
     * @param level 世界
     * @param x X坐标
     * @param y Y坐标
     * @param z Z坐标
     * @param brewLevel 酿造等级
     * @param owner 所有者实体
     */
    public void makeThrownPotion(Level level, double x, double y, double z, int brewLevel, @Nullable Entity owner) {
        DrinkEffectData effectData = DrinkEffectDataReloadListener.INSTANCE.get(this);
        if (effectData == null) {
            return;
        }
        var effects = effectData.effects();
        if (brewLevel < IBarrel.BREWING_STARTED || brewLevel > effects.size()) {
            return;
        }

        List<MobEffectInstance> instances = Lists.newArrayList();
        for (DrinkEffectData.Entry entry : effects.get(brewLevel - 1)) {
            if (level.random.nextFloat() < entry.probability()) {
                MobEffect effect = entry.effect();
                int duration = entry.duration() * 20;
                int amplifier = entry.amplifier();
                instances.add(new MobEffectInstance(effect, duration, amplifier));
            }
        }

        ThrownPotion potion = new ThrownPotion(level, x, y, z);
        if (owner instanceof LivingEntity livingEntity) {
            potion.setOwner(livingEntity);
        }

        ItemStack stack = new ItemStack(this);
        PotionUtils.setCustomEffects(stack, instances);
        potion.setItem(stack);

        level.addFreshEntity(potion);
    }

    /**
     * 获取容器物品
     * <p>
     * 返回KT原版的空瓶
     * 
     * @return 空瓶物品
     */
    @Override
    public Item getContainerItem() {
        return ForgeRegistries.ITEMS.getValue(new ResourceLocation("kaleidoscope_tavern", "empty_bottle"));
    }
}
