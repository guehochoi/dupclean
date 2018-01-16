package tools.hash;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import tools.file.TreeHash;

public class HashKeeper {
	
	// singleton
	private static Map<HashKey, List<String>> hashToFiles = null;

	private HashKeeper() {
	}
	
	public synchronized static Map<HashKey, List<String>> getHashToFilepathsMap() {
		if (hashToFiles == null) {
			hashToFiles = new HashMap<HashKey, List<String>>();
		}
		return hashToFiles;
	}
	
	/**
	 * initializes the map with the hash as key and list of filepath as value
	 */
	public static void init(String rootpath) {
		final TreeHash treeHash = new TreeHash();
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				System.out.println("hashing processed " + treeHash.getCount() + " files");
			}
			
		}, 7*1000, 5*1000);
		try {
			Files.walkFileTree(Paths.get(rootpath), treeHash);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.print("there was an error while walking the file tree");
		}
		timer.cancel();
		timer.purge();
	}
	
	public static class HashKey {
		private final byte[] hashInBytes;
		public HashKey(byte[] hashInBytes) {
			if (hashInBytes == null)
	        {
	            throw new NullPointerException();
	        }
			this.hashInBytes = hashInBytes;
		}
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof HashKey)) {
				return false;
			}
			HashKey hashKey = (HashKey) obj;
			return Arrays.equals(this.hashInBytes, hashKey.hashInBytes);
		}
		@Override
		public int hashCode() {
			return Arrays.hashCode(hashInBytes);
		}
		public byte[] getHashInBytes() {
			return hashInBytes;
		}
		
	}
}
