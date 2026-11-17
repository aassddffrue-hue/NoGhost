package com.kovak.noghost;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class NoGhost extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        getLogger().info("NoGhost Plugin Enabled! Fixing Ghost Hits...");
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        if (!(event.getEntity() instanceof Player)) return;

        Player attacker = (Player) event.getDamager();
        Player victim = (Player) event.getEntity();

        double damage = event.getDamage();
        
        if (damage > 0) {
            // إلغاء تأخير الضرر عشان كل ضربة تنحسب
            victim.setNoDamageTicks(0); 

            // التأكد من حساب الضربة حتى لو كان اللاعب يطير
            double distance = attacker.getLocation().distance(victim.getLocation());
            
            if (distance > 3.0 && distance < 6.0) {
                event.setDamage(damage);
            }
            
            // التغلب على مشكلة الـ Ghost Hits
            Vector direction = attacker.getLocation().getDirection().normalize();
            Vector toVictim = victim.getLocation().toVector().subtract(attacker.getLocation().toVector()).normalize();
            
            if (direction.dot(toVictim) > 0.0) {
                event.setCancelled(false); 
            }
        }
    }
}
