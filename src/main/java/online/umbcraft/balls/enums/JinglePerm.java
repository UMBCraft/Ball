package online.umbcraft.balls.enums;

public enum JinglePerm {



    WAND("jingle.wand"),

    TOURNAMENT("jingle.tournament");

    final public String path;

    private JinglePerm(String permissionPath) {
        this.path = permissionPath;
    }


    public String toString() {
        return path;
    }
}
