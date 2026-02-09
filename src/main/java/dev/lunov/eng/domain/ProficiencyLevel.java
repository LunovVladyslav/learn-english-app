package dev.lunov.eng.domain;

import lombok.Getter;

@Getter
public enum ProficiencyLevel {
    A1(100.0, 1.0, 5.0,0.7),
    A2(500.0,5.0, 10.0,1.1),
    B1(1400.0,8.0, 12.0,1.5),
    B2(2500.0,10.0, 15.0,1.7),
    C1(5000.0,15.0, 20.0,2.0),
    C2(10000.0,18.0, 25.0,2.5),
    NATIVE(20000.0,20.0, 30.0,3.0);

    private final double levelPoints;
    private final double levelBonus;
    private final double basePointsMin;
    private final double basePointsMax;

    ProficiencyLevel(double levelPoints, double basePointsMin, double basePointsMax, double levelBonus) {
        this.levelPoints = levelPoints;
        this.basePointsMin = basePointsMin;
        this.basePointsMax = basePointsMax;
        this.levelBonus = levelBonus;
    }

}
