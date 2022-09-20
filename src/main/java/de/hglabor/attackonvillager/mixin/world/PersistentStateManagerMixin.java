package de.hglabor.attackonvillager.mixin.world;

import de.hglabor.attackonvillager.raid.RaidManager;
import net.minecraft.world.PersistentStateManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;

import static de.hglabor.attackonvillager.AttackOnVillagerClient.MOD_ID;

@Mixin(PersistentStateManager.class)
abstract class PersistentStateManagerMixin {
    private static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    @Shadow
    @Final
    private File directory;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void createRaidDir(CallbackInfo ci) {
        if (directory.getAbsolutePath().contains("DIM1") || directory.getAbsolutePath().contains("DIM-1")) return;
        File raidDir = new File(directory.getParentFile() + "/raids/");
        if (!raidDir.exists()) {
            if (raidDir.mkdirs()) {
                LOGGER.info("created raid directory: " + raidDir.getPath());
            }
        }
        RaidManager.INSTANCE.setRaidDirectory(raidDir);
        LOGGER.info("Found raid dir " + raidDir.getAbsolutePath());
    }
}
