package com.mycompany.toweroftrial;

import java.io.*;
import java.util.*;

public class SaveData {

    private static final String SAVE_FOLDER = "saves";

    public SaveData() {
        File folder = new File(SAVE_FOLDER);
        if (!folder.exists()) {
            folder.mkdir();
        }
    }

    // List all save files by name (without extension)
    public List<String> listSaves() {
        File folder = new File(SAVE_FOLDER);
        String[] files = folder.list((dir, name) -> name.endsWith(".sav"));
        List<String> saveNames = new ArrayList<>();
        if (files != null) {
            for (String f : files) {
                saveNames.add(f.substring(0, f.length() - 4));
            }
        }
        return saveNames;
    }

    // Save file under a name (if you want to support multiple saves)
    public void save(Player player) {
        try {
            System.out.print("Enter a name for your save: ");
            Scanner in = new Scanner(System.in);
            String name = in.nextLine().trim();
            if (name.isEmpty()) {
                name = "save";
            }
            FileOutputStream fileOut = new FileOutputStream(SAVE_FOLDER + "/" + name + ".sav");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(player);
            out.close();
            fileOut.close();
            System.out.println("Game saved as '" + name + "'.");
        } catch (IOException i) {
            i.printStackTrace();
            System.out.println("Error saving the game.");
        }
    }

    // Load by save name
    public Player loadByName(String name) {
        try {
            FileInputStream fileIn = new FileInputStream(SAVE_FOLDER + "/" + name + ".sav");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            Player p = (Player) in.readObject();
            in.close();
            fileIn.close();
            return p;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading save '" + name + "'.");
            return null;
        }
    }

    // The old load() method can be adapted to call loadByName()
    public Player load(Scanner in) {
        List<String> saves = listSaves();
        if (saves.isEmpty()) {
            return null;
        }
        System.out.println("Available saves:");
        for (int i = 0; i < saves.size(); i++) {
            System.out.println((i + 1) + ". " + saves.get(i));
        }
        System.out.print("Choose a save to load: ");
        String input = in.nextLine().trim();
        int idx;
        try {
            idx = Integer.parseInt(input) - 1;
        } catch (Exception e) {
            return null;
        }
        if (idx < 0 || idx >= saves.size()) {
            return null;
        }
        return loadByName(saves.get(idx));
    }

    // Delete a save by name
    public boolean deleteSave(String name) {
        File file = new File(SAVE_FOLDER + "/" + name + ".sav");
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }
}
