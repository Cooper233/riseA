package org.rise.activeSkills.effect;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.rise.EntityInf;
import org.rise.State.Attr;
import org.rise.State.RAState;
import org.rise.activeSkills.ConstantEffect;
import org.rise.skill.Enable.EnableEffectBase;
import org.rise.skill.SkillAPI;
import org.rise.skill.SkillBase;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public abstract class ShieldBase extends ActiveBase {
    public double maxHealth;
    public double recoverSpeed;
    public double armor;
    public String shieldName;

    @Override
    public List<String> ApplyMod(RAState state) {
        List<String> list = new LinkedList<>();
        double mod = 1.0 + state.getAttr(Attr.SKILL_LEVEL) * 0.3;
        double cd = this.cd * (1.0 - state.getAttr(Attr.SKILL_LEVEL) * 0.05) / (1.0 + state.getAttr(Attr.SKILL_ACCELERATE) / 100);
        double max = this.maxHealth * mod * (1.0 + levelModifier * state.getAttr(Attr.HP) / 50);
        double rc = this.recoverSpeed * mod * (1.0 + state.getAttr(Attr.RECOVER_EFFECT) / 100);
        double ar = this.armor * mod;
        list.add("§6应用加成后数值：");
        list.add("§7护盾耐久         §e§l" + String.format("%.2f", max));
        list.add("§7每秒恢复         §e§l" + String.format("%.2f", rc));
        list.add("§7护盾装甲         §e§l" + String.format("%.2f", ar));
        list.add("§7冷却时间         §e§l" + String.format("%.2f", cd));
        return list;
    }

    @Override
    public void skillAffect(Player player, boolean keyState) {
        if (!keyState) return;
        RAState state = EntityInf.getPlayerState(player).analyze(2, player);
        state.applyModifier(player);
        List<ActiveBase> list = ConstantEffect.getAffectingSkill(player);
        if (!ConstantEffect.usingShield.contains(player.getUniqueId())) {
            SkillBase skill = new SkillBase("展开" + shieldName, 0, "SHIELD", 0, 1, 0, "SHIELD", null, new EnableEffectBase("§f[§6ISAAC§f]已展开" + shieldName, null, null, "modularwarfare:effect.shield_open"));
            boolean ifSec = SkillAPI.performSkill(player, skill, false);
            if (!ifSec) return;
            UUID i = player.getUniqueId();
            list.add(this);
            ConstantEffect.constant.put(i, list);
            ConstantEffect.usingShield.add(i);
            double mod = 1.0 + state.getAttr(Attr.SKILL_LEVEL) * levelModifier;
            double hp;
            double max = this.maxHealth * (mod + levelModifier * state.getAttr(Attr.HP) / 50);
//            player.sendMessage(""+max);
            hp = ConstantEffect.ShieldHealth.getOrDefault(i, max);
            ConstantEffect.ShieldHealth.put(i, hp);
            BossBar bar;
            if (ConstantEffect.shieldGUI.containsKey(i)) bar = ConstantEffect.shieldGUI.get(i);
            else bar = Bukkit.createBossBar("护盾生命值", BarColor.WHITE, BarStyle.SEGMENTED_12);
            bar.setProgress(hp / max);
            bar.setVisible(true);
            bar.setColor(BarColor.WHITE);
            if (hp / max <= 0.3) bar.setColor(BarColor.RED);
            else bar.setColor(BarColor.WHITE);
            bar.addPlayer(player);
            ConstantEffect.shieldGUI.put(i, bar);
            ConstantEffect.lastUseShield.put(i, System.currentTimeMillis());
            ConstantEffect.lastShieldDamaged.put(i, System.currentTimeMillis());
        } else {
            UUID i = player.getUniqueId();
            player.playSound(player.getEyeLocation(), "modularwarfare:effect.shield_close", 16, 1);
            ConstantEffect.usingShield.remove(i);
            ConstantEffect.lastUseShield.put(i, System.currentTimeMillis());
        }
    }

    @Override
    public void ticklyCheck(Player player) {

    }

    @Override
    public void secondlyCheck(Player player) {
        UUID i = player.getUniqueId();
        RAState state = EntityInf.getPlayerState(player).analyze(2, player);
        state.applyModifier(player);
        BossBar bar = ConstantEffect.shieldGUI.get(i);
        List<ActiveBase> list = ConstantEffect.getAffectingSkill(player);
        if (!ConstantEffect.ShieldHealth.containsKey(i)) return;
        double hp = ConstantEffect.ShieldHealth.get(i);
        double mod = 1.0 + state.getAttr(Attr.SKILL_LEVEL) * levelModifier;
        double max = this.maxHealth * (mod + levelModifier * state.getAttr(Attr.HP) / 50);
//        player.sendMessage(""+max);
        double rc = this.recoverSpeed * mod * (1.0 + state.getAttr(Attr.RECOVER_EFFECT) / 100);
        if (ConstantEffect.usingShield.contains(i)) {
            if (System.currentTimeMillis() - ConstantEffect.lastShieldDamaged.get(i) > 3000) {
                hp += rc;
                hp = Math.min(hp, max);
                ConstantEffect.ShieldHealth.put(i, hp);
                bar.setProgress(hp / max);
                if (hp / max <= 0.3) bar.setColor(BarColor.RED);
                else bar.setColor(BarColor.WHITE);
            }
        } else {
            if (System.currentTimeMillis() - ConstantEffect.lastUseShield.get(i) > 1000) {
                hp += 2 * rc;
                hp = Math.min(hp, max);
                ConstantEffect.ShieldHealth.put(i, hp);
                bar.setProgress(hp / max);
                if (hp / max <= 0.3) bar.setColor(BarColor.RED);
                else bar.setColor(BarColor.WHITE);
                if (hp == max) {
                    list.remove(this);
                    ConstantEffect.constant.put(i, list);
                    bar.setVisible(false);
                    bar.removePlayer(player);
                }
            }
        }
    }
}
