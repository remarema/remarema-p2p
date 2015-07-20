package remarema.p2p;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

public class Hello {

	@GET
	@Produces(MediaType.TEXT_XML)
	@Path("/xml")
	public String sayXMLHello() {
		return "<?xml version=\"1.0\"?>" + "<hello> Hello Jersey" + "</hello>";
	}

	@GET
	@Produces(MediaType.TEXT_HTML)
	@Path("/html")
	public String sayHtmlHello() {
		return "<html> " + "<title>"
				+ "Hello Jersey"
				+ "</title>"
				+ "<body><h1>"
				+ "Hello Jersey"
				+ "</body></h1>"
				+ "</html> ";
	}

	@Path("/txt")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String sayPlainTextHello() {
		return "Hello Jersey";
	}

	@Path("/x")
	@GET
	@Produces(MediaType.TEXT_XML)
	public FileInfo getInfo() {
		FileInfo info = new FileInfo();
		info.setDirectory(false);
		info.setName("sample");
		return info;
	}

}
