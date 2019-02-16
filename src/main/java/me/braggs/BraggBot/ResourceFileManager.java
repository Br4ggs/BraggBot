package me.braggs.BraggBot;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.FileAlreadyExistsException;
import java.util.NoSuchElementException;

/**
 * Retrieves, edits and generally manages files in the /resources/ folder.
 */
//TODO: use classloader.getresource()?
public class ResourceFileManager {
    public final static ResourceFileManager INSTANCE = new ResourceFileManager();

    private String path = System.getProperty("user.dir") + "/resources/";
    private Gson json;

    protected ResourceFileManager() {
        json = new GsonBuilder().setPrettyPrinting().create();
    }

    /**
     * Shorthand overload for creating a jsonfile which allows overwrite
     * @param fileName The name of the new json file.
     * @param data The object to serialize, leave null if you want to create an empty file.
     * @throws IOException
     */
    public void createJsonFile(String fileName, Object data) throws IOException {
        createJsonFile(fileName, data, true);
    }

    /**
     * Creates a new json file.
     *
     * @param fileName the name of the new json file.
     * @param data     the object to serialize, leave null if you want
     *                 to create an empty file.
     * @throws IOException
     */
    public void createJsonFile(String fileName, Object data, boolean allowedToOverwrite)
            throws IOException {
        if (doesExist(path + fileName) && !allowedToOverwrite) {
            throw new FileAlreadyExistsException("The file already exists and was not allowed to overwrite");
        }

        FileWriter writer = new FileWriter(path + fileName);
        json.toJson(data, writer);
        writer.flush();
        writer.close();
    }


    /**
     * Checks whether a file already exists or not.
     *
     * @param fileName The name of the json file.
     * @return A boolean representing wether the file exists.
     */
    public boolean doesExist(String fileName) {
        File file = new File(path + fileName);
        return file.exists();
    }

    /**
     * Gets the specified json file and returns it.
     *
     * @param fileName The name of the json file.
     * @return The json file as a JsonElement.
     * @throws IOException If the file cannot be found.
     */
    public JsonElement getJsonFile(String fileName) throws IOException {
        FileReader reader = new FileReader(path + fileName);
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(reader);
        reader.close();

        return element;
    }

    /**
     * Shorthand overload which automatically parses to desired type.
     * @param fileName The name of the json file.
     * @param classType The class of the file
     * @param <T> The type of the class
     * @return An object of type T
     * @throws IOException If the file cannot be found.
     */
    public <T> T getJsonFile(String fileName, Class<T> classType) throws IOException {
        JsonElement data = getJsonFile(fileName);
        return json.fromJson(data, classType);
    }

    /**
     * Deletes a specified json file.
     * @param fileName The name of the json file.
     * @return A boolean representing wether the file has been deleted.
     */
    public boolean RemoveJsonFile(String fileName) {
        File file = new File(path + fileName);
        return file.delete();
    }

    /**
     * Shorthand overload for setting values which allows making new entries
     *
     * @param fileName The name of the json file.
     * @param keyName The name of the key to be modified.
     * @param value The new value for the key.
     * @throws IOException If the file cannot be found.
     */
    public void setJsonValue(String fileName, String keyName, Object value) throws IOException {
        setJsonValue(fileName, keyName, value, true);
    }

    /**
     * Sets or creates a key in a json object file to a given value.
     *
     * @param fileName              The name of the file itself.
     * @param keyName               The name of the key to be modified.
     * @param value                 The new value for the key.
     * @param allowedToMakeNewEntry If false, the method will throw an exception when the key is not
     *                              already present
     * @throws IOException If the file cannot be found.
     */
    public void setJsonValue(String fileName, String keyName, Object value, boolean allowedToMakeNewEntry)
            throws IOException {
        JsonObject object = getJsonFile(fileName).getAsJsonObject();

        boolean hasKey = object.has(keyName);
        if (!hasKey && !allowedToMakeNewEntry){
            throw new NoSuchElementException("The given key could not be found in the json file and was not" +
                    "allowed to make a new entry");
        }

        JsonElement data = json.toJsonTree(value);

        object.add(keyName, data);

        createJsonFile(fileName, object);
    }

    /**
     * Gets a specific element from a json object file.
     *
     * @param fileName The name of the json file.
     * @param keyName  The name of the element, null if no element with the keyname could be found.
     * @return The element as a JsonElement.
     * @throws IOException If the file cannot be found.
     */
    public JsonElement getJsonValue(String fileName, String keyName) throws IOException {
        JsonObject object = getJsonFile(fileName).getAsJsonObject();
        return object.get(keyName);
    }

    /**
     * Shorthand overload which automatically parses to desired type.
     * @param fileName
     * @param keyName
     * @param classType
     * @param <T>
     * @return
     * @throws IOException
     */
    public <T> T getJsonValue(String fileName, String keyName, Class<T> classType) throws IOException {
        JsonElement object = getJsonValue(fileName, keyName);
        return json.fromJson(object, classType);
    }

    /**
     * Removes an entry from a json object file.
     * This method is best used when removing simple non-nested json entries.
     * If you need to modify a more complex json structure (like an object or array),
     * use the setJsonValue(String fileName, String keyName, JsonElement value) method.
     *
     * @param fileName The name of the json file.
     * @param keyName The name of the element.
     * @throws IOException
     */
    public void removeJsonValue(String fileName, String keyName) throws IOException {
        JsonObject object = getJsonFile(fileName).getAsJsonObject();

        object.remove(keyName);

        createJsonFile(fileName, object);
    }

    /**
     * Sets or creates a value in a json array file.
     *
     * @param fileName The name of the json file.
     * @param index The index of the value
     * @param newValue The new value for the index.
     * @throws IOException If the file cannot be found
     */
    public void setJsonArrayValue(String fileName, int index, Object newValue) throws IOException {

        JsonArray array = getJsonFile(fileName).getAsJsonArray();
        JsonElement element = json.toJsonTree(newValue);

        array.set(index, element);

        createJsonFile(fileName, array);
    }

    /**
     * Gets a specific element from a json array file.
     * @param fileName The name of the json file.
     * @param index The index of the element.
     * @return The element as a JsonElement.
     * @throws IOException If the file cannot be found.
     */
    public JsonElement getJsonArrayValue(String fileName, int index) throws IOException {
        return getJsonFile(fileName).getAsJsonArray().get(index);
    }

    /**
     * Shorthand overload which automatically parses to desired type.
     * @param fileName The name of the json file.
     * @param index The index of the element
     * @param classType The class of the file
     * @param <T> The type of the class
     * @return An object of type T
     * @throws IOException If the file cannot be found.
     */
    public <T> T getJsonArrayValue(String fileName, int index, Class<T> classType) throws IOException {
        JsonElement data = getJsonArrayValue(fileName, index);
        return json.fromJson(data, classType);
    }

    /**
     * Removes a specific element from a json array file.
     *
     * @param filename The name of the json file.
     * @param index The index to be removed.
     * @throws IOException If the file cannot be found.
     */
    public void removeJsonArrayValue(String filename, int index) throws IOException {
        JsonArray array = getJsonFile(filename).getAsJsonArray();

        array.remove(index);

        createJsonFile(filename, array);
    }
}
