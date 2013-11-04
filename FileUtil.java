
import java.io.*;
import java.util.*;
import java.util.logging.*;

public class FileUtil {
	public static List<String> Read(String filename) {
	    sLogger.fine("Read(" + filename + ")");
	    BufferedReader br = null;
	    try {
    	    br = new BufferedReader(new FileReader(filename));
    	    return Read(br);
	    } catch(IOException ioe) {
	        sLogger.log(Level.WARNING, "Unable to load " + filename, ioe);
	    } finally {
	        Close(br);
	    }
        return new ArrayList<String>();
	}

    public static List<String> GetFilename(String[] args) {
        List<String> filenames = new ArrayList<String>(args.length);
        for(String arg : args) {
            if(arg.startsWith("*")) {
                final String suffix = arg.substring(1);
                filenames.addAll(GetFilename(new File(".").list(new FilenameFilter() {
                    public boolean accept(File dir, String name) {
                        return name.endsWith(suffix) && name.substring(0, name.length() - suffix.length()).indexOf(".") < 0;
                    }
                })));
            } else {
                filenames.add(arg);
            }
        }
        return filenames;
    }

	public static Properties LoadProperties(String filename) {
	    Properties properties = new Properties();
	    InputStream in = FileUtil.class.getClassLoader().getResourceAsStream(filename);
	    try {
            properties.load(in);
	    } catch(IOException ioe) {
	        sLogger.log(Level.WARNING, "Unable to load " + filename, ioe);
	    }
	    sLogger.fine("loadProperties: " + properties);
	    return properties;
	}

	public static void Copy(Properties source, Map<String,Float> target) {
        for(Iterator i = source.entrySet().iterator(); i.hasNext(); ) {
            Map.Entry<String,String> entry = (Map.Entry<String,String>)i.next();
            target.put(entry.getKey(), Float.valueOf(entry.getValue()));
        }
        sLogger.fine("Copy: " + target);
	}

	public static List<String> Read(BufferedReader br) {
	    List<String> lines = new ArrayList<String>();
	    try {
    	    String line;
    	    while((line = br.readLine()) != null) {
    	        lines.add(line);
    	    }
	    } catch(IOException ioe) {
	        sLogger.log(Level.WARNING, "Unable to read from " + br, ioe);
	    }
	    sLogger.fine("Read Loaded " + lines.size() + " line(s).");
        return lines;
	}

	private static void Close(BufferedReader br) {
        if(br != null) {
            try {
                br.close();
            } catch(Exception e) {
               //ignore
            }
        }
	}

	public static List<String> ReadResource(String filename) {
	    sLogger.fine("ReadResource(" + filename + ")");
	    BufferedReader br = null;
	    try {
            br = new BufferedReader(new InputStreamReader(FileUtil.class.getClassLoader().getResourceAsStream(filename)));
    	    return Read(br);
	    } finally {
	        Close(br);
        }
	}

	public static String Concatenate(List<String> lines) {
        StringBuffer sb = new StringBuffer();
        for(String line : lines) {
            sb.append(line).append("\r\n");
        }
        return sb.toString();
	}

	public static boolean Write(String filename, String line) {
	    List<String> lines = new ArrayList<String>();
	    lines.add(line);
	    return Write(filename, lines);
	}

	public static boolean Write(String filename, List<String> lines) {
	    sLogger.fine("Write(" + filename + ")");
	    BufferedWriter bw = null;
	    try {
    	    bw = new BufferedWriter(new FileWriter(filename));
    	    for(String line : lines) {
    	        bw.write(line, 0, line.length());
    	        bw.newLine();
    	    }
	    } catch(IOException ioe) {
	        sLogger.log(Level.WARNING, "Unable to write " + filename, ioe);
	        return false;
	    } finally {
	        if(bw != null) {
	            try {
	                bw.close();
	            } catch(Exception e) {
	               //ignore
	            }
	        }
	    }
	    sLogger.fine("Write wrote " + lines.size() + " line(s).");
        return true;
	}

    private static Logger sLogger = Logger.getLogger(FileUtil.class.getName());
}

