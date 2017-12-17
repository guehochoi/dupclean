package tools.stream;

import java.io.Closeable;
import java.io.IOException;

public class ResourceManagementHelper {
	public static void closeQuietly(Closeable resource) {
		if (resource == null) 
			return;
		try {
			resource.close();
		} catch (IOException e) {
			System.out.println("error occured while closing " + resource.toString() + ": " + e.getLocalizedMessage());
		}
	}
}
