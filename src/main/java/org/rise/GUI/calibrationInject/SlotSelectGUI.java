package org.rise.GUI.calibrationInject;

import com.killercraft.jimy.CustomShopAPI;
import lk.vexview.api.VexViewAPI;
import lk.vexview.gui.OpenedVexGui;
import lk.vexview.gui.VexGui;
import lk.vexview.gui.components.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.rise.State.AttrModifier;
import org.rise.extra.Pair;
import org.rise.refit.CalibrationData;
import org.rise.refit.CalibrationItem;
import org.rise.refit.RefitBase;
import org.rise.refit.RefitSlotBase;
import org.rise.riseA;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class SlotSelectGUI implements Listener {
    private static void addButton(VexGui gui, String type, String s, AttrModifier.Attr a, RefitBase base, AttrModifier.Attr now, double val, CalibrationItem item, int pos) {
        String name = riseA.attrName.get(a);
        name += " " + base.prefix + String.format("%.2f", val);
        RefitSlotBase slot = riseA.refitBaseMap.get(s).getSlot(type, now);
        if (slot.ifPercent) name += "%";
        name += "  ";
        int t1 = (int) ((val - slot.min) / (slot.max - slot.min) * 100);
        for (int j = 10; j <= 100; j += 10) {
            if (j <= t1) name = name + "◆";
            else name = name + "◇";
        }
        ButtonFunction f = new ButtonFunction() {
            @Override
            public void run(Player player) {
                OpenedVexGui g = VexViewAPI.getPlayerCurrentGui(player);
                VexGui gu = g.getVexGui();
                List<VexComponents> components = new LinkedList<>(gu.getComponents());
                for (VexComponents c : components) {
                    if (c instanceof VexScrollingList) {
                        VexScrollingList sl = (VexScrollingList) c;
                        g.removeDynamicComponent(sl);
                    }
                    if (c instanceof VexButton) {
                        VexButton b = (VexButton) c;
                        String id = (String) b.getId();
                        if (id.contains("ac_confirm")) {
                            g.removeDynamicComponent(b);
                        }
                    }
                }
                VexScrollingList sl = new VexScrollingList(365, 25, 170, 200, Math.max(10 + 20 * base.refits.get(type).size(), 200));
                int l = 0;
                for (RefitSlotBase slot : base.refits.get(type)) {
                    AttrModifier.Attr attr = slot.type;
                    if (a != null && attr != a && item.getAttrNum(s, attr) != 0) {
                        continue;
                    }
                    String name = riseA.attrName.get(attr);
                    double prc = CalibrationData.getData(player.getUniqueId(), type, s, attr);
                    if (prc <= 0) {
                        name += " §7[暂无提取数值]";
                    } else {
                        RefitBase base = riseA.refitBaseMap.get(s);
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
                    ButtonFunction f1 = new ButtonFunction() {
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
                                    if (id.contains("ac_confirm")) {
                                        g.removeDynamicComponent(b);
                                    }
                                }
                            }
                            if (prc <= 0) return;
                            ButtonFunction f2 = new ButtonFunction() {
                                @Override
                                public void run(Player player) {
                                    List<CalibrationData.calibraConsume> consume = CalibrationData.getConsume(type, item.level);
                                    for (CalibrationData.calibraConsume m : consume) {
                                        int now = CustomShopAPI.checkCost(player.getName(), m.id);
                                        if (now < m.num) {
                                            player.sendMessage("§6[§fISAAC§6]§c材料不足!");
                                            return;
                                        }
                                    }
                                    CalibrationData.performCalibration(player, item, s, now, attr);
                                    for (CalibrationData.calibraConsume m : consume) {
                                        CustomShopAPI.delCost(player.getName(), m.id, m.num);
                                    }
                                    player.sendMessage("§6[§fISAAC§6]§f校准完成！");
                                    EquipSelectGUI.open(player, type);
                                }
                            };
                            VexButton b = new VexButton("ac_confirm", "确认进行注入", "[local]ISAC/test-button.png", "[local]ISAC/test-button_.png", 420, 235, 80, 20, f2);
                            VexText t = new VexText(340, 235, Arrays.asList("§d§e§l§f当前选中：" + riseA.attrName.get(attr)));
                            VexViewAPI.getPlayerCurrentGui(player).addDynamicComponent(b);
                            VexViewAPI.getPlayerCurrentGui(player).addDynamicComponent(t);
                        }
                    };
                    VexButton button = new VexButton("ac_t_" + attr.name(), name, "[local]ISAC/test-button.png", "[local]ISAC/test-button_.png", 10, l * 20, 150, 20, f1);
                    sl.addComponent(button);
                    l += 1;
                }
                g.addDynamicComponent(sl);
            }
        };
        VexButton button = new VexButton("ac_" + s + "_" + now.name(), name, "[local]ISAC/test-button.png", "[local]ISAC/test-button_.png", 70, 20 + pos * 23, 150, 20, f);
        gui.addComponent(button);
    }

    public static void open(Player player, String type, CalibrationItem item) {
        riseA.CaliDataMap.put(player, new riseA.playerSelectData(type, null, null));
        //        int x = VexViewAPI.getPlayerClientWindowWidth(player), y = VexViewAPI.getPlayerClientWindowHeight(player);
        int x = 1366 / 2, y = 768 / 2;
        int midX = x / 2, midY = y / 2;
        VexGui gui = new VexGui("[local]ISAC/inv-back-1.png", midX - 300, midY - 130, 600, 260);
        VexText text = new VexText(45, -5, Arrays.asList("§f校准-§6数值注入-§f" + item.self.getItemMeta().getDisplayName()), 2);
        int num = 0;
        if (item.calibrated) {
            addButton(gui, type, item.caliType, item.caliAttr, riseA.refitBaseMap.get(item.caliType), item.caliAttr, item.getAttrNum(item.caliType, item.caliAttr), item, 1);
        } else {
            for (String s : item.data.keySet()) {
                List<Pair<AttrModifier.Attr, Double>> list = item.data.get(s);
                RefitBase base = riseA.refitBaseMap.get(s);
                for (Pair<AttrModifier.Attr, Double> i : list) {
                    num += 1;
                    AttrModifier.Attr a = i.getKey();
                    addButton(gui, type, s, a, base, i.getKey(), i.getValue(), item, num);
                }
            }
        }
        VexSlot slot = new VexSlot(9991200, 70, 25, item.self);
        List<String> ht = new LinkedList<>();
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
        VexText con = new VexText(250, 23, ht);
        gui.addComponent(text);
        gui.addComponent(slot);
        gui.addComponent(con);
        VexViewAPI.openGui(player, gui);
    }
}
