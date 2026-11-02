package com.sdstudio.ntmwar.ballistics;

/**
 * Неизменяемый набор параметров выстрела.
 * <p>
 * В 0.1 параметры используются только в тултипах; это заготовка для будущей баллистики (сущность снаряда,
 * пробитие брони плитами, углы встречи, рикошеты). Все конкретные значения — игровые плейсхолдеры для баланса
 * и собраны в одном месте: {@link ShellSpecs}.
 */
public final class ShellSpec {

    private final String id;
    private final int caliberMm;
    private final ShellType type;
    private final float muzzleVelocity;
    private final float projectileMass;
    private final float basePenetration;
    private final float explosiveMass;

    /**
     * @param id              registry name выстрела (он же id предмета), например {@code shell_125_heat}
     * @param caliberMm       калибр, мм
     * @param type            тип снаряда
     * @param muzzleVelocity  начальная скорость, м/с
     * @param projectileMass  масса снаряда (для БОПС — подкалиберной части), кг
     * @param basePenetration базовое пробитие по нормали, мм гомогенной брони (RHA): для БОПС — у дульного среза,
     *                        для кумулятива и ОФС от дальности не зависит
     * @param explosiveMass   масса разрывного заряда в тротиловом эквиваленте, кг (0 — нет заряда)
     */
    public ShellSpec(String id, int caliberMm, ShellType type, float muzzleVelocity, float projectileMass,
        float basePenetration, float explosiveMass) {
        if (id == null || id.isEmpty()) throw new IllegalArgumentException("Shell id must not be empty");
        if (type == null) throw new IllegalArgumentException("Shell type must not be null: " + id);
        if (caliberMm <= 0 || muzzleVelocity <= 0 || projectileMass <= 0 || basePenetration < 0 || explosiveMass < 0) {
            throw new IllegalArgumentException("Invalid ballistic parameters for shell " + id);
        }
        if (type.hasExplosiveFiller() != (explosiveMass > 0)) {
            throw new IllegalArgumentException("Explosive filler does not match shell type for " + id);
        }
        this.id = id;
        this.caliberMm = caliberMm;
        this.type = type;
        this.muzzleVelocity = muzzleVelocity;
        this.projectileMass = projectileMass;
        this.basePenetration = basePenetration;
        this.explosiveMass = explosiveMass;
    }

    public String getId() {
        return id;
    }

    public int getCaliberMm() {
        return caliberMm;
    }

    public ShellType getType() {
        return type;
    }

    public float getMuzzleVelocity() {
        return muzzleVelocity;
    }

    public float getProjectileMass() {
        return projectileMass;
    }

    public float getBasePenetration() {
        return basePenetration;
    }

    public float getExplosiveMass() {
        return explosiveMass;
    }

    /** Дульная энергия снаряда, Дж (E = m·v²/2). Пригодится для модели пробития БОПС. */
    public double getMuzzleEnergy() {
        return 0.5D * projectileMass * muzzleVelocity * muzzleVelocity;
    }

    @Override
    public String toString() {
        return "ShellSpec{" + id + ", " + caliberMm + "mm " + type + "}";
    }
}
