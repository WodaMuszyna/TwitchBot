package me.wodamuszyna.twitchbot.commands.twitch;

import com.google.gson.Gson;
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
import java.util.List;

public class ICommandManager {

    private static List<ICommand> commands;
    private static File file;
    private static Gson gson = new Gson();

    public static void init(){
        String path = null;
        try {
            path = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        if(path != null)
            path = path.substring(0, path.lastIndexOf("/"));
        file = new File(path+"/commands.json");

        if(!file.exists()){
            try {
                if(file.createNewFile()){
                    FileWriter fw = new FileWriter(file);
                    fw.write("[]");
                    fw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            Reader reader = Files.newBufferedReader(file.toPath());
            Type listType = new TypeToken<ArrayList<ICommand>>() {}.getType();
            commands = gson.fromJson(reader, listType);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void addCommand(ICommand cmd){ commands.add(cmd); }

    public static void removeCommand(ICommand cmd){
        commands.remove(cmd);
    }

    public static List<ICommand> getCommands(){ return commands; }

    public static ICommand get(String name){
        return commands.stream().filter(command -> (command != null && command.getName().equalsIgnoreCase(name))).findFirst().orElse(null);
    }

    //\$\((.+?)\)

    public static class SaveThread extends Thread{
        @Override
        public void run() {
            try{
                FileWriter fw = new FileWriter(file);
                fw.write(gson.toJson(commands));
                fw.close();
            }catch (IOException ex){
                ex.printStackTrace();
            }
        }
    }
}
