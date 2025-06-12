package com.mycompany.toweroftrial;

import java.util.LinkedList;
import java.util.Queue;
import java.util.HashMap;
import java.util.Scanner;

public class Tower {

    private final HashMap<Integer, Queue<Monster>> floors = new HashMap<>();
    private final HashMap<Integer, Queue<Monster>> floorTemplates = new HashMap<>();
    private static final String[] monsterNames = {"Goblin", "Wolf", "Skeleton", "Orc", "Demon"};
    private static final String[] bossNames = {"Goblin King", "Alpha Wolf", "Lich", "Orc Chieftain", "Demon Lord"};

    private int highestClearedFloor = 0; // 0 = none cleared, 1 = cleared floor 1, etc.

    public Tower() {
        // Initialize floor templates and current floors
        for (int f = 1; f <= 5; f++) {
            Queue<Monster> templateQ = new LinkedList<>();
            for (int i = 0; i < 5; i++) {
                templateQ.add(new Monster(monsterNames[f - 1], 60 + f * 15, 20 + f * 5, 10 + f * 2, 5 + f, 12 + f * 3, false));
            }
            templateQ.add(new Monster(bossNames[f - 1], 150 + f * 30, 40 + f * 10, 20 + f * 3, 20 + f * 2, 30 + f * 8, true));
            floorTemplates.put(f, templateQ);
            // Start with a fresh copy for actual floors
            floors.put(f, cloneFloor(templateQ));
        }
    }

    private Queue<Monster> cloneFloor(Queue<Monster> templateQ) {
        Queue<Monster> newQ = new LinkedList<>();
        for (Monster m : templateQ) {
            Monster copy = new Monster(m.name, m.maxHp, m.maxMp, m.attack, m.goldReward, m.expReward, m.isBoss);
            newQ.add(copy);
        }
        return newQ;
    }

    public void climb(Player player, Scanner in) {
        int startFloor = chooseStartingFloor(in);
        for (int f = startFloor; f <= 5; f++) {
            if (floors.get(f).isEmpty()) {
                floors.put(f, cloneFloor(floorTemplates.get(f)));
            }
            System.out.println("Entering Floor " + f + "...");
            Queue<Monster> floor = floors.get(f);
            boolean floorCleared = true;
            while (!floor.isEmpty()) {
                Monster m = floor.peek();
                Battle battle = new Battle(player, m, in);
                boolean result = battle.run();
                if (!result) {
                    System.out.println("You have fallen or fled... Returning to main menu.");
                    player.hp = player.maxHp;
                    player.mp = player.maxMp;
                    floorCleared = false;
                    break;
                }
                floor.poll();
            }
            if (floorCleared) {
                System.out.println("You cleared Floor " + f + "!");
                if (f > highestClearedFloor) {
                    highestClearedFloor = f;
                }

                // Prompt: Proceed or return
                if (f < 5) {
                    System.out.println("What would you like to do?");
                    System.out.println("1. Proceed to next floor");
                    System.out.println("2. Return to safe zone");
                    String input = in.nextLine().trim();
                    if (input.equals("2")) {
                        System.out.println("Returning to safe zone...");
                        player.hp = player.maxHp;
                        player.mp = player.maxMp;
                        break;
                    }
                }
            } else {
                break;
            }
        }
        if (highestClearedFloor == 5) {
            System.out.println("Congratulations! You cleared the Tower!");
        }
    }

    private int chooseStartingFloor(Scanner in) {
        if (highestClearedFloor == 0) {
            return 1;
        }
        System.out.println("Choose a floor to start from:");
        for (int i = 1; i <= highestClearedFloor; i++) {
            System.out.println(i + ". Floor " + i + " (Re-enter)");
        }
        System.out.println((highestClearedFloor + 1) + ". Floor " + (highestClearedFloor + 1) + " (Continue)");
        int choice = 0;
        while (choice < 1 || choice > highestClearedFloor + 1) {
            System.out.print("Enter your choice: ");
            try {
                choice = Integer.parseInt(in.nextLine().trim());
            } catch (Exception e) {
                choice = 0;
            }
        }
        return choice;
    }

    // Save highestClearedFloor for persistence if needed
    public int getHighestClearedFloor() {
        return highestClearedFloor;
    }

    public void setHighestClearedFloor(int val) {
        highestClearedFloor = val;
    }
}
