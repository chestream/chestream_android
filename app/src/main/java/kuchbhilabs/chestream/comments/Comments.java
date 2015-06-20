package kuchbhilabs.chestream.comments;

/**
 * Created by naman on 20/06/15.
 */
public class Comments {

    String avatar,username,comment;

    public String getAvatar(){
        return avatar;
    }

    public String getUsername(){
        return username;
    }

    public String getComment(){
        return comment;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAvatar(String avatar){
        this.avatar=avatar;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
