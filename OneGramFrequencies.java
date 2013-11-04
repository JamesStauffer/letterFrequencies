import java.io.*;
import java.util.*;
import java.util.logging.*;

public class OneGramFrequencies {

    public static OneGramFrequencies Get(boolean onlyLetters) {
        if(letterFrequencies == null) {
            letterFrequencies = new OneGramFrequencies(onlyLetters);
            letterFrequencies.getStandardFrequencies();//Pre-load
        }
        return letterFrequencies;
    }

    public static void main(String[] args) {
        if(args.length > 0) {
            OneGramFrequencies lf = new OneGramFrequencies(false);
            Map<String,Float> props = lf.getStandardFrequencies();
            for(String arg : FileUtil.GetFilename(args)) {
                Map<String,Float> fileFrequencies = lf.getFileFrequencies(arg);
                //print(fileFrequencies);
                Map<String,Float> percentDiff = lf.getPercentDifference(fileFrequencies);
                float standardDeviation = lf.getStandardDeviation(percentDiff);
                float KLDivergence = lf.getKLDivergence(fileFrequencies);
                System.out.println(arg + "\t Standard Deviation: " + standardDeviation
                    + "\t K-L Divergence: " + KLDivergence);
            }
        } else {
            OneGramFrequencies lf = new OneGramFrequencies(false);
            Map<String,Float> props = lf.getStandardFrequencies();
            Map<String,Float> percentDiff = lf.getPercentDifference(props);
            float standardDeviation = lf.getStandardDeviation(percentDiff);
            System.out.println("StandardDeviation:" + standardDeviation);
            float KLDivergence = lf.getKLDivergence(props);
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

    public static float GetKLDivergence(String filename, boolean onlyLetters) {
        OneGramFrequencies lf = new OneGramFrequencies(onlyLetters);
        Map props = lf.getStandardFrequencies();
        Map fileFrequencies = lf.getFileFrequencies(filename);
        float KLDivergence = lf.getKLDivergence(fileFrequencies);
        return KLDivergence;
    }

    public OneGramFrequencies(boolean onlyLetters) {
        this.onlyLetters = onlyLetters;
    }

	private void loadStandard() {
	    if(standardFrequencies != null) {
	        return;
	    }
	    Properties properties = FileUtil.LoadProperties("OneGramFrequencies.properties");
	    standardFrequencies = new TreeMap<String,Float>();
        FileUtil.Copy(properties, standardFrequencies);
	    logger.fine("loadStandard: " + standardFrequencies);
	}

    /**
     * @see http://en.wikipedia.org/wiki/Kullback-Leibler_divergence
     */
    public float getKLDivergence(Map<String,Float> frequencies) {
        double klDivergence = 0.0d;
	    for(Map.Entry<String,Float> standardFrequency : standardFrequencies.entrySet()) {
	        double p = standardFrequency.getValue();
	        double q = 0.0d;
	        if(frequencies.containsKey(standardFrequency.getKey())) {
	            q = frequencies.get(standardFrequency.getKey());
	        }
	        double temp = p * Math.log(p / q);
	        klDivergence += temp;
	    }
	    logger.fine("getKLDivergence: " + klDivergence);
        return (float)klDivergence;
    }

	public Map<String,Float> getStandardFrequencies() {
        loadStandard();
	    return standardFrequencies;
	}

	public Map<String,Float> getPercentDifference(Map<String,Float> frequencies) {
        loadStandard();
	    Map<String,Float> percentDifference = new HashMap<String,Float>();
	    for(Map.Entry<String,Float> standardFrequency : standardFrequencies.entrySet()) {
	        Float frequency = frequencies.get(standardFrequency.getKey());
	        if(frequency == null) {
	            frequency = 0.0f;
	        }
	        float percent = frequency / standardFrequency.getValue();
	        percentDifference.put(standardFrequency.getKey(), Float.valueOf(percent));
	    }
	    logger.fine("getPercentDifference: " + percentDifference);
	    return percentDifference;
	}

	public float getStandardDeviation(String data) {
	    return getStandardDeviation(getPercentDifference(getFrequencies(data)));
	}

	public float getStandardDeviation(Map<String,Float> percentDifferences) {
	    return getStandardDeviation(percentDifferences.values());
	}

    /**
     * Assumes average is 1.0.
     */
	public float getStandardDeviation(Collection<Float> values) {
	    double runningTotal = 0.0;
	    for(Float value : values) {
	        float deviation = value - 1.0f;
	        double square = deviation * deviation;
	        runningTotal += square;
	    }
	    float standardDeviation = (float)Math.sqrt(runningTotal / values.size());
	    logger.fine("getStandardDeviation: " + standardDeviation);
	    return standardDeviation;
	}

	public Map<String,Float> getFrequencies(String data) {
	    Map<String,MutableInteger> counts = new HashMap<String,MutableInteger>();
	    MutableInteger totalLetters = new MutableInteger();
	    recordCounts(counts, data, totalLetters);
	    logger.fine("Counts: " + counts);
        return getFrequencies(counts, totalLetters.getValue());
	}

	public void recordCounts(Map<String,MutableInteger> counts, String data, MutableInteger totalLetters) {
        for(int index = 0; index < data.length(); index++) {
            char chr = data.charAt(index);
            if(!onlyLetters || Character.isLetter(chr)) {
                String key = String.valueOf(Character.toLowerCase(chr));
                MutableInteger mi = counts.get(key);
                if(mi == null) {
                    mi = new MutableInteger();
                    counts.put(key, mi);
                }
                mi.increment();
                totalLetters.increment();
            }
        }
	}

	public Map<String,Float> getFileFrequencies(String filename) {
	    Map<String,MutableInteger> counts = new HashMap<String,MutableInteger>();
	    MutableInteger totalLetters = new MutableInteger();
	    for(String line : FileUtil.Read(filename)) {
            recordCounts(counts, line, totalLetters);
	    }
	    logger.fine("Counts: " + counts);
        return getFrequencies(counts, totalLetters.getValue());
	}

	public static void print(Map<String,Float> frequencies) {
        for(Map.Entry<String,Float> entry : frequencies.entrySet()) {
            System.out.println(entry);
        }
	}

	private Map<String,Float> getFrequencies(Map<String,MutableInteger> counts, int totalLetters) {
	    Map<String,Float> frequencies = new TreeMap<String,Float>();
	    for(Map.Entry<String,MutableInteger> entry : counts.entrySet()) {
	        frequencies.put(entry.getKey(), (float)((float)entry.getValue().getValue())/totalLetters);
	    }
	    if(totalLetters > 100) {//Probably a file
            logger.fine("Frequencies: " + frequencies);
            //print(frequencies);
	    } else {//Probably a word
    	    logger.fine("Frequencies: " + frequencies);
	    }
	    return frequencies;
	}

    private boolean onlyLetters;//Only record letters
	private Map<String,Float> standardFrequencies;
	private Logger logger = Logger.getLogger(getClass().getName());
    private static OneGramFrequencies letterFrequencies;
	private static Logger sLogger = Logger.getLogger(OneGramFrequencies.class.getName());
}
