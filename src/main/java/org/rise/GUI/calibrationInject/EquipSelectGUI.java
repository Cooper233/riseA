package org.rise.GUI.calibrationInject;

import com.killercraft.jimy.CustomShopAPI;
import lk.vexview.api.VexViewAPI;
import lk.vexview.gui.OpenedVexGui;
import lk.vexview.gui.VexGui;
import lk.vexview.gui.components.*;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.rise.State.AttrModifier;
import org.rise.extra.Pair;
import org.rise.refit.CalibrationData;
import org.rise.refit.CalibrationItem;
import org.rise.refit.RefitSlotBase;
import org.rise.riseA;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class EquipSelectGUI implements Listener {
    private static void addItemComponent(Player player, String type, VexScrollingList list, CalibrationItem item, int pos) {
        VexSlot slot = new VexSlot(1100 + pos, 5, 4 + 20 * pos, item.self);
        VexText text = new VexText(25, 4 + 20 * pos, Arrays.asList(item.self.getItemMeta().getDisplayName()));
        ButtonFunction f = new ButtonFunction() {
            @Override
            public void run(Player player) {
                OpenedVexGui g = VexViewAPI.getPlayerCurrentGui(player);
                VexGui gu = g.getVexGui();
                List<VexComponents> components = new LinkedList<>(gu.getComponents());
                for (VexComponents c : components) {
                    if (c instanceof VexText) {
                        VexText t = (VexText) c;
                        if (t.getText().get(0).contains("§d§e§l"))
                            g.removeDynamicComponent(t);
                    }
                    if (c instanceof VexButton) {
                        VexButton b = (VexButton) c;
                        String id = (String) b.getId();
                        if (id.contains("es_confirm")) {
                            g.removeDynamicComponent(b);
                        }
                    }
                }
                List<String> tmp = new LinkedList<>();
                tmp.add("§d§e§l" + item.self.getItemMeta().getDisplayName());
                tmp.addAll(item.self.getItemMeta().getLore());
                VexText lore = new VexText(280, 25, tmp);
                ButtonFunction f1 = new ButtonFunction() {
                    @Override
                    public void run(Player player) {
                        SlotSelectGUI.open(player, type, item);
                    }
                };
                VexButton button = new VexButton("es_confirm", "确认进行校准", "[local]ISAC/test-button.png", "[local]ISAC/test-button_.png", 450, 30, 70, 20, f1);
                OpenedVexGui gui = VexViewAPI.getPlayerCurrentGui(player);
                gui.addDynamicComponent(lore);
                gui.addDynamicComponent(button);
            }
        };
        List<String> ht = new LinkedList<>();
        ht.add("§6[§f可注入槽位§6]");
        if (item.calibrated) {
            Pair<AttrModifier.Attr, Double> pair = item.data.get(item.caliType).get(item.caliPos);
            String prefix = riseA.refitBaseMap.get(item.caliType).prefix;
            ht.add(prefix + ">§7" + riseA.attrName.get(pair.getKey()) + "  " + prefix + "§l" + pair.getValue());
        } else {
            for (String k : item.data.keySet()) {
                List<Pair<AttrModifier.Attr, Double>> list1 = item.data.get(k);
                for (Pair<AttrModifier.Attr, Double> pair : list1) {
                    String prefix = riseA.refitBaseMap.get(k).prefix;
                    String res = prefix + ">§7" + riseA.attrName.get(pair.getKey()) + "  " + prefix + "§l" + pair.getValue();
                    RefitSlotBase base = riseA.refitBaseMap.get(k).getSlot(type, pair.getKey());
                    if (base.ifPercent) res += "%";
                    res += "  ";
                    int t1 = (int) ((pair.getValue() - base.min) / (base.max - base.min) * 100);
                    for (int j = 10; j <= 100; j += 10) {
                        if (j <= t1) res = res + "◆";
                        else res = res + "◇";
                    }
                    ht.add(res);
                }
            }
        }
        ht.add("§b[§f材料消耗§b]");
        List<CalibrationData.calibraConsume> consume = CalibrationData.getConsume(type, item.level);
        for (CalibrationData.calibraConsume i : consume) {
            String res = "§f|-";
            String name = CustomShopAPI.getCostName(i.id);
            res += "§f" + name + " ";
            if (CustomShopAPI.checkCost(player.getName(), i.id) > i.num) {
                res += "§f";
            } else res += "§c";
            res += "" + CustomShopAPI.checkCost(player.getName(), i.id) + "/" + i.num;
            ht.add(res);
        }
        VexHoverText hoverText = new VexHoverText(ht);
        VexButton button = new VexButton("es_" + pos, "", "[local]ISAC/test-button.png", "[local]ISAC/test-button_.png", 5, 4 + 20 * pos, 150, 20, f, hoverText);
        list.addComponent(button);
        list.addComponent(text);
        list.addComponent(slot);
    }

    public static void open(Player player, String type) {
        //        int x = VexViewAPI.getPlayerClientWindowWidth(player), y = VexViewAPI.getPlayerClientWindowHeight(player);
        int x = 1366 / 2, y = 768 / 2;
        int midX = x / 2, midY = y / 2;
        VexGui gui = new VexGui("[local]ISAC/inv-back-1.png", midX - 300, midY - 130, 600, 260);
        VexText text = new VexText(45, -5, Arrays.asList("§f校准-§6数值注入-§f§l" + type), 2);
//        addAttrComponent(player,type,slot,list, AttrModifier.Attr.FINAL_DAMAGE);
        List<CalibrationItem> CItems = new LinkedList<>();
        PlayerInventory inv = player.getInventory();
        for (int i = 0; i < 36; i++) {
            ItemStack item = inv.getItem(i);
            if (item != null && item.getType() != Material.AIR && item.hasItemMeta()) {
                CalibrationItem tmp = new CalibrationItem(item);
                if (tmp.type != null && tmp.type.equals(type)) {
                    CItems.add(tmp);
                }
            }
        }
        VexScrollingList list = new VexScrollingList(65, 25, 170, 200, Math.max(10 + CItems.size() * 20, 200));
        for (CalibrationItem i : CItems) {
            addItemComponent(player, type, list, i, CItems.indexOf(i));
        }
        VexButton mark = new VexButton("IES", "", "[local]ISAC/test-button.png", "[local]ISAC/test-button_.png", -2000, -2000, 1, 1);
        gui.setClosable(false);
        gui.addAllComponents(Arrays.asList(text, list, mark));
        VexViewAPI.openGui(player, gui);
    }
}

