package kuchbhilabs.chestream.externalapi;

/**
 * Created by root on 27/6/15.
 */
public class ParseTables {

    public static class Users {
        public static final String USERNAME = "username";
        public static final String FULLY_REGISTERED = "fully_registered";
        public static final String AVATAR = "avatar";
        public static final String NAME = "name";
        public static final String EMAIL = "email_id";
        public static final String DOB = "DOB";
        public static final String CITY = "CITY";
        public static final String UPVOTED = "upvoted";
        public static final String DOWNVOTED = "downvoted";
    }

    public static class Videos {
        public static final String _NAME = "Videos";
        public static final String USER = "user";
        public static final String UPVOTE = "upvotes";
        public static final String TITLE = "title";
        public static final String PLAYED = "played";
        public static final String COMPILED = "compiled";
        public static final String URL = "url";
        public static final String LOCATION = "user_location";
        public static final String GIF = "video_gif";
        public static final String URL_M3U8 = "video_m3u8";
        public static final String USER_AVATAR = "user_avatar";
        public static final String USER_USERNAME = "user_name";
        public static final String VIDEO_THUMBNAIL="video_thumbnail";
    }

    public static final class Comments {
        public static final String _NAME = "Comments";
        public static final String TEXT = "comment";
        public static final String USER = "user";
        public static final String IMAGE = "image";
        public static final String VIDEO = "video_object";
        public static final String CHANNELS = "channel_object";

    }
}
