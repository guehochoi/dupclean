package app;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnmappableCharacterException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Formatter;
import java.util.FormatterClosedException;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

import tools.hash.HashHelper;
import tools.hash.HashKeeper;

/**
 * @author ghrchoi
 *
 */
public class DuplicateCleaner {
	
	private static Scanner scanner = new Scanner(System.in);
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/*byte[] hash1 = HashHelper.getHashInBytes("src/main/resources/images/20160116_140836.jpg", "SHA-1");
		byte[] hash2 = HashHelper.getHashInBytes("src/main/resources/images/duplicated140836.jpg", "SHA-1");

		System.out.println("img1: " + HashHelper.hashByteToString(hash1));
		System.out.println("img2: " + HashHelper.hashByteToString(hash2));*/
		
		String input = promptInput();
		Path path = Paths.get(input);
		while (!Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
			System.out.println("your input: " + input);
			usage();
			input = promptInput();
			path = Paths.get(input);
		}
		
		System.out.println("You entered: " + path.toAbsolutePath().toString());
		computeCountAndSize(path.toAbsolutePath().toString());
		System.out.printf("%d files detected, total %s\n", count, humanReadableByteCount(size, false));
		System.out.println("Would you like to continue? (y or any key)");
		String confirmation = scanner.nextLine();
		if (!confirmation.equalsIgnoreCase("y")) {
			System.out.println("You shouldn't mess with me, bye");
			System.exit(-1);
		}

		long startTime = System.currentTimeMillis();
		try {
			HashKeeper.init(input);
		} catch(Exception e) {
			e.printStackTrace();
			System.err.print("error during init");
			System.exit(-1);
		}
		long hashEndTime = System.currentTimeMillis();
		System.out.println("Completed hashing in " + (hashEndTime - startTime) + "ms");
		
		Map<HashKeeper.HashKey, List<String>> map = HashKeeper.getHashToFilepathsMap();
		
		Path output_filepath = Paths.get("output");
		Charset charset = Charset.forName("ISO-2022-KR");
		
		try (BufferedWriter writer = Files.newBufferedWriter(output_filepath, charset)) {
			StringBuilder sb = new StringBuilder(); 
			Formatter formatter = new Formatter(sb, Locale.US); 
			
			for (HashKeeper.HashKey key : map.keySet()) {
				List<String> filenames = map.get(key);
				if (filenames.size() > 1) {
					sb.setLength(0);
					formatter.format("Hash: %s has %d collisions\n", HashHelper.hashByteToString(key.getHashInBytes()), filenames.size());
					writer.write(sb.toString());
					
					for (String filename : filenames) {
						sb.setLength(0);
						formatter.format("Filename: %s\n", filename);
						writer.write(sb.toString());
					}
				}
			}
			formatter.close();
			writer.close();
		
		} catch (UnmappableCharacterException e) { 
			e.printStackTrace();
			System.err.println("Character Encoding Error");
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("There was an I/O Error while executing application");
		} catch (FormatterClosedException | IllegalFormatException e) {
			e.printStackTrace();
			System.err.println("Error with formatter");
		} catch (Exception e) {
			System.err.println("Unexpected Error has occurred");
		}
		
		System.out.println("Success");
	}
	
	private static int count = 0;
	private static int dirCount = 0;
	private static long size = 0;
	private static void computeCountAndSize(String dirPath) {
	    File f = new File(dirPath);
	    File[] files = f.listFiles();

	    if (files != null)
	    for (int i = 0; i < files.length; i++) {
	        
	        File file = files[i];

	        if (file.isDirectory()) {   
	        	dirCount++;
	        	computeCountAndSize(file.getAbsolutePath()); 
	        } else {
	        	count ++;
	        	size += file.length();
	        }
	    }
	}
	public static String humanReadableByteCount(long bytes, boolean si) {
	    int unit = si ? 1000 : 1024;
	    if (bytes < unit) return bytes + " B";
	    int exp = (int) (Math.log(bytes) / Math.log(unit));
	    String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
	    return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}
	
	private static String promptInput() {
		String input = null;
		do {
			System.out.print("Enter the filepath: ");
			input = scanner.nextLine();
		} while (input == null || input.isEmpty() || input.trim().equals(""));
		return input;
	}
	private static void usage() {
		System.out.println("That's not how you use it");
		System.out.println("For all files in D directory, the app will find duplicates");
		System.out.println("The app will assess the duplicates and isolate them in a folder before deletion");
		System.out.println("you will have chance to review them");
		System.out.println("Enter the filepath to D");
	}
	
}
