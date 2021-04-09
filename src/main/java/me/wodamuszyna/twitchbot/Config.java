package me.wodamuszyna.twitchbot;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Config {

    public static String token;
    public static String client_id;
    public static String client_secret;
    public static String discord_token;
    public static String bot_ownerid;
    public static String server_id;
    public static String channel_id;
    public static HashSet<String> channels;
    public static Map<String, ArrayList<String>> messages;

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
        File file = new File(path+File.separator+"config.json");

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
                channels = new HashSet<>();
                for(JsonElement e : object.get("channels").getAsJsonObject().get("name").getAsJsonArray()){
                    channels.add(e.getAsString());
                }
                messages = new HashMap<>();
                for (JsonElement e : object.get("messages").getAsJsonArray()){
                    JsonObject o = e.getAsJsonObject();
                    Type listType = new TypeToken<ArrayList<String>>() {}.getType();
                    messages.put(o.get("id").getAsString(), new Gson().fromJson(o.get("data"), listType));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            System.out.println("CONFIG FILE NOT FOUND");
            System.out.println("RENAME EXAMPLE_CONFIG.JSON TO CONFIG.JSON AND FILL UP");
            System.exit(0);
        }
    }
}
