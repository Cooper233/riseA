package org.rise;

import net.minecraft.server.v1_12_R1.ItemStack;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.NBTTagList;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;
import org.rise.GUI.calibrationExtract.AttrSelectGUI;
import org.rise.GUI.calibrationExtract.TypeSelectGUI;
import org.rise.GUI.testgui;
import org.rise.Inventory.ModuleGui;
import org.rise.Inventory.ModuleL2;
import org.rise.Inventory.ModuleL3;
import org.rise.Listener.*;
import org.rise.State.Attr;
import org.rise.State.ExtraHp;
import org.rise.activeSkills.ActiveAPI;
import org.rise.activeSkills.ActiveListener;
import org.rise.activeSkills.ConstantEffect;
import org.rise.effect.CustomEffect;
import org.rise.handler.EntityUpdate;
import org.rise.refit.CalibrationData;
import org.rise.refit.RefitBase;
import org.rise.refit.TalentRefit;
import org.rise.skill.Effect.EffectBase;
import org.rise.skill.Effect.EffectCustomEffect;
import org.rise.skill.SkillAPI;
import org.rise.skill.SkillBase;
import org.rise.skill.TargetBase;
import org.rise.talent.TalentType;
import org.rise.team.TeamBase;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static org.rise.State.Attr.*;

public class
riseA extends JavaPlugin implements Listener {
    public static String critChanceS;
    public static String critRateS;
    public static String headshotRateS;
    public static String nonHeadshotS;
    public static String damageS;
    public static String finalDamageS;
    public static String trueDamageS;
    public static String hpS;
    public static String percentHpS;
    public static String hpRegenS;
    public static String physicalResistanceS;
    public static String specialResistanceS;
    public static String physicalPiercingS;
    public static String avoidRateS;
    public static String hitRateS;
    public static String speedS;
    public static String percentDamageS = "";
    public static String buffGiverS;
    public static String suitMarkS;
    public static String overChargeS;
    public static String typeBuffS;
    public static String typeMarkS;
    public static String healthBuffS;
    public static String expBounceS;
    public static String onKillRegenS;
    public static String nfAbilityS;
    public static String debuffResistanceS;
    public static String skillLevelS;
    public static String skillDamageS;
    public static String debuffEffectS;
    public static String recoverEffectS;
    public static String skillAccelerateS;
    public static String pulseResistanceS;
    public static String bindingS;
    public static String notBelongS;
    public static String talentS;
    public static String refitMarkS;
    public static String levelMarkS;
    public static String module1S;
    public static String module2S;
    public static String module3S;
    public static String moduleSkillS;
    public static String moduleName;
    public static String moduleError;
    public static String moduleShift;
    public static String moduleTitle;
    public static String skillBaseLevelS;
    public static String calibratedMarkS;
    public static List<String> moduleDescribe = new LinkedList<>();
    public static Map<String, PotionEffectType> buffMap = new HashMap<>();
    public static Map<String, TalentType> talentMap = new HashMap<>();
    public static Map<TalentType, String> talentMapReflect = new HashMap<>();
    public static Map<Attr, String> attrName = new HashMap<>();
    public static double critChanceMax;
    public static double critRateMin;
    public static double critRateDefault;
    public static double critRateMax;
    public static double headshotRateDefault;
    public static double finalDamageMax;
    public static double trueDamageMax;
    public static double percentHpMax;
    public static double hpRegenMax;
    public static double physicalMax;
    public static double physicalPiercingMax;
    public static double avoidRateMax;
    public static double hitRateMax;
    public static double hitRateDefault;
    public static double speedMax;
    public static double percentDamageMax;
    public static double extraHpMax;
    public static Map<String, SkillBase> skills = new HashMap<>();
    public static Map<String, RefitBase> refitBaseMap = new HashMap<>();
    public static List<Integer> npcFriendly = new LinkedList<>();
    public static List<Integer> npcEnemy = new LinkedList<>();
    public static List<String> npcElc = new LinkedList<>();


    public static String downedSound;
    public static String needReviveSound;
    public static String revivedSound;
    public static String protectSound;
    public static String refitSound;
    public static String killedSound;

    public static class suitEffect {
        public int amont;
        public List<String> lores;

        suitEffect(int a, List<String> l) {
            amont = a;
            lores = l;
        }
    }

    public static Map<String, List<suitEffect>> suitMap = new HashMap<>();
    public static Map<String, List<String>> typeMap = new HashMap<>();
    public static Map<String, String> reflexTypeMap = new HashMap<>();
    public static List<String> secAbleType = new LinkedList<>();
    public static Map<String, String> pressSkillMap = new HashMap<>();//技能标识-技能id


    public static File folder = null;
    public static File modFolder = null;
    public static File calibrateFolder = null;
    public static File importFolder = null;
    public static File entityData = null;
    public static Map<String, org.bukkit.inventory.ItemStack> importItems = new HashMap<>();

    public static World tmpWorld = null;
    public static ConfigurationSection config;

    public static class playerSelectData {
        public String type;
        public String slot;
        public Attr attr;

        public playerSelectData(String t, String s, Attr a) {
            type = t;
            slot = s;
            attr = a;
        }
    }

    ;
    public static Map<Player, playerSelectData> CaliDataMap = new HashMap<>();

    /***
     * 重载配置文件
     */
    public void configReload() {
        reloadConfig();
        skills.clear();
        importItems.clear();

        suitMap.clear();
        ;
        typeMap.clear();
        reflexTypeMap.clear();
        pressSkillMap.clear();
        npcFriendly.clear();
        npcEnemy.clear();
        npcElc.clear();
        ConstantEffect.constant.clear();
        ConstantEffect.usingGel.clear();
        ConstantEffect.platformId.clear();
        ConstantEffect.lastActive.clear();
        config = getConfig();
        //关键词读取
        critChanceS = config.getString("critChance");
        attrName.put(CRIT, critChanceS);
        critRateS = config.getString("critRate");
        attrName.put(CRIT_RATE, critRateS);
        headshotRateS = config.getString("headshotRate");
        attrName.put(HEADSHOT_RATE, headshotRateS);
        nonHeadshotS = config.getString("nonHeadshot");
        damageS = config.getString("damage");
        attrName.put(DAMAGE, damageS);
        finalDamageS = config.getString("finalDamage");
        attrName.put(FINAL_DAMAGE, finalDamageS);
        trueDamageS = config.getString("trueDamage");
        attrName.put(TRUE_DAMAGE, trueDamageS);
        hpS = config.getString("hp");
        attrName.put(HP, hpS);
        percentHpS = config.getString("percentHp");
        attrName.put(PERCENT_DAMAGE, percentHpS);
        hpRegenS = config.getString("hpRegen");
        attrName.put(HP_REGEN, hpRegenS);
        physicalResistanceS = config.getString("physicalResistance");
        attrName.put(PHYSICAL_RESISTANCE, physicalResistanceS);
        specialResistanceS = config.getString("specialResistance");
        attrName.put(SPECIAL_RESISTANCE, specialResistanceS);
        physicalPiercingS = config.getString("physicalPiercing");
        attrName.put(PHYSICAL_PIERCING, physicalPiercingS);
        avoidRateS = config.getString("avoidRate");
        attrName.put(AVOID, avoidRateS);
        hitRateS = config.getString("hitRate");
        attrName.put(HIT, hitRateS);
        speedS = config.getString("speed");
        attrName.put(SPEED, speedS);
        percentDamageS = config.getString("percentDamage");
        attrName.put(PERCENT_DAMAGE, percentDamageS);
        buffGiverS = config.getString("buffGiver");
        overChargeS = config.getString("overCharge");
        suitMarkS = config.getString("suitMark");
        typeBuffS = config.getString("typeBuff");
        typeMarkS = config.getString("typeMark");
        healthBuffS = config.getString("healthBuff");
        expBounceS = config.getString("expBounce");
        attrName.put(EXP_BOUNCE, expBounceS);
        onKillRegenS = config.getString("onKillRegen");
        attrName.put(ON_KILL_REGEN, onKillRegenS);
        nfAbilityS = config.getString("nfAbility");
        attrName.put(NF_ABILITY, nfAbilityS);
        debuffResistanceS = config.getString("debuffResistance");
        attrName.put(DEBUFF_RESISTANCE, debuffResistanceS);
        skillLevelS = config.getString("skillLevel");
        attrName.put(SKILL_LEVEL, skillLevelS);
        skillDamageS = config.getString("skillDamage");
        attrName.put(SKILL_DAMAGE, skillDamageS);
        debuffEffectS = config.getString("debuffEffect");
        attrName.put(DEBUFF_EFFECT, debuffEffectS);
        recoverEffectS = config.getString("recoverEffect");
        attrName.put(RECOVER_EFFECT, recoverEffectS);
        skillAccelerateS = config.getString("skillAccelerate");
        attrName.put(SKILL_ACCELERATE, skillAccelerateS);
        pulseResistanceS = config.getString("pulseResistance");
        attrName.put(PULSE_RESISTANCE, pulseResistanceS);
        bindingS = config.getString("binding");
        notBelongS = config.getString("notBelong");
        talentS = config.getString("talent");
        refitMarkS = config.getString("refitMark");
        levelMarkS = config.getString("levelMark");
        module1S = config.getString("module1");
        module2S = config.getString("module2");
        module3S = config.getString("module3");
        moduleSkillS = config.getString("moduleSkill");
        moduleDescribe = config.getStringList("moduleDescribe");
        moduleName = config.getString("moduleName");
        moduleError = config.getString("moduleError");
        moduleShift = config.getString("moduleShift");
        moduleTitle = config.getString("moduleTitle");
        skillBaseLevelS = config.getString("skillBaseLevel");
        calibratedMarkS = config.getString("calibratedMark");
        //数值上限读取
        critChanceMax = config.getDouble("critChanceMax");
        critRateDefault = config.getDouble("critRateDefault");
        critRateMin = config.getDouble("critRateMin");
        critRateMax = config.getDouble("critRateMax");
        headshotRateDefault = config.getDouble("headshotRateDefault");
        finalDamageMax = config.getDouble("finalDamageMax");
        trueDamageMax = config.getDouble("trueDamageMax");
        percentHpMax = config.getDouble("percentHpMax");
        hpRegenMax = config.getDouble("hpRegenMax");
        physicalMax = config.getDouble("physicalMax");
        physicalPiercingMax = config.getDouble("physicalPiercingMax");
        avoidRateMax = config.getDouble("avoidRateMax");
        hitRateMax = config.getDouble("hitRateMax");
        hitRateDefault = config.getDouble("hitRateDefault");
        speedMax = config.getDouble("speedMax");
        percentDamageMax = config.getDouble("percentDamageMax");
        extraHpMax = config.getDouble("extraHpMax");
        downedSound = config.getString("downed");
        needReviveSound = config.getString("needRevive");
        revivedSound = config.getString("revived");
        killedSound = config.getString("killed");
        refitSound = config.getString("refit");
        protectSound = config.getString("protect");
        revivedSound = config.getString("revived");
        revivedSound = config.getString("revived");
        npcFriendly.addAll(config.getIntegerList("npc-friend"));
        npcEnemy.addAll(config.getIntegerList("npc-enemy"));
        attrName.put(DAMAGE_RECEIVE, "受到伤害比例");
        ConfigurationSection pressSkill = config.getConfigurationSection("pressSkill");
        for (String i : pressSkill.getKeys(false)) {
            pressSkillMap.put(pressSkill.getString(i), i);
        }
        npcElc.addAll(config.getStringList("npc-elc"));
        ConfigurationSection suits = config.getConfigurationSection("suits");
        Set<String> suitNames = suits.getKeys(false);
        for (String i : suitNames) {
            ConfigurationSection now = suits.getConfigurationSection(i);
            String Symbol = now.getString("Symbol");
            List<suitEffect> eff = new LinkedList<>();
            ConfigurationSection attr = now.getConfigurationSection("Attr");
            for (int j = 1; j < 10; j++) {
                if (!attr.contains(Integer.toString(j))) continue;
                eff.add(new suitEffect(j, attr.getStringList(Integer.toString(j))));
            }
            suitMap.put(Symbol, eff);
        }
        ConfigurationSection typeList = config.getConfigurationSection("typeList");
        Set<String> typeNames = typeList.getKeys(false);
        for (String i : typeNames) {
            typeMap.put(i, typeList.getStringList(i));
            for (String j : typeList.getStringList(i)) {
                reflexTypeMap.put(j, i);
            }
        }
        File skillsLoc = new File(riseA.folder, "Skills");
        if (!skillsLoc.exists()) {
            skillsLoc.mkdirs();
            saveResource("Skills/Example.yml", false);
        }
        for (File i : Objects.requireNonNull(skillsLoc.listFiles())) {
            ConfigurationSection file = YamlConfiguration.loadConfiguration(i);
            for (String j : file.getKeys(false)) {
                SkillBase skill = new SkillBase(file.getConfigurationSection(j));
                skills.put(skill.name, skill);
            }
        }
        for (File i : Objects.requireNonNull(importFolder.listFiles())) {
            ConfigurationSection file = YamlConfiguration.loadConfiguration(i);
            for (String j : file.getKeys(false)) {
                importItems.put(j, file.getItemStack(j));
            }
        }
        secAbleType = config.getStringList("secAbleType");
        File refitLoc = new File(riseA.folder, "refit.yml");
        if (!refitLoc.exists()) {
            saveResource("refit.yml", false);
        }
        ConfigurationSection refit = YamlConfiguration.loadConfiguration(refitLoc);
        RefitBase.refitType.clear();
        refitBaseMap.clear();
        RefitBase.refitType.addAll(refit.getStringList("typeList"));
        TalentRefit.init(refit.getConfigurationSection("talent"));
        refit = refit.getConfigurationSection("refit");
        for (String i : refit.getKeys(false)) {
            ConfigurationSection rb = refit.getConfigurationSection(i);
            refitBaseMap.put(i, new RefitBase(rb));
        }
        ConfigurationSection activeSound = config.getConfigurationSection("activeSound");
        for (String i : activeSound.getKeys(false)) {
            ActiveAPI.activeSound.put(i.toUpperCase(), activeSound.getString(i));
        }
        ActiveAPI.init();
        CalibrationData.init();
        entityData = new File(folder, "entityData.yml");
        if (!entityData.exists()) {
            try {
                entityData.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("rsa")) {
            if (args.length == 0) {
                sender.sendMessage("riseA已启用");
            } else {
                //打开模块菜单
                if (args.length == 1 && Objects.equals(args[0], "mod")) {
                    if (sender instanceof Player) {
                        if (!ModuleGui.guiList.containsKey(((Player) sender).getUniqueId())) {
                            ModuleGui.guiInit((Player) sender, true);
                        }
                        ((Player) sender).openInventory(ModuleGui.guiList.get(((Player) sender).getUniqueId()));
                    }
                    return true;
                }
                //改装手中物品（不推荐）
                if (Objects.equals(args[0], "rf")) {
                    Player player = (Player) sender;
                    org.bukkit.inventory.ItemStack s = player.getEquipment().getItemInMainHand();
                    boolean b = RefitBase.performRefit(s);
                    if (b) player.sendMessage("§e你手中的装备已改造！");
                    player.getEquipment().setItemInMainHand(s);
                    return true;
                }
                //队伍相关
                if (Objects.equals(args[0], "team")) {
                    Player player = (Player) sender;
                    if (args.length == 1) {
                        if (TeamBase.getNowTeam(player) == -1) {
                            player.sendMessage("§6[§fISAAC§6]§c>>§f你目前不在小队中");
                        } else {
                            int t1 = TeamBase.getNowTeam(player);
                            Player t = Bukkit.getPlayer(TeamBase.teamLeader.get(t1));
                            player.sendMessage("§6[§fISAAC§6]§b>>§f小队信息");
                            player.sendMessage("§f队长：" + t.getDisplayName());
                            player.sendMessage("§6>>§f小队成员");
                            List<UUID> team = TeamBase.teamInfo.get(t.getUniqueId());
                            for (UUID i : team) {
                                player.sendMessage("|-" + Bukkit.getPlayer(i).getDisplayName());
                            }
                        }
                        if (TeamBase.teamRequest.containsKey(player.getUniqueId())) {
                            player.sendMessage("§6[§fISAAC§6]§6//§f待处理的请求___");
                            TeamBase.request request = TeamBase.teamRequest.get(player.getUniqueId());
                            String msg = "§b|>§f";
                            if (request.type == 0) {
                                msg += "加入你的小队";
                            }
                            if (request.type == 1) {
                                msg += "邀请加入小队";
                            }
                            msg += "  请求者：" + Bukkit.getPlayer(request.id).getDisplayName();
                        }
                    }
                    if (args.length == 2) {
                        if (!TeamBase.teamRequest.containsKey(player.getUniqueId())) {
                            player.sendMessage("§6[§fISAAC§6]§c没有待处理的请求");
                            return true;
                        }
                        TeamBase.request request = TeamBase.teamRequest.get(player.getUniqueId());
                        if (System.currentTimeMillis() - request.time > 30000) {
                            player.sendMessage("§6[§fISAAC§6]§c请求已失效！");
                            TeamBase.teamRequest.remove(player.getUniqueId());
                        }
                        if (Objects.equals(args[1], "ac")) {
                            player.sendMessage("§6[§fISAAC§6]§2已同意请求");
                            if (request.type == 0) {
                                TeamBase.joinTeam(Bukkit.getPlayer(request.id), player);
                            }
                            if (request.type == 1) {
                                TeamBase.joinTeam(player, Bukkit.getPlayer(request.id));
                            }
                            TeamBase.teamRequest.remove(player.getUniqueId());
                        }
                        if (Objects.equals(args[1], "de")) {
                            player.sendMessage("§6[§fISAAC§6]§c已拒绝请求");
                            TeamBase.teamRequest.remove(player.getUniqueId());
                        }
                    }
                }
                if (Objects.equals(args[0], "leave")) {
                    TeamBase.leaveTeam((Player) sender);
                }
                if (Objects.equals(args[0], "join")) {
                    TeamBase.sendJoinRequest((Player) sender, Bukkit.getPlayer(args[1]));
                }
                if (Objects.equals(args[0], "invite")) {
                    TeamBase.sendInvite((Player) sender, Bukkit.getPlayer(args[1]));
                }
                if (!sender.isOp()) return false;
                //以下是op的测试命令，或者是技能触发命令
                if (args.length == 1 && Objects.equals(args[0], "reload")) {
                    configReload();
                }
                //维克的测试
                if (args.length == 2 && Objects.equals(args[0], "vict")) {
                    if (!sender.isOp()) return false;
                    org.bukkit.inventory.ItemStack a = ((Player) sender).getInventory().getItemInMainHand();
                    Method method[] = CraftItemStack.class.getMethods();
                    Object obj = null;
                    for (Method i : method) {
                        if (i.getName().equals("asNMSCopy")) {
                            try {
                                obj = i.invoke(CraftItemStack.class, a);
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    ItemStack item = (ItemStack) obj;
                    NBTTagCompound nbt = item.getTag();
                    byte[] arr = nbt.getByteArray("Instance");
                    int ammo = (((int) arr[160]) << 8) | arr[161];
                    sender.sendMessage("当前弹药:" + ammo);
                    if (Objects.equals(args[1], "")) args[1] = "1";
                    int ammo1 = Integer.parseInt(args[1]);
                    byte a1 = (byte) (ammo1 >> 8), a2 = (byte) (ammo1 & 255);
                    arr[160] = a1;
                    arr[161] = a2;
                    nbt.setByteArray("Instance", arr);
                    nbt.setInt("Ammo", ammo1);
                    item.setTag(nbt);
                    for (Method i : CraftItemStack.class.getMethods()) {
                        if (i.getName().equals("asBukkitCopy")) {
                            try {
                                a = (org.bukkit.inventory.ItemStack) i.invoke(CraftItemStack.class, item);
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    PlayerInventory inv = ((Player) sender).getInventory();
                    inv.setItemInMainHand(a);
                }
                //给予自身30秒的额外生命值
                if (args.length == 2 && Objects.equals(args[0], "exhp")) {
                    ExtraHp.addExHp(EntityInf.getEntityExtraHp((Entity) sender), new ExtraHp(Double.parseDouble(args[1]), 30000), (Entity) sender);
                }
                //指令释放技能
                if (Objects.equals(args[0], "s")) {
                    if (args.length == 2) {
                        Player player = (Player) sender;
                        SkillAPI.performSkill(player, getSkill(args[1]), false);
                    }
                    if (args.length == 3) {
                        Player player = Bukkit.getPlayer(args[2]);
                        SkillAPI.performSkill(player, getSkill(args[1]), false);
                    }
                }
                //使手上物品带上绑定标签
                if (Objects.equals(args[0], "b")) {
                    Player player = (Player) sender;
                    if (player.getEquipment().getItemInMainHand() == null || player.getEquipment().getItemInMainHand().getType() == Material.AIR)
                        return false;
                    org.bukkit.inventory.ItemStack item = player.getEquipment().getItemInMainHand();
                    ItemMeta meta = item.getItemMeta();
                    List<String> lore;
                    if (meta.hasLore()) lore = meta.getLore();
                    else lore = new LinkedList<>();
                    lore.add(riseA.bindingS + "#NULL");
                    meta.setLore(lore);
                    item.setItemMeta(meta);
                    player.getEquipment().setItemInMainHand(item);
                }
                //导入配置文件
                if (Objects.equals(args[0], "ip")) {
                    Player player = (Player) sender;
                    EntityEquipment equip = player.getEquipment();
                    File loc = new File(importFolder, "import.yml");
                    if (!loc.exists()) {
                        try {
                            loc.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    FileConfiguration file = YamlConfiguration.loadConfiguration(loc);
                    file.set("ip-" + equip.getItemInMainHand().getItemMeta().getDisplayName(), equip.getItemInMainHand());
                    try {
                        file.save(loc);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                //导出配置文件中的物品
                if (Objects.equals(args[0], "ex")) {
                    Player player = (Player) sender;
                    Inventory inv = player.getInventory();
//                    File loc=new File(riseA.folder,"import.yml");
//                    if(!loc.exists()){
//                        try {
//                            loc.createNewFile();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                        return false;
//                    }
//                    FileConfiguration file = YamlConfiguration.loadConfiguration(loc);
//                    org.bukkit.inventory.ItemStack item=file.getItemStack(args[1]);
                    for (File i : Objects.requireNonNull(importFolder.listFiles())) {
                        ConfigurationSection file = YamlConfiguration.loadConfiguration(i);
                        for (String j : file.getKeys(false)) {
                            importItems.put(j, file.getItemStack(j));
                        }
                    }
                    org.bukkit.inventory.ItemStack item = importItems.get(args[1]);
                    if (item == null) return false;
                    inv.addItem(item);
                }
                //使玩家倒地
                if (Objects.equals(args[0], "d")) {
                    riseAPI.setPlayerDowned(Bukkit.getPlayer(args[1]));
                    Bukkit.getPlayer(args[1]).setHealth(Bukkit.getPlayer(args[1]).getMaxHealth());
                }
                //移除范围内实体
                if (Objects.equals(args[0], "removeE")) {
                    double dx1 = Double.parseDouble(args[1]), dy1 = Double.parseDouble(args[2]), dz1 = Double.parseDouble(args[3]);
                    double dx2 = Double.parseDouble(args[4]), dy2 = Double.parseDouble(args[5]), dz2 = Double.parseDouble(args[6]);
                    double x1 = Math.min(dx1, dx2), y1 = Math.min(dy1, dy2), z1 = Math.min(dz1, dz2);
                    double x2 = Math.max(dx1, dx2), y2 = Math.max(dy1, dy2), z2 = Math.max(dz1, dz2);
                    World tmp = Bukkit.getWorld(args[7]);
                    if (tmp == null) return false;
                    Collection<Entity> list = tmp.getEntities();
                    for (Entity i : list) {
                        Location loc = i.getLocation();
                        if (i instanceof Player) continue;
                        if (loc.getX() >= x1 && loc.getX() <= x2 && loc.getY() >= y1 && loc.getY() <= y2 && loc.getZ() >= z1 && loc.getZ() <= z2) {
                            i.remove();
                        }
                    }
                    return true;
                }
                //设置单个方块
                if (Objects.equals(args[0], "setblock")) {
                    int num = 1;
                    if (args.length == 7) {
                        num = 2;
                    }
                    int x = Integer.parseInt(args[num + 1]), y = Integer.parseInt(args[num + 2]), z = Integer.parseInt(args[num + 3]);
                    World world = Bukkit.getWorld(args[num + 4]);
                    Block block = world.getBlockAt(x, y, z);
                    Material a = Material.getMaterial(Integer.parseInt(args[1]));
                    block.setType(a);
                    if (num == 2) block.setData(Byte.parseByte(args[2]));
                    return true;
                }
                //打开进阶菜单
                if (Objects.equals(args[0], "vt")) {
                    testgui.test((Player) sender);
                    return true;
                }
                //没用
                if (Objects.equals(args[0], "vtd")) {
                    testgui.test1((Player) sender);
                    return true;
                }
                //储存校准数据
                if (Objects.equals(args[0], "sc")) {
                    CalibrationData.saveToFile((Player) sender);
                    return true;
                }
                //打开校准-提取
                if (Objects.equals(args[0], "cet")) {
                    if (args.length == 2) {
                        TypeSelectGUI.open(Bukkit.getPlayer(args[1]));
                    } else TypeSelectGUI.open((Player) sender);
                    return true;
                }
                //打开校准-注入
                if (Objects.equals(args[0], "cit")) {
                    if (args.length == 2) {
                        org.rise.GUI.calibrationInject.TypeSelectGUI.open(Bukkit.getPlayer(args[1]));
                    } else org.rise.GUI.calibrationInject.TypeSelectGUI.open((Player) sender);
                    return true;
                }
                //维克的设置装备的测试
                if (Objects.equals(args[0], "vt1")) {
                    net.minecraft.server.v1_12_R1.Entity iplayer;
                    CraftEntity c = (CraftEntity) sender;
                    try {
                        Method m = c.getClass().getMethod("getHandle");
                        iplayer = (net.minecraft.server.v1_12_R1.EntityPlayer) m.invoke(c);
                        NBTTagCompound nbt = iplayer.save(new NBTTagCompound());
                        iplayer.f(nbt);
                        if (nbt.hasKey("ForgeCaps") && nbt.getCompound("ForgeCaps").hasKey("mw:player_custom_inventory")) {
                            NBTTagCompound vicinv = nbt.getCompound("ForgeCaps").getCompound("mw:player_custom_inventory");
                            NBTTagList list = vicinv.getList("CustomInvTag", 10);
                            NBTTagList lf = new NBTTagList();
                            if (!list.isEmpty()) {
                                for (int i = 0; i < list.size(); i++) {
                                    NBTTagCompound it = list.get(i);
                                    lf.add(it);
                                    if (!it.hasKey("tag")) continue;
                                    if (!it.getCompound("tag").hasKey("display")) continue;
                                    if (!it.getCompound("tag").getCompound("display").hasKey("Name")) continue;
                                    NBTTagCompound tt = it.getCompound("tag");
                                    NBTTagCompound t1 = tt.getCompound("display");
                                    t1.setString("Name", "233");
                                    tt.set("display", t1);
                                    it.set("tag", tt);
                                    lf.remove(i);
                                    lf.add(it);
                                }
                            }
                            NBTTagCompound fc = nbt.getCompound("ForgeCaps");
                            vicinv.set("CustomInvTag", lf);
                            fc.set("mw:player_custom_inventory", vicinv);
                            nbt.set("ForgeCaps", fc);
                            iplayer.f(nbt);
                        }
                    } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                //使全服玩家带上干扰效果
                if (Objects.equals(args[0], "emp")) {
                    TargetBase t1 = TargetBase.SELF;
                    List<EffectBase> eff = new LinkedList<>();
                    eff.add(new EffectCustomEffect(CustomEffect.DISTURBED, 10, 0, t1));
                    SkillBase skill = new SkillBase("emp", 0, "emp", 0, 1, 0, "emp", eff, null);
                    for (Player i : Bukkit.getOnlinePlayers()) {
                        SkillAPI.performSkill(i, skill, true);
                    }
                    return true;
                }
            }
        }
        return true;
    }


    public static SkillBase getSkill(String name) {
        return skills.get(name);
    }


    public void onEnable() {
        saveDefaultConfig();
        folder = getDataFolder();
        modFolder = new File(folder, "PlayerMod");
        calibrateFolder = new File(folder, "PlayerCalibration");
        importFolder = new File(folder, "ImportItem");
        TargetBase.SELF = new TargetBase(TargetBase.Type.SELF);
        TargetBase.TEAM = new TargetBase(TargetBase.Type.TEAM);
        if (!modFolder.exists()) {
            modFolder.mkdirs();
        }
        if (!calibrateFolder.exists()) {
            calibrateFolder.mkdirs();
        }
        if (!importFolder.exists()) {
            importFolder.mkdirs();
        }
        buffMap.put("急速", PotionEffectType.SPEED);
        buffMap.put("缓慢", PotionEffectType.SLOW);
        buffMap.put("力量", PotionEffectType.INCREASE_DAMAGE);
        buffMap.put("生命恢复", PotionEffectType.REGENERATION);
        buffMap.put("抗性提升", PotionEffectType.DAMAGE_RESISTANCE);
        buffMap.put("防火", PotionEffectType.FIRE_RESISTANCE);
        buffMap.put("水下呼吸", PotionEffectType.WATER_BREATHING);
        buffMap.put("失明", PotionEffectType.BLINDNESS);
        buffMap.put("急迫", PotionEffectType.FAST_DIGGING);
        buffMap.put("饥饿", PotionEffectType.HUNGER);
        buffMap.put("隐形", PotionEffectType.INVISIBILITY);
        buffMap.put("跳跃提升", PotionEffectType.JUMP);
        buffMap.put("挖掘疲劳", PotionEffectType.SLOW_DIGGING);
        buffMap.put("虚弱", PotionEffectType.WEAKNESS);
        buffMap.put("凋零", PotionEffectType.WITHER);
        buffMap.put("破防", PotionEffectType.getByName("potioncore:broken_armor"));
        buffMap.put("电击", PotionEffectType.getByName("potioncore:spin"));
        buffMap.put("沉重", PotionEffectType.getByName("potioncore:weight"));
        talentMap.put("杀手", TalentType.KILLER);
        talentMap.put("维护保存", TalentType.MAINTAIN);
        talentMap.put("完美维护保存", TalentType.MAINTAIN_PERFECT);
        talentMap.put("压力使然", TalentType.PRESSURE);
        talentMap.put("短兵相接", TalentType.CLOSE_COMBAT);
        talentMap.put("虐待狂", TalentType.SADISM);
        talentMap.put("报复之心", TalentType.REVENGE);
        talentMap.put("游骑兵", TalentType.RANGER);
        talentMap.put("完美游骑兵", TalentType.RANGER_PERFECT);
        talentMap.put("双手沉稳", TalentType.STEADY);
        talentMap.put("刺击", TalentType.PRICK);
        talentMap.put("致盲", TalentType.BLIND);
        talentMap.put("长存", TalentType.PERMANENCE);
        talentMap.put("改革", TalentType.REVOLT);
        talentMap.put("未来完成式", TalentType.FUTURE);
        talentMap.put("同步伤害", TalentType.SYNCHRO);
        talentMap.put("停搏", TalentType.ASYSTOLE);
        talentMap.put("第一滴血", TalentType.FIRST_BLOOD);
        talentMap.put("拳拳到肉", TalentType.PUNCH);
        talentMap.put("局外人", TalentType.OUTSIDER);
        talentMap.put("冰暴", TalentType.ICE_STORM);
        talentMap.put("最终壁垒", TalentType.BARRIER);
        talentMap.put("最终壁垒-战术扩容", TalentType.BARRIER_EXPANSION);
        talentMap.put("最终壁垒-保全分析", TalentType.BARRIER_ANALYSIS);
        talentMap.put("鼓起勇气", TalentType.COURAGE);
        talentMap.put("前线防护-绝缘防护", TalentType.COURAGE_PROTECT);
        talentMap.put("前线防护-临场修复", TalentType.COURAGE_FIX);
        talentMap.put("前线防护-重型装甲", TalentType.COURAGE_ARMOR);
        talentMap.put("前线防护-快速修复", TalentType.COURAGE_RECOVER);
        talentMap.put("前线防护-精炼修复", TalentType.COURAGE_REFINE);
        talentMap.put("安全护卫", TalentType.PROTECT);
        talentMap.put("吸血生物", TalentType.HEMOPHAGIA);
        talentMap.put("无人能挡", TalentType.UNSTOPPABLE);
        talentMap.put("震荡", TalentType.SHOCK);
        talentMap.put("警戒", TalentType.ALERT);
        talentMap.put("肾上腺素爆发", TalentType.ADRENALINE);
        talentMap.put("牢不可破", TalentType.UNBREAKABLE);
        talentMap.put("完美牢不可破", TalentType.UNBREAKABLE_PERFECT);
        talentMap.put("玻璃大炮", TalentType.GLASS_CANNON);
        talentMap.put("抹灭性破坏", TalentType.OBLITERATIVE);
        talentMap.put("猎头者", TalentType.HEAD_HUNTER);
        talentMap.put("白獒之瞳", TalentType.WHITE_AOGOU);
        talentMap.put("无言杀意", TalentType.SILENT_KILLING);
        talentMap.put("KIMERA", TalentType.KIMERA);
        talentMap.put("适应本能", TalentType.CHAMELEON);
        talentMap.put("砥砺刀锋", TalentType.CRAFTSMAN);
        talentMap.put("铤而走险", TalentType.RISK);
        talentMap.put("铤而走险-绝缘保护", TalentType.RISK_PROTECT);
        talentMap.put("铤而走险-动能转换", TalentType.RISK_TRANSFORM);
        talentMap.put("铤而走险-幽灵", TalentType.RISK_GHOST);
        talentMap.put("铤而走险-装甲升级", TalentType.RISK_UPGRADE);
        talentMap.put("寻光者", TalentType.PATHFINDER);
        talentMap.put("牧羊人", TalentType.NOMAD);
        for (String i : talentMap.keySet()) {
            talentMapReflect.put(talentMap.get(i), i);
        }
        configReload();
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(this, EntityUpdate.EntitySecondlyCheck, 0L, 20L);
        scheduler.scheduleSyncRepeatingTask(this, EntityUpdate.playerSecondlyCheck, 0L, 60L);
        scheduler.scheduleSyncRepeatingTask(this, EntityUpdate.WrongEntityRemove, 0L, 100L);
        scheduler.scheduleSyncRepeatingTask(this, EntityUpdate.playerTicklyCheck, 0L, 1L);
        scheduler.scheduleSyncRepeatingTask(this, EntityUpdate.EntityTicklyCheck, 0, 1L);
        scheduler.scheduleSyncRepeatingTask(this, ConstantEffect.secondlyCheck, 0, 20L);
        scheduler.scheduleSyncRepeatingTask(this, ConstantEffect.ticklyCheck, 0, 2L);
        Bukkit.getPluginManager().registerEvents(new EntityAttackProcess(), this);
//        Bukkit.getPluginManager().registerEvents(new PlayerAttackProcess(),this);
//        Bukkit.getPluginManager().registerEvents(new EntityAttackPlayerProcess(),this);
//        Bukkit.getPluginManager().registerEvents(new EntityAttackEntityProcess(),this);
        Bukkit.getPluginManager().registerEvents(new PlayerEarnExpProcess(), this);
        Bukkit.getPluginManager().registerEvents(new ModuleL2(), this);
        Bukkit.getPluginManager().registerEvents(new ModuleL3(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerResetAttrListener(), this);
        Bukkit.getPluginManager().registerEvents(new EntityDeathProcess(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerPickupItemProcess(), this);
        Bukkit.getPluginManager().registerEvents(new PreventPlayerGetBinded(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerRightClickListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerReviving(), this);
        Bukkit.getPluginManager().registerEvents(new ActiveListener(), this);
        Bukkit.getPluginManager().registerEvents(new testgui(), this);
        Bukkit.getPluginManager().registerEvents(new AttrSelectGUI(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerPressEscEvent(), this);
        boolean haveTmp = false;
        for (World i : Bukkit.getServer().getWorlds())
            if (Objects.equals(i.getName(), "riseA_TmpWorld")) {
                haveTmp = true;
                break;
            }
        if (!haveTmp) {
            WorldCreator creator = new WorldCreator("riseA_TmpWorld");
            creator = creator.generator(new io.nv.bukkit.CleanroomGenerator.CleanroomChunkGenerator());
            Bukkit.getServer().createWorld(creator);
        }
        tmpWorld = Bukkit.getServer().getWorld("riseA_TmpWorld");
    }


}
