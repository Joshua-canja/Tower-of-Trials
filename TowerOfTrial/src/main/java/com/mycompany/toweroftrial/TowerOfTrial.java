/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.mycompany.toweroftrial;

/**
 *
 * @author Admin
 */
import java.util.Scanner;

public class TowerOfTrial {

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        Game game = new Game(in);
        game.start();
    }
}
