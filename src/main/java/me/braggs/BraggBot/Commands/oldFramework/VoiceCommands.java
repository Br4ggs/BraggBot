package me.braggs.BraggBot.Commands.oldFramework;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import me.braggs.BraggBot.*;
import me.braggs.BraggBot.Commands.CommandArgs;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.audio.AudioSendHandler;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.managers.AudioManager;

import java.awt.*;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

public class VoiceCommands //perhaps split this class up in 2 parts?
{
    public String voiceID;
    AudioLoadResultHandler handler;
    VoiceChannel channel;
    AudioManager manager;

    TrackScheduler trackScheduler;

    AudioPlayerManager playerManager;
    AudioPlayer player;

    @CommandAnnotation(level = 1)
    public void joinVC(CommandSender sender, CommandArgs arguments){
        //could you perhaps use flatmap for this?
        for (VoiceChannel vc : sender.guild.getVoiceChannels()){
            List<User> users = vc.getMembers().stream().map(Member::getUser).collect(Collectors.toList());
            if(!users.contains(sender.user)){
                continue;
            }
            channel = vc;
        }
        if(channel == null){
            sender.sendMessage("you're not in a voicechannel silly!");
            return;
        }
        manager = sender.guild.getAudioManager();
        manager.openAudioConnection(channel);
        sender.sendMessage("Joining " + channel.getName() + "...\nUse `%help addsong` to learn how to play songs");

        playerManager = new DefaultAudioPlayerManager();

        AudioSourceManagers.registerRemoteSources(playerManager);

        player = playerManager.createPlayer();

        trackScheduler = new TrackScheduler(player);
        player.addListener(trackScheduler);
        player.setVolume(Config.getInstance().getVoiceVolume());

        Method[] voiceMethods = App.voiceCommands.getClass().getMethods();
        Reflector.addMethodsToManager(voiceMethods, App.voiceCommands, CommandAnnotation.commandType.voice, App.commandManager);

        voiceID = channel.getId();
    }

    @CommandAnnotation(typeOfCommand = CommandAnnotation.commandType.voice, level = 1)
    public void leaveVC(CommandSender sender, CommandArgs arguments){
        sender.sendMessage("alright i'll leave... :cry:");
        manager.closeAudioConnection();
        channel = null;
        playerManager.shutdown();
        player.destroy();
        Reflector.removeMethodsFromManager(CommandAnnotation.commandType.voice, App.commandManager);

        voiceID = null;
    }

    @CommandAnnotation(arguments = {Commandarguments.paramType.StringCommand, Commandarguments.paramType.String}, typeOfCommand = CommandAnnotation.commandType.voice, level = 1)
    public void addSong(CommandSender sender, CommandArgs arguments){
        playerManager.loadItem(arguments.getArgument(1), new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                trackScheduler.queue(audioTrack);
                System.out.println(player.getPlayingTrack());
                if(!player.getPlayingTrack().equals(audioTrack)){
                    sender.sendMessage(sender.user.getName() + " added to que: " + audioTrack.getInfo().title);
                }
                else {
                    sender.sendMessage("now playing: " + audioTrack.getInfo().title);
                }
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                for (AudioTrack track : audioPlaylist.getTracks()) {
                    trackScheduler.queue(track);
                }
                sender.sendMessage(sender.user.getName() + " loaded a playlist: " + audioPlaylist.getName());
            }

            @Override
            public void noMatches() {
                sender.sendMessage("sorry i couldn't find that one :confused:");
            }

            @Override
            public void loadFailed(FriendlyException e) {
                sender.sendMessage("shit just exploded: " + e);
            }
        });
        manager.setSendingHandler(new AudioPlayerSendHandler(player));
    }

    @CommandAnnotation(typeOfCommand = CommandAnnotation.commandType.voice, level = 1)
    public void skipSong(CommandSender sender, CommandArgs arguments){
        if(trackScheduler.getQueue().size() < 1){
            sender.sendMessage("there is nothing in the que to skip to :confused:");
            return;
        }
        trackScheduler.nextTrack();
        sender.sendMessage(sender.user.getName() + " skipped current track, now playing: " + player.getPlayingTrack().getInfo().title);
    }
    @CommandAnnotation(typeOfCommand = CommandAnnotation.commandType.voice, level = 1)
    public void pause(CommandSender sender, CommandArgs arguments){
        if (player.isPaused()){
            player.setPaused(false);
            sender.sendMessage("ill continue to play :musical_note: ");
        }
        else {
            player.setPaused(true);
            sender.sendMessage("ill mute myself for now :mute:");
        }
    }

    @CommandAnnotation(typeOfCommand = CommandAnnotation.commandType.voice, level = 1)
    public void showQueue(CommandSender sender, CommandArgs arguments){
        LinkedHashMap<String, AudioTrack> songs = trackScheduler.getQueue();
        AudioTrack currentTrack = player.getPlayingTrack();
        if(songs.size() < 1 && currentTrack == null){
            sender.sendMessage("the queue is currently empty :slight_frown:");
            return;
        }
        System.out.println(System.lineSeparator());
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("current song queue");
        embed.setColor(Color.MAGENTA);
        embed.setDescription("total count: " + (songs.size() + 1));

        StringBuilder builder = new StringBuilder();
        int counter = 2;
        for (AudioTrack track : songs.values()){
            if(builder.length() < 900){
                builder.append(counter + " : " + track.getInfo().title + " : " + System.lineSeparator() + track.getInfo().identifier + System.lineSeparator());
                counter++;
            }
            else {
                builder.append("and another " + ((songs.size() + 1) - counter) + " more...");
                break;
            }
        }
        embed.addField("currently playing: ", currentTrack.getInfo().title + " : " + currentTrack.getInfo().identifier, false);
        if(!builder.toString().equals("")){
            embed.addField("List:","```" + builder.toString() + "```", false);

        }
        sender.sendEmbed(embed);
    }

    @CommandAnnotation(arguments = {Commandarguments.paramType.StringCommand, Commandarguments.paramType.String}, typeOfCommand = CommandAnnotation.commandType.voice, level = 1)
    public void getSong (CommandSender sender, CommandArgs arguments){
        LinkedHashMap<String, AudioTrack> songs = new LinkedHashMap<>();
        songs.put(player.getPlayingTrack().getIdentifier(),player.getPlayingTrack());
        songs.putAll(trackScheduler.getQueue());
        System.out.println(songs.size());
        songs.forEach((s, audioTrack) -> System.out.println(s + " " + audioTrack));

        if(songs.containsKey(arguments.getArgument(1))){
            AudioTrack track = songs.get(arguments.getArgument(1));
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle(track.getInfo().title, track.getInfo().uri);
            embed.setColor(Color.MAGENTA);
            embed.addField("Author", track.getInfo().author, true);
            embed.addField("Lenght", String.valueOf(track.getInfo().length), true);
            sender.sendEmbed(embed);
            return;
        }
        sender.sendMessage("sorry, i couldn't find that one in the current queue :sob:");
    }

    @CommandAnnotation(typeOfCommand = CommandAnnotation.commandType.voice, level = 1)
    public void getVolume(CommandSender sender, CommandArgs arguments){
        sender.sendMessage("My current server-volume is: " + player.getVolume() + "%");
    }

    @CommandAnnotation(arguments = {Commandarguments.paramType.StringCommand, Commandarguments.paramType.Integer}, requiredArgs = 0, defArgs = "20", typeOfCommand = CommandAnnotation.commandType.voice, level = 1)
    public void setVolume(CommandSender sender, CommandArgs arguments){
        player.setVolume(Integer.parseInt(arguments.getArgument(1)));
        Config.getInstance().setVoiceVolume(Integer.parseInt(arguments.getArgument(1)));
        sender.sendMessage("My server-volume is has been set to: " + player.getVolume() + "%");
    }
}

class AudioPlayerSendHandler implements AudioSendHandler {
    private final AudioPlayer audioPlayer;
    private AudioFrame lastFrame;

    public AudioPlayerSendHandler(AudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;
    }

    @Override
    public boolean canProvide() {
        lastFrame = audioPlayer.provide();
        return lastFrame != null;
    }

    @Override
    public byte[] provide20MsAudio() {
        return lastFrame.data;
    }

    @Override
    public boolean isOpus() {
        return true;
    }
}
