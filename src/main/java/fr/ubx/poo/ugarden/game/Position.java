package fr.ubx.poo.ugarden.game;

public record Position (int level, int x, int y) {
    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getLevel() {
        return this.level;
    }
}
