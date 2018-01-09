package app;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
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
		System.out.println("Is this correct? (y or any key)");
		String confirmation = scanner.nextLine();
		if (!confirmation.equalsIgnoreCase("y")) {
			System.out.println("You shouldn't mess with me, bye");
			System.exit(-1);
		}
		
		try {
			HashKeeper.init(input);
		} catch(Exception e) {
			e.printStackTrace();
			System.err.print("error during init");
			System.exit(-1);
		}
		
		System.out.println("\nResult: ===================================");
		Map<HashKeeper.HashKey, List<String>> map = HashKeeper.getHashToFilepathsMap();
		for (HashKeeper.HashKey key : map.keySet()) {
			for (String filename : map.get(key)) {
				System.out.printf("Filename: %s, Hash: %s, Count: %d\n", 
						filename, 
						HashHelper.hashByteToString(key.getHashInBytes()),
						map.get(key).size());
			}
		}
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
