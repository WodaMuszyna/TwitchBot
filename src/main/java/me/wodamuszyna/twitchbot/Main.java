package me.wodamuszyna.twitchbot;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

import javax.security.auth.login.LoginException;

public class Main {

    private static TwitchClient client;
    private static JDA jda = null;
    public static void main(String[] args) {
        Config.init();
        TwitchClientBuilder clientBuilder = TwitchClientBuilder.builder();
        OAuth2Credential credential = new OAuth2Credential("twitch", Config.token);
        client = clientBuilder
                .withClientId(Config.client_id)
                .withClientSecret(Config.client_secret)
                .withEnableHelix(true)
                .withEnableChat(true)
                .withEnablePubSub(true)
                .withChatAccount(credential)
                .build();

        for(String name : Config.channels){
            client.getChat().joinChannel(name);
        }
        for(String id : Config.messages.keySet()){
            client.getPubSub().listenForFollowingEvents(credential, id);
            client.getPubSub().listenForCheerEvents(credential, id);
            client.getPubSub().listenForChannelSubGiftsEvents(credential, id);
            client.getPubSub().listenForSubscriptionEvents(credential, id);
        }

        try {
            jda = JDABuilder.createDefault(Config.discord_token).build();
        } catch (LoginException e) {
            e.printStackTrace();
        }
        client.getEventManager().getEventHandler(SimpleEventHandler.class).registerListener(new EventListener());
        CommandClientBuilder bd = new CommandClientBuilder();
        bd.setOwnerId(Config.bot_ownerid);
        bd.setPrefix("!");
        bd.setActivity(Activity.watching("Ephy watering her crops"));
        CommandClient client = bd.build();
        jda.getPresence().setActivity(Activity.watching("Ephy watering her crops"));
        jda.addEventListener(new EventListener());

    }
    public static TwitchClient getClient(){ return client;}
    public static JDA getJDA(){ return jda;}
}
