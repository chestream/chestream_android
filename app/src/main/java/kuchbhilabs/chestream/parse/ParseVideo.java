package kuchbhilabs.chestream.parse;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by omerjerk on 4/7/15.
 */
@ParseClassName("Videos")
public class ParseVideo extends ParseObject {

    //-2 indicated that it's voting wrt the current user has not been fetched yet
    public int isVoted = -2;

    @Override
    public boolean equals (Object o) {
        ParseVideo object = (ParseVideo) o;
        return object.getObjectId().equals(getObjectId());
    }

    public boolean isVoteStatusFetched() {
        return isVoted != -2;
    }
}
