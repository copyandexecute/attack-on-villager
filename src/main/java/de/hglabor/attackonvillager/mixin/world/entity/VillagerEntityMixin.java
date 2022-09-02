package de.hglabor.attackonvillager.mixin.world.entity;

import de.hglabor.attackonvillager.AttackOnVillagerClient;
import de.hglabor.attackonvillager.entity.villager.AttackedVillager;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.TradeOffer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VillagerEntity.class)
public abstract class VillagerEntityMixin extends MerchantEntity implements AttackedVillager {
    private static final TrackedData<ItemStack> WEAPON = DataTracker.registerData(VillagerEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);

    private ItemStack weapon = Items.AIR.getDefaultStack();

    public VillagerEntityMixin(EntityType<? extends MerchantEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "prepareOffersFor", at = @At("TAIL"))
    private void makeExpensive(PlayerEntity player, CallbackInfo ci) {
        if (player.hasStatusEffect(AttackOnVillagerClient.VILLAIN_OF_THE_VILLAGE_EFFECT)) {
            StatusEffectInstance statusEffectInstance = player.getStatusEffect(AttackOnVillagerClient.VILLAIN_OF_THE_VILLAGE_EFFECT);
            int j = statusEffectInstance.getAmplifier();
            for (TradeOffer tradeOffer2 : this.getOffers()) {
                double d = 0.3 + 0.0625 * (double) j;
                int k = (int) Math.floor(d * (double) tradeOffer2.getOriginalFirstBuyItem().getCount());
                tradeOffer2.increaseSpecialPrice(Math.max(k, 1));
            }
        }
    }

    /**
     * @author NoRiskk
     * @reason weil ichs kann
     */
    @Overwrite
    public static DefaultAttributeContainer.Builder createVillagerAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 3.0).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.5).add(EntityAttributes.GENERIC_FOLLOW_RANGE, 48.0);
    }

    @Override
    public void playAttackSound() {
        SoundEvent soundEvent = SoundEvents.ENTITY_VILLAGER_NO;
        this.playSound(soundEvent, this.getSoundVolume(), this.getSoundPitch());
    }

    @Override
    public void shoot(LivingEntity target, float progress) {
        ItemStack itemStack = Items.ARROW.getDefaultStack();
        PersistentProjectileEntity persistentProjectileEntity = this.createArrowProjectile(itemStack, progress);
        double d = target.getX() - this.getX();
        double e = target.getBodyY(0.3333333333333333) - persistentProjectileEntity.getY();
        double f = target.getZ() - this.getZ();
        double g = Math.sqrt(d * d + f * f);
        persistentProjectileEntity.setVelocity(d, e + g * (double) 0.2f, f, 1.6f, 14 - this.world.getDifficulty().getId() * 4);
        this.playSound(SoundEvents.ENTITY_ARROW_SHOOT, 1.0f, 1.0f / (this.getRandom().nextFloat() * 0.4f + 0.8f));
        this.world.spawnEntity(persistentProjectileEntity);
    }

    @Override
    public void setWeapon(ItemStack weapon) {
        this.dataTracker.set(WEAPON, weapon);
    }

    @Override
    public ItemStack getWeapon() {
        return this.dataTracker.get(WEAPON);
    }

    @Inject(method = "initDataTracker", at = @At("TAIL"))
    private void injected(CallbackInfo ci) {
        this.dataTracker.startTracking(WEAPON, Items.AIR.getDefaultStack());
    }

    protected PersistentProjectileEntity createArrowProjectile(ItemStack arrow, float damageModifier) {
        return ProjectileUtil.createArrowProjectile(this, arrow, damageModifier);
    }

    @Override
    public void throwPotion(LivingEntity enemy) {

        Vec3d vec3d = enemy.getVelocity();
        double d = enemy.getX() + vec3d.x - this.getX();
        double e = enemy.getEyeY() - 1.100000023841858 - this.getY();
        double f = enemy.getZ() + vec3d.z - this.getZ();
        double g = Math.sqrt(d * d + f * f);
        Potion potion = Potions.HARMING;
        if (enemy instanceof RaiderEntity) {
            if (enemy.getHealth() <= 4.0F) {
                potion = Potions.HEALING;
            } else {
                potion = Potions.REGENERATION;
            }

            this.setTarget(null);
        } else if (g >= 8.0 && !enemy.hasStatusEffect(StatusEffects.SLOWNESS)) {
            potion = Potions.SLOWNESS;
        } else if (enemy.getHealth() >= 8.0F && !enemy.hasStatusEffect(StatusEffects.POISON)) {
            potion = Potions.POISON;
        } else if (g <= 3.0 && !enemy.hasStatusEffect(StatusEffects.WEAKNESS) && this.random.nextFloat() < 0.25F) {
            potion = Potions.WEAKNESS;
        }

        PotionEntity potionEntity = new PotionEntity(this.world, this);
        potionEntity.setItem(PotionUtil.setPotion(new ItemStack(Items.SPLASH_POTION), potion));
        potionEntity.setPitch(potionEntity.getPitch() - -20.0F);
        potionEntity.setVelocity(d, e + g * 0.2, f, 0.75F, 8.0F);
        if (!this.isSilent()) {
            this.world.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_WITCH_THROW, this.getSoundCategory(), 1.0F, 0.8F + this.random.nextFloat() * 0.4F);
        }

        this.world.spawnEntity(potionEntity);
    }

}
