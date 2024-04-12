/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.ugarden.go.personage;

import fr.ubx.poo.ugarden.game.*;
import fr.ubx.poo.ugarden.go.GameObject;
import fr.ubx.poo.ugarden.go.Movable;
import fr.ubx.poo.ugarden.go.TakeVisitor;
import fr.ubx.poo.ugarden.go.WalkVisitor;
import fr.ubx.poo.ugarden.go.decor.Decor;
import fr.ubx.poo.ugarden.go.bonus.*;
import fr.ubx.poo.ugarden.go.decor.Tree;
import fr.ubx.poo.ugarden.go.decor.ground.Door;
import fr.ubx.poo.ugarden.go.decor.ground.Flowers;

import java.util.LinkedList;
import fr.ubx.poo.ugarden.engine.Timer;

public class Player extends GameObject implements Movable, TakeVisitor, WalkVisitor {

    private Direction direction;
    private boolean moveRequested = false;

    private int lives;

    private int keys;
    
    private int energy ;
    private int diseaseLevel ;

    private boolean invincible = false;

    private final LinkedList<Timer> timersTab = new LinkedList<>();

    private Timer timerWait;

    private Timer oneSecond;

    private Timer timerInvincible ;
    
    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    private boolean foundPrincess ;

    public Direction getDirection() {
        return direction;
    }

    public void requestMove(Direction direction) {
        if (direction != this.direction) {
            this.direction = direction;
            setModified(true);
        }
        moveRequested = true;
    }

    @Override
    public final boolean canMove(Direction direction) {

        Position nextPos = direction.nextPosition(getPosition());

        Decor next = game.world().getGrid().get(nextPos);

        if (next instanceof Flowers || next instanceof Tree || !game.world().getGrid().inside(nextPos) ) return false;

        if(this.energy == 0) return false;

        if(next.energyConsumptionWalk() > this.energy) return false;



        return true;
    }


    public void update(long now) {
        if (moveRequested) {
            if (canMove(direction)) {
                doMove(direction);
            }
        }
        moveRequested = false;

        for (Timer timer : timersTab) {
            timer.update(now);
            if (!timer.isRunning()) {
                diseaseLevel--;
                timersTab.remove(timer);
            }
        }

        if (this.isModified()) {
            timerWait = new Timer(game.configuration().energyRecoverDuration());
            timerWait.start();
            oneSecond = new Timer(1);
            oneSecond.start();
        }

        timerWait.update(now);
        oneSecond.update(now);
        if (!timerWait.isRunning()) {
            if (this.energy < game.configuration().playerEnergy() && !oneSecond.isRunning()) {
                this.energy++;
                oneSecond = new Timer(game.configuration().energyRecoverDuration());
                oneSecond.start();
            }
        }

        if (isInvincible()) {
            if (timerInvincible == null) {
                timerInvincible = new Timer(game.configuration().playerInvincibilityDuration());
                timerInvincible.start();
            }

            timerInvincible.update(now);

            if (!timerInvincible.isRunning()) {
                this.invincible = false;
                timerInvincible = null;

            }
        }

    }





    public Player(Game game, Position position) {
        super(game, position);
        this.direction = Direction.DOWN;
        this.lives = game.configuration().playerLives();
        this.keys = 0;
        this.energy = game.configuration().playerEnergy();
        this.diseaseLevel = 1;
        this.foundPrincess = false;
    }

    @Override
    public void take(Heart bonus) {
        this.lives++;
        game.world().getGrid().get(this.getPosition()).getBonus().remove();
    }

    public void take(Key bonus) {
        this.keys++;
        game.world().getGrid().get(this.getPosition()).getBonus().remove();
    }

    public void take(Princess bonus) {
        foundPrincess = true;
    }

    public void take(PoisonedApple bonus) {

        diseaseLevel++;

        Timer timerPoisoned = new Timer(game.configuration().diseaseDuration());

        timerPoisoned.start();

        timersTab.add(timerPoisoned);

        bonus.remove();
    }


    public void take(Apple bonus) {
        if (this.energy + game.configuration().energyBoost() > game.configuration().playerEnergy()) {
            this.energy = 100;
        } else {
            this.energy += game.configuration().energyBoost();
        }
        this.diseaseLevel = 1;
        bonus.remove();

        timersTab.clear();
    }

    public void take(Door bonus) {
        if (this.keys > 0) {
            this.keys--;

        }
    }





    public void removeLife() {
        this.lives--;
    }

    public void removeEnergy(Decor next) {



        this.energy -= next.energyConsumptionWalk() * this.diseaseLevel;

        if(this.energy <= 0) {this.energy = 0;}
    }

    @Override
    public void doMove(Direction direction) {
        Position nextPos = direction.nextPosition(getPosition());
        Decor next = game.world().getGrid().get(nextPos);
        setPosition(nextPos);
        if (next != null) {

                next.takenBy(this);
                removeEnergy(next);

        }



    }

    public int getNbKeys() {
        return keys;
    }

    public void removeKey() {
        this.keys--;
    }

    public boolean isInvincible() {
        return this.invincible;
    }

    public void setInvincible(boolean b) {
        invincible = b;
    }


    public boolean isFoundPrincess() {
        return this.foundPrincess;
    }

    @Override
    public String toString() {
        return "Player";
    }


    public char[] getKeys() {
        return Integer.toString(this.keys).toCharArray();
    }


    public char[] getEnergy() {
        return Integer.toString(this.energy).toCharArray();
    }

    public char[] getDiseaseLevel() {
        return Integer.toString(this.diseaseLevel).toCharArray();
    }
}
