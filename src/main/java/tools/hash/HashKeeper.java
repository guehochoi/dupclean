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
			
		}, 10*1000, 10*1000);
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
		private final byte[] hashByteSHA1;
		private final byte[] hashByteMD5;
		public HashKey(byte[] hashByteSHA1, byte[] hashByteMD5) {
			if (hashByteSHA1 == null || hashByteMD5 == null)
	        {
	            throw new NullPointerException();
	        }
			this.hashByteSHA1 = hashByteSHA1;
			this.hashByteMD5 = hashByteMD5;
		}
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof HashKey)) {
				return false;
			}
			HashKey hashKey = (HashKey) obj;
			return Arrays.equals(this.hashByteSHA1, hashKey.hashByteSHA1) && Arrays.equals(this.hashByteMD5, hashKey.hashByteMD5);
		}
		@Override
		public int hashCode() {
			return Arrays.hashCode(hashByteSHA1) + Arrays.hashCode(hashByteMD5);
		}
		public byte[] getHashByteSHA1() {
			return hashByteSHA1;
		}
		public byte[] getHashByteMD5() {
			return hashByteMD5;
		}
	}
}
