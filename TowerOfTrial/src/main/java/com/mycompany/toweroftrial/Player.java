package com.mycompany.toweroftrial;

import java.util.HashMap;
import java.io.Serializable;

public class Player implements Serializable {

    public String name, playerClass;
    public int level = 1, exp = 0, expToNext = 30;
    public int hp, maxHp, mp, maxMp, gold = 0;
    public int attack, baseAttack;
    public HashMap<String, Integer> items = new HashMap<>();
    public java.util.ArrayList<Skill> skills = new java.util.ArrayList<>();
    public String equippedItem = null;

    // === Status Effect Fields ===
    public int stunCounter = 0;
    public int poisonCounter = 0;
    public int slowCounter = 0;
    public int attackDownCounter = 0;
    public int poisonDamage = 0;
    public int currentTowerFloor = 1;
    public int maxTowerFloorCleared = 0;

    public Player(String name, String playerClass) {
        this.name = name;
        this.playerClass = playerClass;
        switch (playerClass) {
            case "Warrior":
                maxHp = 120;
                maxMp = 30;
                baseAttack = 30;
                break;
            case "Assassin":
                maxHp = 90;
                maxMp = 40;
                baseAttack = 30;
                break;
            case "Mage":
                maxHp = 80;
                maxMp = 70;
                baseAttack = 30;
                break;
            case "Archer":
                maxHp = 100;
                maxMp = 50;
                baseAttack = 30;
                break;
            default:
                baseAttack = 30;
        }
        attack = baseAttack;
        hp = maxHp;
        mp = maxMp;
        skills.add(getBasicSkill());
    }

    public Skill getBasicSkill() {
        switch (playerClass) {
            case "Warrior":
                return new Skill("Power Strike", "Deal heavy physical damage to one enemy.", 15, 1.4);
            case "Assassin":
                return new Skill("Backstab", "Deal critical damage to one enemy.", 15, 1.5);
            case "Mage":
                return new Skill("Fireball", "Deal fire magic damage to one enemy.", 15, 1.6);
            case "Archer":
                return new Skill("Piercing Arrow", "Deal ranged damage that ignores some defense.", 15, 1.3);
        }
        return new Skill("Unknown", "No description.", 0, 1.0);
    }

    public void showStatus() {
        UI.printStatus(this);
    }

    public void showInventory() {
        UI.printInventory(items);
    }

    public void gainExp(int amt) {
        exp += amt;
        while (exp >= expToNext) {
            exp -= expToNext;
            level++;
            expToNext += 15;
            maxHp += 15;
            maxMp += 10;
            baseAttack += 5;
            attack = baseAttack;
            hp = maxHp;
            mp = maxMp;
            unlockSkill();
            System.out.println("You leveled up! Now level " + level + "!");
        }
    }

    public void unlockSkill() {
        if (level == 2) {
            Skill newSkill = null;
            switch (playerClass) {
                case "Warrior":
                    // Shield Bash: stun + moderate damage, let's use 1.1x
                    newSkill = new Skill("Shield Bash", "Stun an enemy and deal moderate damage.", 20, false, "stun", 1, 1, 1.1);
                    break;
                case "Assassin":
                    // Poison Blade: poisons over time, damage is lighter, let's use 1.0x
                    newSkill = new Skill("Poison Blade", "Attack that poisons the enemy over time.", 20, false, "poison", 5, 3, 1.0);
                    break;
                case "Mage":
                    // Ice Spike: slows enemy, let's use 1.2x
                    newSkill = new Skill("Ice Spike", "Deal ice magic damage and slow the enemy.", 20, false, "slow", 1, 2, 1.2);
                    break;
                case "Archer":
                    // Double Shot: two attacks at 80% each, handled specially in Battle.java, set multiplier to 0.8 for each shot
                    newSkill = new Skill("Double Shot", "Shoot two arrows, each dealing 80% damage.", 20, false, "double", 80, 0, 0.8);
                    break;
            }
            if (newSkill != null) {
                skills.add(newSkill);
                System.out.println("You unlocked new skill: " + newSkill.name);
            }
        }
    }

    public void restoreStats() {
        this.hp = this.maxHp;
        this.mp = this.maxMp;
        this.poisonCounter = 0;
        this.poisonDamage = 0;
        this.attackDownCounter = 0;
    }

    public void addGold(int amt) {
        gold += amt;
    }

    public void addItem(String itemName, int quantity) {
        items.put(itemName, items.getOrDefault(itemName, 0) + quantity);
    }

    public boolean useItem(int idx) {
        String[] itemArray = items.keySet().toArray(new String[0]);
        if (idx < 0 || idx >= itemArray.length) {
            return false;
        }
        String it = itemArray[idx];
        int count = items.get(it);
        if (it.contains("HP Potion")) {
            int restore = 30;
            hp += restore;
            if (hp > maxHp) {
                hp = maxHp;
            }
            System.out.println("You used HP Potion! Restored " + restore + " HP.");
        } else if (it.contains("Mana Potion")) {
            int restore = 20;
            mp += restore;
            if (mp > maxMp) {
                mp = maxMp;
            }
            System.out.println("You used Mana Potion! Restored " + restore + " MP.");
        }
        if (count > 1) {
            items.put(it, count - 1);
        } else {
            items.remove(it);
        }
        return true;
    }
}
