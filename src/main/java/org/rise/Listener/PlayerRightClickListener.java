package org.rise.Listener;

import lk.vexview.event.KeyBoardPressEvent;
import lk.vexview.event.MinecraftKeys;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.rise.GUI.testgui;
import org.rise.refit.RefitBase;
import org.rise.riseA;
import org.rise.skill.SkillAPI;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class PlayerRightClickListener implements Listener {
    @EventHandler
    public void process(KeyBoardPressEvent event) {
        int key = event.getKey();
        if (!MinecraftKeys.KEY_X.isTheKey(key)) return;
        Player player = event.getPlayer();
        ItemStack s = player.getEquipment().getItemInMainHand();
        boolean f = RefitBase.performRefit(s);
        if (f) {
            event.getPlayer().sendMessage("§f[§6ISAAC§f]装备 " + s.getItemMeta().getDisplayName() + " §b已认证§f！");
            event.getPlayer().sendMessage("§f[§6ISAAC§f]§6改装模块§f已部署!");
            player.playSound(player.getLocation(), riseA.refitSound, 1, 1);
        }
        event.getPlayer().getEquipment().setItemInMainHand(s);
    }

    @EventHandler
    public void skill_process(KeyBoardPressEvent event) {
        int key = event.getKey();
        if (!MinecraftKeys.KEY_G.isTheKey(key) || !event.getEventKeyState()) return;
        Player player = event.getPlayer();
        ItemStack s = player.getEquipment().getItemInMainHand();
        if (s.hasItemMeta() && s.getItemMeta().hasLore()) {
            List<String> lore = s.getItemMeta().getLore();
            for (String i : lore) {
                for (String j : riseA.pressSkillMap.keySet()) {
                    if (i.contains(j)) {
                        SkillAPI.performSkill(event.getPlayer(), riseA.getSkill(riseA.pressSkillMap.get(j)), false);
                    }
                }
            }
        }
    }

    @EventHandler
    public void fixMWF(KeyBoardPressEvent event) {
        int key = event.getKey();
        if (!MinecraftKeys.KEY_J.isTheKey(key) || !event.getEventKeyState()) return;
        Player player = event.getPlayer();
        net.minecraft.server.v1_12_R1.Entity iplayer;
        CraftItemStack itemStack;
        ItemStack item = player.getInventory().getItemInMainHand();
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
            NBTTagCompound nbt = new NBTTagCompound();
            nbt = cItem.getTag();
            if (nbt.hasKey("ammo")) nbt.remove("ammo");
            cItem.setTag(nbt);
            Object obj1 = null;
            for (Method j : method) {
                if (j.getName().equals("asBukkitCopy")) {
                    try {
                        obj1 = j.invoke(CraftItemStack.class, cItem);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
            ItemStack a = CraftItemStack.asBukkitCopy(cItem);
            player.getInventory().setItemInMainHand(a);
        }
    }

    @EventHandler
    public void openTestMenu(KeyBoardPressEvent event) {
        int key = event.getKey();
        if (!MinecraftKeys.KEY_I.isTheKey(key) || !event.getEventKeyState()) return;
        Player player = event.getPlayer();
        testgui.test(player);
    }
}
