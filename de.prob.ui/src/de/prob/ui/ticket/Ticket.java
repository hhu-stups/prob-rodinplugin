package de.prob.ui.ticket;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

public class Ticket {

	private static final String ADDRESS = "http://cobra.cs.uni-duesseldorf.de/trac/xmlrpc";
	XmlRpcClient client = new XmlRpcClient();
	XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();

	int id;
	String summary;
	String type;
	String description;
	String reporter;
	String priority;
	String component;
	String version;
	String keywords;
	String cc;
	List<Attachment> attachments;
	String sensitive;

	public Ticket(String reporter, String summary, String cc,
			String description, Boolean sensitive) {

		this.sensitive = sensitive ? "1" : "0";
		this.id = 0;
		this.summary = summary;
		this.type = "defect";
		this.description = description;
		this.reporter = reporter;
		this.priority = "major";
		this.component = "Eclipse Plugin";
		this.version = ""; // get current version!?
		this.keywords = "user discovered bug";
		this.cc = cc;
		this.attachments = new ArrayList<Attachment>();

		// Configuration
		try {
			this.config.setServerURL(new URL(ADDRESS));
			// this.config.setBasicUserName(USER_NAME);
			// this.config.setBasicPassword(PASSWORD);
			this.config.setConnectionTimeout(60 * 1000);
			this.config.setReplyTimeout(3 * 60 * 1000);

			this.client.setConfig(this.config);

		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	public void addAttachment(Attachment a) {
		this.attachments.add(a);
	}

	public void send() throws XmlRpcException {

		// "ticket.create"
		String methodName = "ticket.create";

		Hashtable<String, Object> attributes = new Hashtable<String, Object>();
		attributes.put("reporter", this.reporter);
		attributes.put("type", this.type);
		attributes.put("priority", this.priority);
		attributes.put("component", this.component);
		attributes.put("version", this.version);
		attributes.put("keywords", this.keywords);
		attributes.put("sensitive", this.sensitive);
		attributes.put("cc", this.cc);

		Object[] params = new Object[] { this.summary, this.description,
				attributes, true };
		// "true" means notify user by e-mail to affirm ticket's creation

		Integer ticketID;
		ticketID = (Integer) this.client.execute(methodName, params);
		this.id = ticketID.intValue();

		// "ticket.putAttachment"
		String methodName2 = "ticket.putAttachment";

		for (Attachment a : this.attachments) {
			// put single attachment to the ticket created even
			String filename = a.getFilename();
			String path = a.getFilepath().toOSString();
			String attachmentDescription = a.getDescription();
			InputStream in;

			try {
				in = new FileInputStream(path);
				byte[] data;
				data = readData(in); // -> IOException
				Object[] params2 = new Object[] { this.id, filename,
						attachmentDescription, data, true };
				String result;
				result = (String) this.client.execute(methodName2, params2);
				System.out.println("put Attachment " + result
						+ " to ticket ID " + this.id + ".");
			} catch (XmlRpcException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

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
