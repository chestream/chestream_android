package kuchbhilabs.chestream.fragments.channels;

import java.util.List;

/**
 * Created by naman on 20/08/15.
 */
public class ChannelModel {

    public final String picture;
    public final String name;
    public final String info;
    public final int activeUsers;
    public final List<String> videoIds;

    public ChannelModel(String picture,String name,String info,int activeUsers,List<String> videoIds){
        this.picture=picture;
        this.name=name;
        this.info=info;
        this.activeUsers=activeUsers;
        this.videoIds=videoIds;
    }


}
