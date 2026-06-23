package com.timnorthrop.itemoftheday;

import org.bukkit.Bukkit;

import java.time.LocalDate;

public class DateWatcher {

    private LocalDate currentDate = LocalDate.now();
    private final ItemOfTheDayPlugin plugin;

    public DateWatcher(ItemOfTheDayPlugin plugin) {
        this.plugin = plugin;
    }

    public void start() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            LocalDate now = LocalDate.now();

            if (!now.equals(currentDate)) {
                currentDate = now;
                onNewDay(now);
            }
        }, 0L, 20L * 60L); // check for new date every 1min
    }

    private void onNewDay(LocalDate date) {
        plugin.getLogger().info("New date detected (" + date + "). Shuffling Item of the Day...");
        plugin.shuffleIotd();
        plugin.setRewardOrDefault(null);
    }
}
