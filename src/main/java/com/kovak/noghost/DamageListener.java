package com.kovak.noghost;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;
import org.bukkit.Location;

public class DamageListener implements Listener {

    private final NoGhost plugin;

    public DamageListener(NoGhost plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        if (!(event.getEntity() instanceof Player)) return;

        Player attacker = (Player) event.getDamager();
        Player victim = (Player) event.getEntity();

        Location attackerLoc = attacker.getLocation();
        Location victimLoc = victim.getLocation();
        double distance = attackerLoc.distance(victimLoc);

        // التأكد من المسافة (4.5 بلوك للبراكتس)
        if (distance <= plugin.getConfig().getDouble("max-hit-distance", 4.5)) {
            
            // إجبار السيرفر على قبول الضربة
            event.setCancelled(false);
            
            // إلغاء التأخير (Invincibility frames) عشان الكومبو
            if (plugin.getConfig().getBoolean("remove-invincibility-frames", true)) {
                victim.setNoDamageTicks(0);
            }

            // فحص إذا اللاعب في السماء (Air Hit Fix)
            boolean isVictimInAir = !victim.isOnGround();
            boolean isFlying = victim.isFlying() || victim.isGliding();
            
            if (isVictimInAir || isFlying) {
                double baseDamage = event.getDamage();
                if (baseDamage < 1.0) baseDamage = 1.0; 
                
                // إرسال الضرر مباشرة (Bypass)
                victim.damage(baseDamage, attacker);
                event.setCancelled(false);
            }
        }

        // التأكد من اتجاه النظر لمنع Ghost Hits
        Vector toVictim = victimLoc.toVector().subtract(attackerLoc.toVector());
        
        if (toVictim.lengthSquared() > 0) {
            toVictim.normalize();
            Vector attackerDirection = attackerLoc.getDirection().normalize();
            
            if (attackerDirection.dot(toVictim) > -0.1) {
                event.setCancelled(false);
            }
        }
    }
}
