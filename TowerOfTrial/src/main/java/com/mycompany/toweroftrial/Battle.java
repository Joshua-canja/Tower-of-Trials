package com.mycompany.toweroftrial;


import java.util.Scanner;
import java.util.Random;

public class Battle {
    private final Player player;
    private final Monster monster;
    private final Scanner in;
    private final Random rand = new Random();

    public Battle(Player player, Monster monster, Scanner in) {
        this.player = player;
        this.monster = monster;
        this.in = in;
    }

    public boolean run() {
    UI.printBattleStart(player, monster);
    if (monster.isBoss && !monster.passiveSkills.isEmpty()) {
        System.out.print("Boss Passive Skills:\n" + monster.getPassiveDesc());
    }
    while (monster.hp > 0 && player.hp > 0) {
        UI.battleOptions();
        String act = in.nextLine().trim();
        boolean playerTurnConsumed = false;

        if (act.equals("1")) { // Normal Attack
            int dmg = 25 + player.level * 3;
            dmg = monster.applyPassive(dmg);
            monster.hp -= dmg;
            if (monster.hp < 0) monster.hp = 0;
            System.out.println("You attack the " + monster.name + " and dealt " + dmg + " damage!");
            playerTurnConsumed = true;
        } else if (act.equals("2")) { // Inventory
            if (player.items.isEmpty()) {
                System.out.println("No items!");
                continue;
            }
            UI.printInventory(player.items);
            System.out.println("Use which item? (0 to cancel)");
            int idx;
            try { idx = Integer.parseInt(in.nextLine().trim()) - 1; } catch (Exception e) { continue; }
            if (idx < 0) continue;
            if (!player.useItem(idx)) continue;
            continue;
        } else if (act.equals("3")) { // Skills
            UI.printSkills(player.skills);
            System.out.println("Use which skill? (0 to cancel)");
            int sidx;
            try { sidx = Integer.parseInt(in.nextLine().trim()) - 1; } catch (Exception e) { continue; }
            if (sidx < 0 || sidx >= player.skills.size()) continue;
            Skill skill = player.skills.get(sidx);
            int cost = skill.manaCost;
            if (player.mp < cost) {
                System.out.println("Not enough MP!");
                continue;
            }
            // Skill logic:
            int dmg = 0;
            if (skill.name.equals("Power Strike") || skill.name.equals("Backstab") || skill.name.equals("Fireball") || skill.name.equals("Piercing Arrow")) {
                dmg = 40 + player.level * 4;
                dmg = monster.applyPassive(dmg);
                monster.hp -= dmg;
                player.mp -= cost;
                System.out.println("You used " + skill.name + " and dealt " + dmg + " damage!");
            } else if (skill.name.equals("Shield Bash")) { // Warrior 2nd
                dmg = 30 + player.level * 2;
                dmg = monster.applyPassive(dmg);
                monster.hp -= dmg;
                player.mp -= cost;
                System.out.println("You used Shield Bash and dealt " + dmg + " damage! (Enemy stunned: Next turn, boss/monster skips attack - not implemented)");
            } else if (skill.name.equals("Poison Blade")) { // Assassin 2nd
                dmg = 20 + player.level * 2;
                dmg = monster.applyPassive(dmg);
                monster.hp -= dmg;
                player.mp -= cost;
                System.out.println("You used Poison Blade and dealt " + dmg + " damage! (Enemy poisoned: loses 5 HP per turn - not implemented)");
            } else if (skill.name.equals("Ice Spike")) { // Mage 2nd
                dmg = 24 + player.level * 2;
                dmg = monster.applyPassive(dmg);
                monster.hp -= dmg;
                player.mp -= cost;
                System.out.println("You used Ice Spike and dealt " + dmg + " damage! (Enemy slowed: less attack next turn - not implemented)");
            } else if (skill.name.equals("Double Shot")) { // Archer 2nd
                int base = 25 + player.level * 3;
                int singleDmg = (int)(base * 0.8);
                singleDmg = monster.applyPassive(singleDmg);
                monster.hp -= singleDmg;
                System.out.println("You used Double Shot! First hit: " + singleDmg + " damage!");
                if (monster.hp <= 0) { player.mp -= cost; break; }
                // Second hit
                monster.hp -= singleDmg;
                player.mp -= cost;
                System.out.println("Second hit: " + singleDmg + " damage!");
            }
            if (monster.hp < 0) monster.hp = 0;
            playerTurnConsumed = true;
        } else if (act.equals("4")) {
            System.out.println("You fled from battle!");
            return false;
        }

        if (monster.hp <= 0) break;

        // Monster's turn
        if (playerTurnConsumed) {
            Skill mSkill = monster.chooseSkill();
            if (mSkill == null) {
                int mobAtk = monster.attack;
                player.hp -= mobAtk;
                if (player.hp < 0) player.hp = 0;
                System.out.println("The " + monster.name + " attacks you and deals " + mobAtk + " damage!");
            } else {
                monster.mp -= mSkill.manaCost;
                if (monster.isBoss) {
                    if (mSkill.name.equals("Roar")) {
                        System.out.println("Boss uses " + mSkill.name + "! Your attack is reduced for 2 turns! (No actual stat change, demonstration only)");
                    } else if (mSkill.name.equals("Mega Slam")) {
                        int dmg2 = monster.attack + 20;
                        player.hp -= dmg2;
                        if (player.hp < 0) player.hp = 0;
                        System.out.println("Boss uses " + mSkill.name + "! It deals " + dmg2 + " damage!");
                    }
                } else {
                    int dmg2 = monster.attack + 5;
                    player.hp -= dmg2;
                    if (player.hp < 0) player.hp = 0;
                    System.out.println("The " + monster.name + " uses " + mSkill.name + " and deals " + dmg2 + " damage!");
                }
            }
            UI.printBattleStatus(player, monster);
        }
    }
    if (player.hp > 0) {
        System.out.println("You have slain the " + monster.name + "!");
        System.out.println("You gained " + monster.expReward + " Exp and " + monster.goldReward + " Gold!\n");
        player.gainExp(monster.expReward);
        player.addGold(monster.goldReward);
        return true;
    }
    return false;
}

    public void runTutorial() {
        System.out.println("Name: " + player.name + "\tLvl." + player.level + "\t\tMonster: " + monster.name);
        System.out.println("HP: " + player.hp + "/" + player.maxHp + " MP: " + player.mp + "/" + player.maxMp +
                           "\tExp:" + player.exp + "/" + player.expToNext +
                           "\tHP: " + monster.hp + "/" + monster.maxHp + " MP: " + monster.mp + "/" + monster.maxMp);
        while (monster.hp > 0 && player.hp > 0) {
            UI.battleOptions();
            String act = in.nextLine().trim();

            if (act.equals("1")) {
                int dmg = 25;
                dmg = monster.applyPassive(dmg);
                monster.hp -= dmg;
                if (monster.hp < 0) monster.hp = 0;
                System.out.println("You attack the goblin and dealt " + dmg + " damage!");
            } else if (act.equals("2")) {
                System.out.println("No items!");
                continue;
            } else if (act.equals("3")) {
                UI.printSkills(player.skills);
                System.out.println("Use which skill? (0 to cancel)");
                int sidx;
                try {
                    sidx = Integer.parseInt(in.nextLine().trim()) - 1;
                } catch (Exception e) {
                    continue;
                }
                if (sidx < 0 || sidx >= player.skills.size()) continue;
                Skill skill = player.skills.get(sidx);
                int cost = skill.manaCost;
                if (player.mp < cost) {
                    System.out.println("Not enough MP!");
                    continue;
                }
                int dmg = 40;
                dmg = monster.applyPassive(dmg);
                monster.hp -= dmg;
                player.mp -= cost;
                if (monster.hp < 0) monster.hp = 0;
                System.out.println("You used " + skill.name + " and dealt " + dmg + " damage!");
            } else if (act.equals("4")) {
                System.out.println("You fled from battle! (Not allowed in tutorial)");
            }

            if (monster.hp <= 0) break;
            int goblinAtk = 10;
            player.hp -= goblinAtk;
            if (player.hp < 0) player.hp = 0;
            System.out.println("You have been attack by a Goblin you lose 10 HP\n");
            System.out.println("Player HP: " + player.hp + "/" + player.maxHp + " MP: " + player.mp + "/" + player.maxMp +
                               " | Goblin HP: " + monster.hp + "/" + monster.maxHp + " MP: " + monster.mp + "/" + monster.maxMp);
        }
        if (player.hp > 0) {
            System.out.println("You have slain the Goblin!");
            System.out.println("You gained " + monster.expReward + " Exp and " + monster.goldReward + " Gold!\n");
        }
    }
}