package com.mycompany.toweroftrial;

import java.io.Serializable;

public class Skill implements Serializable {

    public String name;
    public String description;
    public int manaCost;
    public boolean isPassive;
    public String effectType; // "stun", "poison", "slow", "atkDown", "double", etc.
    public int effectValue;   // e.g., poison damage per turn, percent, etc.
    public int duration;      // status effect duration (in turns)
    public double multiplier; // NEW: damage multiplier for skills (default 1.0)

    // For basic skills
    public Skill(String name, String description, int manaCost, double multiplier) {
        this(name, description, manaCost, false, null, 0, 0, multiplier);
    }

    // For passive skills
    public Skill(String name, String description, int manaCost, boolean isPassive) {
        this(name, description, manaCost, isPassive, null, 0, 0, 1.0);
    }

    // For skills with effects
    public Skill(String name, String description, int manaCost, boolean isPassive, String effectType, int effectValue, int duration, double multiplier) {
        this.name = name;
        this.description = description + " (Mana Cost: " + manaCost + ")" + (isPassive ? " [Passive]" : "");
        this.manaCost = manaCost;
        this.isPassive = isPassive;
        this.effectType = effectType;
        this.effectValue = effectValue;
        this.duration = duration;
        this.multiplier = multiplier;
    }
}
