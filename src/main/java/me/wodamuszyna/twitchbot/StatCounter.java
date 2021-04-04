package me.wodamuszyna.twitchbot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class StatCounter {

    public String channel;
    public ArrayList<String> chatMsgs;
    public HashSet<String> follows;
    public HashSet<String> subs;
    public HashMap<String, Integer> bits;
    public ArrayList<String> logs;

    public static ArrayList<StatCounter> counters = new ArrayList<>();

    public StatCounter(String channel){
        this.channel = channel;
        this.chatMsgs = new ArrayList<>();
        this.follows = new HashSet<>();
        this.subs = new HashSet<>();
        this.bits = new HashMap<>();
        this.logs = new ArrayList<>();
        addCounter(this);
    }

    public void addLog(String msg){
        logs.add(msg+System.lineSeparator());
    }

    public ArrayList<String> getLogs(){
        return logs;
    }

    public void addMsg(String msg){
        chatMsgs.add(msg);
    }

    public void addFollow(String user){
        follows.add(user);
    }

    public void addSub(String user){
        subs.add(user);
    }

    public void addBits(String user, int amount){
        if(bits.get(user) != null){
            bits.put(user, bits.get(user)+amount);
            return;
        }
        bits.put(user, amount);
    }

    public ArrayList<String> getChatMsgs() {
        return chatMsgs;
    }

    public String getChannel(){ return channel;}

    public void setChannel(String channel){ this.channel = channel; }

    public HashSet<String> getFollows() {
        return follows;
    }

    public HashSet<String> getSubs() {
        return subs;
    }

    public HashMap<String, Integer> getBits() {
        return bits;
    }

    public void addCounter(StatCounter counter){ counters.add(counter); }

    public ArrayList<StatCounter> getCounters(){ return counters; }

    public static StatCounter get(String name){
        return counters.stream().filter(counter -> (counter != null && counter.getChannel().equalsIgnoreCase(name))).findFirst().orElse(null);
    }
}
