package kuchbhilabs.chestream;


public class QueueVideos {

    String title;
    String avatar_url;
    String username;
    String gif_url;
    String image;
    int numberOfVotes;
    String location;

    public QueueVideos(String title, String username, String avatar_url, String gif_url, int numberOfVotes, String location, String image) {
        this.title = title;
        this.username = username;
        this.avatar_url = avatar_url;
        this.gif_url = gif_url;
        this.numberOfVotes = numberOfVotes;
        this.location = location;
        this.image = image;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGif_url() {
        return gif_url;
    }

    public void setGif_url(String gif_url) {
        this.gif_url = gif_url;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    public int getNumberOfVotes() {
        return numberOfVotes;
    }

    public void setNumberOfVotes(int numberOfVotes) {
        this.numberOfVotes = numberOfVotes;
    }


}
