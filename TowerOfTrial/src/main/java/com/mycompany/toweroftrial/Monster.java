package com.mycompany.toweroftrial;

import java.util.ArrayList;
import java.util.Random;

public class Monster {

    String name;
    int hp, maxHp, mp, maxMp, attack, goldReward, expReward;
    boolean isBoss;
    ArrayList<Skill> activeSkills = new ArrayList<>();
    ArrayList<Skill> passiveSkills = new ArrayList<>();
    Random rand = new Random();

    // == Status Effect Fields ==
    public int stunCounter = 0;
    public int poisonCounter = 0;
    public int slowCounter = 0;
    public int attackDownCounter = 0;
    public int poisonDamage = 0;

    public Monster(String name, int hp, int mp, int atk, int gold, int exp, boolean boss) {
        this.name = name;
        this.maxHp = hp;
        this.hp = hp;
        this.maxMp = mp;
        this.mp = mp;
        this.attack = atk;
        this.goldReward = gold;
        this.expReward = exp;
        this.isBoss = boss;
        assignSkills();
    }

    private void assignSkills() {
        if (!isBoss) {
            // Savage Bite: let's say 1.2x
            activeSkills.add(new Skill("Savage Bite", "A ferocious bite that deals extra damage", 10, false, "extra", 5, 0, 1.2));
        } else {
            passiveSkills.add(new Skill("Thick Hide", "Reduces all damage taken by 5", 0, true, "defense", 5, 0, 1.0));
            activeSkills.add(new Skill("Roar", "Decreases player's attack for 2 turns", 15, false, "atkDown", 5, 2, 1.0));
            activeSkills.add(new Skill("Mega Slam", "Deals massive damage, but costs a lot of MP", 30, false, "massive", 20, 0, 1.5));
        }
    }

    public boolean hasEnoughMp(Skill skill) {
        return mp >= skill.manaCost;
    }

    public Skill chooseSkill() {
        // Boss: random among active skills + basic attack
        // Minion: random between active skill and basic attack
        int choice = rand.nextInt(activeSkills.size() + 1);
        if (choice == 0) {
            return null; // basic attack
        }
        Skill skill = activeSkills.get(choice - 1);
        if (hasEnoughMp(skill)) {
            return skill;
        }
        return null; // fallback to basic attack if not enough MP
    }

    public int applyPassive(int dmg) {
        // Only "Thick Hide" implemented for now
        if (isBoss && !passiveSkills.isEmpty()) {
            for (Skill s : passiveSkills) {
                if (s.name.equals("Thick Hide")) {
                    dmg -= 5;
                }
            }
        }
        return Math.max(dmg, 0);
    }

    public String getPassiveDesc() {
        if (!passiveSkills.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (Skill s : passiveSkills) {
                sb.append(s.name).append(": ").append(s.description).append("\n");
            }
            return sb.toString();
        }
        return "";
    }
}
