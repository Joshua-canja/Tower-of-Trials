package com.mycompany.toweroftrial;

import java.util.HashMap;
import java.util.Scanner;

public class UI {

    public static void printIntro(Scanner in) {
        System.out.println("Tower trial the place where you can get anything. Power, Fame, Money…..");
        System.out.println("Would you like to enter?......");
        System.out.println("Choices: 1. Yes  2. No");
        String choice = in.nextLine();
        if (choice.trim().equals("2")) {
            System.out.println("Error….Error…Error.. You enter The tower of trial");
        }
    }

    public static String askName(Scanner in) {
        System.out.println("Enter your name Challenger of the Tower!");
        System.out.print("Name: ");
        return in.nextLine();
    }

    public static String askClass(Scanner in) {
        System.out.println("Now I will give you the right to choose your class… please choice wisely your choice could alter your fate….");
        System.out.println("Choices. 1. Warrior 2. Assassin 3. Mage 4. Archer");
        String[] classes = {"Warrior", "Assassin", "Mage", "Archer"};
        int classIndex = Integer.parseInt(in.nextLine().trim()) - 1;
        return classes[classIndex];
    }

    public static void printChosenClass(String playerClass) {
        System.out.println("You Choose " + playerClass);
        System.out.println("Now that you have choosen your class…. Get ready to enter the Tower of Trial….");
        System.out.println("3..\n2.\n1.");
        System.out.println("Welcome to Tower of Trials");
    }

    public static void tutorialStart() {
        System.out.println("The Tutorial begins please defeat the Goblin\n");
    }

    public static void printTutorialClear() {
        System.out.println("Congratulations for defeating the Goblin… You will be move to the Safe zone..\nYou gained 10 Exp for Passing the Tutorial\nGood luck Climbing the Tower….\n");
    }

    public static void mainMenu() {
        System.out.println(">>>>>>>>>>>>>>>>>>Main Menu<<<<<<<<<<<<<<<<");
        System.out.println("1. Status\n2. Inventory\n3. Shop\n4. Enter Tower\n5. Save Progress\n6. Exit");
    }

    public static void printStatus(Player player) {
        System.out.println("Name: " + player.name + "\tLvl." + player.level);
        System.out.println("HP: " + player.hp + "/" + player.maxHp
                + "\tMP: " + player.mp + "/" + player.maxMp
                + "\tExp:" + player.exp + "/" + player.expToNext);
        System.out.println("Gold: " + player.gold);
        System.out.println("Equipped: " + (player.equippedItem == null ? "None" : player.equippedItem));
        System.out.println("Skills:");
        for (Skill s : player.skills) {
            System.out.println(" - " + s.name + ": " + s.description);
        }
        System.out.println();
    }

    public static void printInventory(HashMap<String, Integer> items) {
        System.out.println("------Inventory------");
        if (items.isEmpty()) {
            System.out.println("No items.");
        } else {
            int idx = 1;
            for (String name : items.keySet()) {
                int count = items.get(name);
                System.out.println(idx + ". " + name + " x" + count);
                idx++;
            }
        }
        System.out.println("---------------------");
    }

    public static void printShop(java.util.ArrayList<ShopItem> stock, int gold) {
        System.out.println("Your Gold: " + gold);
        for (int i = 0; i < stock.size(); i++) {
            ShopItem s = stock.get(i);
            System.out.println((i + 1) + ". " + s.name + "\tPrice: " + s.price + " Gold");
        }
    }

    public static void printBattleStart(Player player, Monster monster) {
        System.out.println("--- Player Stats ---");
        System.out.println("Name: " + player.name + " (" + player.playerClass + ")  Lv." + player.level);
        System.out.println("HP: " + player.hp + "/" + player.maxHp + "   MP: " + player.mp + "/" + player.maxMp);
        System.out.println("EXP: " + player.exp + "/" + player.expToNext + "   Gold: " + player.gold);
        System.out.println();

        System.out.println("--- Monster Stats ---");
        System.out.println("Name: " + monster.name + (monster.isBoss ? " (Boss)" : ""));
        System.out.println("HP: " + monster.hp + "/" + monster.maxHp + "   MP: " + monster.mp + "/" + monster.maxMp);
        System.out.println("Type: " + (monster.isBoss ? "Boss" : "Minion"));
        if (monster.isBoss && monster.passiveSkills != null && !monster.passiveSkills.isEmpty()) {
            for (Skill s : monster.passiveSkills) {
                System.out.println("Passive: " + s.name + " - " + s.description);
            }
        }
        System.out.println("-----------------------");
    }

    public static void battleOptions() {
        System.out.println("Choose: 1. Attack  2. Inventory  3. Skills 4. Flee");
    }

    public static void printSkills(java.util.ArrayList<Skill> skills) {
        System.out.println("Your Skills:");
        for (int i = 0; i < skills.size(); i++) {
            System.out.println((i + 1) + ". " + skills.get(i).name + " - " + skills.get(i).description);
        }
    }

    public static void printBattleStatus(Player player, Monster monster) {
        System.out.println("--- Player Stats ---");
        System.out.println("HP: " + player.hp + "/" + player.maxHp + "   MP: " + player.mp + "/" + player.maxMp);
        System.out.println();
        System.out.println("--- Monster Stats ---");
        System.out.println("HP: " + monster.hp + "/" + monster.maxHp + "   MP: " + monster.mp + "/" + monster.maxMp);
        System.out.println("-----------------------");
    }

}
