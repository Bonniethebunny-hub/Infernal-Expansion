package com.nekomaster1000.infernalexp.entities;

import com.nekomaster1000.infernalexp.util.RegistryHandler;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.CaveSpiderEntity;
import net.minecraft.entity.monster.SpiderEntity;
import net.minecraft.entity.monster.piglin.PiglinBruteEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.UUID;

public class CerobeetleEntity extends CreatureEntity {

    public float shellRotationMultiplier = 0.0F;

    public CerobeetleEntity(EntityType<? extends CreatureEntity> type, World worldIn) {
        super(type, worldIn);
    }

    private static final RangedInteger RANGED_INT = TickRangeConverter.convertRange(20, 39);
    private int attackTimer;
    private int angerTime;
    private UUID angerTarget;

    //ATTRIBUTES
    public static AttributeModifierMap.MutableAttribute setCustomAttributes() {
        return MobEntity.func_233666_p_()
                .createMutableAttribute(Attributes.MAX_HEALTH, 10.0D)
                .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.4D)
                .createMutableAttribute(Attributes.ATTACK_DAMAGE, 3.0D)
                .createMutableAttribute(Attributes.ATTACK_KNOCKBACK, 4.0D);
    }

    //BEHAVIOUR
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new MeleeAttackGoal(this, 0.6D, true));
        this.goalSelector.addGoal(1, new SwimGoal(this));
        this.goalSelector.addGoal(1, new MoveTowardsTargetGoal(this, 0.6D, 32.0F));
        this.goalSelector.addGoal(2, new LookAtGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.addGoal(2, new LookAtGoal(this, WarpbeetleEntity.class, 8.0f));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomWalkingGoal(this, 0.5d));
        this.goalSelector.addGoal(4, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(0, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, SpiderEntity.class, true, false));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, CaveSpiderEntity.class, true, false));
    }

    //---
    //Retaliating

    @OnlyIn(Dist.CLIENT)
    public void handleStatusUpdate(byte id) {
        if (id == 4) {
            this.attackTimer = 10;
            this.playSound(RegistryHandler.cerobeetle_roar, 1.0F, 1.0F);
        }else {
            super.handleStatusUpdate(id);
        }

    }

    public void livingTick() {
        super.livingTick();
        if (this.attackTimer > 0) {
            --this.attackTimer;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public int getAttackTimer() {
        return this.attackTimer;
    }

    public boolean attackEntityAsMob(Entity entityIn) {
        this.attackTimer = 10;
        this.world.setEntityState(this, (byte)4);
        float f = this.getAttackDamage();
        float f1 = (int)f > 0 ? f / 2.0F + (float)this.rand.nextInt((int)f) : f;
        float f2 = (float)this.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
        boolean flag = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), f1);
        if (flag) {
            ((LivingEntity)entityIn).applyKnockback(f2 * 0.5F, (double) MathHelper.sin(this.rotationYaw * ((float)Math.PI / 180F)), (double)(-MathHelper.cos(this.rotationYaw * ((float)Math.PI / 180F))));
            entityIn.setMotion(entityIn.getMotion().mul(1.0D, 2.5D, 1.0D));
            this.setMotion(this.getMotion().mul(0.6D, 1.0D, 1.6D));
            //Don't touch above line - Multiplies Vertical knock-back by 1X that of the Horizontal knock-back
            this.applyEnchantments(this, entityIn);
        }

        this.playSound(RegistryHandler.cerobeetle_roar, 1.0F, 1.0F);
        return flag;
    }

    private float getAttackDamage() {
        return (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
    }
    //---

    //EXP POINTS
    @Override
    protected int getExperiencePoints(PlayerEntity player) {
        return 1 + this.world.rand.nextInt(4);
    }

    //SOUNDS
    @Override
    protected SoundEvent getAmbientSound() { return RegistryHandler.cerobeetle_ambient; }
    @Override
    protected SoundEvent getDeathSound() { return RegistryHandler.cerobeetle_death; }
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) { return RegistryHandler.cerobeetle_hurt; }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(SoundEvents.ENTITY_PIG_STEP, 0.15F, 1.0F);
    }

}
