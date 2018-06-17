package me.braggs.BraggBot.Commands.oldFramework;

import me.braggs.BraggBot.Commandarguments;
import me.braggs.BraggBot.Commandarguments.paramType;

import me.braggs.BraggBot.CommandSender;
import me.braggs.BraggBot.Commands.CommandArgs;
import net.dean.jraw.RedditClient;
import net.dean.jraw.http.NetworkAdapter;
import net.dean.jraw.http.OkHttpNetworkAdapter;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.models.Submission;
import net.dean.jraw.oauth.Credentials;
import net.dean.jraw.oauth.OAuthHelper;
import net.dean.jraw.references.SubredditReference;
import net.dv8tion.jda.core.EmbedBuilder;
import org.apache.commons.validator.routines.UrlValidator;

import java.awt.*;
import java.util.regex.Pattern;

public class RedditCommands {

    static RedditClient redditClient;
    static NetworkAdapter adapter;
    static Credentials credentials;

    public RedditCommands() {
        String username = Config.getInstance().getRedditUserName();
        String pswd = Config.getInstance().getRedditPswd();
        String ID = Config.getInstance().getRedditID();
        String secret = Config.getInstance().getRedditSecret();

        UserAgent myUserAgent = new UserAgent("desktop", "me.braggs.BraggBot", "v0.1", "BraggBot");
        credentials = Credentials.script(username, pswd, ID, secret);
        adapter = new OkHttpNetworkAdapter(myUserAgent);

        redditClient = OAuthHelper.automatic(adapter, credentials);
    }

    @CommandAnnotation(arguments = {Commandarguments.paramType.StringCommand, paramType.String}, level = 1)
    public void getRandomPost(CommandSender sender, CommandArgs arguments) {
        String subRedName = arguments.getArgument(1).replace("r/", "");
        sender.sendMessage("gimme a sec, retrieving freshest post of r/" + subRedName);
        try {
            SubredditReference sp = redditClient.subreddit(subRedName);
            Submission submission = sp.randomSubmission().getSubject();
            String thumbnail = submission.getThumbnail();
            EmbedBuilder embed = new EmbedBuilder();

            if(UrlValidator.getInstance().isValid(thumbnail)){
                embed.setThumbnail(thumbnail);
                embed.addField("Link", submission.getUrl(), false);
            }

            if (submission.isNsfw()) {
                embed.setTitle("\u26a0\ufe0fNSFW\u26a0\ufe0f" + submission.getTitle());
                embed.setThumbnail("https://i.imgur.com/DbJRkS3.jpg");
            }else {
                embed.setTitle(submission.getTitle());
                Pattern regex = Pattern.compile("(.png|.jpg|.gif)$");
                if (regex.matcher(submission.getUrl()).find()) {
                    embed.setImage(submission.getUrl());
                }
            }

            if(submission.getSelfText().length() < 2000){
                embed.setDescription(submission.getSelfText());
            }

            embed.setColor(new Color(255, 86, 0));
            embed.addField("Score", String.valueOf(submission.getScore()), false);
            embed.addField("CommentCount", String.valueOf(submission.getCommentCount()), false);
            embed.addField("Post", "https://reddit.com" + submission.getPermalink(), false);

            sender.sendEmbed(embed);
        } catch (Exception e) {
            e.printStackTrace();
            sender.sendMessage("sorry i wasnt able to get that subreddit :confused: ");
        }
    }
}
