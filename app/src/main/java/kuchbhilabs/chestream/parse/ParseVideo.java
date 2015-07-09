package kuchbhilabs.chestream.parse;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by omerjerk on 4/7/15.
 */
@ParseClassName("Videos")
public class ParseVideo extends ParseObject {

    @Override
    public boolean equals (Object o) {
        ParseVideo object = (ParseVideo) o;
        return object.getObjectId().equals(getObjectId());
    }
}
