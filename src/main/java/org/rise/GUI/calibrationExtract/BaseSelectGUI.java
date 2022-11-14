package org.rise.GUI.calibrationExtract;

import lk.vexview.api.VexViewAPI;
import lk.vexview.gui.VexGui;
import lk.vexview.gui.components.ButtonFunction;
import lk.vexview.gui.components.VexButton;
import lk.vexview.gui.components.VexText;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class BaseSelectGUI {
    public static void open(Player player, String type) {
        //        int x = VexViewAPI.getPlayerClientWindowWidth(player), y = VexViewAPI.getPlayerClientWindowHeight(player);
        int x = 1366 / 2, y = 768 / 2;
        int midX = x / 2, midY = y / 2;
        VexGui gui = new VexGui("[local]ISAC/inv-back-1.png", midX - 300, midY - 130, 600, 260);
        VexText text = new VexText(45, -5, Arrays.asList("§f校准-§6数值提取-§f§l" + type), 2);
        switch (type) {
            case "精准射手步枪":
            case "突击步枪":
            case "冲锋枪":
            case "狙击枪":
            case "机枪":
            case "手枪":
            case "霰弹枪": {
                ButtonFunction f1 = new ButtonFunction() {
                    @Override
                    public void run(Player player) {
                        AttrSelectGUI.open(player, type, "武器核心槽位");
                    }
                };
                VexButton core = new VexButton("ss_core", "武器核心槽位", "[local]ISAC/test-button.png", "[local]ISAC/test-button_.png", 100, 30, 100, 200, f1);
                ButtonFunction f2 = new ButtonFunction() {
                    @Override
                    public void run(Player player) {
                        AttrSelectGUI.open(player, type, "武器额外槽位");
                    }
                };
                VexButton extra = new VexButton("ss_extra", "武器额外槽位", "[local]ISAC/test-button.png", "[local]ISAC/test-button_.png", 250, 30, 100, 200, f2);
                gui.addAllComponents(Arrays.asList(core, extra));
            }
            case "头盔":
            case "胸甲":
            case "枪套":
            case "护膝":
            case "胸挂":
            case "背包":
            case "普通装备": {
                ButtonFunction f1 = new ButtonFunction() {
                    @Override
                    public void run(Player player) {
                        AttrSelectGUI.open(player, type, "装备核心槽位");
                    }
                };
                VexButton core = new VexButton("ss_core", "装备核心槽位", "[local]ISAC/test-button.png", "[local]ISAC/test-button_.png", 100, 30, 100, 200, f1);
                ButtonFunction f2 = new ButtonFunction() {
                    @Override
                    public void run(Player player) {
                        AttrSelectGUI.open(player, type, "装备额外槽位");
                    }
                };
                VexButton extra = new VexButton("ss_extra", "装备额外槽位", "[local]ISAC/test-button.png", "[local]ISAC/test-button_.png", 250, 30, 100, 200, f2);
                gui.addAllComponents(Arrays.asList(core, extra));
            }

        }
        VexButton mark = new VexButton("EBS", "", "[local]ISAC/test-button.png", "[local]ISAC/test-button_.png", -2000, -2000, 1, 1);
        gui.setClosable(false);
        gui.addComponent(text);
        gui.addComponent(mark);
        VexViewAPI.openGui(player, gui);
    }
}
