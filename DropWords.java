
import java.util.*;

public class DropWords {
    public static void main(String[] args) {
        InitDropWords();
        for(String arg : args) {
            String fileContents = FileUtil.Concatenate(FileUtil.Read(arg));
            StringBuffer changedContentsBuffer = new StringBuffer(fileContents.length());
            for(StringTokenizer st = new StringTokenizer(fileContents, " \t\n\r\f", true); st.hasMoreTokens(); ) {
                String token = st.nextToken();
                if(!DropWords.contains(token)) {
                    changedContentsBuffer.append(token);
                }
            }
            String changedContents = changedContentsBuffer.toString();
            FileUtil.Write(arg + ".dropWords.txt", changedContents);
            OneGramFrequencies lf = OneGramFrequencies.Get(true);
            System.out.println(arg + ": " + lf.getStandardDeviation(fileContents) 
                + " -> " + lf.getStandardDeviation(changedContents));
        }
    }

    public static HashSet<String> InitDropWords() {
        if(DropWords == null) {
            final String filename = "DropWords.txt";
            String fileContents = FileUtil.Concatenate(FileUtil.ReadResource(filename));
            DropWords = new HashSet<String>();
            for(StringTokenizer st = new StringTokenizer(fileContents); st.hasMoreTokens(); ) {
                DropWords.add(st.nextToken());
            }
        }
        return DropWords;
    }

    private static HashSet<String> DropWords;
}
