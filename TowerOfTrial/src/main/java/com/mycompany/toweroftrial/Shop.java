package com.mycompany.toweroftrial;

import java.util.ArrayList;
import java.util.Scanner;

public class Shop {

    private final ArrayList<ShopItem> stock = new ArrayList<>();

    public Shop() {
        stock.add(new ShopItem("HP Potion (Basic)", 10));
        stock.add(new ShopItem("Mana Potion (Basic)", 12));
    }

    public void visit(Player player, Scanner in) {
        while (true) {
            UI.printShop(stock, player.gold);
            System.out.println((stock.size() + 1) + ". Exit");
            System.out.print("Which item would you like to buy? ");
            String input = in.nextLine().trim();
            int sel;
            try {
                sel = Integer.parseInt(input);
            } catch (Exception e) {
                continue;
            }
            if (sel == stock.size() + 1) {
                break;
            }
            if (sel < 1 || sel > stock.size()) {
                continue;
            }
            ShopItem item = stock.get(sel - 1);
            System.out.print("How many would you like to buy? ");
            int qty;
            try {
                qty = Integer.parseInt(in.nextLine().trim());
            } catch (Exception e) {
                System.out.println("Invalid quantity.");
                continue;
            }
            int totalCost = item.price * qty;
            if (player.gold < totalCost) {
                System.out.println("Not enough gold!");
                continue;
            }
            player.gold -= totalCost;
            player.addItem(item.name, qty);
            System.out.println("Purchased " + item.name + " x" + qty + "!");
        }
    }
}
