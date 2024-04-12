package fr.ubx.poo.ugarden.launcher;

import fr.ubx.poo.ugarden.game.Position;

import java.util.LinkedList;

import static fr.ubx.poo.ugarden.launcher.MapEntity.Grass;
import static fr.ubx.poo.ugarden.launcher.MapEntity.Player;

public class MapLevel {

    private final int width;
    private final int height;
    private final MapEntity[][] grid;


    private Position playerPosition = null;

    public MapLevel(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new MapEntity[height][width];
    }

    public int width() {
        return width;    }

    public int height() {
        return height;
    }

    public MapEntity get(int i, int j) {
        return grid[j][i];
    }

    public void set(int i, int j, MapEntity mapEntity) {
        grid[j][i] = mapEntity;
    }

    public Position getPlayerPosition() {
        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++)
                if (grid[j][i] == Player) {
                    if (playerPosition != null)
                        throw new RuntimeException("Multiple definition of player");
                    set(i, j, Grass);
                    // Player can be only on level 1
                    playerPosition = new Position(1, i, j);
                }
        return playerPosition;
    }

    public LinkedList<Position> getBeePositions(){
        LinkedList<Position> beePositions = new LinkedList<>();
        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++)
                if (grid[j][i] == MapEntity.Bee) {
                    beePositions.add(new Position(1, i, j));
                    set(i, j, Grass);
                }
        return beePositions;
    }

    //same with doors
    public LinkedList<Position> getDoorPositions(){
        LinkedList<Position> doorPositions = new LinkedList<>();
        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++)
                if ((grid[j][i] == MapEntity.DoorNextClosed) || (grid[j][i] == MapEntity.DoorNextOpened)) {
                    doorPositions.add(new Position(1, i, j));
                    set(i, j, Grass);
                }
        return doorPositions;
    }
}