package online.umbcraft.balls.enums;

public enum JinglePerm {



    WAND("jingle.wand"),

    TOURNAMENT("jingle.tournament"),

    SPECTATOR("jingle.spectate"),

    ANTITEAM("jingle.antiteam");

    final public String path;

    private JinglePerm(String permissionPath) {
        this.path = permissionPath;
    }


    public String toString() {
        return path;
    }
}
