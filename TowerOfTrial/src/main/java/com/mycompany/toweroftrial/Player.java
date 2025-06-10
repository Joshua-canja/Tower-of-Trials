package com.mycompany.toweroftrial;

import java.util.HashMap;
import java.io.Serializable;

public class Player implements Serializable {
    String name, playerClass;
    int level = 1, exp = 0, expToNext = 30;
    int hp, maxHp, mp, maxMp, gold = 0;
    HashMap<String, Integer> items = new HashMap<>();
    java.util.ArrayList<Skill> skills = new java.util.ArrayList<>();
    String equippedItem = null;

    public Player(String name, String playerClass) {
        this.name = name;
        this.playerClass = playerClass;
        switch (playerClass) {
            case "Warrior": maxHp=120; maxMp=30; break;
            case "Assassin": maxHp=90; maxMp=40; break;
            case "Mage": maxHp=80; maxMp=70; break;
            case "Archer": maxHp=100; maxMp=50; break;
        }
        hp = maxHp; mp = maxMp;
        skills.add(getBasicSkill());
    }
    
    

    public Skill getBasicSkill() {
        switch (playerClass) {
            case "Warrior": return new Skill("Power Strike", "Deal heavy physical damage to one enemy.", 15);
            case "Assassin": return new Skill("Backstab", "Deal critical damage to one enemy.", 15);
            case "Mage": return new Skill("Fireball", "Deal fire magic damage to one enemy.", 15);
            case "Archer": return new Skill("Piercing Arrow", "Deal ranged damage that ignores some defense.", 15);
        }
        return new Skill("Unknown", "No description.", 0);
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
            hp = maxHp; mp = maxMp;
            unlockSkill();
            System.out.println("You leveled up! Now level " + level + "!");
        }
    }
    public void unlockSkill() {
    if (level == 2) {
        Skill newSkill = null;
        switch (playerClass) {
            case "Warrior": newSkill = new Skill("Shield Bash", "Stun an enemy and deal moderate damage.", 20); break;
            case "Assassin": newSkill = new Skill("Poison Blade", "Attack that poisons the enemy over time.", 20); break;
            case "Mage": newSkill = new Skill("Ice Spike", "Deal ice magic damage and slow the enemy.", 20); break;
            case "Archer": newSkill = new Skill("Double Shot", "Shoot two arrows, each dealing 80% damage.", 20); break;
        }
        if (newSkill != null) {
            skills.add(newSkill);
            System.out.println("You unlocked new skill: " + newSkill.name);
        }
    }
}
    public void addGold(int amt) {
        gold += amt;
    }

    public void addItem(String itemName, int quantity) {
        items.put(itemName, items.getOrDefault(itemName, 0) + quantity);
    }

    public boolean useItem(int idx) {
        String[] itemArray = items.keySet().toArray(new String[0]);
        if (idx < 0 || idx >= itemArray.length) return false;
        String it = itemArray[idx];
        int count = items.get(it);
        if (it.contains("HP Potion")) {
            int restore = 30;
            hp += restore;
            if (hp > maxHp) hp = maxHp;
            System.out.println("You used HP Potion! Restored " + restore + " HP.");
        } else if (it.contains("Mana Potion")) {
            int restore = 20;
            mp += restore;
            if (mp > maxMp) mp = maxMp;
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