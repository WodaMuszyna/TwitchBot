package me.wodamuszyna.twitchbot.commands.twitch;

public class ICommand {
    public String name;
    public String permission;
    public String message;

    public ICommand(String name, String permission, String message){
        this.name = name;
        this.permission = permission;
        this.message = message;
        ICommandManager.addCommand(this);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
