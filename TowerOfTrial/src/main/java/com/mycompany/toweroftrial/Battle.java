package com.mycompany.toweroftrial;

import java.util.Scanner;

public class Battle {

    private Player player;
    private Monster monster;
    private Scanner in;

    public Battle(Player player, Monster monster, Scanner in) {
        this.player = player;
        this.monster = monster;
        this.in = in;
    }

    public boolean run() {
        while (player.hp > 0 && monster.hp > 0) {
            // --- Process status effects ---
            processPlayerStatusEffects();
            if (player.hp <= 0) {
                break;
            }
            processMonsterStatusEffects();
            if (monster.hp <= 0) {
                break;
            }

            // --- Player Turn ---
            if (player.hp > 0) {
                if (player.stunCounter > 0) {
                    System.out.println("You are stunned and cannot act this turn!");
                } else {
                    UI.printBattleStatus(player, monster);
                    UI.battleOptions();
                    String input = in.nextLine().trim();
                    switch (input) {
                        case "1":
                            attackMonster();
                            break;
                        case "2":
                            player.showInventory();
                            break;
                        case "3":
                            usePlayerSkill();
                            break;
                        case "4":
                            System.out.println("You fled!");
                            player.restoreStats();
                            return false;
                        default:
                            System.out.println("Invalid input.");
                            break;
                    }
                }
            }

            // --- Monster Turn ---
            if (monster.hp > 0) {
                if (monster.stunCounter > 0) {
                    System.out.println(monster.name + " is stunned and cannot act this turn!");
                } else {
                    useMonsterAction();
                }
            }
        }
        if (player.hp <= 0) {
            System.out.println("You have been defeated!");
            return false;
        } else {
            System.out.println("You defeated " + monster.name + "!");
            System.out.println("You gained " + monster.expReward + " EXP and " + monster.goldReward + " Gold!");
            player.gainExp(monster.expReward);
            player.addGold(monster.goldReward);
            return true;
        }

    }

    private void attackMonster() {
        int dmg = player.attack;
        if (monster.slowCounter > 0) {
            dmg += 2; // Optional: slow makes them take more
        }
        dmg = monster.applyPassive(dmg);
        monster.hp -= dmg;
        if (monster.hp < 0) {
            monster.hp = 0;
        }
        System.out.println("You attacked and dealt " + dmg + " damage!");
    }

    private void usePlayerSkill() {
        UI.printSkills(player.skills);
        System.out.print("Choose a skill: ");
        int idx;
        try {
            idx = Integer.parseInt(in.nextLine().trim()) - 1;
        } catch (Exception e) {
            System.out.println("Invalid input.");
            return;
        }
        if (idx < 0 || idx >= player.skills.size()) {
            System.out.println("Invalid skill choice.");
            return;
        }
        Skill skill = player.skills.get(idx);
        if (player.mp < skill.manaCost) {
            System.out.println("Not enough MP!");
            return;
        }
        player.mp -= skill.manaCost;

        // By default, skill damage is player's attack * skill.multiplier
        int dmg = (int) Math.round(player.attack * skill.multiplier);

        if ("stun".equals(skill.effectType)) {
            monster.stunCounter = skill.duration;
            System.out.println(monster.name + " is stunned for " + skill.duration + " turn(s)!");
        } else if ("poison".equals(skill.effectType)) {
            monster.poisonCounter = skill.duration;
            monster.poisonDamage = skill.effectValue;
            System.out.println(monster.name + " is poisoned!");
        } else if ("slow".equals(skill.effectType)) {
            monster.slowCounter = skill.duration;
            System.out.println(monster.name + " is slowed!");
        } else if ("double".equals(skill.effectType)) {
            // Double Shot: two hits, each at skill.multiplier
            int shot1 = (int) Math.round(player.attack * skill.multiplier);
            int shot2 = (int) Math.round(player.attack * skill.multiplier);
            shot1 = monster.applyPassive(shot1);
            shot2 = monster.applyPassive(shot2);
            monster.hp -= (shot1 + shot2);
            System.out.println("Double Shot! You hit for " + shot1 + " and " + shot2 + "!");
            return;
        }

        dmg = monster.applyPassive(dmg);
        monster.hp -= dmg;
        if (monster.hp < 0) {
            monster.hp = 0;
        }
        System.out.println("You used " + skill.name + " and dealt " + dmg + " damage!");
    }

    private void useMonsterAction() {
        Skill skill = monster.chooseSkill();
        if (skill == null) {
            int dmg = monster.attack;
            if (player.slowCounter > 0) {
                dmg -= 2;
            }
            if (player.attackDownCounter > 0) {
                dmg -= 5;
            }
            if (dmg < 0) {
                dmg = 0;
            }
            player.hp -= dmg;
            if (player.hp < 0) {
                player.hp = 0;
            }
            System.out.println(monster.name + " attacks and deals " + dmg + " damage!");
        } else {
            monster.mp -= skill.manaCost;
            if ("atkDown".equals(skill.effectType)) {
                player.attackDownCounter = skill.duration;
                System.out.println("Your attack is reduced for " + skill.duration + " turn(s)!");
            } else if ("massive".equals(skill.effectType)) {
                int dmg = monster.attack + skill.effectValue;
                player.hp -= dmg;
                if (player.hp < 0) {
                    player.hp = 0;
                }
                System.out.println(monster.name + " uses " + skill.name + " and deals " + dmg + " massive damage!");
            } else if ("extra".equals(skill.effectType)) {
                int dmg = monster.attack + skill.effectValue;
                player.hp -= dmg;
                if (player.hp < 0) {
                    player.hp = 0;
                }
                System.out.println(monster.name + " uses " + skill.name + " and deals " + dmg + " extra damage!");
            } else {
                int dmg = monster.attack + 5;
                player.hp -= dmg;
                if (player.hp < 0) {
                    player.hp = 0;
                }
                System.out.println(monster.name + " uses " + skill.name + " and deals " + dmg + " damage!");
            }
        }
    }

    private void processPlayerStatusEffects() {
        if (player.poisonCounter > 0) {
            if (player.hp < 0) {
                player.hp = 0;
            }
            player.hp -= player.poisonDamage;
            System.out.println("You take " + player.poisonDamage + " poison damage!");
            player.poisonCounter--;
            if (player.hp <= 0) {
                player.hp = 0;
                System.out.println("You have been killed by poison!");
            }
            if (player.poisonCounter == 0) {
                System.out.println("Poison wore off!");
            }
        }
        if (player.attackDownCounter > 0) {
            player.attackDownCounter--;
            if (player.attackDownCounter == 0) {
                System.out.println("Your attack is back to normal.");
            }
        }
    }

    private void processMonsterStatusEffects() {
        if (monster.poisonCounter > 0) {
            monster.hp -= monster.poisonDamage;
            if (monster.hp < 0) {
                monster.hp = 0;
            }
            System.out.println(monster.name + " takes " + monster.poisonDamage + " poison damage!");
            monster.poisonCounter--;
            if (monster.hp <= 0) {
                monster.hp = 0;
                System.out.println(monster.name + " has been killed by poison!");
            }
            if (monster.poisonCounter == 0) {
                System.out.println(monster.name + "'s poison wore off!");
            }
        }
        if (monster.attackDownCounter > 0) {
            monster.attackDownCounter--;
            if (monster.attackDownCounter == 0) {
                System.out.println(monster.name + "'s attack is back to normal.");
            }
        }
    }
}
