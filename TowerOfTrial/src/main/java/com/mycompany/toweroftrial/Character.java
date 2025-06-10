/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.toweroftrial;

import java.util.HashMap;
import java.util.Map;

public class Character {
    public String name;
    public int level;
    public int hp, maxHp;
    public int mp, maxMp;
    public Map<String, Integer> statusEffects = new HashMap<>(); // e.g., "poison": 2
    public Map<String, Integer> statusEffectValues = new HashMap<>(); // e.g., "poisonDamage": 5

    public Character(String name, int level, int maxHp, int maxMp) {
        this.name = name;
        this.level = level;
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.maxMp = maxMp;
        this.mp = maxMp;
    }
}
