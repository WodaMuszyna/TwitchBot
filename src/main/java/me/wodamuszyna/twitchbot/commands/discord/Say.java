package me.wodamuszyna.twitchbot.commands.discord;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.wodamuszyna.twitchbot.Config;
import me.wodamuszyna.twitchbot.Main;
import net.dv8tion.jda.api.entities.Message;

import java.util.concurrent.TimeUnit;

public class Say extends Command {

    public Say(){
        super.name = "say";
        super.arguments = "<channelname> <message>";
    }

    @Override
    protected void execute(CommandEvent e) {
        String[] a = e.getArgs().split(" ");
        Message msg = e.getMessage();
        if(e.getChannel().getId().equalsIgnoreCase(Config.channel_id)){
            msg.delete().complete();
            if (a.length < 2) {
                e.getChannel().sendMessage("Command usage: !say <channelname> <message>").complete().delete().queueAfter(10, TimeUnit.SECONDS);
                return;
            }
            if (!Config.channels.contains(a[0])) {
                e.getChannel().sendMessage("I'm not in " + a[0] + "'s coop!").complete().delete().queueAfter(10, TimeUnit.SECONDS);
                return;
            }
            StringBuilder sb = new StringBuilder(a[1]);
            for (int i = 2; i < a.length; ++i) {
                sb.append(" ").append(a[i]);
            }
            e.getChannel().sendMessage("You said: " + sb.toString() + " on " + a[0] + "'s chat").complete().delete().queueAfter(15, TimeUnit.SECONDS);
            Main.getClient().getChat().sendMessage(a[0], "[Discord] " + e.getAuthor().getName() + " > " + sb.toString());
        }
    }
}
