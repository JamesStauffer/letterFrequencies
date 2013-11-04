import java.io.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.logging.*;

/**
 * Frequencies of 2-letter pairs
 */
public class DoubleTwoGramFrequencies {

    public static DoubleTwoGramFrequencies Get(boolean onlyLetters) {
        if(doubleTwoGramFrequencies == null) {
            doubleTwoGramFrequencies = new DoubleTwoGramFrequencies (onlyLetters);
        }
        return doubleTwoGramFrequencies;
    }

    public static void main(String[] args) {
        if(args.length > 0) {
            DoubleTwoGramFrequencies lf = new DoubleTwoGramFrequencies(true);
            for(String arg : FileUtil.GetFilename(args)) {
                Map fileFrequencies = lf.getFileFrequencies(arg);
                if(fileFrequencies.size() > 0) {
                    Map<String,Map<String,Float>> percentDiff = lf.getPercentDifference(fileFrequencies);
                    float standardDeviation = lf.getStandardDeviation(percentDiff);
                    System.out.println(arg + "\t Standard Deviation: " + standardDeviation);
                }
            }
        }
    }

    public DoubleTwoGramFrequencies(boolean onlyLetters) {
        this.onlyLetters = onlyLetters;
    }

	public Map<String,Map<String,Float>> getPercentDifference(Map<String,Map<String,Double>> frequencies) {
        final double standardFrequency = 1.0d / (DoubleSubstitute.ALL_LETTERS.length() * DoubleSubstitute.ALL_LETTERS.length());
	    Map<String,Map<String,Float>> percentDifferences = new TreeMap<String,Map<String,Float>>();
        for(int index1 = 0; index1 < DoubleSubstitute.ALL_LETTERS.length(); index1++) {
	        String firstLetter = DoubleSubstitute.ALL_LETTERS.substring(index1, index1 + 1);
	        Map<String,Float> secondDifferences = null;
	        if(!percentDifferences.containsKey(firstLetter)) {
	            secondDifferences = new TreeMap<String,Float>();
	            percentDifferences.put(firstLetter, secondDifferences);
	        } else {
	            secondDifferences = percentDifferences.get(firstLetter);
	        }

            for(int index2 = 0; index2 < DoubleSubstitute.ALL_LETTERS.length(); index2++) {
	            String secondLetter = DoubleSubstitute.ALL_LETTERS.substring(index2, index2 + 1);
    	        Double frequency = GetFrequency(firstLetter, secondLetter, frequencies);
    	        if(frequency != null) {
        	        float percent = (float)(frequency / standardFrequency);
        	        if(percent > 1.2f || percent < 0.8f) {
                        logger.info("getPercentDifference: " + firstLetter + "," + secondLetter + " = " 
                            + frequency + "/" + standardFrequency + " = " + percent);
        	        }
        	        secondDifferences.put(secondLetter, Float.valueOf(percent));
    	        }
	        }
	    }
	    logger.fine("getPercentDifference: " + percentDifferences);
	    return percentDifferences;
	}

	public static Double GetFrequency(String firstLetter, String secondLetter, Map<String,Map<String,Double>> frequencies) {
	    Map<String,Double> secondFrequencies = frequencies.get(firstLetter);
	    if(secondFrequencies == null) {
	        return null;
	    }
	    return secondFrequencies.get(secondLetter);
	}

	public static Double SetFrequency(String firstLetter, String secondLetter, 
	    Map<String,Map<String,Double>> frequencies, Double freqency)
    {
	    Map<String,Double> secondFrequencies = frequencies.get(firstLetter);
	    if(secondFrequencies == null) {
	        secondFrequencies = new TreeMap<String,Double>();
	    }
        return secondFrequencies.put(secondLetter, freqency);
    }

	public float getStandardDeviation(Map<String,Map<String,Float>> percentDifferences) {
	    Collection<Float> allPercents = new ArrayList(percentDifferences.size() * percentDifferences.size());
	    for(Map.Entry<String,Map<String,Float>> seconds : percentDifferences.entrySet()) {
	        allPercents.addAll(seconds.getValue().values());
	    }
	    logger.fine("getStandardDeviation All Percents: " + allPercents);
	    return getStandardDeviation(allPercents);
	}

    /**
     * Assumes average is 1.0.
     */
	public float getStandardDeviation(Collection<Float> values) {
	    BigDecimal runningTotal = BigDecimal.ZERO;
	    for(Float value : values) {
	        float deviation = value - 1.0f;
	        double square = (float)deviation * deviation;
	        runningTotal = runningTotal.add(new BigDecimal(square));
            logger.fine("getStandardDeviation Value: " + value + " Deviation: "
                + deviation + " Square: " + square + " RunningTotal: " + runningTotal);
	    }
	    logger.fine("getStandardDeviation Total: " + runningTotal);
	    BigDecimal averageRunningTotal = runningTotal.divide(new BigDecimal(values.size()), 10, BigDecimal.ROUND_HALF_UP);
	    logger.fine("getStandardDeviation Average Running Total: " + averageRunningTotal);
	    float standardDeviation = (float)Math.sqrt(averageRunningTotal.floatValue());
	    logger.fine("getStandardDeviation: " + standardDeviation);
	    return standardDeviation;
	}

	public Map<String,Map<String,Double>> getFileFrequencies(String filename) {
	    Map<String,Map<String,MutableInteger>> counts = new TreeMap<String,Map<String,MutableInteger>>();
	    MutableInteger totalLetters = new MutableInteger();
	    for(String line : FileUtil.Read(filename)) {
            recordCounts(counts, line, totalLetters);
	    }
	    logger.fine("getFileFrequencies Counts: " + counts);
	    logger.fine("getFileFrequencies TotalLetters: " + totalLetters);
        return getFrequencies(counts, totalLetters.getValue());
	}

	public void recordCounts(Map<String,Map<String,MutableInteger>> counts, String data, MutableInteger totalLetters) {
	    if(data.length() == 0) {
	        return;
	    }
	    if(Character.isLetter(data.charAt(data.length() - 1))) {
	        data += " ";//Line separators are treated as a space.
	    }
        for(int index = 0; index < data.length() - 1; index += 2) {
            char chr = data.charAt(index);
            if(!onlyLetters || Character.isLetter(chr) || chr == ' ') {
                String firstLetter = String.valueOf(Character.toLowerCase(chr));
                if(!counts.containsKey(firstLetter)) {
                    counts.put(firstLetter, new TreeMap<String,MutableInteger>());
                }
                Map<String,MutableInteger> secondCounts = counts.get(firstLetter);
                String secondLetter = String.valueOf(Character.toLowerCase(data.charAt(index + 1)));
                if(onlyLetters && !Character.isLetter(secondLetter.charAt(0)) && !secondLetter.equals(" ")) {
                    secondLetter = " ";
                }
                MutableInteger mi = secondCounts.get(secondLetter);
                if(mi == null) {
                    mi = new MutableInteger();
                    secondCounts.put(secondLetter, mi);
                }
                mi.increment();
                totalLetters.increment();
            }
        }
	}

	private Map<String,Map<String,Double>> getFrequencies(Map<String,Map<String,MutableInteger>> counts, int totalLetters) {
	    Map<String,Map<String,Double>> frequencies = new TreeMap<String,Map<String,Double>>();
	    for(Map.Entry<String,Map<String,MutableInteger>> entry : counts.entrySet()) {
	        Map<String,Double> secondFrequencies = new TreeMap<String,Double>();
	        frequencies.put(entry.getKey(), secondFrequencies);
            for(Map.Entry<String,MutableInteger> entryTwo : entry.getValue().entrySet()) {
    	        secondFrequencies.put(entryTwo.getKey(), (double)((float)entryTwo.getValue().getValue())/totalLetters);
	        }
	    }
        logger.fine("Frequencies: " + frequencies);
	    return frequencies;
	}

    private boolean onlyLetters;//Only record letters
	private Map<String,Map<String,Double>> standardFrequencies;//Map<First Letter,Map<Second Letter,Frequency>>
	private Logger logger = Logger.getLogger(getClass().getName());
    private static DoubleTwoGramFrequencies doubleTwoGramFrequencies;
	private static Logger sLogger = Logger.getLogger(DoubleTwoGramFrequencies.class.getName());
}

