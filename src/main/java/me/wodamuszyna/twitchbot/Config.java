package me.wodamuszyna.twitchbot;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.nio.file.Files;

public class Config {

    public static String token;
    public static String client_id;
    public static String client_secret;
    public static String discord_token;
    public static String bot_ownerid;
    public static String server_id;
    public static String channel_id;

    public static void init(){
        ClassLoader classLoader = Config.class.getClassLoader();

        String path = null;
        try {
            path = new File(Config.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        if(path != null)
        path = path.substring(0, path.lastIndexOf("/"));
        File file = new File(path+"/config.json");

        if(file.exists()){
            try {
                Reader reader = Files.newBufferedReader(file.toPath());
                JsonObject object = new Gson().fromJson(reader, JsonObject.class);
                token = object.get("token").getAsString();
                client_id = object.get("client_id").getAsString();
                client_secret = object.get("client_secret").getAsString();
                discord_token = object.get("discord_token").getAsString();
                bot_ownerid = object.get("bot_ownerid").getAsString();
                server_id = object.get("server_id").getAsString();
                channel_id = object.get("channel_id").getAsString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
