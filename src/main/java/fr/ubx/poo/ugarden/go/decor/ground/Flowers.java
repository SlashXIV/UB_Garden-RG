/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.ugarden.go.decor.ground;

import fr.ubx.poo.ugarden.game.Position;
import fr.ubx.poo.ugarden.go.bonus.Bonus;
import fr.ubx.poo.ugarden.go.personage.Player;

public class Flowers extends Ground {
    public Flowers(Position position) {
        super(position);
    }

    @Override
    public void takenBy(Player player) {
        Bonus bonus = getBonus();
        if (bonus != null) {
            bonus.takenBy(player);
        }
    }

}
