package me.wodamuszyna.twitchbot.commands.twitch;

import java.util.ArrayList;
import java.util.List;

public class ICommandConfig {
    public List<ICommandConfigElement> commands = new ArrayList<>();

    public ICommandConfig(){}

    public void addCommand(ICommandConfigElement command){ commands.add(command);}

    public static class ICommandConfigElement{
        public String channel;
        public List<ICommand> data;

        public ICommandConfigElement(String channel, List<ICommand> data){
            this.channel = channel;
            this.data = data;
        }

        public void setChannel(String channel){ this.channel = channel;}

        public void setData(List<ICommand> data){this.data = data;}
    }
}
