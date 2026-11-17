package com.kovak.noghost;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class NoGhost extends JavaPlugin {

    @Override
    public void onEnable() {
        // حفظ ملف الإعدادات الافتراضي إذا ما كان موجود
        saveDefaultConfig();
        
        // تسجيل حدث الضربات (DamageListener)
        Bukkit.getPluginManager().registerEvents(new DamageListener(this), this);
        
        getLogger().info("NoGhost Plugin Enabled! Forcing all hits to register.");
    }

    @Override
    public void onDisable() {
        getLogger().info("NoGhost Plugin Disabled.");
    }
}
