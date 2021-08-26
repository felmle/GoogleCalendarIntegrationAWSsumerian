package function;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileNotFoundException;
import java.io.StringReader;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * some functions used throughout the project when working with map to string or object conversion
 */
public class MapEdit
{
    private String castLinkedHashMapStringToJsonString(String input) {
        return new Gson().toJson(input, Map.class);
    }

    /**
     * @param input the string containing a map structure
     * @return the map constructed by the strings structure
     */
    public Map<String, Object> castStringToMap(String input)
    {
        String inputString = input;

        JsonElement root = JsonParser.parseReader(new StringReader(inputString));

        return castToMap(root.getAsJsonObject());
    }

    /**
     * @param jsonObject an object that should be cast to a map
     * @return the map created from the jsonObject
     */
    public Map<String, Object> castToMap(JsonObject jsonObject)
    {
        return jsonObject.keySet()
                .stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        jsonObject::get
                ));
    }
}
