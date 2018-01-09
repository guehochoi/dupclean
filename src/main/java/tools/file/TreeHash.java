package tools.file;

import java.io.IOException;
import java.nio.file.FileSystemLoopException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

import tools.hash.HashHelper;

public class TreeHash implements FileVisitor<Path> {

	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
		return FileVisitResult.CONTINUE;
	}

	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		byte[] hashbyte = HashHelper.getHashInBytes(file, "SHA-1");
		String filepath = file.toAbsolutePath().toString();
		HashHelper.addItemToMap(hashbyte, filepath);
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
	
}
