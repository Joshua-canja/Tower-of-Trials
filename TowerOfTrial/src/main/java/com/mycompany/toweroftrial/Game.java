package com.mycompany.toweroftrial;

import java.util.Scanner;

public class Game {

    private final Scanner in;
    private Player player;
    private final Tower tower = new Tower();
    private final Shop shop = new Shop();
    private final SaveData saveData = new SaveData();

    public Game(Scanner in) {
        this.in = in;
    }

    public void start() {
        int startChoice = showStartMenu();
        if (startChoice == 1) {
            // New Game
            UI.printIntro(in);
            String playerName = UI.askName(in);
            String playerClass = UI.askClass(in);

            player = new Player(playerName, playerClass);
            UI.printChosenClass(playerClass);

            UI.tutorialStart();

            Monster tutorialGoblin = new Monster("Goblin", 70, 20, 10, 5, 12, false);
            Battle tutorial = new Battle(player, tutorialGoblin, in);
            tutorial.runTutorial();

            player.gainExp(12);
            player.addGold(5);

            UI.printTutorialClear();

            player.hp = player.maxHp;
            player.mp = player.maxMp;
            System.out.println("Your HP and MP have been fully restored!");

            menuLoop();
        } else if (startChoice == 2) {
            // Load Game
            player = saveData.load();
            if (player == null) {
                System.out.println("No save file found. Starting new game instead.");
                start(); // restart
            } else {
                menuLoop();
            }
        }
    }

    private int showStartMenu() {
        while (true) {
            System.out.println("==== Tower of Trial ====");
            System.out.println("1. New Game");
            System.out.println("2. Load Game");
            System.out.print("Choose: ");
            String input = in.nextLine().trim();
            if (input.equals("1") || input.equals("2")) {
                return Integer.parseInt(input);
            }
            System.out.println("Invalid input.");
        }
    }

    private void menuLoop() {
        while (true) {
            UI.mainMenu();
            String cmd = in.nextLine().trim();

            switch (cmd) {
                case "1":
                    player.showStatus();
                    break;
                case "2":
                    player.showInventory();
                    break;
                case "3":
                    shop.visit(player, in);
                    break;
                case "4":
                    tower.climb(player, in);
                    break;
                case "5":
                    saveData.save(player);
                    break;
                case "6":
                    System.out.println("Goodbye Hero!");
                    return;
                default:
                    System.out.println("Invalid option.");
                    break;
            }
        }
    }
}
