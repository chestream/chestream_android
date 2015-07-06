package kuchbhilabs.chestream.parse;

import com.parse.ParseObject;

/**
 * Created by omerjerk on 4/7/15.
 */
public class AwsParseObject extends ParseObject {

    @Override
    public boolean equals (Object o) {
        AwsParseObject object = (AwsParseObject) o;
        return object.getObjectId().equals(getObjectId());
    }
}
