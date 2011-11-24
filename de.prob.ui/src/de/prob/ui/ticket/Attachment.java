package de.prob.ui.ticket;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

public class Attachment {

	int ticketId;
	String filename;
	IPath filepath;
	String description;
	byte[] data;
	
	public int getTicketId() {
		return ticketId;
	}

	public void setTicketId(int ticketId) {
		this.ticketId = ticketId;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	public IPath getFilepath() {
		return filepath;
	}
	
	public String getDescription() {
		return this.description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

	public byte[] getData() {
		byte[] res = new byte[data.length];
		System.arraycopy(data, 0, res, 0, data.length);
		return res;
	}
	
	
	public Attachment(String filepath, String description) throws IOException {
		this.ticketId = 0;
		this.filename = "untitled";
		this.filepath = new Path(filepath);
		this.description = description;
		InputStream in = new FileInputStream(filepath);
		this.data = readData(in);
	}
	
	private byte[] readData(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			byte[] buffer = new byte[512];
			while (true) {
				int count = in.read(buffer);
				if (count == -1) {
					return out.toByteArray();
				}
				out.write(buffer, 0, count);
			}
		} finally {
			in.close();
		}
	}
	
}
