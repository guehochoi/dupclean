package app;

import tools.hash.HashHelper;

/**
 * @author ghrchoi
 *
 */
public class DuplicateCleaner {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		HashHelper hashHelper = new HashHelper();
		byte[] hash1 = hashHelper.getHashInBytes("src/main/resources/images/1364442376228.jpg", "SHA-1");
		byte[] hash2 = hashHelper.getHashInBytes("src/main/resources/images/20160204_001043.jpg", "SHA-1");

		System.out.println("img1: " + hashHelper.hashByteToString(hash1));
		System.out.println("img2: " + hashHelper.hashByteToString(hash2));
	}

	
	
	
}
