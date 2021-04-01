package me.wodamuszyna.twitchbot;

import com.github.philippheuer.events4j.simple.domain.EventSubscriber;
import com.github.twitch4j.chat.events.channel.*;
import com.github.twitch4j.events.ChannelGoLiveEvent;
import com.github.twitch4j.events.ChannelGoOfflineEvent;
import com.github.twitch4j.pubsub.events.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import java.awt.*;
import java.io.*;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Map;

public class EventListener extends ListenerAdapter {

    StatCounter counter = null;
    private void summarize(){
        try{
            FileWriter fw = new FileWriter(LocalDate.now()+"-log.txt");
            for(String s : counter.getLogs()){
                fw.write(s);
            }
            fw.close();
        }catch (IOException ex){
            ex.printStackTrace();
        }

        Guild g = Main.getJDA().getGuildById(Config.server_id);
        if(g != null){
            TextChannel c = g.getTextChannelById(Config.channel_id);
            if(c != null){
                int i = 0;
                int j = 0;
                for (Map.Entry<String, Integer> entry : counter.getBits().entrySet()) {
                    i++;
                    j += entry.getValue();
                }
                EmbedBuilder eb = new EmbedBuilder()
                        .setColor(new Color(255, 134, 124))
                        .setTitle("Stream summary")
                        .addField("Total chat messages:", String.valueOf(counter.getChatMsgs().size()), false)
                        .addField("Total follows:", String.valueOf(counter.getFollows().size()), false)
                        .addField("Total subs:", String.valueOf(counter.getSubs().size()), false)
                        .addField("Total bits:", i + " users cheered " + j + " bits in total", false);
                c.sendMessage(eb.build()).queue();
            }
        }
    }

    public void onMessageReceived(MessageReceivedEvent event){
        Message msg = event.getMessage();
        if (msg.getContentRaw().equals("water")) {
            MessageChannel channel = event.getChannel();
            channel.sendMessage("I like being watered \\*cheep\\*")
                    .complete();
        }
    }

    @EventSubscriber
    public void onStart(ChannelGoLiveEvent e){
        if(counter != null) {
            counter.addLog(new Timestamp(System.currentTimeMillis()) + " > Ephymeralis went live: " + e.getStream().getTitle());
        }
    }

    @EventSubscriber
    public void onEnd(ChannelGoOfflineEvent e){
        if(counter != null) {
            counter.addLog(new Timestamp(System.currentTimeMillis()) + " > Ephymeralis has ended the stream");
        }
    }

    @EventSubscriber
    public void onFollow(FollowingEvent e){
        Main.getClient().getChat().sendMessage("Ephymeralis", "Ephy thanks you for the follow {who}".replace("{who}", e.getData().getDisplayName()));
        if(counter != null) {
            counter.addFollow(e.getData().getDisplayName());
            counter.addLog(new Timestamp(System.currentTimeMillis()) + " > " + e.getData().getDisplayName() + " followed");
        }
    }

    @EventSubscriber
    public void onCheer(ChannelBitsEvent e){
        Main.getClient().getChat().sendMessage("Ephymeralis", "Thank you so much for the bits {person}! Ephy is thankful!".replace("{person}", e.getData().getUserName()));
        if(counter != null) {
            counter.addBits(e.getData().getUserName(), e.getData().getBitsUsed());
            counter.addLog(new Timestamp(System.currentTimeMillis()) + " > " + e.getData().getUserName() + " cheered " + e.getData().getBitsUsed() + " bits");
        }
    }

    @EventSubscriber
    public void onRaid(RaidEvent e){
        Main.getClient().getChat().sendMessage("Ephymeralis", "Thank you for the raid {person}! I see you've brought over {number} friends! Welcome!"
                .replace("{person}", e.getRaider().getName()).replace("{number}", e.getViewers().toString()));
        if(counter != null) {
            counter.addLog(new Timestamp(System.currentTimeMillis()) + " > " + e.getRaider().getName() + " raided with " + e.getViewers() + " viewers");
        }
    }

    @EventSubscriber
    public void onSubGiftBomb(GiftSubscriptionsEvent e){
        Main.getClient().getChat().sendMessage("Ephymeralis", "Thank you for being generous {person}, and gifting {number} subs to the viewers!"
        .replace("{person}", e.getUser().getName()).replace("{number}", e.getCount().toString()));
        if(counter != null) {
            for (int i = 0; i < e.getCount(); i++) {
                counter.addSub("gift" + i);
            }
            counter.addLog(new Timestamp(System.currentTimeMillis()) + " > " + e.getUser().getName() + " gifted " + e.getCount() + " subs");
        }
    }

    @EventSubscriber
    public void onSub(ChannelSubscribeEvent e){
//        if(e.getData().getIsGift()){
//            Main.getClient().getChat().sendMessage("Ephymeralis", "Thank you for being generous {person}, and gifting a sub to {person2}!"
//            .replace("{person}", e.getData().getUserName()).replace("{person2}", e.getData().getRecipientDisplayName()));
//            if(counter != null) {
//                counter.addSub(e.getData().getRecipientDisplayName());
//                counter.addLog(new Timestamp(System.currentTimeMillis()) + " > " + e.getData().getUserName() + " gifted a sub to " + e.getData().getRecipientDisplayName());
//            }
//        }else {
            Main.getClient().getChat().sendMessage("Ephymeralis", "Welcome to the sub club, {person}! Enjoy your Ephyrium badge!".replace("{person}", e.getData().getDisplayName()));
            if (counter != null) {
                counter.addSub(e.getData().getDisplayName());
                counter.addLog(new Timestamp(System.currentTimeMillis()) + " > " + e.getData().getDisplayName() + " subscribed");
            }
       // }
    }

    @EventSubscriber
    public void onChat(ChannelMessageEvent e){
        String[] a = e.getMessage().split(" ");
        String cmd = a[0];
        if(e.getUser().getName().equalsIgnoreCase("wodamuszyna") || e.getUser().getName().equalsIgnoreCase("ephymeralis")){
            if(cmd.equalsIgnoreCase("!counter")){
                if(a.length == 1){
                    Main.getClient().getChat().sendMessage("Ephymeralis", "Correct usage: !counter <start/stop>");
                    return;
                }
                switch (a[1]){
                    case "start":
                        if(counter != null){
                            Main.getClient().getChat().sendMessage("Ephymeralis", "Counter is already running!");
                            break;
                        }
                        counter = new StatCounter();
                        Main.getClient().getChat().sendMessage("Ephymeralis", "New counter has been started!");
                        break;
                    case "stop":
                        if(counter == null){
                            Main.getClient().getChat().sendMessage("Ephymeralis", "The counter is not running. You can't stop it");
                            break;
                        }
                        summarize();
                        counter = null;
                        Main.getClient().getChat().sendMessage("Ephymeralis", "Counter has been stopped!");
                        break;
                    default:
                        Main.getClient().getChat().sendMessage("Ephymeralis", "Correct usage: !counter <start/stop>");
                        break;
                }
            }
        }
        if(counter != null) {
            counter.addMsg(e.getUser().getName() + " > " + e.getMessage());
            counter.addLog(new Timestamp(System.currentTimeMillis()) + " > " + e.getUser().getName() + ": " + e.getMessage());
        }
    }

}
