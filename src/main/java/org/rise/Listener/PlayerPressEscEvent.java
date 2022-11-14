package org.rise.Listener;

import lk.vexview.api.VexViewAPI;
import lk.vexview.event.KeyBoardPressEvent;
import lk.vexview.event.MinecraftKeys;
import lk.vexview.event.VexSlotClickEvent;
import lk.vexview.gui.OpenedVexGui;
import lk.vexview.gui.components.ButtonFunction;
import lk.vexview.gui.components.VexButton;
import lk.vexview.gui.components.VexComponents;
import lk.vexview.gui.components.VexText;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.PlayerInventory;
import org.rise.GUI.calibrationExtract.AttrSelectGUI;
import org.rise.GUI.calibrationExtract.BaseSelectGUI;
import org.rise.GUI.calibrationExtract.TypeSelectGUI;
import org.rise.refit.CalibrationData;
import org.rise.refit.CalibrationItem;
import org.rise.riseA;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class PlayerPressEscEvent implements Listener {
    @EventHandler
    public void escClick(KeyBoardPressEvent event) {
        if (!event.getEventKeyState()) return;
        if (MinecraftKeys.KEY_ESC.isTheKey(event.getKey())) {
            Player player = event.getPlayer();
            OpenedVexGui gui = VexViewAPI.getPlayerCurrentGui(event.getPlayer());
            if (gui == null) return;
//            player.sendMessage("escpressed");
            if (gui.getVexGui().getButtonById("EAS") != null) {
                BaseSelectGUI.open(player, riseA.CaliDataMap.get(player).type);
            }
            if (gui.getVexGui().getButtonById("EBS") != null) {
                TypeSelectGUI.open(player);
            }
            if (gui.getVexGui().getButtonById("IES") != null) {
                org.rise.GUI.calibrationInject.TypeSelectGUI.open(player);
            }
        }
    }

    @EventHandler
    public void cItemClick(VexSlotClickEvent event) {
        if (event.getID() / 100 != 10) return;
        OpenedVexGui gui = VexViewAPI.getPlayerCurrentGui(event.getPlayer());
        List<VexComponents> components = gui.getVexGui().getComponents();
        List<VexComponents> tl = new LinkedList<>(components);
        Player player = event.getPlayer();
        for (VexComponents i : tl) {
            if (i instanceof VexText) {
                VexText tmp = (VexText) i;
                if (((VexText) i).getText().get(0).contains("§d§e§l§1")) {
                    gui.removeDynamicComponent(tmp);
                }
            }
            if (i instanceof VexButton) {
                VexButton tmp = (VexButton) i;
                if (Objects.equals(tmp.getId(), "at_confirm")) {
                    gui.removeDynamicComponent(tmp);
                }
            }
        }
        List<String> list = new LinkedList<>();
        list.add("§d§e§l§1§b§l当前选中|>" + event.getItem().getItemMeta().getDisplayName());
        list.addAll(event.getItem().getItemMeta().getLore());
        VexText text = new VexText(400, 50, list);
        riseA.playerSelectData data = riseA.CaliDataMap.get(player);
        CalibrationItem cItem = new CalibrationItem(event.getItem());
        double now = CalibrationData.getData(player.getUniqueId(), data.type, data.slot, data.attr);
        double aft = cItem.getAttrNum(data.slot, data.attr);
        String ts1 = "§d§e§l§1§f即将提取属性：§6" + riseA.attrName.get(data.attr);
        String ts2 = "§f存储数值变化: §6§l" + String.format("%.2f", now) + " §f-> §6§l" + aft;
        VexText t1 = new VexText(260, 130, Arrays.asList(ts1, ts2, "§c警告:提取后物品将消失"));
        ButtonFunction function = new ButtonFunction() {
            @Override
            public void run(Player player) {
                CalibrationData.saveAttrData(player.getUniqueId(), data.type, data.slot, data.attr, aft);
                PlayerInventory inv = player.getInventory();
                inv.removeItem(event.getItem());
                CalibrationData.saveToFile(player);
                AttrSelectGUI.open(player, data.type, data.slot);
            }
        };
        VexButton button = new VexButton("at_confirm", "确认提取", "[local]ISAC/test-button.png", "[local]ISAC/test-button_.png", 280, 190, 50, 30, function);
        gui.addDynamicComponent(text);
        gui.addDynamicComponent(t1);
        gui.addDynamicComponent(button);

    }
}
