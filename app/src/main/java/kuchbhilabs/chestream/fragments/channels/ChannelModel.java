package kuchbhilabs.chestream.fragments.channels;

import java.io.Serializable;
import java.util.List;

/**
 * Created by naman on 20/08/15.
 */
public class ChannelModel implements Serializable {

    public final String id;
    public final String picture;
    public final String name;
    public final String info;
    public final int activeUsers;
    public final List<String> videoIds;

    public ChannelModel(String id,String picture,String name,String info,int activeUsers,List<String> videoIds){
        this.id =id;
        this.picture=picture;
        this.name=name;
        this.info=info;
        this.activeUsers=activeUsers;
        this.videoIds=videoIds;
    }


}
