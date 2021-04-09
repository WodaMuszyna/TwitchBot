package me.wodamuszyna.twitchbot.commands.twitch;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import me.wodamuszyna.twitchbot.Main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ICommandManager {

    private static Map<String, ArrayList<ICommand>> commands;
    private static File file;
    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void init(){
        String path = null;
        try {
            path = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        if(path != null)
            path = path.substring(0, path.lastIndexOf("/"));
        file = new File(path+File.separator+"commands.json");
        if(!file.exists()){
            try {
                if(file.createNewFile()){
                    FileWriter fw = new FileWriter(file);
                    fw.write(gson.toJson(new ICommandConfig()));
                    fw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            Reader reader = Files.newBufferedReader(file.toPath());
            JsonObject o = gson.fromJson(reader, JsonObject.class);
            commands = new HashMap<>();
            for(JsonElement el : o.get("commands").getAsJsonArray()){
                JsonObject ob = el.getAsJsonObject();
                Type listType = new TypeToken<ArrayList<ICommand>>() {}.getType();
                commands.put(ob.get("channel").getAsString(), gson.fromJson(ob.get("data"), listType));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void addCommand(String channel, ICommand cmd){ commands.computeIfAbsent(channel, c -> new ArrayList<>()).add(cmd); }

    public static void removeCommand(String channel, ICommand cmd){
        commands.computeIfPresent(channel, (c, l) -> {l.remove(cmd); return l;});
    }

    public static List<ICommand> getCommands(String channel){ return commands.get(channel); }

    public static ICommand get(String channel, String name){
        for(Map.Entry<String, ArrayList<ICommand>> e : commands.entrySet()){
            if(e.getKey().equalsIgnoreCase(channel)){
                for(ICommand c : e.getValue()){
                    if(c.getName().equalsIgnoreCase(name)) return c;
                }
            }
        }
        return null;
    }

    //\$\((.+?)\)

    public static class SaveThread extends Thread{
        @Override
        public void run() {
            try{
                FileWriter fw = new FileWriter(file);
                ICommandConfig config = new ICommandConfig();
                for(Map.Entry<String, ArrayList<ICommand>> e : commands.entrySet()){
                    ICommandConfig.ICommandConfigElement element = new ICommandConfig.ICommandConfigElement(e.getKey(), e.getValue());
                    config.addCommand(element);
                }
                fw.write(gson.toJson(config));
                fw.close();
            }catch (IOException ex){
                ex.printStackTrace();
            }
        }
    }
}
