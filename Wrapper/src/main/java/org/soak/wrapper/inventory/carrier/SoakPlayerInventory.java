package org.soak.wrapper.inventory.carrier;


import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.soak.exception.NotImplementedException;
import org.soak.map.item.SoakItemStackMap;
import org.soak.map.item.inventory.SoakEquipmentMap;
import org.soak.wrapper.inventory.SoakInventory;
import org.spongepowered.api.item.inventory.Inventory;

public class SoakPlayerInventory extends SoakInventory<org.spongepowered.api.item.inventory.entity.PlayerInventory>
        implements PlayerInventory {

    public SoakPlayerInventory(org.spongepowered.api.item.inventory.entity.PlayerInventory spongeInventory) {
        super(spongeInventory);
    }

    @Override
    public @NotNull ItemStack getItem(@NotNull EquipmentSlot arg0) {
        var equipmentType = SoakEquipmentMap.toSponge(arg0);
        return this.sponge()
                .equipment()
                .slot(equipmentType)
                .map(Inventory::peek)
                .map(SoakItemStackMap::toBukkit)
                .orElseThrow(() -> new RuntimeException("Slot " + arg0.name() + " cannot be applied to Player " +
                                                                "Inventory"));
    }

    @Override
    public HumanEntity getHolder() {
        throw NotImplementedException.createByLazy(PlayerInventory.class, "getHolder");
    }

    @Override
    @NotNull
    public ItemStack[] getArmorContents() {
        ItemStack helmet = this.getHelmet();
        ItemStack chest = this.getChestplate();
        ItemStack legs = this.getLeggings();
        ItemStack boots = this.getBoots();

        return new ItemStack[]{helmet, chest, legs, boots};
    }

    @Override
    public void setArmorContents(ItemStack[] arg0) {
        throw NotImplementedException.createByLazy(PlayerInventory.class, "setArmorContents", ItemStack[].class);
    }

    @Override
    @NotNull
    public ItemStack[] getExtraContents() {
        throw NotImplementedException.createByLazy(PlayerInventory.class, "getExtraContents");
    }

    @Override
    public void setExtraContents(ItemStack[] arg0) {
        throw NotImplementedException.createByLazy(PlayerInventory.class, "setExtraContents", ItemStack[].class);
    }

    @Override
    public ItemStack getHelmet() {
        return this.getItem(EquipmentSlot.HEAD);
    }

    @Override
    public void setHelmet(ItemStack arg0) {
        this.setItem(EquipmentSlot.HEAD, arg0);
    }

    @Override
    public ItemStack getChestplate() {
        return this.getItem(EquipmentSlot.CHEST);
    }

    @Override
    public void setChestplate(ItemStack arg0) {
        this.setItem(EquipmentSlot.CHEST, arg0);
    }

    @Override
    public ItemStack getLeggings() {
        return this.getItem(EquipmentSlot.LEGS);
    }

    @Override
    public void setLeggings(ItemStack arg0) {
        this.setItem(EquipmentSlot.LEGS, arg0);
    }

    @Override
    public ItemStack getBoots() {
        return this.getItem(EquipmentSlot.FEET);
    }

    @Override
    public void setBoots(ItemStack arg0) {
        this.setItem(EquipmentSlot.FEET, arg0);
    }

    @Override
    public void setItem(int arg0, ItemStack arg1) {
        var slot = this.sponge()
                .slot(arg0)
                .orElseThrow(() -> new IndexOutOfBoundsException(arg0 + " must be within " + this.sponge().capacity()));
        var stack = SoakItemStackMap.toSponge(arg1);
        slot.set(stack);
    }

    @Override
    public void setItem(@NotNull EquipmentSlot arg0, ItemStack arg1) {
        var equipmentType = SoakEquipmentMap.toSponge(arg0);
        var stack = SoakItemStackMap.toSponge(arg1);
        var slot = this.sponge()
                .equipment()
                .slot(equipmentType)
                .orElseThrow(() -> new RuntimeException("Slot " + arg0.name() + " is not applicable to Player " +
                                                                "inventory"));
        slot.set(stack);
    }

    @Override
    public @NotNull ItemStack getItemInMainHand() {
        return this.getItem(EquipmentSlot.HAND);
    }

    @Override
    public void setItemInMainHand(ItemStack arg0) {
        this.setItem(EquipmentSlot.HAND, arg0);
    }

    @Override
    public @NotNull ItemStack getItemInOffHand() {
        return this.getItem(EquipmentSlot.OFF_HAND);
    }

    @Override
    public void setItemInOffHand(ItemStack arg0) {
        this.setItem(EquipmentSlot.OFF_HAND, arg0);
    }

    @Deprecated
    @Override
    public @NotNull ItemStack getItemInHand() {
        return this.getItemInMainHand();
    }

    @Deprecated
    @Override
    public void setItemInHand(ItemStack arg0) {
        this.setItemInMainHand(arg0);
    }

    @Override
    public int getHeldItemSlot() {
        return this.sponge().hotbar().selectedSlotIndex();
    }

    @Override
    public void setHeldItemSlot(int arg0) {
        this.sponge().hotbar().setSelectedSlotIndex(arg0);
    }
}
