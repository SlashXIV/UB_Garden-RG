package fr.ubx.poo.ugarden.launcher;

import fr.ubx.poo.ugarden.game.*;

import java.io.*;
import java.util.LinkedList;
import java.util.Properties;

public class GameLauncher {

    private static class LoadSingleton {
        static final GameLauncher INSTANCE = new GameLauncher();
    }
    private GameLauncher() {}

    public static GameLauncher getInstance() {
        return LoadSingleton.INSTANCE;
    }

    private int integerProperty(Properties properties, String name, int defaultValue) {
        return Integer.parseInt(properties.getProperty(name, Integer.toString(defaultValue)));
    }

    private boolean booleanProperty(Properties properties, String name, boolean defaultValue) {
        return Boolean.parseBoolean(properties.getProperty(name, Boolean.toString(defaultValue)));
    }

    private Configuration getConfiguration(Properties properties) {

        // Load parameters
        int playerLives = integerProperty(properties, "playerLives", 5);
        int playerInvincibilityDuration = integerProperty(properties, "playerInvincibilityDuration", 4);
        int beeMoveFrequency = integerProperty(properties, "beeMoveFrequency", 1);
        int playerEnergy = integerProperty(properties, "playerEnergy", 100);
        int energyBoost = integerProperty(properties, "energyBoost", 50);
        int energyRecoverDuration = integerProperty(properties, "energyRecoverDuration", 5);
        int diseaseDuration = integerProperty(properties, "diseaseDuration", 5);

        return new Configuration(playerLives, playerEnergy, energyBoost, playerInvincibilityDuration, beeMoveFrequency, energyRecoverDuration, diseaseDuration);
    }

    public Game load() {
        Properties emptyConfig = new Properties();
        MapLevel mapLevel = new MapLevelDefault();
        Position playerPosition = mapLevel.getPlayerPosition();
        LinkedList<Position> beePositions = mapLevel.getBeePositions();
        LinkedList<Position> doorPositions = mapLevel.getDoorPositions();

        if (playerPosition == null)
            throw new RuntimeException("Player not found");
        if (beePositions == null)
            throw new RuntimeException("Bee not found");

        Configuration configuration = getConfiguration(emptyConfig);
        WorldLevels world = new WorldLevels(1);
        Game game = new Game(world, configuration, playerPosition,beePositions,doorPositions,beePositions,doorPositions);
        Map level = new Level(game, 1, mapLevel);
        world.put(1, level);
        return game;
    }

    public void normal(int w,int h, MapLevel map,  String[] rows){
        for (int row = 0; row < h; row++) {
            String rowData = rows[row];
            for (int col = 0; col < w; col++) {
                char c = rowData.charAt(col);
                if (c != ' ') {
                    map.set(col, row, MapEntity.fromCode(c));
                }
            }
        }
    }

    public void compressed(MapLevel map, String[] rows) {
        for (int j = 0; j < map.height(); j++) {
            int i = 0;
            int k = 0;
            while (k < rows[j].length()) {
                char c = rows[j].charAt(k);
                if (Character.isLetter(c) || c == '+' || c == '-' ) {
                    if (i < map.width()) {
                        map.set(i, j, MapEntity.fromCode(c));
                        i++;
                    }
                    k++;
                }else if(c == '<' || c =='>'){
                    if (i < map.width()) {
                        map.set(i, j, MapEntity.fromCode('D'));
                        i++;
                    }
                    k++;

                }
                else if (Character.isDigit(c)) {
                    int count = Character.getNumericValue(c);
                    char prevChar = rows[j].charAt(k - 1);
                    for (int m = 0; m < count - 1; m++) {
                        if (i < map.width()) {
                            map.set(i, j, MapEntity.fromCode(prevChar));
                            i++;
                        } else if (k == rows[j].length() - 1) {
                            for (int n = i; n < map.width(); n++) {
                                map.set(n, j, MapEntity.fromCode(prevChar));
                            }
                        }
                    }
                    k++;
                }
            }
        }
    }







    public Game loadFile(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean compression = false;
            Properties emptyConfig = new Properties();

            StringBuilder level1String = new StringBuilder();
            StringBuilder level2String = new StringBuilder();
            int playerLives = getConfiguration(emptyConfig).playerLives();
            int beeMoveFrequency = getConfiguration(emptyConfig).beeMoveFrequency();
            int energy =  getConfiguration(emptyConfig).playerEnergy();
            int energyBoost = getConfiguration(emptyConfig).energyBoost();
            int energyRecoverDuration = getConfiguration(emptyConfig).energyRecoverDuration();
            int diseaseDuration = getConfiguration(emptyConfig).diseaseDuration();
            int playerInvincibilityDuration = getConfiguration(emptyConfig).playerInvincibilityDuration();

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("compression = ")) {
                    compression = Boolean.parseBoolean(line.substring(14));
                } else if (line.contains("level1")) {
                    level1String.append(line.split("=")[1].trim());
                }
                else if (line.contains("level2")){
                    level2String.append(line.split("=")[1].trim());
                }
                else if (line.contains("playerLives")) {
                    String playerLivesString = line.split("=")[1].trim();
                    playerLives = Integer.parseInt(playerLivesString);
                }
                else if (line.contains("beeMoveFrequency")) {
                    String beeMoveFrequencyString = line.split("=")[1].trim();
                    beeMoveFrequency = Integer.parseInt(beeMoveFrequencyString);
                }
                else if(line.contains("playerEnergy")){
                    String playerEnergyString = line.split("=")[1].trim();
                    energy = Integer.parseInt(playerEnergyString);
                }
                else if(line.contains("energyBoost")){
                    String energyBoostString = line.split("=")[1].trim();
                    energyBoost = Integer.parseInt(energyBoostString);
                }
                else if(line.contains("energyRecoverDuration")){
                    String energyRecoverDurationString = line.split("=")[1].trim();
                    energyRecoverDuration = Integer.parseInt(energyRecoverDurationString);
                }
                else if(line.contains("diseaseDuration")){
                    String diseaseDurationString = line.split("=")[1].trim();
                    diseaseDuration = Integer.parseInt(diseaseDurationString);
                }
                else if(line.contains("playerInvincibilityDuration")){
                    String playerInvincibilityDurationString = line.split("=")[1].trim();
                    playerInvincibilityDuration = Integer.parseInt(playerInvincibilityDurationString);
                }


            }
            reader.close();

            String level1Data = level1String.toString();
            String[] rows = level1Data.split("x");


            int height = rows.length;
            int width = 0;
            for (String row : rows) {
                width = Math.max(width, row.length());
            }
            MapLevel map = new MapLevel(width, height);


            //same for level2

            String level2Data = level2String.toString();
            String[] rows2 = level2Data.split("x");

            int height2 = rows2.length;
            int width2 = 0;
            for (String row : rows2) {
                width2 = Math.max(width2, row.length());
            }

            int widthx = 0;
            for (int i = 0; i < rows2[0].length(); i++) {
                if (Character.isDigit(rows2[0].charAt(i))) {
                    widthx += Character.getNumericValue(rows2[0].charAt(i)) - 1;
                } else {
                    widthx++;
                }
            }

            MapLevel map2 = new MapLevel(widthx, height2);


            if(!compression) {
                normal(width, height, map, rows);
                normal(width2,height2,map2,rows2);
            } else {
                compressed( map, rows);
                compressed(map2,rows2);
            }



            Position playerPosition = map.getPlayerPosition();
            LinkedList<Position> beePositions = map.getBeePositions();
            LinkedList<Position> doorPositions = map.getDoorPositions();


            LinkedList<Position> beePositions2 = map2.getBeePositions();
            LinkedList<Position> doorPositions2 = map2.getDoorPositions();



            if (playerPosition == null)
                throw new RuntimeException("Player not found");
            if (beePositions == null)
                throw new RuntimeException("Bee not found");

            if (beePositions2 == null)
                throw new RuntimeException("Bee not found");

            if(doorPositions2 == null)
                throw new RuntimeException("Door not found");



            Configuration configuration = new Configuration(playerLives,energy,energyBoost,playerInvincibilityDuration,beeMoveFrequency,energyRecoverDuration,diseaseDuration);



            WorldLevels world = new WorldLevels(2);
            Game game = new Game(world, configuration, playerPosition, beePositions, doorPositions, beePositions2, doorPositions2 );
            Map level = new Level(game, 1, map);
            Map level2 = new Level(game,2,map2);


            world.put(1, level);
            world.put(2,level2);





            return game;


        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return null; // Retourne null en cas d'erreur
    }





}