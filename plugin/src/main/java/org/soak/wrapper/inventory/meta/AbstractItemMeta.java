package org.soak.wrapper.inventory.meta;

import com.destroystokyo.paper.Namespaced;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.tags.CustomItemTagContainer;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.soak.map.SoakMessageMap;
import org.soak.map.item.EnchantmentTypeMap;
import org.soak.map.item.SoakItemFlagMap;
import org.soak.plugin.exception.NotImplementedException;
import org.soak.plugin.utils.DataHelper;
import org.soak.wrapper.persistence.SoakImmutablePersistentDataContainer;
import org.soak.wrapper.persistence.SoakMutablePersistentDataContainer;
import org.spongepowered.api.data.Key;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.data.value.ListValue;
import org.spongepowered.api.data.value.Value;
import org.spongepowered.api.data.value.ValueContainer;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.enchantment.EnchantmentType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class AbstractItemMeta implements ItemMeta, Damageable {

    protected ValueContainer container;

    protected AbstractItemMeta(ValueContainer container) {
        if (!(container instanceof ItemStack || container instanceof ItemStackSnapshot)) {
            throw new RuntimeException("Must be either a ItemStack or ItemStackSnapshot");
        }
        this.container = container;
    }

    public void manipulate(Function<ValueContainer, ValueContainer> function) {
        this.container = function.apply(this.container);
    }

    public boolean isSnapshot() {
        return this.container instanceof ItemStackSnapshot;
    }

    public void copyInto(ItemMeta meta) {
        if (!(meta instanceof AbstractItemMeta into)) {
            throw new RuntimeException("ItemMeta must implement AbstractItemMeta");
        }
        if (into.container instanceof ItemStack stack) {
            this.container = DataHelper.copyInto(stack, this.asSnapshot());
            return;
        }
        this.container = DataHelper.copyInto((ItemStackSnapshot) into.container, this.asSnapshot());
    }

    public ItemStack asStack() {
        if (this.container instanceof ItemStack stack) {
            return stack;
        }
        return ((ItemStackSnapshot) this.container).createStack();
    }

    public ItemStackSnapshot asSnapshot() {
        if (this.container instanceof ItemStack stack) {
            return stack.createSnapshot();
        }
        return ((ItemStackSnapshot) this.container);
    }

    protected ItemStackSnapshot copyToSnapshot() {
        if (this.container instanceof ItemStack stack) {
            return stack.createSnapshot();
        }
        return ((ItemStackSnapshot) this.container).copy();
    }

    protected <T> void set(@NotNull Key<Value<T>> key, @Nullable T value) {
        if (value == null) {
            remove(key);
            return;
        }
        if (this.container instanceof ItemStack stack) {
            stack.offer(key, value);
            return;
        }
        this.container = ((ItemStackSnapshot) this.container).with(key, value)
                .orElseThrow(() -> new RuntimeException("Key of " + key.key()
                        .formatted() + " is not supported with ItemStackSnapshot"));
    }

    protected <T> void setList(@NotNull Key<ListValue<T>> key, @Nullable List<T> value) {
        if (value == null) {
            remove(key);
            return;
        }
        if (this.container instanceof ItemStack stack) {
            stack.offer(key, value);
            return;
        }
        this.container = ((ItemStackSnapshot) this.container).with(key, value)
                .orElseThrow(() -> new RuntimeException("Key of " + key.key()
                        .formatted() + " is not supported with ItemStackSnapshot"));
    }

    private ItemType type() {
        if (this.container instanceof ItemStack stack) {
            return stack.type();
        }
        return ((ItemStackSnapshot) this.container).type();
    }

    protected void remove(@NotNull Key<?> key) {
        if (this.container instanceof ItemStack stack) {
            stack.remove(key);
            return;
        }
        this.container = ((ItemStackSnapshot) this.container).without(key)
                .orElseThrow(() -> new RuntimeException("Key of " + key.key()
                        .formatted() + " is not supported with ItemStackSnapshot"));
    }

    @Override
    public boolean hasDisplayName() {
        return this.container.get(Keys.CUSTOM_NAME).isPresent();
    }

    @Override
    public @Nullable Component displayName() {
        return this.container.get(Keys.CUSTOM_NAME).orElse(null);
    }

    @Override
    public void displayName(@Nullable Component displayName) {
        this.set(Keys.CUSTOM_NAME, displayName);
    }

    @Override
    public @NotNull String getDisplayName() {
        Component displayName = displayName();
        if (displayName == null) {
            displayName = this.type().asComponent();
        }
        return SoakMessageMap.mapToBukkit(displayName);
    }

    @Override
    public void setDisplayName(@Nullable String name) {
        if (name == null) {
            remove(Keys.CUSTOM_NAME);
            return;
        }
        Component displayName = SoakMessageMap.mapToComponent(name);
        displayName(displayName);
    }

    @Override
    public @NotNull BaseComponent[] getDisplayNameComponent() {
        throw NotImplementedException.createByLazy(AbstractItemMeta.class, "getDisplayNameComponent");
    }

    @Override
    public void setDisplayNameComponent(@Nullable BaseComponent[] component) {
        throw NotImplementedException.createByLazy(AbstractItemMeta.class,
                "setDisplayNameComponent",
                BaseComponent.class);
    }

    @Override
    public boolean hasLocalizedName() {
        throw NotImplementedException.createByLazy(AbstractItemMeta.class, "hasLocalizedName");
    }

    @Override
    public @NotNull String getLocalizedName() {
        throw NotImplementedException.createByLazy(AbstractItemMeta.class, "getLocalizedName");
    }

    @Override
    public void setLocalizedName(@Nullable String name) {
        throw NotImplementedException.createByLazy(AbstractItemMeta.class, "setLocalizedName", String.class);
    }

    @Override
    public boolean hasLore() {
        return this.container.get(Keys.LORE).isPresent();
    }

    @Override
    public @Nullable List<Component> lore() {
        return this.container.get(Keys.LORE).orElse(null);
    }

    @Override
    public void lore(@Nullable List<Component> lore) {
        setList(Keys.LORE, lore);
    }

    @Override
    public @Nullable List<String> getLore() {
        List<Component> list = this.lore();
        if (list == null) {
            return null;
        }
        return list.stream().map(SoakMessageMap::mapToBukkit).collect(Collectors.toList());
    }

    @Override
    public void setLore(@Nullable List<String> lore) {
        if (lore == null) {
            remove(Keys.LORE);
            return;
        }
        List<Component> list = lore.stream().map(SoakMessageMap::mapToComponent).collect(Collectors.toList());
        lore(list);
    }

    @Override
    public @Nullable List<BaseComponent[]> getLoreComponents() {
        throw NotImplementedException.createByLazy(ItemMeta.class, "getLoreComponents");
    }

    @Override
    public void setLoreComponents(@Nullable List<BaseComponent[]> lore) {
        throw NotImplementedException.createByLazy(ItemMeta.class, "setLoreComponents", List.class);

    }

    @Override
    public boolean hasCustomModelData() {
        return this.container.get(Keys.CUSTOM_MODEL_DATA).isPresent();
    }

    @Override
    public int getCustomModelData() {
        return this.container.get(Keys.CUSTOM_MODEL_DATA).orElse(-1);
    }

    @Override
    public void setCustomModelData(@Nullable Integer data) {
        if (this.container instanceof ItemStackSnapshot) {
            return; //not supported on snapshot -> maybe a issue
        }
        if (data == null) {
            this.remove(Keys.CUSTOM_MODEL_DATA);
            return;
        }
        this.set(Keys.CUSTOM_MODEL_DATA, data);
    }

    @Override
    public boolean hasEnchants() {
        return this.container.get(Keys.APPLIED_ENCHANTMENTS).isPresent();
    }

    @Override
    public boolean hasEnchant(@NotNull Enchantment ench) {
        var enchantmentType = EnchantmentTypeMap.toSponge(ench);
        return this.container.get(Keys.APPLIED_ENCHANTMENTS)
                .orElse(Collections.emptyList())
                .stream()
                .anyMatch(encha -> encha.type().equals(enchantmentType));
    }

    @Override
    public int getEnchantLevel(@NotNull Enchantment ench) {
        var enchantmentType = EnchantmentTypeMap.toSponge(ench);
        var spongeEnchantments = this.container.get(Keys.APPLIED_ENCHANTMENTS).orElse(new LinkedList<>());
        return spongeEnchantments.stream()
                .filter(encha -> encha.type().equals(enchantmentType))
                .findAny()
                .map(org.spongepowered.api.item.enchantment.Enchantment::level)
                .orElse(-1);
    }

    @Override
    public @NotNull Map<Enchantment, Integer> getEnchants() {
        List<org.spongepowered.api.item.enchantment.Enchantment> spongeEnchantments = this.container.get(Keys.APPLIED_ENCHANTMENTS)
                .orElse(new LinkedList<>());
        return spongeEnchantments
                .stream()
                .collect(Collectors.toMap(ench -> EnchantmentTypeMap.toBukkit(ench.type()),
                        org.spongepowered.api.item.enchantment.Enchantment::level));
    }

    @Override
    public boolean addEnchant(@NotNull Enchantment ench, int level, boolean ignoreLevelRestriction) {
        var enchantment = org.spongepowered.api.item.enchantment.Enchantment.of(EnchantmentTypeMap.toSponge(ench),
                level);
        return modifyEnchantments(enchantments -> {
            enchantments.add(enchantment);
            return enchantments;
        });
    }

    private boolean modifyEnchantments(Function<List<org.spongepowered.api.item.enchantment.Enchantment>, List<org.spongepowered.api.item.enchantment.Enchantment>> apply) {
        try {
            List<org.spongepowered.api.item.enchantment.Enchantment> appliedEnchantments = this.container.get(Keys.APPLIED_ENCHANTMENTS)
                    .orElse(new LinkedList<>());
            List<org.spongepowered.api.item.enchantment.Enchantment> appliedChanges = apply.apply(appliedEnchantments);
            this.setList(Keys.APPLIED_ENCHANTMENTS, appliedChanges);

            if (this.container.supports(Keys.STORED_ENCHANTMENTS)) {
                var storedEnchantments = this.container.get(Keys.STORED_ENCHANTMENTS).orElse(new LinkedList<>());
                var storedChanges = apply.apply(storedEnchantments);
                this.setList(Keys.STORED_ENCHANTMENTS, storedChanges);
            }
        } catch (Throwable e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean removeEnchant(@NotNull Enchantment ench) {
        EnchantmentType type = EnchantmentTypeMap.toSponge(ench);
        return modifyEnchantments(enchantments -> {
            Collection<org.spongepowered.api.item.enchantment.Enchantment> toRemove = enchantments.stream()
                    .filter(enchantment -> enchantment.type().equals(type))
                    .collect(Collectors.toSet());
            enchantments.removeAll(toRemove);
            return enchantments;
        });
    }

    @Override
    public boolean hasConflictingEnchant(@NotNull Enchantment ench) {
        throw NotImplementedException.createByLazy(ItemMeta.class, "hasConflictingEnchant", Enchantment.class);
    }

    @Override
    public void addItemFlags(@NotNull ItemFlag... itemFlags) {
        modifyItemFlags(true, itemFlags);
    }

    @Override
    public void removeItemFlags(@NotNull ItemFlag... itemFlags) {
        modifyItemFlags(false, itemFlags);
    }

    private void modifyItemFlags(boolean as, ItemFlag... itemFlags) {
        for (ItemFlag flag : itemFlags) {
            this.set(SoakItemFlagMap.toSponge(flag), as);
        }
    }

    @Override
    public @NotNull Set<ItemFlag> getItemFlags() {
        return this.container.getKeys().stream().map(key -> {
            try {
                return SoakItemFlagMap.toBukkit(key);
            } catch (RuntimeException e) {
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    @Override
    public boolean hasItemFlag(@NotNull ItemFlag flag) {
        Key<Value<Boolean>> key = SoakItemFlagMap.toSponge(flag);
        return this.container.get(key).orElse(false);
    }

    @Override
    public boolean isUnbreakable() {
        return this.container.get(Keys.IS_UNBREAKABLE).orElse(false);
    }

    @Override
    public void setUnbreakable(boolean unbreakable) {
        set(Keys.IS_UNBREAKABLE, unbreakable);
    }

    @Override
    public boolean hasAttributeModifiers() {
        throw NotImplementedException.createByLazy(ItemMeta.class, "hasAttributeModifiers");
    }

    @Override
    public @Nullable Multimap<Attribute, AttributeModifier> getAttributeModifiers() {
        throw NotImplementedException.createByLazy(ItemMeta.class, "getAttributeModifiers");
    }

    @Override
    public void setAttributeModifiers(@Nullable Multimap<Attribute, AttributeModifier> attributeModifiers) {
        throw NotImplementedException.createByLazy(ItemMeta.class, "setAttributeModifiers", Multimaps.class);

    }

    @Override
    public @NotNull Multimap<Attribute, AttributeModifier> getAttributeModifiers(@NotNull EquipmentSlot slot) {
        throw NotImplementedException.createByLazy(ItemMeta.class, "getAttributeModifiers", EquipmentSlot.class);
    }

    @Override
    public @Nullable Collection<AttributeModifier> getAttributeModifiers(@NotNull Attribute attribute) {
        throw NotImplementedException.createByLazy(ItemMeta.class, "getAttributeModifiers", Attribute.class);
    }

    @Override
    public boolean addAttributeModifier(@NotNull Attribute attribute, @NotNull AttributeModifier modifier) {
        throw NotImplementedException.createByLazy(ItemMeta.class,
                "addAttributeModifiers",
                Attribute.class,
                AttributeModifier.class);
    }

    @Override
    public boolean removeAttributeModifier(@NotNull Attribute attribute) {
        throw NotImplementedException.createByLazy(ItemMeta.class, "removeAttributeModifier", Attribute.class);
    }

    @Override
    public boolean removeAttributeModifier(@NotNull EquipmentSlot slot) {
        throw NotImplementedException.createByLazy(ItemMeta.class, "removeAttributeModifier", EquipmentSlot.class);
    }

    @Override
    public boolean removeAttributeModifier(@NotNull Attribute attribute, @NotNull AttributeModifier modifier) {
        throw NotImplementedException.createByLazy(ItemMeta.class,
                "removeAttributeModifier",
                Attribute.class,
                AttributeModifier.class);
    }

    @Override
    @Deprecated
    public @NotNull CustomItemTagContainer getCustomTagContainer() {
        throw NotImplementedException.createByLazy(ItemMeta.class, "getCustomTagContainer");
    }

    @Override
    public void setVersion(int version) {
        throw NotImplementedException.createByLazy(ItemMeta.class, "setVersion", int.class);
    }

    @Override
    public Set<Material> getCanDestroy() {
        throw NotImplementedException.createByLazy(ItemMeta.class, "getCanDestroy");
    }

    @Override
    public void setCanDestroy(Set<Material> canDestroy) {
        throw NotImplementedException.createByLazy(ItemMeta.class, "setCanDestroy", Set.class);
    }

    @Override
    public Set<Material> getCanPlaceOn() {
        throw NotImplementedException.createByLazy(ItemMeta.class, "getCanPlaceOn");
    }

    @Override
    public void setCanPlaceOn(Set<Material> canPlaceOn) {
        throw NotImplementedException.createByLazy(ItemMeta.class, "setCanPlaceOn", Set.class);
    }

    @Override
    public @NotNull Set<Namespaced> getDestroyableKeys() {
        throw NotImplementedException.createByLazy(ItemMeta.class, "getDestroyableKeys");
    }

    @Override
    public void setDestroyableKeys(@NotNull Collection<Namespaced> canDestroy) {
        throw NotImplementedException.createByLazy(ItemMeta.class, "setDestroyableKeys", Collection.class);
    }

    @Override
    public @NotNull Set<Namespaced> getPlaceableKeys() {
        throw NotImplementedException.createByLazy(ItemMeta.class, "getPlaceableKeys");
    }

    @NotNull
    @Override
    public void setPlaceableKeys(@NotNull Collection<Namespaced> canPlaceOn) {
        throw NotImplementedException.createByLazy(ItemMeta.class, "setPlaceableKeys");
    }

    @Override
    public boolean hasPlaceableKeys() {
        throw NotImplementedException.createByLazy(ItemMeta.class, "hasPlaceableKeys");
    }

    @Override
    public boolean hasDestroyableKeys() {
        throw NotImplementedException.createByLazy(ItemMeta.class, "hasDestroyableKeys");
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        throw NotImplementedException.createByLazy(ItemMeta.class, "serialize");
    }

    @Override
    public @NotNull PersistentDataContainer getPersistentDataContainer() {
        if (this.container instanceof ItemStack stack) {
            return new SoakMutablePersistentDataContainer<>(stack);
        }
        return new SoakImmutablePersistentDataContainer<>((ItemStackSnapshot) this.container);
    }

    @Override
    public boolean hasDamage() {
        return getDamage() != 0;
    }

    @Override
    public int getDamage() {
        return this.container.getInt(Keys.MAX_DURABILITY).orElse(0) - this.container.getInt(Keys.ITEM_DURABILITY)
                .orElse(0);
    }

    @Override
    public void setDamage(int damage) {
        int durability = this.container.getInt(Keys.MAX_DURABILITY).orElse(0) - damage;
        if (durability < 0) {
            return;
        }
        this.set(Keys.ITEM_DURABILITY, durability);
    }

    @Override
    public abstract @NotNull AbstractItemMeta clone();
}
