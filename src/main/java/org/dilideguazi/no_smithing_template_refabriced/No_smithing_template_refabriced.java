package org.dilideguazi.no_smithing_template_refabriced;

import fuzs.forgeconfigapiport.fabric.api.v5.ConfigRegistry;
import net.fabricmc.api.ModInitializer;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class No_smithing_template_refabriced implements ModInitializer {
    public static final String MOD_ID = "no_smithing_template_refabriced";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ConfigRegistry.INSTANCE.register(
                MOD_ID,
                ModConfig.Type.COMMON,
                Config.SPEC
        );

        LOGGER.info("No Smithing Template ReFabriced Mod Loaded - Ready to craft without templates!");
    }
}
