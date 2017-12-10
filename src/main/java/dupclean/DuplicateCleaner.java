/**
 * 
 */
package dupclean;

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
		Path image1 = Paths.get("src/main/resources/images/1364442376228.jpg");
		Path image2 = Paths.get("src/main/resources/images/20160204_001043.jpg");
		
		MessageDigest md = null;
		MessageDigest md2 = null;
		try {
			md = MessageDigest.getInstance("SHA-1");
			md2 = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		}
		
		byte[] image1sha1 = new byte[20];
		byte[] image2sha1 = new byte[20];
		
		try  
		{
			InputStream is = Files.newInputStream(image1);
			DigestInputStream dis = new DigestInputStream(is, md);
			int bytesRead = 0;
			byte[] byteStream = new byte[1024];
			while (dis.read(byteStream) > 0) {
				bytesRead += byteStream.length;
			}
			System.out.println("bytes read: " + bytesRead);
			image1sha1 = md.digest();
			
			InputStream is2 = Files.newInputStream(image2);
			bytesRead = 0;
			byte[] byteStream2 = new byte[1024];
			DigestInputStream dis2 = new DigestInputStream(is2, md2);
			while (dis2.read(byteStream2) > 0) {
				bytesRead += byteStream2.length;
			}
			image2sha1 = md2.digest();
			
		  /* Read decorated stream (dis) to EOF as normal... */
		} catch (IOException e) {
			System.err.print("IO Error Occurred");
			e.printStackTrace();
		}
		System.out.println("SHA1 of Image1: " + Hex.encodeHexString(image1sha1));
		System.out.println("SHA1 of Image2: " + javax.xml.bind.DatatypeConverter.printHexBinary(image2sha1));
		
	}

}
