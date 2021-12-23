package online.umbcraft.balls.levels.components.perks;


import online.umbcraft.balls.levels.components.perks.major.GroupedPerk;
import online.umbcraft.balls.levels.components.perks.major.PotencyPerk;
import online.umbcraft.balls.levels.components.perks.major.SpeedPerk;
import online.umbcraft.balls.levels.components.perks.major.TargetPerk;
import online.umbcraft.balls.levels.components.perks.median.*;
import online.umbcraft.balls.levels.components.perks.minor.*;

public enum Perk {


        // minor perks:

    // iron helmet [x]
    HELMET(PerkType.MINOR, new HelmetPerk()),

    // better chestplate [x]
    ARMOR(PerkType.MINOR, new ArmorPerk()),

    // slightly increase chances of digging up items [x]
    CHANCES(PerkType.MINOR, new ChancesPerk()),

    // able to do 2 damage each melee hit [x]
    MELEE(PerkType.MINOR, new MeleePerk()),

    // take 2 less fall damage [x]
    FEATHER(PerkType.MINOR, new FeatherPerk()),

    // gain exp faster [x]
    EXP(PerkType.MINOR, new ExpPerk()),

    // enemies hit by your snowballs glow for 5 seconds [x]
    GLOWING(PerkType.MINOR, new GlowingPerk()),

    // play random sounds to other players as you move [x]
    SOUNDS(PerkType.MINOR, new SoundsPerk()),




        // median perks:

    // jump boost 1 [x]
    JUMP(PerkType.MEDIAN, new JumpPerk()),

    // invisibility (but armor is visible) [x]
    INVISIBILITY(PerkType.MEDIAN, new InvisibilityPerk()),

    // your snowballs do 0.75 more damage [x]
    DAMAGE(PerkType.MEDIAN, new DamagePerk()),

    // gain an extra heart of HP [x]
    HEALTH(PerkType.MEDIAN, new HealthPerk()),

    // punching someone gives them slowness for 2 seconds [x]
    SLOW(PerkType.MEDIAN, new SlowPerk()),




        // major perks

    // speed 1 [x]
    SPEED(PerkType.MAJOR, new SpeedPerk()),

    // more potent items
    POTENCY(PerkType.MAJOR, new PotencyPerk()),

    // enemies hit by your snowballs take more damage for 2 seconds (does not stack) [x]
    TARGET(PerkType.MAJOR, new TargetPerk()),

    // take less damage the more people are around you [x]
    GROUPED(PerkType.MAJOR, new GroupedPerk());

    // chance for snowballs to set enemies on fire

    //

    public PerkType type;
    private PerkImplementation impl;

    Perk(PerkType type, PerkImplementation impl) {
        this.type = type;
        this.impl = impl;
    }

    public PerkImplementation getImplementation() {
        return impl;
    }
}