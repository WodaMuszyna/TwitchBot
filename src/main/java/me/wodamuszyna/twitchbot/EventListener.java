package me.wodamuszyna.twitchbot;

import com.github.philippheuer.events4j.simple.domain.EventSubscriber;
import com.github.twitch4j.chat.events.channel.*;
import com.github.twitch4j.common.enums.CommandPermission;
import com.github.twitch4j.events.ChannelGoLiveEvent;
import com.github.twitch4j.events.ChannelGoOfflineEvent;
import com.github.twitch4j.pubsub.events.*;
import me.wodamuszyna.twitchbot.commands.twitch.ICommand;
import me.wodamuszyna.twitchbot.commands.twitch.ICommandManager;
import me.wodamuszyna.twitchbot.commands.twitch.impl.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class EventListener extends ListenerAdapter {

    StatCounter counter = null;
    Guild g;

    private void summarize(String channel){
        counter = StatCounter.get(channel);
        if(counter != null) {
            try {
                FileWriter fw = new FileWriter(channel+"-"+LocalDate.now() + "-log.txt");
                for (String s : counter.getLogs()) {
                    fw.write(s);
                }
                fw.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            if(channel.equalsIgnoreCase("ephymeralis")) {
                g = Main.getJDA().getGuildById(Config.server_id);
                if (g != null) {
                    TextChannel c = g.getTextChannelById(Config.channel_id);
                    if (c != null) {
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
        if(e.getChannel().getName().equalsIgnoreCase("ephymeralis")){
            g = Main.getJDA().getGuildById(Config.server_id);
            if (g != null) {
                TextChannel c = g.getTextChannelById(Config.channel_id);
                if (c != null) {
                    c.sendMessage("Ephy just went live on Twitch! Join cozy stream here:").complete();
                    c.sendMessage("https://twitch.tv/ephymeralis").complete();
                }
            }
        }
        counter = StatCounter.get(e.getChannel().getName());
        if(counter != null) {
            counter.addLog(new Timestamp(System.currentTimeMillis()) + " > "+e.getChannel().getName()+" went live: " + e.getStream().getTitle());
        }
    }

    @EventSubscriber
    public void onEnd(ChannelGoOfflineEvent e){
        counter = StatCounter.get(e.getChannel().getName());
        if(counter != null) {
            counter.addLog(new Timestamp(System.currentTimeMillis()) + " > "+e.getChannel().getName()+" has ended the stream");
        }
    }

    @EventSubscriber
    public void onFollow(FollowingEvent e){
        String name = Main.getClient().getChat().getChannelIdToChannelName().get(e.getChannelId());
        Main.getClient().getChat().sendMessage(name, Config.messages.get(e.getChannelId()).get(0).replace("{who}", e.getData().getDisplayName()));
        counter = StatCounter.get(name);
        if(counter != null) {
            counter.addFollow(e.getData().getDisplayName());
            counter.addLog(new Timestamp(System.currentTimeMillis()) + " > " + e.getData().getDisplayName() + " followed");
        }
    }

    @EventSubscriber
    public void onCheer(ChannelBitsEvent e){
        Main.getClient().getChat().sendMessage(e.getData().getChannelName(), Config.messages.get(e.getData().getChannelId()).get(1).replace("{person}", e.getData().getUserName()));
        counter = StatCounter.get(e.getData().getChannelName());
        if(counter != null) {
            counter.addBits(e.getData().getUserName(), e.getData().getBitsUsed());
            counter.addLog(new Timestamp(System.currentTimeMillis()) + " > " + e.getData().getUserName() + " cheered " + e.getData().getBitsUsed() + " bits");
        }
    }

    @EventSubscriber
    public void onRaid(RaidEvent e){
        Main.getClient().getChat().sendMessage(e.getChannel().getName(), Config.messages.get(e.getChannel().getId()).get(2)
                .replace("{person}", e.getRaider().getName()).replace("{number}", e.getViewers().toString()));
        counter = StatCounter.get(e.getChannel().getName());
        if(counter != null) {
            counter.addLog(new Timestamp(System.currentTimeMillis()) + " > " + e.getRaider().getName() + " raided with " + e.getViewers() + " viewers");
        }
    }

    @EventSubscriber
    public void onSubGiftBomb(GiftSubscriptionsEvent e){
        Main.getClient().getChat().sendMessage(e.getChannel().getName(), Config.messages.get(e.getChannel().getId()).get(3)
        .replace("{person}", e.getUser().getName()).replace("{number}", e.getCount().toString()));
        counter = StatCounter.get(e.getChannel().getName());
        if(counter != null) {
            for (int i = 0; i < e.getCount(); i++) {
                counter.addSub("gift" + i);
            }
            counter.addLog(new Timestamp(System.currentTimeMillis()) + " > " + e.getUser().getName() + " gifted " + e.getCount() + " subs");
        }
    }

    @EventSubscriber
    public void onSub(ChannelSubscribeEvent e){
            Main.getClient().getChat().sendMessage(e.getData().getChannelName(), Config.messages.get(e.getData().getChannelId()).get(4).replace("{person}", e.getData().getDisplayName()));
            counter = StatCounter.get(e.getData().getChannelName());
            if (counter != null) {
                counter.addSub(e.getData().getDisplayName());
                counter.addLog(new Timestamp(System.currentTimeMillis()) + " > " + e.getData().getDisplayName() + " subscribed");
            }
    }

    @EventSubscriber
    public void onChat(ChannelMessageEvent e){
        String[] a = e.getMessage().split(" ");
        String cmd = a[0];
        if(e.getPermissions().contains(CommandPermission.MODERATOR)){
            if(cmd.equalsIgnoreCase("!counter")){
                counter = StatCounter.get(e.getChannel().getName());
                if(a.length == 1){
                    Main.getClient().getChat().sendMessage(e.getChannel().getName(), "Correct usage: !counter <start/stop>");
                    return;
                }
                switch (a[1]){
                    case "start":
                        if(counter != null){
                            Main.getClient().getChat().sendMessage(e.getChannel().getName(), "Counter is already running!");
                            break;
                        }
                        counter = new StatCounter(e.getChannel().getName());
                        Main.getClient().getChat().sendMessage(e.getChannel().getName(), "New counter has been started!");
                        break;
                    case "stop":
                        if(counter == null){
                            Main.getClient().getChat().sendMessage(e.getChannel().getName(), "The counter is not running. You can't stop it");
                            break;
                        }
                        summarize(e.getChannel().getName());
                        counter = null;
                        Main.getClient().getChat().sendMessage(e.getChannel().getName(), "Counter has been stopped!");
                        break;
                    default:
                        Main.getClient().getChat().sendMessage(e.getChannel().getName(), "Correct usage: !counter <start/stop>");
                        break;
                }
            }
            if(cmd.equalsIgnoreCase("!command")){
                Command.execute(e.getUser(), e.getChannel().getName(), a);
                return;
            }
        }
        if(cmd.startsWith("!")) {
            ICommand command = ICommandManager.get(e.getChannel().getName(), cmd);
            if (command != null) {
                if (e.getPermissions().contains(CommandPermission.valueOf(command.getPermission().toUpperCase(Locale.ROOT)))) {
                    Main.getClient().getChat().sendMessage(e.getChannel().getName(), command.getMessage());
                    return;
                }
            }
        }
        if(counter != null) {
            counter.addMsg(e.getUser().getName() + " > " + e.getMessage());
            counter.addLog(new Timestamp(System.currentTimeMillis()) + " > " + e.getUser().getName() + ": " + e.getMessage());
        }
    }
}
