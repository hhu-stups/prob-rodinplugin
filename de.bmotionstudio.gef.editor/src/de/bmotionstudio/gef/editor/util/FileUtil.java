/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class FileUtil {

	/** Fast & simple file copy. */
	public static void copyFile(File source, File dest) throws IOException {
		FileChannel in = null, out = null;
		try {
			in = new FileInputStream(source).getChannel();
			out = new FileOutputStream(dest).getChannel();

			long size = in.size();
			MappedByteBuffer buf = in.map(FileChannel.MapMode.READ_ONLY, 0,
					size);

			out.write(buf);
		} finally {
			if (in != null)
				in.close();
			if (out != null)
				out.close();
		}
	}

	public static void deleteFile(File f) {
		// Attempt to delete it
		boolean success = f.delete();
		if (!success)
			throw new IllegalArgumentException("Delete: deletion failed");
	}

}
