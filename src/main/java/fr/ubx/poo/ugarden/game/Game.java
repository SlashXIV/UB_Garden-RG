package fr.ubx.poo.ugarden.game;

import fr.ubx.poo.ugarden.go.decor.ground.Door;
import fr.ubx.poo.ugarden.go.personage.Bee;
import fr.ubx.poo.ugarden.go.personage.Player;

import java.util.LinkedList;


public class Game {

    private final Configuration configuration;

    public Configuration configuration() {
        return configuration;
    }

    private final World world;
    private final Player player;

    private final LinkedList<Bee> bees = new LinkedList<>();

    private final LinkedList<Door> doors = new LinkedList<>();

    private final LinkedList<Bee> bees2 = new LinkedList<>();

    private final LinkedList<Door> doors2 = new LinkedList<>();





    private boolean switchLevelRequested = false;
    private int switchLevel;

    public Game(World world, Configuration configuration, Position playerPosition, LinkedList<Position> beesPositions, LinkedList<Position> doorPositions, LinkedList<Position> beesPositions2, LinkedList<Position>doorPositions2) {
        this.configuration = configuration;
        this.world = world;
        player = new Player(this, playerPosition);
        for (Position position : beesPositions) {
            bees.add(new Bee(this, position));
        }
        for (Position position : doorPositions) {
            doors.add(new Door(this, position));
        }
        for (Position position : beesPositions2) {
            bees2.add(new Bee(this, position));
        }
        for (Position position : doorPositions2) {
            doors2.add(new Door(this, position));
        }
    }

    public Player getPlayer() {
        return this.player;
    }



    public LinkedList<Bee> getBeeList() {
        return this.bees;
    }

    public LinkedList<Door> getDoorList() {
        return this.doors;
    }

    public LinkedList<Bee> getBeeList2() {
        return this.bees2;
    }

    public LinkedList<Door> getDoorList2() {
        return this.doors2;
    }



    public World world() {
        return world;
    }

    public boolean isSwitchLevelRequested() {
        return switchLevelRequested;
    }

    public int getSwitchLevel() {
        return switchLevel;
    }

    public void requestSwitchLevel(int level) {
        this.switchLevel = level;
        switchLevelRequested = true;
    }

    public void clearSwitchLevel() {
        switchLevelRequested = false;
    }

}
