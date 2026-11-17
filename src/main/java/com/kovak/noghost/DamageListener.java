package com.kovak.noghost;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

public class DamageListener implements Listener {

    private final NoGhost plugin;

    public DamageListener(NoGhost plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        if (!(event.getEntity() instanceof Player)) return;

        Player attacker = (Player) event.getDamager();
        Player victim = (Player) event.getEntity();

        double damage = event.getDamage();
        
        if (damage > 0) {
            // 1. إلغاء التأخير (No Damage Ticks) عشان كل ضربة تنحسب
            if (plugin.getConfig().getBoolean("remove-invincibility-frames", true)) {
                victim.setNoDamageTicks(0); 
            }

            // 2. التأكد من حساب الضربة حتى لو كان اللاعب في السماء
            if (plugin.getConfig().getBoolean("force-damage", true)) {
                double distance = attacker.getLocation().distance(victim.getLocation());
                double maxDist = plugin.getConfig().getDouble("max-hit-distance", 6.0);
                
                if (distance <= maxDist) {
                    event.setDamage(damage);
                }
            }
            
            // 3. التغلب على الـ Ghost Hits
            Vector direction = attacker.getLocation().getDirection().normalize();
            Vector toVictim = victim.getLocation().toVector().subtract(attacker.getLocation().toVector()).normalize();
            
            if (direction.dot(toVictim) > 0.0) {
                event.setCancelled(false); 
            }
        }
    }
}
