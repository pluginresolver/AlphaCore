package me.pauzen.bukkitcommonpluginapi.fake;

import com.google.common.base.Function;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.Map;

public class FakeEntityDamageByEntityEvent extends EntityDamageByEntityEvent {
    
    public FakeEntityDamageByEntityEvent(Entity damager, Entity damagee, DamageCause cause, int damage) {
        super(damager, damagee, cause, damage);
    }

    public FakeEntityDamageByEntityEvent(Entity damager, Entity damagee, DamageCause cause, double damage) {
        super(damager, damagee, cause, damage);
    }

    public FakeEntityDamageByEntityEvent(Entity damager, Entity damagee, DamageCause cause, Map<DamageModifier, Double> modifiers, Map<DamageModifier, ? extends Function<? super Double, Double>> modifierFunctions) {
        super(damager, damagee, cause, modifiers, modifierFunctions);
    }
}
