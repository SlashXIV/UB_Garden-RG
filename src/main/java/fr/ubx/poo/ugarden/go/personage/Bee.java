package fr.ubx.poo.ugarden.go.personage;

import fr.ubx.poo.ugarden.game.Direction;
import fr.ubx.poo.ugarden.game.Game;
import fr.ubx.poo.ugarden.game.Position;
import fr.ubx.poo.ugarden.go.GameObject;
import fr.ubx.poo.ugarden.go.Movable;
import fr.ubx.poo.ugarden.engine.Timer;


public class Bee extends GameObject implements Movable {

    Timer beeFrenquency;

    private Direction direction;

    private boolean moveRequested = false;


    public Bee(Game game, Position position) {
        super(game, position);
        this.direction = Direction.DOWN;
        beeFrenquency = new Timer(game.configuration().beeMoveFrequency());
        beeFrenquency.start();

    }


    public void requestMove(Direction direction) {
        if (direction != this.direction) {
            this.direction = direction;
            setModified(true);
        }
        moveRequested = true;
    }

    public void update(long now) {

        if (moveRequested) {
            if (canMove(direction)) {
                doMove(direction);
            }
        }
        moveRequested = false;

        beeFrenquency.update(now);

        if (!beeFrenquency.isRunning()) {

            Direction randomDirection = Direction.random();

                requestMove(randomDirection);

                beeFrenquency = new Timer(game.configuration().beeMoveFrequency());
                beeFrenquency.start();

        }
    }

    @Override
    public final boolean canMove(Direction direction) {

        Position nextPos = direction.nextPosition(getPosition());

        if(!game.world().getGrid().inside(nextPos)) return false;

        return true;
    }

    @Override
    public void doMove(Direction direction) {

        Position nextPos = direction.nextPosition(getPosition());
        setPosition(nextPos);

    }



    @Override

    public String toString() {
        return "Bee";
    }

    public Direction getDirection() {
        return direction;
    }


}

