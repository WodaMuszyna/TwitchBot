package me.wodamuszyna.twitchbot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class StatCounter {

    public ArrayList<String> chatMsgs;
    public HashSet<String> follows;
    public HashSet<String> subs;
    public HashMap<String, Integer> bits;
    public ArrayList<String> logs;

    public StatCounter(){
        this.chatMsgs = new ArrayList<>();
        this.follows = new HashSet<>();
        this.subs = new HashSet<>();
        this.bits = new HashMap<>();
        this.logs = new ArrayList<>();
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

    public void setChatMsgs(ArrayList<String> chatMsgs) {
        this.chatMsgs = chatMsgs;
    }

    public HashSet<String> getFollows() {
        return follows;
    }

    public void setFollows(HashSet<String> follows) {
        this.follows = follows;
    }

    public HashSet<String> getSubs() {
        return subs;
    }

    public void setSubs(HashSet<String> subs) {
        this.subs = subs;
    }

    public HashMap<String, Integer> getBits() {
        return bits;
    }

    public void setBits(HashMap<String, Integer> bits) {
        this.bits = bits;
    }
}
