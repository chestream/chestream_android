package kuchbhilabs.chestream.fragments.queue;


import com.parse.ParseException;
import com.parse.ParseUser;

public class QueueVideos {

    String title;
    String avatar_url;
    ParseUser user;
    String gif_url;
    String numberOfVotes;
    String location;
    String url;

    public QueueVideos(String title, String avatar_url, ParseUser user,  String gif_url,
                       String numberOfVotes, String location, String url) {
        this.title = title;
        this.user = user;
        try {
            this.user.fetchIfNeeded();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.avatar_url = avatar_url;
        this.gif_url = gif_url;
        this.numberOfVotes = numberOfVotes;
        this.location = location;
        this.url = url;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }

    public ParseUser getUser() {
        return user;
    }

    public void setUser(String username) {
        this.user = user;
    }

    public String getGif_url() {
        return gif_url;
    }

    public void setGif_url(String gif_url) {
        this.gif_url = gif_url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getNumberOfVotes() {
        return numberOfVotes;
    }

    public void setNumberOfVotes(String numberOfVotes) {
        this.numberOfVotes = numberOfVotes;
    }


}
