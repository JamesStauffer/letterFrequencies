import java.util.*;
import java.util.logging.*;

public class AddLetters {
    public static void main(String[] args) {
        GetInverse();
        for(String arg : args) {
            String fileContents = FileUtil.Concatenate(FileUtil.Read(arg));
            StringBuffer changedContentsBuffer = new StringBuffer(fileContents.length());
            for(int index = 0; index < fileContents.length(); index++) {
                char chr = fileContents.charAt(index);
                changedContentsBuffer.append(chr);
                if(Character.isLetter(chr)) {//Add extra letter
                    if(random.nextInt(5) == 0) {//20% chance
                        changedContentsBuffer.append(getChar());
                    }
                } else if (' ' ==  chr) {//Add extra word
                    if(random.nextInt(3) == 0) {//20% chance
                        changedContentsBuffer.append(word());
                    }
                }
            }
            String changedContents = changedContentsBuffer.toString();
            FileUtil.Write(arg + ".addLetters.txt", changedContents);
            OneGramFrequencies lf = OneGramFrequencies.Get(true);
            System.out.println(arg + ": " + lf.getStandardDeviation(fileContents) 
                + " -> " + lf.getStandardDeviation(changedContents));
        }
    }

    private static String word() {
        int length = 1 + (int)Math.abs(random.nextGaussian() * 3);
        StringBuffer sb = new StringBuffer(length + 1);
        for(int index = 0; index < length; index++) {
            sb.append(getChar());
        }
        sb.append(" ");
        return sb.toString();
    }

    private static char getChar() {
        double randomPosition = 1.0d - random.nextFloat();// 0 < x <= 1 (do it this way so that all characters are correctly possible.
        double total = 0.0d;
        for(Map.Entry<String,Float> entry : GetInverse().entrySet()) {
            total += entry.getValue();
            if(total >= randomPosition) {
                sLogger.fine("getChar " + randomPosition + " -> " + entry.getKey());
                return entry.getKey().charAt(0);
            }
        }
        return ' ';//This should never happen
    }

	private static Map<String,Float> GetInverse() {
	    if(inverseLetterFrequencies == null) {
    	    Properties properties = FileUtil.LoadProperties("InverseLetterFrequencies.properties");
    	    inverseLetterFrequencies = new TreeMap<String,Float>();//TreeMap to ensure consistent ordering
    	    FileUtil.Copy(properties, inverseLetterFrequencies);
	    }
	    return inverseLetterFrequencies;
	}

    private static HashSet<String> DropWords;
    private static Random random = new Random();
    private static Map<String,Float> inverseLetterFrequencies;
	private static Logger sLogger = Logger.getLogger(OneGramFrequencies.class.getName());
}

