package online.umbcraft.balls.commands;

public enum JinglePerm {



    WAND("jingle.wand"),

    TOURNAMENT("jingle.tournament"),

    SPECTATOR("jingle.spectate"),

    EXP("jingle.exp"),

    ANTITEAM("jingle.antiteam");

    final public String path;

    private JinglePerm(String permissionPath) {
        this.path = permissionPath;
    }


    public String toString() {
        return path;
    }
}
