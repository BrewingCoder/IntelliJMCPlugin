package com.brewingcoder.mcmodgen;

import org.apache.commons.lang.StringUtils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public final class Helpers {

    public static void MakeProjectFile(String sourceFile, String destFile, String projectName, String SourceName, String newRegistryName) {
        InputStream resourceAsStream = GenerateStairsFromBlock.class.getClassLoader().getResourceAsStream(sourceFile);
        assert resourceAsStream != null;
        String source = new Scanner(resourceAsStream,"UTF-8").useDelimiter("\\A").next();
        source = source.replace("MODID",projectName);
        source = source.replace("BLOCKID",newRegistryName);
        source = source.replace("SOURCEID",SourceName);

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(destFile));
            writer.write(source);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String CreateNewRegistryLine(String selectedText, String registryName, String newRegistryName,String blockName, String newBlockName) {
        String results =selectedText.replace(registryName,newRegistryName);
        results = results.replace(blockName,newBlockName);
        return results.replaceAll("\\s+", " ") + "\n";
    }

    public static String ExtractRegistryNameFromLine(String line){
        String[] values = StringUtils.substringsBetween(line,"\"","\"");
        if (values.length == 0) return null;
        return values[0].trim();
    }

    public static String ExtractBlockNameFromLine(String line){
        String[] values = StringUtils.substringsBetween(line,"Block "," =");
        if (values.length == 0) return null;
        return values[0].trim();
    }
}
