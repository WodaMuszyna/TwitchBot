package me.wodamuszyna.twitchbot.commands.discord;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.entities.Message;

import java.util.concurrent.TimeUnit;

public class Random extends Command {

    public Random(){
        super.name = "random";
        super.arguments = "<from> <to>";
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] a = event.getArgs().split(" ");
        Message msg = event.getMessage();
        msg.delete().complete();
        if(a.length < 2){
            event.getChannel().sendMessage("Command usage: !random <min> <max>").complete().delete().queueAfter(10, TimeUnit.SECONDS);
            return;
        }
        java.util.Random r = new java.util.Random();
        try {
            int min = Integer.parseInt(a[0]);
            int max = Integer.parseInt(a[1]);
            if(min > max){
                event.getChannel().sendMessage("Invalid range!").complete().delete().queueAfter(10, TimeUnit.SECONDS);
                return;
            }
            int res = r.nextInt((max-min)+1)+min;
            event.getChannel().sendMessage("Generated number: "+ res).complete().delete().queueAfter(20, TimeUnit.SECONDS);
        }catch (NumberFormatException ex){
            event.getChannel().sendMessage("The arguments must be numbers in integer range!").complete().delete().queueAfter(10, TimeUnit.SECONDS);
        }
    }
}
