import java.io.*;
import java.util.*;
import java.util.logging.*;

public class DoubleSubstitute {

    public static void main(String[] args) {
        LoadMapping();
        OneGramFrequencies lf = OneGramFrequencies.Get(true);
        for(String arg : FileUtil.GetFilename(args)) {
            try {
                String fileContents = FileUtil.Concatenate(FileUtil.Read(arg));
                Map<String,List<String>> mapping = GetMapping(lf.getFrequencies(fileContents));
                StringBuffer changedContentsBuffer = new StringBuffer(fileContents.length());
                for(int index = 0; index < fileContents.length(); index++) {
                    String chr = fileContents.substring(index, index + 1);
                    List<String> mappingList = mapping.get(chr);
                    if(mappingList != null && mappingList.size() > 0) {
                        int selectedIndex = mappingList.size() > 1 ? rand.nextInt(mappingList.size()) : 0;
                        changedContentsBuffer.append(mappingList.get(selectedIndex));
                    }
                    if(changedContentsBuffer.length() % 80 == 0) {
                        changedContentsBuffer.append("\n");
                    }
                }
                String changedContents = changedContentsBuffer.toString();
                FileUtil.Write(arg + ".DoubleSubstitute.txt", changedContents);
                System.out.println(arg + ": " + lf.getStandardDeviation(fileContents) 
                    + " -> " + lf.getStandardDeviation(changedContents));
                logger.fine(arg + " Percent Difference: " + lf.getPercentDifference(lf.getFrequencies(changedContents)));
            } catch(Exception e) {
                logger.log(Level.WARNING, "Unable to process " + arg, e);
            }
        }
    }

    public static void LoadLetterPairs() {
        if(startingLetterPairs != null) {
            return;
        }
        startingLetterPairs = new ArrayList<String>(26 * 26);
        String letters = ALL_LETTERS;
        for(int index1 = 0; index1 < letters.length(); index1++) {
            for(int index2 = 0; index2 < letters.length(); index2++) {
                startingLetterPairs.add(new String(new char[]{letters.charAt(index1), letters.charAt(index2)}));
            }
        }
        logger.fine("LoadMapping LetterPairs" + startingLetterPairs);
    }

    public final static String ALL_LETTERS = "abcdefghijklmnopqrstuvwxyz";

    public static void LoadMapping() {
        if(standardMapping != null) {
            return;
        }
        LoadLetterPairs();
        standardMapping = GetMapping(new OneGramFrequencies(false).getStandardFrequencies());
    }

    public static Map<String,List<String>> GetMapping(Map<String,Float> frequencies) {
        List<String> letterPairs = new ArrayList<String>(startingLetterPairs);
        logger.fine("GetMapping for " + frequencies.size() + " frequencies.");
        Map<String,List<String>> mapping = new TreeMap<String,List<String>>();
        for(Map.Entry<String,Float> frequency : frequencies.entrySet()) {
            int letterPairsNeeded = (int)(frequency.getValue() * frequencies.size()
                * frequencies.size() * 0.999 + 0.5);//Multiple by 0.999 so rounding errors don't make it go over.
            logger.fine("GetMapping " + frequency.getKey() + " needs " + letterPairsNeeded + " letter pairs of remaining " + letterPairs.size());
            List<String> mappingList = new ArrayList<String>(letterPairsNeeded);
            mapping.put(frequency.getKey(), mappingList);
            for(int letterIndex = 0; letterIndex < letterPairsNeeded; letterIndex++) {
                int letterPairIndex = letterPairs.size() > 1 ? rand.nextInt(letterPairs.size()) : 0;
                String mappedLetters = letterPairs.get(letterPairIndex);
                mappingList.add(mappedLetters);
                letterPairs.remove(letterPairIndex);
            }
            logger.fine("GetMapping " + frequency.getKey() + " assigned " + mappingList);
        }
        logger.fine("GetMapping: " + mapping);
        return mapping;
    }

    private static Map<String,List<String>> standardMapping;
    private static List<String> startingLetterPairs;
    private static Random rand = new Random();
	private static Logger logger = Logger.getLogger(DoubleSubstitute.class.getName());
}
