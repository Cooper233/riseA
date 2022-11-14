package org.rise.GUI.calibrationExtract;

import lk.vexview.api.VexViewAPI;
import lk.vexview.gui.VexGui;
import lk.vexview.gui.components.ButtonFunction;
import lk.vexview.gui.components.VexButton;
import lk.vexview.gui.components.VexText;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.Arrays;

public class TypeSelectGUI implements Listener {
    public static void open(Player player) {
        //        int x = VexViewAPI.getPlayerClientWindowWidth(player), y = VexViewAPI.getPlayerClientWindowHeight(player);
        int x = 1366 / 2, y = 768 / 2;
        int midX = x / 2, midY = y / 2;
        VexGui gui = new VexGui("[local]ISAC/inv-back-1.png", midX - 300, midY - 130, 600, 260);
        VexText text = new VexText(45, -5, Arrays.asList("§f校准-§6数值提取"), 2);
        ButtonFunction f1 = new ButtonFunction() {
            @Override
            public void run(Player player) {
                BaseSelectGUI.open(player, "突击步枪");
            }
        };
        VexButton button_ar = new VexButton("tb_ar", "", "[local]ISAC/calibra/cali-ar.png", "[local]ISAC/calibra/cali-ar_.png", 70, 10, 64, 64, f1);
        ButtonFunction f2 = new ButtonFunction() {
            @Override
            public void run(Player player) {
                BaseSelectGUI.open(player, "精准射手步枪");
            }
        };
        VexButton button_dmr = new VexButton("tb_dmr", "", "[local]ISAC/calibra/cali-dmr.png", "[local]ISAC/calibra/cali-dmr_.png", 170, 10, 64, 64, f2);
        ButtonFunction f3 = new ButtonFunction() {
            @Override
            public void run(Player player) {
                BaseSelectGUI.open(player, "狙击枪");
            }
        };
        VexButton button_rf = new VexButton("tb_rf", "", "[local]ISAC/calibra/cali-rf.png", "[local]ISAC/calibra/cali-rf_.png", 270, 10, 64, 64, f3);
        ButtonFunction f4 = new ButtonFunction() {
            @Override
            public void run(Player player) {
                BaseSelectGUI.open(player, "霰弹枪");
            }
        };
        VexButton button_sg = new VexButton("tb_sg", "", "[local]ISAC/calibra/cali-sg.png", "[local]ISAC/calibra/cali-sg_.png", 370, 10, 64, 64, f4);
        ButtonFunction f5 = new ButtonFunction() {
            @Override
            public void run(Player player) {
                BaseSelectGUI.open(player, "机枪");
            }
        };
        VexButton button_mg = new VexButton("tb_mg", "", "[local]ISAC/calibra/cali-mg.png", "[local]ISAC/calibra/cali-mg_.png", 470, 10, 64, 64, f5);
        ButtonFunction f6 = new ButtonFunction() {
            @Override
            public void run(Player player) {
                BaseSelectGUI.open(player, "手枪");
            }
        };
        VexButton button_hg = new VexButton("tb_hg", "", "[local]ISAC/calibra/cali-hg.png", "[local]ISAC/calibra/cali-hg_.png", 70, 76, 64, 64, f6);
        ButtonFunction f7 = new ButtonFunction() {
            @Override
            public void run(Player player) {
                BaseSelectGUI.open(player, "冲锋枪");
            }
        };
        VexButton button_smg = new VexButton("tb_smg", "", "[local]ISAC/calibra/cali-smg.png", "[local]ISAC/calibra/cali-smg_.png", 170, 76, 64, 64, f7);
        ButtonFunction f8 = new ButtonFunction() {
            @Override
            public void run(Player player) {
                BaseSelectGUI.open(player, "头盔");
            }
        };
        VexButton button_mask = new VexButton("tb_mask", "头盔", "[local]ISAC/test-button.png", "[local]ISAC/test-button_.png", 70, 142, 64, 45, f8);
        ButtonFunction f9 = new ButtonFunction() {
            @Override
            public void run(Player player) {
                BaseSelectGUI.open(player, "胸甲");
            }
        };
        VexButton button_armor = new VexButton("tb_armor", "胸甲", "[local]ISAC/test-button.png", "[local]ISAC/test-button_.png", 170, 142, 64, 45, f9);
        ButtonFunction f10 = new ButtonFunction() {
            @Override
            public void run(Player player) {
                BaseSelectGUI.open(player, "枪套");
            }
        };
        VexButton button_holster = new VexButton("tb_holster", "枪套", "[local]ISAC/test-button.png", "[local]ISAC/test-button_.png", 270, 142, 64, 45, f10);
        ButtonFunction f11 = new ButtonFunction() {
            @Override
            public void run(Player player) {
                BaseSelectGUI.open(player, "护膝");
            }
        };
        VexButton button_kneepad = new VexButton("tb_kneepad", "护膝", "[local]ISAC/test-button.png", "[local]ISAC/test-button_.png", 370, 142, 64, 45, f11);
        ButtonFunction f12 = new ButtonFunction() {
            @Override
            public void run(Player player) {
                BaseSelectGUI.open(player, "胸挂");
            }
        };
        VexButton button_extra = new VexButton("tb_extra", "胸挂", "[local]ISAC/test-button.png", "[local]ISAC/test-button_.png", 470, 142, 64, 45, f12);
        ButtonFunction f13 = new ButtonFunction() {
            @Override
            public void run(Player player) {
                BaseSelectGUI.open(player, "背包");
            }
        };
        VexButton button_bag = new VexButton("tb_bag", "背包", "[local]ISAC/test-button.png", "[local]ISAC/test-button_.png", 70, 210, 64, 45, f13);
        ButtonFunction f14 = new ButtonFunction() {
            @Override
            public void run(Player player) {
                BaseSelectGUI.open(player, "普通装备");
            }
        };
        VexButton button_normal = new VexButton("tb_normal", "背包", "[local]ISAC/test-button.png", "[local]ISAC/test-button_.png", 170, 210, 64, 45, f13);
        gui.addAllComponents(Arrays.asList(text, button_ar, button_dmr, button_rf, button_sg, button_mg, button_hg, button_smg, button_mask, button_armor, button_holster, button_kneepad, button_extra, button_bag, button_normal));
        VexViewAPI.openGui(player, gui);
    }
}
