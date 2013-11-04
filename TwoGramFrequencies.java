import java.io.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.logging.*;

public class TwoGramFrequencies {

    public static TwoGramFrequencies Get(boolean onlyLetters) {
        if(twoFrequencies == null) {
            twoFrequencies = new TwoGramFrequencies (onlyLetters);
        }
        return twoFrequencies;
    }

    public static void main(String[] args) {
        if(args.length > 0) {
            TwoGramFrequencies lf = new TwoGramFrequencies(true);
            Map<String,Map<String,Double>> props = lf.getStandardFrequencies();
            for(String arg : FileUtil.GetFilename(args)) {
                Map fileFrequencies = lf.getFileFrequencies(arg);
                if(fileFrequencies.size() > 0) {
                    Map<String,Map<String,Float>> percentDiff = lf.getPercentDifference(fileFrequencies);
                    float standardDeviation = lf.getStandardDeviation(percentDiff);
                    System.out.println(arg + "\t Standard Deviation: " + standardDeviation);
                }
            }
        } else {
            TwoGramFrequencies lf = new TwoGramFrequencies(false);
            Map<String,Map<String,Double>> props = lf.getStandardFrequencies();
            Map<String,Map<String,Float>> percentDiff = lf.getPercentDifference(props);
            float standardDeviation = lf.getStandardDeviation(percentDiff);
            System.out.println("Standard Frequencies: " + props);
            System.out.println("StandardDeviation: " + standardDeviation);
        }
    }

    public static float GetStandardDeviation(String filename, boolean onlyLetters) {
        OneGramFrequencies lf = new OneGramFrequencies(onlyLetters);
        Map props = lf.getStandardFrequencies();
        Map fileFrequencies = lf.getFileFrequencies(filename);
        Map<String,Float> percentDiff = lf.getPercentDifference(fileFrequencies);
        float standardDeviation = lf.getStandardDeviation(percentDiff);
        return standardDeviation;
    }

    public TwoGramFrequencies(boolean onlyLetters) {
        this.onlyLetters = onlyLetters;
        loadStandard();
    }

    public Map<String,Map<String,Double>> getStandardFrequencies() {
        loadStandard();
        return standardFrequencies;
    }

	public Map<String,Map<String,Float>> getPercentDifference(Map<String,Map<String,Double>> frequencies) {
        loadStandard();
	    Map<String,Map<String,Float>> percentDifferences = new TreeMap<String,Map<String,Float>>();
	    for(Map.Entry<String,Map<String,Double>> oneStandardFrequencies : standardFrequencies.entrySet()) {
	        String firstLetter = oneStandardFrequencies.getKey();
	        Map<String,Float> secondDifferences = null;
	        if(!percentDifferences.containsKey(firstLetter)) {
	            secondDifferences = new TreeMap<String,Float>();
	            percentDifferences.put(firstLetter, secondDifferences);
	        } else {
	            secondDifferences = percentDifferences.get(firstLetter);
	        }

	        for(Map.Entry<String,Double> twoStandardFrequencies : oneStandardFrequencies.getValue().entrySet()) {
	            String secondLetter = twoStandardFrequencies.getKey();
    	        Double frequency = GetFrequency(firstLetter, secondLetter, frequencies);
    	        if(frequency != null) {
        	        float percent = (float)(frequency / twoStandardFrequencies.getValue());
        	        if(frequency.equals(twoStandardFrequencies.getValue())) {
        	            percent = 1.0f;
        	        }
        	        if(percent > 1000.0f) {
                        logger.fine("getPercentDifference: " + firstLetter + "," + secondLetter + " = " 
                            + frequency + "/" + twoStandardFrequencies.getValue() + " = " + percent);
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
        for(int index = 0; index < data.length() - 1; index++) {
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

    /**
     * Data from http://www.data-compression.com/english.shtml#second
     */
	private void loadStandard() {
	    if(standardFrequencies != null) {
	        return;
	    }
	    List<String> lines = FileUtil.ReadResource("TwoGramFrequencies.csv");
	    String[] header = null;
        standardFrequencies = new TreeMap<String,Map<String,Double>>();
        OneGramFrequencies oneGramFrequencies = new OneGramFrequencies(true);
        int frequencyCount = 0;
	    for(String line : lines) {
	        logger.fine("loadStandard Line:" + line);
	        String[] parts = line.split(",");
	        logger.fine("loadStandard Parts:" + Arrays.asList(parts));
	        if(parts[0].length() == 0) {//Header
	            header = parts;
                logger.fine("loadStandard Header:" + Arrays.asList(header));
	            for(int index = 1; index < parts.length; index++) {
	                standardFrequencies.put(parts[index], new TreeMap<String,Double>());
	            }
	        } else {
	            String firstLetter = parts[0];
	            for(int index = 1; index < parts.length; index++) {
	                String secondLetter = header[index];
//	                System.out.println("firstLetter='" + firstLetter + "'");
//	                Float firstLetterFrequency = oneGramFrequencies.getStandardFrequencies().get(firstLetter);
//	                if(firstLetterFrequency != null) {
//                        double overallFrequency = firstLetterFrequency.doubleValue() * new Double(parts[index]).doubleValue();
                        Double overallFrequency = new Double(parts[index]);
                        if(overallFrequency.doubleValue() > 0.0d) {
                            standardFrequencies.get(firstLetter).put(secondLetter, overallFrequency);
                        }
                        frequencyCount++;
//	                }
	            }
	        }
	    }
	    logger.fine("loadStandard: " + standardFrequencies);
	    logger.fine("loadStandard: Loaded " + frequencyCount + " two-gram frequencies.");
	}

    private boolean onlyLetters;//Only record letters
	private Map<String,Map<String,Double>> standardFrequencies;//Map<First Letter,Map<Second Letter,Frequency>>
	private Logger logger = Logger.getLogger(getClass().getName());
    private static TwoGramFrequencies twoFrequencies;
	private static Logger sLogger = Logger.getLogger(TwoGramFrequencies.class.getName());
}

