package me.wodamuszyna.twitchbot.commands.twitch.impl;

import com.github.twitch4j.common.enums.CommandPermission;
import com.github.twitch4j.common.events.domain.EventUser;
import me.wodamuszyna.twitchbot.Main;
import me.wodamuszyna.twitchbot.commands.twitch.ICommand;
import me.wodamuszyna.twitchbot.commands.twitch.ICommandManager;

import java.util.List;
import java.util.Locale;

public class Command {

    public static void execute(EventUser u, String channel, String[] a){
        String perm = "EVERYONE";
        String cmd;
        ICommand command;
        if(a.length < 2){
            Main.getClient().getChat().sendMessage(channel, "Command usage: !command <add/edit/delete>");
            return;
        }
        switch (a[1]){
            case "add":
                if(a.length < 4){
                    Main.getClient().getChat().sendMessage(channel, "Command usage: !command add <name> (-p=permissions) <response>");
                    break;
                }
                cmd = a[2];
                if(!cmd.startsWith("!")) cmd = "!"+cmd;
                command = ICommandManager.get(channel, cmd);
                if(command != null){
                    Main.getClient().getChat().sendMessage(channel, "Command {0} already exists".replace("{0}", cmd));
                    break;
                }
                if(a[3].startsWith("-p")){
                    perm = a[3].split("=")[1].toUpperCase(Locale.ROOT);
                    if(!isValid(perm)){
                        Main.getClient().getChat().sendMessage(channel, "Invalid permission level");
                        break;
                    }
                    StringBuilder sb = new StringBuilder(a[4]);
                    for(int i=5; i < a.length; ++i){
                        sb.append(" ").append(a[i]);
                    }
                    new ICommand(channel, cmd, perm, sb.toString());
                }else{
                    StringBuilder sb = new StringBuilder(a[3]);
                    for(int i=4; i < a.length; ++i){
                        sb.append(" ").append(a[i]);
                    }
                    new ICommand(channel, cmd, perm, sb.toString());
                }
                Main.getClient().getChat().sendMessage(channel, "Successfully added command {0}".replace("{0}", cmd));
                break;
            case "edit":
                if(a.length < 4){
                    Main.getClient().getChat().sendMessage(channel, "Command usage: !command edit <name> (-p=permissions) <response>");
                    break;
                }
                cmd = a[2];
                if(!cmd.startsWith("!")) cmd = "!"+cmd;
                command = ICommandManager.get(channel, cmd);
                if(command == null){
                    Main.getClient().getChat().sendMessage(channel, "Command {0} does not exist".replace("{0}", cmd));
                    break;
                }
                if(a[3].startsWith("-p")){
                    perm = a[3].split("=")[1].toUpperCase(Locale.ROOT);
                    if(!isValid(perm)){
                        Main.getClient().getChat().sendMessage(channel, "Invalid permission level");
                        break;
                    }
                    StringBuilder sb = new StringBuilder(a[4]);
                    for(int i=5; i < a.length; ++i){
                        sb.append(" ").append(a[i]);
                    }
                    ICommandManager.removeCommand(channel, command);
                    new ICommand(channel, cmd, perm, sb.toString());
                }else{
                    StringBuilder sb = new StringBuilder(a[3]);
                    for(int i=4; i < a.length; ++i){
                        sb.append(" ").append(a[i]);
                    }
                    ICommandManager.removeCommand(channel, command);
                    new ICommand(channel, cmd, perm, sb.toString());
                }
                Main.getClient().getChat().sendMessage(channel, "Successfully edited command {0}".replace("{0}", cmd));
                break;
            case "delete":
                if(a.length < 3){
                    Main.getClient().getChat().sendMessage(channel, "Command usage: !command delete <name>");
                    break;
                }
                cmd = a[2];
                if(!cmd.startsWith("!")) cmd = "!"+cmd;
                command = ICommandManager.get(channel, cmd);
                if(command == null){
                    Main.getClient().getChat().sendMessage(channel, "Command {0} does not exist".replace("{0}", cmd));
                    break;
                }
                ICommandManager.removeCommand(channel, command);
                Main.getClient().getChat().sendMessage(channel, "Successfully removed command {0}".replace("{0}", cmd));
                break;
            case "list":
                StringBuilder res = new StringBuilder(ICommandManager.getCommands(channel).get(0).getName());
                List<ICommand> cmds = ICommandManager.getCommands(channel);
                if(cmds.size() < 1){
                    Main.getClient().getChat().sendMessage(channel, "No commands found for this channel");
                    break;
                }
                for(int i=1; i < cmds.size(); i++){
                    res.append(", ").append(cmds.get(i).getName());
                }
                Main.getClient().getChat().sendMessage(channel, "List of available commands: "+res.toString());
                break;
            default:
                break;
        }
    }

    private static boolean isValid(String s){
        for(CommandPermission en : CommandPermission.values()){
            if(en.name().equalsIgnoreCase(s)) return true;
        }
        return false;
    }
}
