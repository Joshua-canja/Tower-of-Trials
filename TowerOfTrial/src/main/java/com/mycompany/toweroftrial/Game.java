package com.mycompany.toweroftrial;

import java.util.*;

public class Game {

    private Scanner in;
    private Player player;
    private SaveData saveData;
    private Random random = new Random();

    // Track which bosses have been used this run
    private Set<Integer> usedBossIndexes = new HashSet<>();

    public Game(Scanner in) {
        this.in = in;
        saveData = new SaveData();
    }

    public void start() {
        System.out.println(">>>> Tower of Trial <<<<");
        System.out.println("1. New Game\n2. Load Game\n3. Exit");
        String input = in.nextLine().trim();
        switch (input) {
            case "1":
                newGame();
                break;
            case "2":
                loadGameWithDeleteOption();
                break;
            default:
                System.out.println("Goodbye!");
                System.exit(0);
        }
    }

    private void loadGameWithDeleteOption() {
        // Get list of saves from SaveData (implement listSaves in SaveData)
        List<String> saveNames = saveData.listSaves();
        if (saveNames.isEmpty()) {
            System.out.println("No save files found. Starting new game instead.");
            newGame();
            return;
        }
        while (true) {
            System.out.println("\n=== Saved Games ===");
            for (int i = 0; i < saveNames.size(); i++) {
                System.out.println((i + 1) + ". " + saveNames.get(i));
            }
            System.out.println((saveNames.size() + 1) + ". Delete a save");
            System.out.println((saveNames.size() + 2) + ". Back to main menu");
            System.out.print("Select a save to load, or choose another option: ");
            String input = in.nextLine().trim();
            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (Exception e) {
                System.out.println("Invalid input.");
                continue;
            }
            if (choice >= 1 && choice <= saveNames.size()) {
                // Load selected save
                player = saveData.loadByName(saveNames.get(choice - 1));
                if (player == null) {
                    System.out.println("Failed to load the save.");
                    continue;
                }
                menuLoop();
                return;
            } else if (choice == saveNames.size() + 1) {
                // Delete option
                System.out.print("Enter the number of the save to delete: ");
                String delInput = in.nextLine().trim();
                int delChoice;
                try {
                    delChoice = Integer.parseInt(delInput);
                } catch (Exception e) {
                    System.out.println("Invalid input.");
                    continue;
                }
                if (delChoice >= 1 && delChoice <= saveNames.size()) {
                    boolean deleted = saveData.deleteSave(saveNames.get(delChoice - 1));
                    if (deleted) {
                        System.out.println("Save deleted.");
                        saveNames = saveData.listSaves(); // Refresh the list
                        if (saveNames.isEmpty()) {
                            System.out.println("No saves left. Returning to main menu.");
                            return;
                        }
                    } else {
                        System.out.println("Failed to delete save.");
                    }
                } else {
                    System.out.println("Invalid save number.");
                }
            } else if (choice == saveNames.size() + 2) {
                // Back to main menu
                return;
            } else {
                System.out.println("Invalid choice.");
            }
        }
    }

    private void newGame() {
        UI.printIntro(in);
        String name = UI.askName(in);
        String playerClass = UI.askClass(in);
        UI.printChosenClass(playerClass);
        player = new Player(name, playerClass);

        // --- Tutorial battle ---
        UI.tutorialStart();
        Monster tutorialGoblin = new Monster("Goblin", 40, 0, 7, 0, 5, false);
        Battle tutorialBattle = new Battle(player, tutorialGoblin, in);
        boolean win = tutorialBattle.run();
        if (win) {
            UI.printTutorialClear();
            player.gainExp(10);
            menuLoop();
        } else {
            System.out.println("You failed the tutorial! Try again...");
            newGame();
        }
    }

    private void menuLoop() {
        while (true) {
            UI.mainMenu();
            String input = in.nextLine().trim();
            switch (input) {
                case "1":
                    player.showStatus();
                    break;
                case "2":
                    player.showInventory();
                    break;
                case "3":
                    openShop();
                    break;
                case "4":
                    enterTower();
                    break;
                case "5":
                    saveData.save(player);
                    break;
                case "6":
                    System.out.println("Goodbye!");
                    System.exit(0);
                default:
                    System.out.println("Invalid input.");
            }
        }
    }

    private void openShop() {
        while (true) {
            System.out.println("=== Welcome to the Shop! ===");
            System.out.println("Your Gold: " + player.gold);
            System.out.println("1. Buy HP Potion (20 Gold)");
            System.out.println("2. Buy Mana Potion (15 Gold)");
            System.out.println("3. Exit Shop");
            System.out.print("Choose an option: ");
            String input = in.nextLine().trim();
            switch (input) {
                case "1":
                    if (player.gold >= 20) {
                        player.addItem("HP Potion", 1);
                        player.gold -= 20;
                        System.out.println("Bought 1 HP Potion.");
                    } else {
                        System.out.println("Not enough gold.");
                    }
                    break;
                case "2":
                    if (player.gold >= 15) {
                        player.addItem("Mana Potion", 1);
                        player.gold -= 15;
                        System.out.println("Bought 1 Mana Potion.");
                    } else {
                        System.out.println("Not enough gold.");
                    }
                    break;
                case "3":
                    System.out.println("You left the shop.");
                    return;
                default:
                    System.out.println("Invalid input.");
            }
            System.out.println();
        }
    }

    // Tower feature with progress
    private void enterTower() {
        // If player has made progress, give a choice
        if (player.maxTowerFloorCleared > 0) {
            System.out.println("You have previously cleared up to Floor " + player.maxTowerFloorCleared + ".");
            System.out.println("1. Re-enter a cleared floor");
            System.out.println("2. Proceed to next uncleared floor (Floor " + (player.maxTowerFloorCleared + 1) + ")");
            System.out.println("3. Exit");
            String choice = in.nextLine().trim();
            if (choice.equals("1")) {
                // Let player pick which floor to replay
                System.out.print("Which floor do you want to replay (1-" + player.maxTowerFloorCleared + ")? ");
                String flrStr = in.nextLine().trim();
                int flr;
                try {
                    flr = Integer.parseInt(flrStr);
                } catch (Exception e) {
                    System.out.println("Invalid floor.");
                    return;
                }
                if (flr < 1 || flr > player.maxTowerFloorCleared) {
                    System.out.println("Invalid floor.");
                    return;
                }
                runTowerFloor(flr, false); // false = don't update progress
                return;
            } else if (choice.equals("2")) {
                int startFloor = player.maxTowerFloorCleared + 1;
                runTowerFloor(startFloor, true);
                return;
            } else {
                System.out.println("You leave the tower for now.");
                return;
            }
        } else {
            // First time
            runTowerFloor(1, true);
        }
    }

    // Helper to run a single floor, update progress if needed
    private void runTowerFloor(int floor, boolean updateProgress) {
        usedBossIndexes.clear();
        System.out.println("==== FLOOR " + floor + " ====");
        // 5 minions per floor
        for (int minionNum = 1; minionNum <= 5; minionNum++) {
            Monster minion = generateMinion(floor);
            System.out.println("Minion " + minionNum + " appears: " + minion.name + " (HP: " + minion.hp + ", ATK: " + minion.attack + ")");
            Battle battle = new Battle(player, minion, in);
            boolean win = battle.run();
            if (!win) {
                System.out.println("You were defeated on floor " + floor + ", minion " + minionNum + ". Returning to main menu.");
                player.restoreStats(); // <--- Reset stats on defeat
                player.currentTowerFloor = floor;
                return;
            }
        }
        // Boss fight with unique boss
        Monster boss = generateUniqueBoss(floor);
        System.out.println("BOSS of Floor " + floor + " appears: " + boss.name + " (HP: " + boss.hp + ", ATK: " + boss.attack + ")");
        Battle bossBattle = new Battle(player, boss, in);
        boolean win = bossBattle.run();
        if (!win) {
            System.out.println("You were defeated by the boss of floor " + floor + ". Returning to main menu.");
            player.restoreStats(); // <--- Reset stats on defeat
            player.currentTowerFloor = floor;
            return;
        } else {
            System.out.println("You cleared floor " + floor + "!");
            player.hp = Math.min(player.hp + 30, player.maxHp);
            player.mp = Math.min(player.mp + 15, player.maxMp);
            System.out.println("You feel a little refreshed (+30 HP, +15 MP).");
            if (updateProgress && floor > player.maxTowerFloorCleared) {
                player.maxTowerFloorCleared = floor;
                player.currentTowerFloor = floor;
            }
            if (floor == 5) {
                System.out.println("Congratulations! You cleared all 5 floors of the Tower of Trial!");
                player.currentTowerFloor = 1;
                return;
            }
        }
        // Prompt for next floor
        if (floor < 5 && updateProgress) {
            System.out.println("Do you want to proceed to the next floor? (y/n)");
            String input = in.nextLine().trim().toLowerCase();
            if (input.equals("y")) {
                runTowerFloor(floor + 1, true);
            } else {
                System.out.println("You leave the tower for now. Progress saved.");
            }
        }
    }

    // Helper to generate a minion for a given floor
    private Monster generateMinion(int floor) {
        String[] names = {"Slime", "Bat", "Skeleton", "Wolf", "Orc", "Goblin", "Lizardman"};
        String name = names[random.nextInt(names.length)];
        int baseHp = 35 + floor * 10 + random.nextInt(11);
        int baseAtk = 8 + floor * 2 + random.nextInt(4);
        int gold = 10 + floor * 2 + random.nextInt(5);
        int exp = 8 + floor * 2 + random.nextInt(5);
        return new Monster(name, baseHp, 0, baseAtk, gold, exp, false);
    }

    // Helper to generate a unique boss for a given floor
    private Monster generateUniqueBoss(int floor) {
        String[] bossNames = {
            "Giant Slime", "Vampire Bat", "Bone Knight", "Dire Wolf", "Orc Chieftain", "Goblin King", "Lizardman Captain"
        };
        List<Integer> availableIndexes = new ArrayList<>();
        for (int i = 0; i < bossNames.length; i++) {
            if (!usedBossIndexes.contains(i)) {
                availableIndexes.add(i);
            }
        }
        if (availableIndexes.isEmpty()) {
            availableIndexes.add(0); // fallback
        }
        int chosenIdx = availableIndexes.get(random.nextInt(availableIndexes.size()));
        usedBossIndexes.add(chosenIdx);

        String name = bossNames[chosenIdx];
        int baseHp = 80 + floor * 25 + random.nextInt(21);
        int baseAtk = 16 + floor * 4 + random.nextInt(6);
        int gold = 40 + floor * 5 + random.nextInt(11);
        int exp = 35 + floor * 7 + random.nextInt(13);
        return new Monster(name, baseHp, 0, baseAtk, gold, exp, true);
    }
}
