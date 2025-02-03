package org.soak.wrapper.plugin.lifecycle.event;

import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventOwner;
import io.papermc.paper.plugin.lifecycle.event.handler.configuration.LifecycleEventHandlerConfiguration;
import org.jetbrains.annotations.NotNull;
import org.soak.exception.NotImplementedException;

@SuppressWarnings("NonExtendableApiUsage")
public class SoakLifecycleEventManager<O extends LifecycleEventOwner> implements LifecycleEventManager<@NotNull O> {

    @Override
    public void registerEventHandler(@NotNull LifecycleEventHandlerConfiguration<? super O> lifecycleEventHandlerConfiguration) {
        throw NotImplementedException.createByLazy(LifecycleEventManager.class,
                                                   "registerEventHandler",
                                                   LifecycleEventHandlerConfiguration.class);
    }
}
