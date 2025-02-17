package org.soak.wrapper.scheduler;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitWorker;
import org.jetbrains.annotations.NotNull;
import org.soak.plugin.SoakManager;
import org.spongepowered.api.scheduler.ScheduledTask;

public class SoakBukkitWorker implements BukkitWorker {

    private final ScheduledTask scheduledTask;

    public SoakBukkitWorker(ScheduledTask task) {
        this.scheduledTask = task;
    }

    public ScheduledTask scheduled() {
        return this.scheduledTask;
    }

    @Override
    public int getTaskId() {
        return scheduledTask.name().hashCode();
    }

    @Override
    public @NotNull Plugin getOwner() {
        return SoakManager.getManager()
                .getSoakContainer(scheduledTask.task().plugin())
                .orElseThrow(() -> new IllegalStateException("Cannot find bukkit plugin for id of " + this.scheduledTask.task()
                        .plugin()
                        .metadata()
                        .id()))
                .getBukkitInstance();
    }

    @Override
    public @NotNull Thread getThread() {
        return Thread.currentThread(); //TODO
    }
}
