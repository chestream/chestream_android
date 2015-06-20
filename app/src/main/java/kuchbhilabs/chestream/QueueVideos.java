package kuchbhilabs.chestream;

/**
 * Created by raghav on 15/06/15.
 */
public class QueueVideos {

    String title;
    String image;
    int numberOfVotes;
    String location;

    public QueueVideos(String title, String image, int numberOfVotes, String location) {
        this.title = title;
        this.image = image;
        this.numberOfVotes = numberOfVotes;
        this.location = location;
    }
}
