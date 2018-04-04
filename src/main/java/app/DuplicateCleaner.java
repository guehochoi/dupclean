package app;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.UnmappableCharacterException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Formatter;
import java.util.FormatterClosedException;
import java.util.IllegalFormatException;
import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

import app.ui.MainFrame;
import tools.hash.HashHelper;
import tools.hash.HashKeeper;

/**
 * @author ghrchoi
 *
 */
public class DuplicateCleaner {
	
	private static Scanner scanner = new Scanner(System.in);
	private static MainFrame mainframe = new MainFrame();
	/**
	 * @param args
	 */
	public static void main(String[] args) {
	
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
		Charset charset = Charset.forName("UTF-8");
		boolean addNewLine = false;
		try (BufferedWriter writer = Files.newBufferedWriter(output_filepath, charset)) {
			StringBuilder sb = new StringBuilder(); 
			//Formatter formatter = new Formatter(sb, Locale.US); 
			
			for (HashKeeper.HashKey key : map.keySet()) {
				List<String> filenames = map.get(key);
				if (filenames.size() > 1) {
					sb.setLength(0);
					if (addNewLine) {
						writer.newLine();
						addNewLine = false;
					}
					//formatter.format("Hash: %s has %d collisions\n", HashHelper.hashByteToString(key.getHashInBytes()), filenames.size());
					//writer.write(sb.toString());
					
					for (Iterator<String> it = filenames.iterator(); it.hasNext();) {
						sb.append(it.next());
						if (it.hasNext()) {
							sb.append("|");
						}
					}
					
					writer.write(sb.toString());
					addNewLine = true;
				}
			}
			//formatter.close();
			writer.close();
		
		} catch (UnmappableCharacterException e) { 
			e.printStackTrace();
			addNewLine = true;
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("There was an I/O Error while executing application");
		} catch (Exception e) {
			System.err.println("Unexpected Error has occurred");
		}
		
		System.out.println("Successfully finished hashing");
		
		try (BufferedReader reader = Files.newBufferedReader(output_filepath, charset)) {
			String line;
			while ( (line = reader.readLine()) != null) {
				String[] dups = line.split("\\|"); 
				if (dups.length < 2) {
					continue;
				}
				displayDuplicates(dups);
				int keepIndex = userChoiceInput();
				if (keepIndex < 0) { // skip 
					continue;
				}
				deleteDuplicates(dups, keepIndex);
				
			}
				
			reader.close();
		
		} catch (UnmappableCharacterException e) { 
			e.printStackTrace();
			addNewLine = true;
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("There was an I/O Error while executing application");
		} catch (Exception e) {
			System.err.println("Unexpected Error has occurred");
		}
	}
	
	private static void deleteDuplicates(String[] dups, int keepIndex) {
		if (keepIndex < 0 || keepIndex > dups.length -1) {
			System.out.println("ERROR: keepIndex: " + keepIndex + ", arrayLength: " + dups.length);
			return;
		}
		int i = 0; 
		while (i < dups.length) {
			if (i == keepIndex) {
				i++;
				continue;
			}
			try {
				Path p = Paths.get(dups[i]);
				Path target = null;
				if (!Files.isDirectory(p, LinkOption.NOFOLLOW_LINKS)) {
					int j = 0;
					while (Files.exists(target = Paths.get("D:\\\\Dump\\Archive\\" + p.getFileName() + j), LinkOption.NOFOLLOW_LINKS)) {
						j++;
					}
				}
				//Files.delete(p);
				Files.move(p, target, StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			i++;
		}
	}

	private static int userChoiceInput() {
		System.out.println("select one to keep: ");
		int response = -1;
		try {
			response = Integer.parseInt(scanner.nextLine());
		}catch (InputMismatchException | NumberFormatException e) {
			e.printStackTrace();
		}
		
		return response;
	}

	private static void displayDuplicates(String[] dups) {
		for (int i = 0; i < dups.length; i++) {
			System.out.println("[" + i + "]" + dups[i]);
		}
		mainframe.clearImagePanel();
		mainframe.displayDuplicates(dups);
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
