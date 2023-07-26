package it.units.sim.yourtube.data;

import androidx.room.TypeConverter;

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

public class CategoryConverter {
    private static final Gson gson = new Gson();

    @TypeConverter
    public static List<String> stringToUserSubscriptionList(String data) {
        if (data == null)
            return null;
        return Arrays.asList(gson.fromJson(data, String[].class));
    }

    @TypeConverter
    public static String userSubscriptionListToString(List<String> userSubscriptions) {
        return gson.toJson(userSubscriptions);
    }
}
