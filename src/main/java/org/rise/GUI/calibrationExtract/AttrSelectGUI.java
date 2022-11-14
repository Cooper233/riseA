package org.rise.GUI.calibrationExtract;

import lk.vexview.api.VexViewAPI;
import lk.vexview.gui.OpenedVexGui;
import lk.vexview.gui.VexGui;
import lk.vexview.gui.components.*;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.rise.State.AttrModifier;
import org.rise.refit.CalibrationData;
import org.rise.refit.CalibrationItem;
import org.rise.refit.RefitBase;
import org.rise.refit.RefitSlotBase;
import org.rise.riseA;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class AttrSelectGUI implements Listener {

    public static void addAttrComponent(Player player, String type, String slot, VexScrollingList list, AttrModifier.Attr attr) {
        int num;
        if (list.getComponents() == null) num = 0;
        else num = list.getComponents().size();
        String name = riseA.attrName.get(attr);
        double prc = CalibrationData.getData(player.getUniqueId(), type, slot, attr);
        if (prc <= 0) {
            name += " §7[暂无提取数值]";
        } else {
            RefitBase base = riseA.refitBaseMap.get(slot);
            RefitSlotBase slotBase = base.getSlot(type, attr);
            double pre = (prc - slotBase.min) / (slotBase.max - slotBase.min);
            name += " §7[§f" + String.format("%d", (int) (pre * 100)) + "% §6§l";
            for (int i = 0; i <= pre * 100; i += 10) {
                name += "=";
            }
            name += "§f§l";
            for (int i = 100; i > pre * 100; i -= 10) {
                name += "=";
            }
            name += " §6" + String.format("%.2f", prc);
            if (slotBase.ifPercent) name += "%";
            name += "§7]";
        }
        ButtonFunction f = new ButtonFunction() {
            @Override
            public void run(Player player) {
                OpenedVexGui gui = VexViewAPI.getPlayerCurrentGui(player);
                List<VexComponents> components = gui.getVexGui().getComponents();
                List<VexComponents> tl = new LinkedList<>(components);
                for (VexComponents i : tl) {
                    if (i instanceof VexText) {
                        VexText tmp = (VexText) i;
                        if (((VexText) i).getText().get(0).contains("§d§e§l")) {
                            gui.removeDynamicComponent(tmp);
                        }
                    }
                    if (i instanceof VexSlot) {
                        VexSlot tmp = (VexSlot) i;
                        gui.removeDynamicComponent(tmp);
                    }
                    if (i instanceof VexButton) {
                        VexButton tmp = (VexButton) i;
                        if (Objects.equals(tmp.getId(), "at_confirm")) {
                            gui.removeDynamicComponent(tmp);
                        }
                    }
                }
                ConfigurationSection config = riseA.config;
                config = config.getConfigurationSection("description");
                VexHoverText hoverText = new VexHoverText(config.getStringList(attr.name()));
                VexText attrName = new VexText(260, 30, Arrays.asList("§d§e§l§f§l" + riseA.attrName.get(attr)), 3, hoverText, 100);
                riseA.CaliDataMap.put(player, new riseA.playerSelectData(type, slot, attr));
                String p = "§d§e§l";
                double prc = CalibrationData.getData(player.getUniqueId(), type, slot, attr);
                if (prc <= 0) {
                    p += "§7[暂无提取数值]";
                } else {
                    RefitBase base = riseA.refitBaseMap.get(slot);
                    RefitSlotBase slotBase = base.getSlot(type, attr);
                    double pre = (prc - slotBase.min) / (slotBase.max - slotBase.min);
                    p += "§7[§f提取进度:" + String.format("%d", (int) (pre * 100)) + "% §6§l";
                    for (int i = 0; i <= pre * 100; i += 10) {
                        p += "=";
                    }
                    p += "§f§l";
                    for (int i = 100; i > pre * 100; i -= 10) {
                        p += "=";
                    }
                    p += " §6储存数值:" + String.format("%.2f", prc);
                    if (slotBase.ifPercent) p += "%";
                    p += "§7]";
                }
                VexText pText = new VexText(365, 35, Arrays.asList(p), 1.25);
                VexText text1 = new VexText(250, 60, Arrays.asList("§d§e§l§6§l>§f可用于提取的物品"), 1.5);
                gui.addDynamicComponent(attrName);
                gui.addDynamicComponent(pText);
                gui.addDynamicComponent(text1);
                List<CalibrationItem> items = new LinkedList<>();
                PlayerInventory inv = player.getInventory();
                for (int i = 0; i < 36; i++) {
                    ItemStack item = inv.getItem(i);
                    if (item == null || item.getType() == Material.AIR) continue;
                    CalibrationItem cItem = new CalibrationItem(item);
                    if (cItem.type != null) {
                        if (cItem.type.equals(type) && cItem.data.get(slot) != null && cItem.getAttrNum(slot, attr) > CalibrationData.getData(player.getUniqueId(), type, slot, attr)) {
                            items.add(cItem);
                        }
                    }
                }
                for (int i = 0; i < items.size(); i++) {
                    VexSlot slots = new VexSlot(1000 + i, (i % 6) * 25 + 255, 75 + 25 * (i / 6), items.get(i).self);
                    gui.addDynamicComponent(slots);
                }
//                int j=1;
//                while(j>0) {
//                    gui=VexViewAPI.getPlayerCurrentGui(player);
//                    gui.getVexGui().addAllComponents(Arrays.asList(attrName,pText));
//                    j-=1;
//                }
            }
        };
        VexButton button = new VexButton("ac_" + attr.name(), name, "[local]ISAC/test-button.png", "[local]ISAC/test-button_.png", 10, num * 23, 150, 20, f);
        list.addComponent(button);
    }


    public static void open(Player player, String type, String slot) {
        //        int x = VexViewAPI.getPlayerClientWindowWidth(player), y = VexViewAPI.getPlayerClientWindowHeight(player);
        int x = 1366 / 2, y = 768 / 2;
        int midX = x / 2, midY = y / 2;
        VexGui gui = new VexGui("[local]ISAC/inv-back-1.png", midX - 300, midY - 130, 600, 260);
        RefitBase base = riseA.refitBaseMap.get(slot);
        VexText text = new VexText(45, -5, Arrays.asList("§f校准-§6数值提取-§f§l" + type + "-" + base.prefix + slot), 2);
//        addAttrComponent(player,type,slot,list, AttrModifier.Attr.FINAL_DAMAGE);
        List<RefitSlotBase> slots = base.refits.get(type);
        VexScrollingList list = new VexScrollingList(65, 25, 170, 200, Math.max(slots.size() * 23, 200));
        for (RefitSlotBase i : slots) {
            addAttrComponent(player, type, slot, list, i.type);
        }
        VexButton mark = new VexButton("EAS", "", "[local]ISAC/test-button.png", "[local]ISAC/test-button_.png", -2000, -2000, 1, 1);
        gui.setClosable(false);
        gui.addAllComponents(Arrays.asList(text, list, mark));
        VexViewAPI.openGui(player, gui);
        riseA.CaliDataMap.put(player, new riseA.playerSelectData(type, slot, null));
    }
}
