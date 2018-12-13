package roart.database;

import java.util.List;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMarshaller;
import com.google.common.reflect.TypeToken;

import roart.model.FileLocation;
/*
public class ListFileObjectConverter implements DynamoDBMarshaller<List<FileLocation>>
{

    private static final Gson GSON = new Gson();

    @Override
    public String marshall(List<?> value) {
        Type listOfObjectType = new TypeToken<List<?>>(){}.getType();
        return GSON.toJson(value, listOfObjectType);
    }

    @Override
    public List<?> unmarshall(Class<List<?>> clazz, String value) {
        Type listOfObjectType = new TypeToken<List<?>>(){}.getType();
        return GSON.fromJson(value, listOfObjectType);
    }

}
*/