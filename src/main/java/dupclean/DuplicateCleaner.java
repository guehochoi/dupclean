/**
 * 
 */
package dupclean;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Hex;

/**
 * @author ghrchoi
 *
 */
public class DuplicateCleaner {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		DuplicateCleaner duplicateCleaner = new DuplicateCleaner();
		byte[] hash1 = duplicateCleaner.getHashInBytes("src/main/resources/images/1364442376228.jpg", "SHA-1");
		byte[] hash2 = duplicateCleaner.getHashInBytes("src/main/resources/images/20160204_001043.jpg", "SHA-1");
		

		System.out.println("img1: " + duplicateCleaner.hashByteToString(hash1));
		System.out.println("img2: " + duplicateCleaner.hashByteToString(hash2));
	}

	/**
	 * function to calculate hash of a file
	 * @param filepath path of the file to be hashed
	 * @param algorithms desired hash algirhtm to be used
	 * @return hash byte array of the file
	 */
	public byte[] getHashInBytes(String filepath, String algorithm) {
		Path path = Paths.get(filepath);
		MessageDigest md = null;
		InputStream is = null;
		DigestInputStream dis = null;
		byte[] hashInByte = new byte[20];
		try {
			md = MessageDigest.getInstance(algorithm);
			is = Files.newInputStream(path);
			/* Read decorated stream (dis) to EOF as normal... */
			dis = new DigestInputStream(is, md);
			int bytesRead = 0;
			byte[] byteStream = new byte[1024];
			while (dis.read(byteStream) > 0) {
				bytesRead += byteStream.length;
			}
			System.out.println("bytes read for file " + path + ": " + bytesRead);
			hashInByte = md.digest();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} finally {
			closeQuietly(is);
			closeQuietly(dis);
		}
		return hashInByte;
	}
	protected void closeQuietly(Closeable resource) {
		if (resource == null) 
			return;
		try {
			resource.close();
		} catch (IOException e) {
			System.out.println("error occured while closing " + resource.toString() + ": " + e.getLocalizedMessage());
		}
	}
	protected String hashByteToString(byte[] hashInByte) {
//		Hex.encodeHexString(hashInByte) -- lowercase
		return Hex.encodeHexString(hashInByte);
	}
}
