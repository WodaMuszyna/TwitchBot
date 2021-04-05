package me.wodamuszyna.twitchbot.commands.discord;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.entities.Message;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public class Base64 extends Command {

    public Base64(){
        super.name = "base64";
        super.arguments = "<-e/-d> <text>";
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] a = event.getArgs().split(" ");
        Message msg = event.getMessage();
        msg.delete().complete();
        if(a.length < 2){
            event.getChannel().sendMessage("Command usage: !base64 <-e/-d> <text>").complete().delete().queueAfter(10, TimeUnit.SECONDS);
            return;
        }
        StringBuilder sb = new StringBuilder(a[1]);
        for(int i=2; i < a.length; ++i){
            sb.append(" ").append(a[i]);
        }
        switch (a[0]){
            case "-e":
                event.getChannel().sendMessage("Command output: "+ java.util.Base64.getEncoder().encodeToString(sb.toString().getBytes(StandardCharsets.UTF_8))).complete().delete().queueAfter(15, TimeUnit.SECONDS);
                break;
            case "-d":
                event.getChannel().sendMessage("Command output: "+new String(java.util.Base64.getDecoder().decode(sb.toString()))).complete().delete().queueAfter(15, TimeUnit.SECONDS);
                break;
            default:
                break;
        }
    }
}
