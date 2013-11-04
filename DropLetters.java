
import java.util.*;

public class DropLetters {
    public static void main(String[] args) {
        InitDropLetters();
        for(String arg : args) {
            String fileContents = FileUtil.Concatenate(FileUtil.Read(arg));
            String re = "[" + DropLetters + "]";
            String changedContents = fileContents.replaceAll(re, "");
            FileUtil.Write(arg + ".dropLetters.txt", changedContents);
            OneGramFrequencies lf = OneGramFrequencies.Get(true);
            System.out.println(arg + ": " + lf.getStandardDeviation(fileContents) 
                + " -> " + lf.getStandardDeviation(changedContents));
        }
    }

    public static void InitDropLetters() {
        if(DropLetters == null) {
            final String filename = "DropLetters.txt";
            DropLetters = FileUtil.ReadResource(filename).get(0);
            System.out.println("InitDropLetters " + DropLetters);
        }
    }

    private static String DropLetters;
}

