package fr.ubx.poo.ugarden.go;

import fr.ubx.poo.ugarden.go.bonus.Heart;

public interface TakeVisitor {

    // HealthInc
    default void take(Heart bonus) {
    }

}
