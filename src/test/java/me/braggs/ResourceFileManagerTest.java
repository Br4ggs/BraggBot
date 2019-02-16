package me.braggs;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.braggs.BraggBot.ResourceFileManager;
import org.junit.Test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class ResourceFileManagerTest {

    /**
     * Create a new json file with a hashmap for data
     */
    @Test
    public void testFileCreation() {
        try {
            Map<String, Integer> testUsers = new HashMap<>();
            testUsers.put("User1", 1);
            testUsers.put("User2", 2);
            testUsers.put("User3", 3);
            ResourceFileManager.INSTANCE.createJsonFile("TEST_HighScore.json", testUsers);

            boolean doesEmptyFileExist = ResourceFileManager.INSTANCE.doesExist("TEST_HighScore.json");
            if (!doesEmptyFileExist) {
               fail();
            }

        } catch (IOException e) {
            fail();
        } finally {
            boolean deleted = ResourceFileManager.INSTANCE.RemoveJsonFile("TEST_HighScore.json");
            if(!deleted){
                fail();
            }
        }
    }

    /**
     * Create an empty json file
     */
    @Test
    public void testEmptyFileCreation() {
        try {
            ResourceFileManager.INSTANCE.createJsonFile("TEST_Empty.json", null);

            boolean doesEmptyFileExist = ResourceFileManager.INSTANCE.doesExist("TEST_Empty.json");
            if (!doesEmptyFileExist) {
                fail();
            }

        } catch (IOException e) {
            fail();
        } finally {
            boolean deleted = ResourceFileManager.INSTANCE.RemoveJsonFile("TEST_Empty.json");
            if(!deleted){
                fail();
            }
        }
    }

    /**
     * Modify a value in the TEST_HighScore.json file
     */
    @Test
    public void testFileModification() {
        try {
            Map<String, Integer> testUsers = new HashMap<>();
            testUsers.put("User1", 1);
            testUsers.put("User2", 2);
            testUsers.put("User3", 3);
            ResourceFileManager.INSTANCE.createJsonFile("TEST_HighScore.json", testUsers);

            ResourceFileManager.INSTANCE.setJsonValue("TEST_HighScore.json", "User2", 999, false);

            int value = ResourceFileManager.INSTANCE.getJsonValue("TEST_HighScore.json", "User2").getAsInt();
            if (value != 999) {
                fail();
            }

        } catch (IOException e) {
            fail();
        } finally {
            boolean deleted = ResourceFileManager.INSTANCE.RemoveJsonFile("TEST_HighScore.json");
            if(!deleted){
                fail();
            }
        }
    }

    /**
     * Try to modify a non-existent value in the TEST_HighScore.json file, while
     * not being allowed to create a new entry. Should throw an exception.
     */
    @Test(expected = NoSuchElementException.class)
    public void testNonExistentValueModification() {
        try {
            Map<String, Integer> testUsers = new HashMap<>();
            testUsers.put("User1", 1);
            testUsers.put("User2", 2);
            testUsers.put("User3", 3);
            ResourceFileManager.INSTANCE.createJsonFile("TEST_HighScore.json", testUsers);

            ResourceFileManager.INSTANCE.setJsonValue("TEST_HighScore.json", "User5", 999, false);

        } catch (IOException e) {
            fail();
        } finally {
            boolean deleted = ResourceFileManager.INSTANCE.RemoveJsonFile("TEST_HighScore.json");
            if(!deleted){
                fail();
            }
        }
    }

    /**
     * Create a new value in the TEST_HighScore.json file
     */
    @Test
    public void testAddValue() {
        try {
            Map<String, Integer> testUsers = new HashMap<>();
            testUsers.put("User1", 1);
            testUsers.put("User2", 2);
            testUsers.put("User3", 3);
            ResourceFileManager.INSTANCE.createJsonFile("TEST_HighScore.json", testUsers);

            ResourceFileManager.INSTANCE.setJsonValue("TEST_HighScore.json", "User999", 12345);

            int value = ResourceFileManager.INSTANCE.getJsonValue("TEST_HighScore.json", "User999").getAsInt();
            if (value != 12345)
                fail();

        } catch (IOException e) {
            fail();
        } finally {
            boolean deleted = ResourceFileManager.INSTANCE.RemoveJsonFile("TEST_HighScore.json");
            if(!deleted){
                fail();
            }
        }
    }

    /**
     * Get a value that doesn't exist from the TEST_HighScore.json file
     */
    @Test
    public void testGetNonExistentValue() {
        try {
            Map<String, Integer> testUsers = new HashMap<>();
            testUsers.put("User1", 1);
            testUsers.put("User2", 2);
            testUsers.put("User3", 3);
            ResourceFileManager.INSTANCE.createJsonFile("TEST_HighScore.json", testUsers);

            JsonElement fakeVal = ResourceFileManager.INSTANCE.getJsonValue("TEST_HighScore.json", "ThisValueDoesntExist");
            if (fakeVal != null)
                fail();

        } catch (IOException e) {
            fail();
        } finally {
            boolean deleted = ResourceFileManager.INSTANCE.RemoveJsonFile("TEST_HighScore.json");
            if(!deleted){
                fail();
            }
        }
    }

    /**
     * Remove a value from TEST_HighScore.json
     */
    @Test
    public void testRemoveValue() {
        try {
            Map<String, Integer> testUsers = new HashMap<>();
            testUsers.put("User1", 1);
            testUsers.put("User2", 2);
            testUsers.put("User3", 3);
            ResourceFileManager.INSTANCE.createJsonFile("TEST_HighScore.json", testUsers);

            ResourceFileManager.INSTANCE.removeJsonValue("TEST_HighScore.json", "User3");

            JsonElement removedVal = ResourceFileManager.INSTANCE.getJsonValue("TEST_HighScore.json", "User3");
            if (removedVal != null)
                fail();

        } catch (IOException e) {
            fail();
        } finally {
            boolean deleted = ResourceFileManager.INSTANCE.RemoveJsonFile("TEST_HighScore.json");
            if(!deleted){
                fail();
            }
        }
    }

    /**
     * Get file TEST_HighScore.json as a JsonObject
     */
    @Test
    public void testGetFile() {
        try {
            Map<String, Integer> testUsers = new HashMap<>();
            testUsers.put("User1", 1);
            testUsers.put("User2", 2);
            testUsers.put("User3", 3);
            ResourceFileManager.INSTANCE.createJsonFile("TEST_HighScore.json", testUsers);

            JsonObject object = ResourceFileManager.INSTANCE.getJsonFile("TEST_HighScore.json").getAsJsonObject();
            if (object == null)
                fail();

        } catch (IOException e) {
            fail();
        } finally {
            boolean deleted = ResourceFileManager.INSTANCE.RemoveJsonFile("TEST_HighScore.json");
            if(!deleted){
                fail();
            }
        }
    }
    /**
     * Get a non-existent file
     */
    @Test(expected = IOException.class)
    public void testGetNonExistentFile() throws IOException {
        ResourceFileManager.INSTANCE.getJsonFile("TEST_ThisFileDoesntExist.json").getAsJsonObject();
    }

    /**
     * Modify a value in a json file array
     */
    @Test
    public void testModifyArray() {
        try{
            int[] array = {1,2,3,4,5};

            ResourceFileManager.INSTANCE.createJsonFile("TEST_Array.json", array);
            ResourceFileManager.INSTANCE.setJsonArrayValue("TEST_Array.json", 2, 999);

            int value = ResourceFileManager.INSTANCE.getJsonArrayValue("TEST_Array.json", 2).getAsInt();
            if (value != 999) {
                fail();
            }
        } catch (IOException e) {
            fail();
        } finally {
            boolean deleted = ResourceFileManager.INSTANCE.RemoveJsonFile("TEST_Array.json");
            if(!deleted){
                fail();
            }
        }
    }

    /**
     * Modify a JsonArray value in a json file
     */
    @Test
    public void testModifyArrayInFile() {
        try {
            PropertyMock prop = new PropertyMock(
                    "Test",
                    15,
                    true,
                    new int[] {1,2,3,4,5},
                    new PropertyMock(
                            "Best",
                            30,
                            false,
                            new int[] {6,7,8,9,10},
                            null
                    ));

            ResourceFileManager.INSTANCE.createJsonFile("TEST_Properties.json", prop);

            int[] array = ResourceFileManager.INSTANCE.getJsonValue(
                    "TEST_Properties.json",
                    "property4",
                    int[].class);

            array[2] = 999;

            ResourceFileManager.INSTANCE.setJsonValue("TEST_Properties.json", "property4", array);

            JsonObject object = ResourceFileManager.INSTANCE.getJsonFile("TEST_Properties.json").getAsJsonObject();
            if (object.get("property4").getAsJsonArray().get(2).getAsInt() != 999){
                fail();
            }
        } catch (IOException e) {
            fail();
        } finally {
            boolean deleted = ResourceFileManager.INSTANCE.RemoveJsonFile("TEST_Properties.json");
            if(!deleted){
                fail();
            }
        }
    }

    /**
     * Modify a JsonObject value in a json file
     */
    @Test
    public void testModifyObjectInFile() {
        try {
            PropertyMock prop = new PropertyMock(
                    "Test",
                    15,
                    true,
                    new int[] {1,2,3,4,5},
                    new PropertyMock(
                            "Best",
                            30,
                            false,
                            new int[] {6,7,8,9,10},
                            null
                    ));

            ResourceFileManager.INSTANCE.createJsonFile("TEST_Properties.json", prop);

            PropertyMock object = ResourceFileManager.INSTANCE.getJsonValue(
                    "TEST_Properties.json",
                    "property5",
                    PropertyMock.class);

            object.property1 = "THISWASMODIFIED";

            ResourceFileManager.INSTANCE.setJsonValue(
                    "TEST_Properties.json",
                    "property5",
                    object);

            JsonObject jsonObject = ResourceFileManager.INSTANCE.getJsonFile("TEST_Properties.json").getAsJsonObject();
            if (!jsonObject.get("property5").getAsJsonObject().get("property1").getAsString().equals("THISWASMODIFIED")){
                fail();
            }
        } catch (IOException e) {
            fail();
        } finally {
            boolean deleted = ResourceFileManager.INSTANCE.RemoveJsonFile("TEST_Properties.json");
            if(!deleted){
                fail();
            }
        }
    }

    private class PropertyMock {
        private String property1;
        private int property2;
        private boolean property3;
        private int[] property4;
        private PropertyMock property5;

        private PropertyMock(String prop1, int prop2, boolean prop3, int[] prop4, PropertyMock prop5) {
            property1 = prop1;
            property2 = prop2;
            property3 = prop3;
            property4 = prop4;
            property5 = prop5;
        }
    }
}
