package org.rise.GUI;

import lk.vexview.api.VexViewAPI;
import lk.vexview.event.VexSlotInteractEvent;
import lk.vexview.gui.OpenedVexGui;
import lk.vexview.gui.VexInventoryGui;
import lk.vexview.gui.components.*;
import lk.vexview.gui.components.expand.VexClickableButton;
import lk.vexview.gui.components.expand.VexSplitImage;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.NBTTagInt;
import net.minecraft.server.v1_12_R1.NBTTagList;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.rise.EntityInf;
import org.rise.Inventory.ModuleGui;
import org.rise.State.AttrModifier;
import org.rise.State.RAstate;
import org.rise.activeSkills.ActiveAPI;
import org.rise.activeSkills.ConstantEffect;
import org.rise.activeSkills.effect.ActiveBase;
import org.rise.refit.TalentRefit;
import org.rise.riseA;
import org.rise.riseAPI;
import org.rise.skill.SkillAPI;
import org.rise.skill.SkillBase;
import org.rise.talent.TalentType;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class testgui implements Listener {
    public static VexImage top = new VexImage("[local]ISAC/inv-top.png", -350, -300, 324, 256);

    public static ItemStack[] getMWFSlot(Player player) {
        ItemStack[] tar = new ItemStack[2];
        if (!ModuleGui.guiList.containsKey(player.getUniqueId())) ModuleGui.guiInit(player, false);
        Inventory inv = ModuleGui.guiList.get(player.getUniqueId());
        net.minecraft.server.v1_12_R1.Entity iplayer;
        CraftItemStack itemStack;
        CraftEntity c = (CraftEntity) player;
        try {
            Method m = c.getClass().getMethod("getHandle");
            iplayer = (net.minecraft.server.v1_12_R1.EntityPlayer) m.invoke(c);
            NBTTagCompound nbt = iplayer.save(new NBTTagCompound());
            iplayer.f(nbt);
            if (nbt.hasKey("ForgeCaps") && nbt.getCompound("ForgeCaps").hasKey("modularwarfare:extraslots")) {
                NBTTagCompound vicinv = nbt.getCompound("ForgeCaps").getCompound("modularwarfare:extraslots");
                NBTTagList list = vicinv.getList("Items", 10);
                if (!list.isEmpty()) {
                    for (int i = 0; i < list.size(); i++) {
                        NBTTagCompound it = list.get(i);
                        net.minecraft.server.v1_12_R1.ItemStack item = new net.minecraft.server.v1_12_R1.ItemStack(it);
                        int slot = it.getInt("Slot");
                        for (Method j : CraftItemStack.class.getMethods()) {
                            if (j.getName().equals("asBukkitCopy")) {
                                try {
                                    tar[slot] = (org.bukkit.inventory.ItemStack) j.invoke(CraftItemStack.class, item);
                                } catch (IllegalAccessException | InvocationTargetException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return tar;
    }

    public static void setMWFSlot(Player player, ItemStack item, int slot) {
        net.minecraft.server.v1_12_R1.Entity iplayer;
        CraftEntity c = (CraftEntity) player;
        try {
            Method m = c.getClass().getMethod("getHandle");
            iplayer = (net.minecraft.server.v1_12_R1.EntityPlayer) m.invoke(c);
            NBTTagCompound nbt = iplayer.save(new NBTTagCompound());
            iplayer.f(nbt);
            if (nbt.hasKey("ForgeCaps") && nbt.getCompound("ForgeCaps").hasKey("modularwarfare:extraslots")) {
                NBTTagCompound vicinv = nbt.getCompound("ForgeCaps").getCompound("modularwarfare:extraslots");
                NBTTagList list = vicinv.getList("Items", 10);
                NBTTagList lf = new NBTTagList();
                if (!list.isEmpty()) {
                    for (int i = 0; i < list.size(); i++) {
                        NBTTagCompound it = list.get(i);
                        if (it.getInt("Slot") != slot) lf.add(it);
                    }
                }
                if (item != null) {
                    Method method[] = CraftItemStack.class.getMethods();
                    Object obj = null;
                    for (Method j : method) {
                        if (j.getName().equals("asNMSCopy")) {
                            try {
                                obj = j.invoke(CraftItemStack.class, item);
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    net.minecraft.server.v1_12_R1.ItemStack cItem = (net.minecraft.server.v1_12_R1.ItemStack) obj;
                    NBTTagCompound nbt1 = new NBTTagCompound();
                    cItem.save(nbt1);
                    NBTTagInt tmp = new NBTTagInt(slot);
                    nbt1.set("Slot", tmp);
                    lf.add(nbt1);
                }
                NBTTagCompound fc = nbt.getCompound("ForgeCaps");
                vicinv.set("Items", lf);
                fc.set("modularwarfare:extraslots", vicinv);
                nbt.set("ForgeCaps", fc);
                iplayer.f(nbt);
            }
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void clearScreen(OpenedVexGui gui) {
        List<VexComponents> list = gui.getVexGui().getComponents();
        boolean f = true;
        while (f) {
            f = false;
            list = gui.getVexGui().getComponents();
            List<VexComponents> tl = new LinkedList<>(list);
            for (int j = 0; j < tl.size(); j++) {
                VexComponents components = tl.get(j);
                if (components instanceof VexSlot) {
                    f = true;
                    VexSlot a = (VexSlot) components;
                    gui.removeDynamicComponent(a);
                }
                if (components instanceof VexText) {
                    VexText text = (VexText) components;
                    if (text.getText().get(0).contains("§7§l§f")) continue;
                    f = true;
                    gui.removeDynamicComponent(text);
                }
                if (components instanceof VexScrollingList) {
                    VexScrollingList l = (VexScrollingList) components;
                    f = true;
                    gui.removeDynamicComponent(l);
                }
            }
        }
        List<VexComponents> c = new LinkedList<>();
        for (int j = 0; j < list.size(); j++) {
            VexComponents components = list.get(j);
            if (c.contains(components)) continue;
            if (components instanceof VexImage) {
                VexImage image = (VexImage) components;
                if (image == top) continue;
                Thread tt = new Thread(new moveParament(gui, components, 18.75, 0, true, 40, true));
                tt.start();
                c.add(components);
//                    gui.removeDynamicComponent(image);
            }
        }
    }

    public static int getLength(double a, double c, int times) {
        int res = 0;
        for (int i = 0; i < times; i++) {
            int d = (int) (a * Math.pow(i * 0.01, 2) + c);
            d = Math.max(d, 1);
            res += d;
        }
        return res;
    }

    public static void moveComList(OpenedVexGui gui, List<VexComponents> components, double a, double c, boolean isLeft, int times) {
        for (VexComponents i : components) {
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Thread t1 = new Thread(new moveParament(gui, i, a, c, isLeft, times));
            t1.start();
        }
    }

    public static void addTextComponent(VexScrollingList list, String s, List<String> hover) {
        int num;
        if (list.getComponents() == null) num = 0;
        else num = list.getComponents().size();
        VexHoverText hoverText = new VexHoverText(hover);
        VexText text = new VexText(15, num * 13, Arrays.asList(s), 1.25, hoverText, 150);
        list.addComponent(text);
    }

    public static void test(Player player) {
//        int x = VexViewAPI.getPlayerClientWindowWidth(player), y = VexViewAPI.getPlayerClientWindowHeight(player);
        int x = 1366 / 2, y = 768 / 2;
        int midX = x / 2, midY = y / 2;
        VexInventoryGui tmp = new VexInventoryGui("[local]ISAC/inv_back.png", midX + 50, midY + 45, 169, 82, 169, 82, 4, 3);
        VexImage equip1 = new VexImage("[local]ISAC/equip-mask.png", 10, -180, 64, 64);
        VexImage equip2 = new VexImage("[local]ISAC/equip-armor.png", 10, -130, 64, 64);
        VexImage equip3 = new VexImage("[local]ISAC/equip-holster.png", 10, -80, 64, 64);
        VexImage equip4 = new VexImage("[local]ISAC/equip-bag.png", 93, -180, 64, 64);
        VexImage equip5 = new VexImage("[local]ISAC/equip-extra.png", 93, -130, 64, 64);
        VexImage equip6 = new VexImage("[local]ISAC/equip-kneepad.png", 93, -80, 64, 64);
        VexSplitImage back1 = new VexSplitImage("[local]ISAC/inv-back-1.png", -45, -200, 0, 0, 256, 200, 256, 180, 256, 256);
        VexText name = new VexText(-240, -195, Arrays.asList("§7§l§f" + player.getDisplayName()), 2);
        String ll = "" + player.getLevel();
        String ln = "等级";
        VexText level1 = new VexText(-309, -190, Arrays.asList("§7§l§f" + "§6" + ln), 1.5);
        VexText level2 = new VexText(-299 - (ll.length() * 2 - 1) * 2, -177, Arrays.asList("§7§l§f" + ll), 1.5);
        VexPlayerDraw playerDraw = new VexPlayerDraw(-180, 35, 80, player);
        PlayerInventory inv = player.getInventory();
        VexSlot slot1 = new VexSlot(101, 18, -156, ModuleGui.getItem(player, 27));
        VexSlot slot2 = new VexSlot(102, 18, -106, ModuleGui.getItem(player, 28));
        VexSlot slot3 = new VexSlot(103, 18, -56, ModuleGui.getItem(player, 29));
        ItemStack[] tar = getMWFSlot(player);
        VexSlot slot4 = new VexSlot(104, 101, -156, ModuleGui.getItem(player, 30));
        VexSlot slot5 = new VexSlot(105, 101, -106, ModuleGui.getItem(player, 31));
        VexSlot slot6 = new VexSlot(106, 101, -56, ModuleGui.getItem(player, 32));
        VexClickableButton button_item = new VexClickableButton("button_item", "", "[local]ISAC/button-item.png", "[local]ISAC/button-item_.png", "[local]ISAC/button-item_.png", 35, -225, 50, 50, false);
        VexClickableButton button_skill = new VexClickableButton("button_skill", "", "[local]ISAC/button-skill.png", "[local]ISAC/button-skill_.png", "[local]ISAC/button-skill_.png", 85, -225, 50, 50, true);
        VexClickableButton button_state = new VexClickableButton("button_state", "", "[local]ISAC/button-state.png", "[local]ISAC/button-state_.png", "[local]ISAC/button-state_.png", 135, -225, 50, 50, true);
        Player tp = Bukkit.getPlayer("Tech635");
        ButtonFunction f2 = player1 -> {
            OpenedVexGui gui = VexViewAPI.getPlayerCurrentGui(player1);
            gui.setButtonClickable("button_item", true);
            gui.setButtonClickable("button_skill", false);
            gui.setButtonClickable("button_state", true);
            clearScreen(gui);
            try {
                Thread.sleep(410);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            VexSplitImage back2 = new VexSplitImage("[local]ISAC/inv-back-1.png", -45, -200, 0, 0, 256, 200, 256, 190, 256, 256);
            VexImage skillQ = new VexImage("[local]ISAC/skill-Q.png", 20, -165, 40, 40);
            VexImage skillT = new VexImage("[local]ISAC/skill-T.png", 108, -150, 40, 40);
            VexImage itemV = new VexImage("[local]ISAC/item-V.png", 108, -75, 40, 40);
            VexImage itemH = new VexImage("[local]ISAC/item-H.png", 20, -60, 40, 40);
            int d = getLength(-18.75, 3, 40);
            back2.setX(back2.getX() + d);
            skillQ.setX(skillQ.getX() + d);
            skillT.setX(skillT.getX() + d);
            itemV.setX(itemV.getX() + d);
            itemH.setX(itemH.getX() + d);
            gui.addDynamicComponent(skillQ);
            gui.addDynamicComponent(skillT);
            gui.addDynamicComponent(itemV);
            gui.addDynamicComponent(itemH);
            gui.addDynamicComponent(back2);
            gui = VexViewAPI.getPlayerCurrentGui(player1);
            moveComList(gui, Arrays.asList(skillQ, skillT, itemV, itemH, back2), -18.75, 3, true, 40);
            try {
                Thread.sleep(410);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Inventory inventory = ModuleGui.guiList.get(player1.getUniqueId());
            ItemStack item1, item2;
            item1 = inventory.getItem(15);
            item2 = inventory.getItem(24);
            List<VexComponents> addList = new LinkedList<>();
            VexSlot skill_1 = new VexSlot(201, 32, -155, item1);
            gui.addDynamicComponent(skill_1);
            if (item1 != null) {
                List<String> t1 = item1.getItemMeta().getLore();
                ActiveBase skill = ActiveAPI.getActiveSkill(item1);
                t1.addAll(skill.ApplyMod(EntityInf.getPlayerState(player1)));
                VexHoverText skill_1H = new VexHoverText(t1);
                VexText skill_1d = new VexText(-15, -175, Arrays.asList(item1.getItemMeta().getDisplayName()), 1.25, skill_1H, 200);
                gui.addDynamicComponent(skill_1d);
                addList.add(skill_1d);
            }
            VexSlot skill_2 = new VexSlot(202, 120, -140, item2);
            gui.addDynamicComponent(skill_2);
            if (item2 != null) {
                List<String> t2 = item2.getItemMeta().getLore();
                ActiveBase skill = ActiveAPI.getActiveSkill(item2);
                t2.addAll(skill.ApplyMod(EntityInf.getPlayerState(player1)));
                VexHoverText skill_2H = new VexHoverText(t2);
                VexText skill_2d = new VexText(70, -105, Arrays.asList(item2.getItemMeta().getDisplayName()), 1.25, skill_2H, 200);
                gui.addDynamicComponent(skill_2d);
                addList.add(skill_2d);
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            gui = VexViewAPI.getPlayerCurrentGui(player1);
            addList.addAll(Arrays.asList(skillQ, skillT, itemV, itemH, back2, skill_1, skill_2));
            int j = 1;
            while (j > 0) {
                gui = VexViewAPI.getPlayerCurrentGui(player1);
                for (VexComponents i : addList) {
                    gui.getVexGui().addComponent(i);
                }
                j -= 1;
            }

//            tp.sendMessage("目前的组件：");
//            gui=VexViewAPI.getPlayerCurrentGui(player1);
//            for(int i=0;i<gui.getVexGui().getComponents().size();i++)
//            {
//                if(gui.getVexGui().getComponents().get(i) instanceof VexImage)
//                {
//                    tp.sendMessage(((VexImage)gui.getVexGui().getComponents().get(i)).getUrl());
//                }
//            }

//            back2.setX(back2.getX() - d);
//            skillQ.setX(skillQ.getX() - d);
//            skillT.setX(skillT.getX() - d);
//            itemV.setX(itemV.getX() - d);
//            itemH.setX(itemH.getX() - d);
//            gui.addDynamicComponent(back2);
        };
        ButtonFunction f1 = player1 -> {
            OpenedVexGui gui = VexViewAPI.getPlayerCurrentGui(player1);
            gui.setButtonClickable("button_item", false);
            gui.setButtonClickable("button_skill", true);
            gui.setButtonClickable("button_state", true);
            clearScreen(gui);
            try {
                Thread.sleep(410);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            int d = getLength(-18.75, 3, 40);
            equip1.setX(10 + d);
            equip2.setX(10 + d);
            equip3.setX(10 + d);
            equip4.setX(93 + d);
            equip5.setX(93 + d);
            equip6.setX(93 + d);
            back1.setX(-45 + d);
            gui.addDynamicComponent(equip1);
            gui.addDynamicComponent(equip2);
            gui.addDynamicComponent(equip3);
            gui.addDynamicComponent(equip4);
            gui.addDynamicComponent(equip5);
            gui.addDynamicComponent(equip6);
            gui.addDynamicComponent(back1);
            gui = VexViewAPI.getPlayerCurrentGui(player1);
            moveComList(gui, Arrays.asList(equip1, equip2, equip3, equip4, equip5, equip6, back1), -18.75, 3, true, 40);
            try {
                Thread.sleep(410);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            gui.addDynamicComponent(slot1);
            gui.addDynamicComponent(slot2);
            gui.addDynamicComponent(slot3);
            gui.addDynamicComponent(slot4);
            gui.addDynamicComponent(slot5);
            gui.addDynamicComponent(slot6);
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            gui = VexViewAPI.getPlayerCurrentGui(player1);
            int j = 1;
            while (j > 0) {
                gui = VexViewAPI.getPlayerCurrentGui(player1);
                for (VexComponents i : Arrays.asList(equip1, equip2, equip3, equip4, equip5, equip6, back1)) {
                    gui.getVexGui().addComponent(i);
                }
                j -= 1;
            }

        };
        ButtonFunction f3 = player1 -> {
            OpenedVexGui gui = VexViewAPI.getPlayerCurrentGui(player1);
            gui.setButtonClickable("button_item", true);
            gui.setButtonClickable("button_skill", true);
            gui.setButtonClickable("button_state", false);
            clearScreen(gui);
            try {
                Thread.sleep(410);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            RAstate state = EntityInf.getPlayerState(player1);
            state = state.applyModifier(player1);
            VexScrollingList list = new VexScrollingList(-20, -180, 216, 180, 1000);
            VexSplitImage back3 = new VexSplitImage("[local]ISAC/inv-back-1.png", -45, -200, 0, 0, 256, 200, 256, 190, 256, 256);
            int d = getLength(-18.75, 3, 40);
            back3.setX(-45 + d);
            moveComList(gui, Arrays.asList(back3), -18.75, 3, true, 40);
            ConfigurationSection config = riseA.config;
            config = config.getConfigurationSection("description");
            addTextComponent(list, "§6§l|||§7§l| §f作战效能： §e" + state.getEffectiveness(), new LinkedList<>());
            addTextComponent(list, state.getAttrDes(AttrModifier.Attr.CRIT), config.getStringList("CRIT"));
            addTextComponent(list, state.getAttrDes(AttrModifier.Attr.CRIT_RATE), config.getStringList("CRIT_RATE"));
            addTextComponent(list, state.getAttrDes(AttrModifier.Attr.HEADSHOT_RATE), config.getStringList("HEADSHOT_RATE"));
            addTextComponent(list, state.getAttrDes(AttrModifier.Attr.DAMAGE), config.getStringList("DAMAGE"));
            addTextComponent(list, state.getAttrDes(AttrModifier.Attr.FINAL_DAMAGE), config.getStringList("FINAL_DAMAGE"));
            addTextComponent(list, state.getAttrDes(AttrModifier.Attr.TRUE_DAMAGE), config.getStringList("TRUE_DAMAGE"));
            addTextComponent(list, state.getAttrDes(AttrModifier.Attr.HP), config.getStringList("HP"));
            addTextComponent(list, state.getAttrDes(AttrModifier.Attr.HP_REGEN), config.getStringList("HP_REGEN"));
            addTextComponent(list, state.getAttrDes(AttrModifier.Attr.PERCENT_HP), config.getStringList("PERCENT_HP"));
            addTextComponent(list, state.getAttrDes(AttrModifier.Attr.PHYSICAL_RESISTANCE), config.getStringList("PHYSICAL_RESISTANCE"));
            addTextComponent(list, state.getAttrDes(AttrModifier.Attr.SPECIAL_RESISTANCE), config.getStringList("SPECIAL_RESISTANCE"));
            addTextComponent(list, state.getAttrDes(AttrModifier.Attr.PHYSICAL_PIERCING), config.getStringList("PHYSICAL_PIERCING"));
            addTextComponent(list, state.getAttrDes(AttrModifier.Attr.SPEED), config.getStringList("SPEED"));
            addTextComponent(list, state.getAttrDes(AttrModifier.Attr.PERCENT_DAMAGE), config.getStringList("PERCENT_DAMAGE"));
            addTextComponent(list, state.getAttrDes(AttrModifier.Attr.HIT), config.getStringList("HIT"));
            addTextComponent(list, state.getAttrDes(AttrModifier.Attr.AVOID), config.getStringList("AVOID"));
            addTextComponent(list, state.getAttrDes(AttrModifier.Attr.EXP_BOUNCE), config.getStringList("EXP_BOUNCE"));
            addTextComponent(list, state.getAttrDes(AttrModifier.Attr.ON_KILL_REGEN), config.getStringList("ON_KILL_REGEN"));
            addTextComponent(list, state.getAttrDes(AttrModifier.Attr.NF_ABILITY), config.getStringList("NF_ABILITY"));
            addTextComponent(list, state.getAttrDes(AttrModifier.Attr.DEBUFF_RESISTANCE), config.getStringList("DEBUFF_RESISTANCE"));
            addTextComponent(list, state.getAttrDes(AttrModifier.Attr.DAMAGE_RECEIVE), config.getStringList("DAMAGE_RECEIVE"));
            addTextComponent(list, state.getAttrDes(AttrModifier.Attr.SKILL_LEVEL), config.getStringList("SKILL_LEVEL"));
            addTextComponent(list, state.getAttrDes(AttrModifier.Attr.SKILL_DAMAGE), config.getStringList("SKILL_DAMAGE"));
            addTextComponent(list, state.getAttrDes(AttrModifier.Attr.DEBUFF_EFFECT), config.getStringList("DEBUFF_EFFECT"));
            addTextComponent(list, state.getAttrDes(AttrModifier.Attr.RECOVER_EFFECT), config.getStringList("RECOVER_EFFECT"));
            addTextComponent(list, state.getAttrDes(AttrModifier.Attr.SKILL_ACCELERATE), config.getStringList("SKILL_ACCELERATE"));
            addTextComponent(list, state.getAttrDes(AttrModifier.Attr.PULSE_RESISTANCE), config.getStringList("PULSE_RESISTANCE"));
            addTextComponent(list, "§6§l|||§7§l| §f已启用的天赋:", Arrays.asList("§f被动/主动"));
            for (TalentType j : state.activeTalent) {
                addTextComponent(list, "§6[§f§l" + riseA.talentMapReflect.get(j) + "§6]", TalentRefit.talentDescription.get(j));
            }
            try {
                Thread.sleep(410);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            gui = VexViewAPI.getPlayerCurrentGui(player1);
            gui.addDynamicComponent(list);
            gui = VexViewAPI.getPlayerCurrentGui(player1);
            int j = 1;
            while (j > 0) {
                gui = VexViewAPI.getPlayerCurrentGui(player1);
                for (VexComponents i : Arrays.asList(list, back3)) {
                    gui.getVexGui().addComponent(i);
                }
                j -= 1;
            }

        };
        button_skill.setFunction(f2);
        button_item.setFunction(f1);
        button_state.setFunction(f3);
        tmp.addAllComponents(Arrays.asList(equip1, equip2, equip3, equip4, equip5, equip6, playerDraw, back1, name, level1, level2, top));
        tmp.addAllComponents(Arrays.asList(slot1, slot2, slot3, slot4, slot5, slot6));
        tmp.addAllComponents(Arrays.asList(button_item, button_skill, button_state));
        VexViewAPI.openGui(player, tmp);
//        VexSplitImage image = new VexSplitImage("[local]ISAC/test-1.png", 10, 10,10,10, 350, 450,20,20, 20, 20);
//        VexSplitImageShow show = new VexSplitImageShow("test-1",0,image);
//        VexViewAPI.sendHUD(player, show);

//        VexImageShow QShow = new VexImageShow("skill-Q", skillQ, 5);
//        VexImageShow TShow = new VexImageShow("skill-T", skillT, 5);
//        VexImageShow VShow = new VexImageShow("item-V", itemV, 5);
//        VexImageShow HShow = new VexImageShow("item-H", itemH, 5);
//        VexViewAPI.sendHUD(player, QShow);
//        VexViewAPI.sendHUD(player, TShow);
//        VexViewAPI.sendHUD(player, VShow);
//        VexViewAPI.sendHUD(player, HShow);
    }

    public static void test1(Player player) {
//        VexInventoryGui tmp=new VexInventoryGui(,300,100,0,0);
        VexViewAPI.removeHUD(player, "test-1");
    }

    @EventHandler
    public void EquipSlotClick(VexSlotInteractEvent.Pre event) {
        Player player = event.getPlayer();
        int id = event.getId();
        if (100 <= id && id <= 106) {
            OpenedVexGui gui = VexViewAPI.getPlayerCurrentGui(player);
            List<VexComponents> list = gui.getVexGui().getComponents();
            for (int i = 0; i < list.size(); i++) {
                VexComponents tmp = list.get(i);
                PlayerInventory inv = player.getInventory();
                if (tmp instanceof VexSlot && ((VexSlot) tmp).getID() == id) {
                    VexInventoryGui gui1 = (VexInventoryGui) gui.getVexGui();
                    ItemStack item = gui1.getCrossItem();
                    if (item == null) {
                        switch (id) {

                            case 101: {
                                ModuleGui.setItem(player, 26, null);
                                break;
                            }
                            case 102: {
                                ModuleGui.setItem(player, 27, null);
                                break;
                            }
                            case 103: {
                                ModuleGui.setItem(player, 28, null);
                                break;
                            }
                            case 104: {
                                ModuleGui.setItem(player, 29, null);
                                break;
                            }
                            case 105: {
                                ModuleGui.setItem(player, 30, null);
                                break;
                            }
                            case 106: {
                                ModuleGui.setItem(player, 31, null);
                                break;
                            }
                        }
                    }
                    String tar = null;
                    switch (id) {
                        case 101: {
                            tar = "头盔";
                            break;
                        }
                        case 102: {
                            tar = "胸甲";
                            break;
                        }
                        case 103: {
                            tar = "枪套";
                            break;
                        }
                        case 104: {
                            tar = "背包";
                            break;
                        }
                        case 105: {
                            tar = "胸挂";
                            break;
                        }
                        case 106: {
                            tar = "护膝";
                            break;
                        }
                    }
                    List<String> itemType = riseAPI.getItemType(item);
                    if (itemType != null && !itemType.contains(tar)) event.setCancelled(true);
                    else {
                        switch (id) {
                            case 101: {
                                ModuleGui.setItem(player, 26, item);
                                break;
                            }
                            case 102: {
                                ModuleGui.setItem(player, 27, item);
                                break;
                            }
                            case 103: {
                                ModuleGui.setItem(player, 28, item);
                                break;
                            }
                            case 104: {
                                ModuleGui.setItem(player, 29, item);
                                break;
                            }
                            case 105: {
                                ModuleGui.setItem(player, 30, item);
                                break;
                            }
                            case 106: {
                                ModuleGui.setItem(player, 31, item);
                                break;
                            }
                        }
                    }
                    ModuleGui.saveGui(player, ModuleGui.guiList.get(player.getUniqueId()));
                    return;
                }
            }
        }
    }

    @EventHandler
    public void SkillSlotClick(VexSlotInteractEvent.Pre event) {
        Player player = event.getPlayer();
        int id = event.getId();
        if (201 <= id && id <= 202) {
            OpenedVexGui gui = VexViewAPI.getPlayerCurrentGui(player);
            List<VexComponents> list = gui.getVexGui().getComponents();
            for (int i = 0; i < list.size(); i++) {
                VexComponents tmp = list.get(i);
                if (tmp instanceof VexSlot && ((VexSlot) tmp).getID() == id) {
                    VexInventoryGui gui1 = (VexInventoryGui) gui.getVexGui();
                    ItemStack item = gui1.getCrossItem();
                    if (item != null) {
                        if (!item.getItemMeta().hasDisplayName()) {
                            event.setCancelled(true);
                            return;
                        }
                        if (!item.getItemMeta().getDisplayName().contains(riseA.moduleSkillS)) {
                            event.setCancelled(true);
                            return;
                        }
                    } else {
                        event.setCancelled(true);
                        return;
                    }
                    for (int j = 0; j < list.size(); j++) {
                        VexComponents t1 = list.get(j);
                        if (t1 instanceof VexText && ((VexText) t1).getText().get(0).contains(riseA.moduleSkillS)) {
                            gui.removeDynamicComponent(((VexText) t1));
                        }
                    }
                    Inventory inv = ModuleGui.guiList.get(player.getUniqueId());
                    ActiveBase now = ActiveAPI.getActiveSkill(event.getItem());
                    ItemStack item1, item2;
                    if (now != null) {
                        String name = now.type.name();
                        SkillBase skill = new SkillBase("技能冷却检测", 0, name.substring(0, name.indexOf('_')), 0, 1, 0, name.substring(0, name.indexOf('_')), null, null);
                        boolean sec = SkillAPI.performSkill(player, skill, false);
                        List<ActiveBase> sk = ConstantEffect.constant.get(player.getUniqueId());
                        if (sk != null && sk.contains(now)) sec = false;
                        if (ConstantEffect.usingShield.contains(player.getUniqueId())) sec = false;
                        if (!sec) {
                            event.setCancelled(true);
                            player.sendMessage("§f[§6ISSAC§f]§c该技能冷却/正在使用中，暂时无法更换！");
                            return;
                        }
                    }
                    if (id == 201) inv.setItem(15, item);
                    else inv.setItem(24, item);
                    ModuleGui.guiList.put(player.getUniqueId(), inv);
                    item1 = inv.getItem(15);
                    item2 = inv.getItem(24);
                    File loc = new File(riseA.modFolder, player.getName() + ".yml");
                    if (!loc.exists()) {
                        try {
                            loc.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    FileConfiguration file = YamlConfiguration.loadConfiguration(loc);
                    if (item1 != null && item1.getType() != Material.AIR) {
                        file.set("Sk1", item1);
                    } else file.set("Sk1", null);
                    if (item2 != null && item2.getType() != Material.AIR) {
                        file.set("Sk2", item2);
                    } else file.set("Sk2", null);
                    try {
                        file.save(loc);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    item1 = inv.getItem(15);
                    item2 = inv.getItem(24);
                    List<VexComponents> addList = new LinkedList<>();
                    if (item1 != null) {
                        List<String> t1 = item1.getItemMeta().getLore();
                        ActiveBase skill = ActiveAPI.getActiveSkill(item1);
                        t1.addAll(skill.ApplyMod(EntityInf.getPlayerState(player)));
                        VexHoverText skill_1H = new VexHoverText(t1);
                        VexText skill_1d = new VexText(-15, -175, Arrays.asList(item1.getItemMeta().getDisplayName()), 1.25, skill_1H, 200);
                        gui.addDynamicComponent(skill_1d);
                        addList.add(skill_1d);
                    }
                    if (item2 != null) {
                        List<String> t2 = item2.getItemMeta().getLore();
                        ActiveBase skill = ActiveAPI.getActiveSkill(item2);
                        t2.addAll(skill.ApplyMod(EntityInf.getPlayerState(player)));
                        VexHoverText skill_2H = new VexHoverText(t2);
                        VexText skill_2d = new VexText(70, -105, Arrays.asList(item2.getItemMeta().getDisplayName()), 1.25, skill_2H, 200);
                        gui.addDynamicComponent(skill_2d);
                        addList.add(skill_2d);
                    }
                    int j = 1;
                    while (j > 0) {
                        gui = VexViewAPI.getPlayerCurrentGui(player);
                        for (VexComponents k : addList) {
                            gui.getVexGui().addComponent(k);
                        }
                        j -= 1;
                    }
                    return;
                }
            }
        }
    }
}
