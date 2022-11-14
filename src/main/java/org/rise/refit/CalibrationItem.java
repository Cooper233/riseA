package org.rise.refit;

import org.bukkit.inventory.ItemStack;
import org.rise.State.AttrModifier;
import org.rise.extra.Pair;
import org.rise.riseA;
import org.rise.talent.TalentType;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CalibrationItem {
    public ItemStack self;
    public String type;
    public Map<String, List<Pair<AttrModifier.Attr, Double>>> data = new HashMap<>();
    public Map<String, List<Integer>> dataPos = new HashMap<>();
    public TalentType talent;
    public boolean calibrated = false;
    public int caliPos = 0;
    public AttrModifier.Attr caliAttr;
    public String caliType;
    public int level;

    public double getAttrNum(String id, AttrModifier.Attr attr) {
        List<Pair<AttrModifier.Attr, Double>> res = data.get(id);
        if (res == null) return 0;
        for (Pair<AttrModifier.Attr, Double> i : res) {
            if (i.getKey() == attr) {
                return i.getValue();
            }
        }
        return 0;
    }

    public CalibrationItem(ItemStack item) {
        self = item;
        if (!item.getItemMeta().hasLore()) return;
        List<String> lore = item.getItemMeta().getLore();
        for (String s : lore) {
            if (s.contains(riseA.refitMarkS)) {
                type = s.replaceAll(riseA.refitMarkS, "");
            } else if (s.contains(riseA.talentS)) {
                String s1 = lore.get(lore.indexOf(s) + 1);
                s1 = s1.replaceAll("§6\\[§f§l", "");
                s1 = s1.replaceAll("§6\\]", "");
                s1 = s1.replaceAll("§c\\[§f§l", "");
                s1 = s1.replaceAll("§c\\]", "");
                s1 = s1.replaceAll("§2\\[§f§l", "");
                s1 = s1.replaceAll("§2\\]", "");
                if (riseA.talentMap.containsKey(s1)) {
                    talent = riseA.talentMap.get(s1);
                }
            } else if (s.contains(riseA.levelMarkS)) {
                if (s.contains("I")) level = 1;
                if (s.contains("II")) level = 2;
                if (s.contains("III")) level = 3;
                if (s.contains("IV")) level = 4;
                if (s.contains("V")) level = 5;
            } else {
                if (type != null) {
                    for (String na : riseA.refitBaseMap.keySet()) {
                        RefitBase tmp = riseA.refitBaseMap.get(na);
                        if (!s.contains(tmp.prefix)) continue;
                        List<Pair<AttrModifier.Attr, Double>> res = data.get(tmp.id);
                        List<Integer> pos = dataPos.get(tmp.id);
                        if (res == null) res = new LinkedList<>();
                        if (pos == null) pos = new LinkedList<>();
                        for (AttrModifier.Attr i : riseA.attrName.keySet()) {
                            if (s.contains(riseA.attrName.get(i))) {
                                String t = s.replaceAll("§[0-9]", "§f");
                                Pattern p = Pattern.compile("[0-9]+(\\.[0-9]+)?");
                                Matcher m = p.matcher(t);
                                double num = 0;
                                if (m.find()) num = Double.parseDouble(m.group());//只加第一次出现的浮点数
                                Pair<AttrModifier.Attr, Double> t1 = new Pair<>(i, num);
                                res.add(t1);
                                pos.add(lore.indexOf(s));
                                if (s.contains(riseA.calibratedMarkS)) {
                                    calibrated = true;
                                    caliPos = res.size() - 1;
                                    caliType = na;
                                    caliAttr = i;
                                }
                                break;
                            }
                        }
                        data.put(tmp.id, res);
                        dataPos.put(tmp.id, pos);
                    }
                }
            }
        }
    }
}
