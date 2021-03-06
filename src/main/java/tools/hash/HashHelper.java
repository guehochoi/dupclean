package tools.hash;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Hex;

import tools.hash.HashKeeper.HashKey;
import tools.stream.ResourceManagementHelper;

public class HashHelper {
	/**
	 * function to calculate hash of a file
	 * @param filepath path of the file to be hashed
	 * @param algorithms desired hash algirhtm to be used
	 * @return hash byte array of the file
	 */
	public static byte[] getHashInBytes(String filepath, String algorithm) {
		Path path = Paths.get(filepath);
		return getHashInBytes(path, algorithm);
	}
	
	private static BufferedWriter writer = null;
	
	public static byte[] getHashInBytes(Path path, String algorithm) {
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
			if (bytesRead < 1024) { 
				return null;
			}
			/*if (writer != null) {
				writer.write("bytes read for file " + path + ": " + bytesRead);
			} else {
				writer = Files.newBufferedWriter(Paths.get("runtime-log"), Charset.forName("UTF-8"), StandardOpenOption.APPEND, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
				writer.write("bytes read for file " + path + ": " + bytesRead);
			}*/
			hashInByte = md.digest();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} finally {
			ResourceManagementHelper.closeQuietly(is);
			ResourceManagementHelper.closeQuietly(dis);
		}
		return hashInByte;
	}
	
	public static String hashByteToString(byte[] hashInByte) {
//		Hex.encodeHexString(hashInByte) -- lowercase
		return Hex.encodeHexString(hashInByte);
	}
	
	public static boolean addItemToMap(byte[] hashbytesha1, byte[] hashbytemd5, String filepath) {
		try {
			Map<HashKey, List<String>> hashToFiles = HashKeeper.getHashToFilepathsMap();
			HashKey key = new HashKey(hashbytesha1, hashbytemd5);
			if (hashToFiles.containsKey(key)) {
				List<String> filepaths = hashToFiles.get(key);
				filepaths.add(filepath);
			} else {
				List<String> filepaths = new ArrayList<String>();
				filepaths.add(filepath);
				hashToFiles.put(key, filepaths);
			}
		} catch(Exception e) {
			return false;
		}
		return true;
	}
}
