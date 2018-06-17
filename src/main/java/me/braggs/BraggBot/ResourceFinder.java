package me.braggs.BraggBot;

import java.io.File;

public class ResourceFinder
{
    //for getting just the path as string
    public static String getResource(String fileName)
    {
        return System.getProperty("user.dir") + "/resources/" + fileName;
    }

    //for getting the actual file
    public static File getFile(String fileName)
    {
        return new File(System.getProperty("user.dir") + "/resources/" + fileName);
    }
}
