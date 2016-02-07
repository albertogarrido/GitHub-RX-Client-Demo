package net.albertogarrido.githubrxclient;

/**
 * Created by AlbertoGarrido on 7/2/16.
 */
public class Contributor {

    String login;
    String html_url;
    int contributions;

    @Override
    public String toString() {
        return login + " (" + contributions + ")";
    }
}
