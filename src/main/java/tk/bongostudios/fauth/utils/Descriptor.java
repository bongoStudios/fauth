package tk.bongostudios.fauth.utils;

public enum Descriptor {
    REGISTER("Please register with §a/register <password> <password>"),
    LOGIN("Login with §a/login <password>");

    public String msg;

    private Descriptor(String msg) {
        this.msg = msg;
    }
}