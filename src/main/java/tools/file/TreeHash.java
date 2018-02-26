package tools.file;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.file.FileSystemLoopException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;

import tools.hash.HashHelper;

public class TreeHash implements FileVisitor<Path> {

	private int count = 0;
	
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
		return FileVisitResult.CONTINUE;
	}

	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		byte[] hashbytesha1 = HashHelper.getHashInBytes(file, "SHA-1");
		byte[] hashbytesmd5 = HashHelper.getHashInBytes(file, "MD5");
		if (hashbytesha1 == null || hashbytesmd5 == null) {
			// The file is less than 1KB, don't care about these
			return FileVisitResult.CONTINUE;
		}
		String filepath = file.toAbsolutePath().toString();
		HashHelper.addItemToMap(hashbytesha1, hashbytesmd5, filepath);
		count ++;
		return FileVisitResult.CONTINUE;
	}

	public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
		if (exc instanceof FileSystemLoopException) {
	        System.err.println("cycle detected: " + file);
	    } else {
	        System.err.println("Error occured while visiting file");
	        exc.printStackTrace();
	    }
	    return FileVisitResult.CONTINUE;
	}

	public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
		return FileVisitResult.CONTINUE;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
	
}
