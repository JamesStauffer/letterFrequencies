import java.io.*;
import java.util.*;
import java.util.logging.*;

import ca.site.elkb.*;

public class Synonym {
    public static void main(String[] args) {
        GetRogetELKB();//Pre-load
        if(args.length == 0) {
            System.out.println("Usage: Synonym [filename | A sentance]");
        } else {
            if(args[0].indexOf(".") < args[0].length() - 1) {//Filename
                for(String arg : args) {
                    System.out.println(arg);
                    List<String> originalWords = ToWordList(FileUtil.Read(arg));
                    String bestWords = ToString(PickBestWords(originalWords));
                    FileUtil.Write(arg + ".syn.txt", bestWords);
                    OneGramFrequencies lf = OneGramFrequencies.Get(true);
                    System.out.println(arg + ": " + lf.getStandardDeviation(ToString(originalWords)) 
                        + " -> " + lf.getStandardDeviation(bestWords));
                }
            } else {
                List<String> argList = new ArrayList<String>();
                Collections.addAll(argList, args);
                System.out.println(argList);
                System.out.println("--------------------------------");
                System.out.println(ToString(PickBestWords(argList)));
            }
        }
    }

    public static String ToString(List<String> wordList) {
        StringBuffer sb = new StringBuffer();
        for(String word : wordList) {
            sb.append(word);
        }
        return sb.toString();
    }

    public static List<String> ToWordList(List<String> lines) {
        List<String> list = new ArrayList<String>();
        for(String line : lines) {
            for(StringTokenizer st = new StringTokenizer(line, " .,;'", true); st.hasMoreTokens(); ) {
                list.add(st.nextToken());
            }
        }
        return list;
    }

    public static List<String> PickBestWords(List<String> wordList) {
        List<String> bestWords = new ArrayList(wordList.size());
        for(String word : wordList) {
            bestWords.add(GetBest(word));
        }
        sLogger.fine("PickBestWords(" + wordList + ") " + bestWords);
        return bestWords;
    }

    public static String GetBest(String original) {
        String bestWord = bestWords.get(original);
        if(bestWord != null) {
            return bestWord;
        }
        List<String> wordList =  GetWordList(original);
        if(wordList != null) {
            bestWord = PickBest(wordList);
            if(original.equals(bestWord) || bestWord == null) {
                sLogger.fine("GetBest(" + original + ") No replacement.");
                bestWords.put(original, original);
                return original;
            } else {
                sLogger.fine("GetBest(" + original + ") " + bestWord);
                bestWords.put(original, bestWord);
                return bestWord;
            }
        } else {
            sLogger.fine("GetBest(" + original + ") No replacement.");
            bestWords.put(original, original);
            return original;
        }
    }

    public static String PickBest(List<String> wordList) {
        if(wordList == null || wordList.size() == 0) {
            return null;
        }
        float bestStandardDeviation = 0.0f;
        String bestWord = null;
        OneGramFrequencies lf = OneGramFrequencies.Get(true);
        for(String word : wordList) {
            if(word.indexOf("\"") < 0 && word.indexOf(" ") < 0) {
                float standardDeviation = lf.getStandardDeviation(word);
                if(standardDeviation > bestStandardDeviation) {
                    bestStandardDeviation = standardDeviation;
                    bestWord = word;
                }
            }//Don't use quotes.
        }
        sLogger.fine("PickBest(" + wordList + ") " + bestWord + " (" + bestStandardDeviation + ")");
        return bestWord;
    }

    public static List<String> GetWordList(String word) {
        sLogger.fine("GetWordList(" + word + ")");
        try {
            RogetELKB elkb = GetRogetELKB();
            ArrayList<String> refList = elkb.index.getStrRefList(word);
            sLogger.fine("GetWordList refList=" + refList);
            if(refList.size() == 0) {
                return null;
            }
            String sRef = refList.get(0);
            Reference ref = new Reference(sRef);
            String sKey = ref.getRefName();
            int iHead = ref.getHeadNum();
            String sPOS = ref.getPos();
            Head elkbHead = elkb.text.getHead(iHead);
            Paragraph elkbPara = elkbHead.getPara(sKey, sPOS);
            SG elkbSG = elkbPara.getSG(word);
            List<SG> sgList = elkbPara.getSGList();
            List<String> wordList = sgList.get(0).getWordList();
            sLogger.fine("GetWordList(" + word + ") " + wordList);
            return wordList;
        } catch(Exception e) {
            sLogger.info("Unable to GetWordList(" + word + ")");
	        sLogger.log(Level.FINE, "Unable to GetWordList(" + word + ")", e);
            return null;
        }
    }

    private static RogetELKB elkb;
    private static Map<String,String> bestWords = new HashMap<String,String>();
    private static OneGramFrequencies letterFrequencies;
	private Logger logger = Logger.getLogger(getClass().getName());
	private static Logger sLogger = Logger.getLogger(Synonym.class.getName());

    private static RogetELKB GetRogetELKB() {
        if(elkb == null) {
            elkb = new RogetELKB(1911);
        }
        return elkb;
    }
}

