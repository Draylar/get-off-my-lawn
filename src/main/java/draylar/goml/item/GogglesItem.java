package draylar.goml.item;

import draylar.goml.GetOffMyLawn;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.Item;

public class GogglesItem extends ArmorItem {

    public GogglesItem() {
        super(ArmorMaterials.IRON, EquipmentSlot.HEAD, new Item.Settings().group(GetOffMyLawn.GROUP).maxDamage(-1));
    }
}
