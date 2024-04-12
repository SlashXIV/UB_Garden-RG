/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.ugarden.go.decor.ground;

import fr.ubx.poo.ugarden.game.Game;
import fr.ubx.poo.ugarden.game.Position;
import fr.ubx.poo.ugarden.go.GameObject;
import fr.ubx.poo.ugarden.go.Takeable;


public class Door extends GameObject implements Takeable {

    public Door(Game game, Position position) {
        super(game, position);
    }



    private boolean isOpen = false;

    private int level = 1;


    public int getLevel(){
        return level;
    }

    public void setLevel(int intDoorLevel){
        this.level = intDoorLevel;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean isOpen) {
        this.isOpen = isOpen;
    }


    public void update(long now) {
        setModified(true);
    }
}
