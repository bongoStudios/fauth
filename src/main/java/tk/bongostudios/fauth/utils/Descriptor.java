package tk.bongostudios.fauth.utils;

public enum Descriptor {
    REGISTER("Please register with §7/register <password> <password>"),
    LOGIN("Login with §7/login <password>");

    public String msg;

    private Descriptor(String msg) {
        this.msg = msg;
    }
}