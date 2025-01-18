package net.mat0u5.lifeseries.series.wildlife;

import net.mat0u5.lifeseries.config.ConfigManager;
import net.mat0u5.lifeseries.series.Series;
import net.mat0u5.lifeseries.series.SeriesList;
import net.mat0u5.lifeseries.series.wildlife.wildcards.SizeShifting;
import net.mat0u5.lifeseries.utils.OtherUtils;
import net.mat0u5.lifeseries.utils.PermissionManager;
import net.mat0u5.lifeseries.utils.TaskScheduler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.HashMap;

import static net.mat0u5.lifeseries.Main.seriesConfig;

public class WildLife extends Series {
    public HashMap<Wildcards, Wildcard> activeWildcards = new HashMap<>();

    @Override
    public SeriesList getSeries() {
        return SeriesList.WILD_LIFE;
    }

    @Override
    public ConfigManager getConfig() {
        return new WildLifeConfig();
    }

    @Override
    public void onPlayerJoin(ServerPlayerEntity player) {
        super.onPlayerJoin(player);

        if (!hasAssignedLives(player)) {
            int lives = seriesConfig.getOrCreateInt("default_lives", 6);
            setPlayerLives(player, lives);
        }
        TaskScheduler.scheduleTask(99, () -> {
            if (PermissionManager.isAdmin(player)) {
                player.sendMessage(Text.of("§7Wild Life commands: §r/lifeseries, /session, /claimkill, /lives"));
            }
            else {
                player.sendMessage(Text.of("§7Wild Life non-admin commands: §r/claimkill, /lives"));
            }
        });
    }

    @Override
    public boolean isAllowedToAttack(ServerPlayerEntity attacker, ServerPlayerEntity victim) {
        if (isOnLastLife(attacker, false)) return true;
        if (attacker.getPrimeAdversary() == victim && (isOnLastLife(victim, false))) return true;

        if (isOnSpecificLives(attacker, 2, false) && (isOnSpecificLives(victim, 3, false) || isOnSpecificLives(victim, 4, false))) return true;
        if (attacker.getPrimeAdversary() == victim && (isOnSpecificLives(victim, 2, false) && (isOnSpecificLives(attacker, 3, false) || isOnSpecificLives(attacker, 4, false)))) return true;
        return false;
    }

    @Override
    public void onPlayerKilledByPlayer(ServerPlayerEntity victim, ServerPlayerEntity killer) {
        if (isAllowedToAttack(killer, victim)) return;
        OtherUtils.broadcastMessageToAdmins(Text.of("§c [Unjustified Kill?] §f"+victim.getNameForScoreboard() + "§7 was killed by §f"
                +killer.getNameForScoreboard() + "§7, who is not §cred name§7 (nor a §eyellow name§7, with the victim being a §agreen name§7)"));
    }

    @Override
    public void tickSessionOn() {
        super.tickSessionOn();
        for (Wildcard wildcard : activeWildcards.values()) {
            wildcard.onTick();
        }
    }


    @Override
    public boolean sessionStart() {
        if (super.sessionStart()) {
            activeWildcards.clear();
            Wildcard.chooseWildcards();
            for (Wildcard wildcard : activeWildcards.values()) {
                //TODO activation timer
                wildcard.activate();
            }
            return true;
        }
        return false;
    }

    @Override
    public void sessionEnd() {
        super.sessionEnd();
        for (Wildcard wildcard : activeWildcards.values()) {
            wildcard.deactivate();
        }
        activeWildcards.clear();
    }

    public void onJump(ServerPlayerEntity player) {
        if (!activeWildcards.containsKey(Wildcards.SIZE_SHIFTING)) return;
        if (activeWildcards.get(Wildcards.SIZE_SHIFTING) instanceof SizeShifting sizeShifting) {
            sizeShifting.onJump(player);
        }
    }
}
