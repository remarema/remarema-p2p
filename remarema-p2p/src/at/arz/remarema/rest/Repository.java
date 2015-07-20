package at.arz.remarema.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import remarema.p2p.FileInfo;
import remarema.p2p.FileRepository;

public class Repository {

	public String DIRECTORY = "\\\\A8600681\\!homelw\\rpci343\\Documents";
	File file = new File(DIRECTORY);
	private FileRepository repository = new FileRepository(file);


	@GET
	@Produces("application/xml")
	@Path("/filelist")
	public Response listFiles() {
		return listFilesFromSubdirectory("/");
	}

	@GET
	@Path("/filelist/{subdirectory:.*}")
	public Response listFilesFromSubdirectory(@PathParam("subdirectory") String subdirectory) {
		final File currentFile = repository.makeFileFromPath(subdirectory);
		if (currentFile.isDirectory()) {
			List<FileInfo> x = repository.listFiles(subdirectory);
			GenericEntity<List<FileInfo>> entity = new GenericEntity<List<FileInfo>>(x) {
				//
			};
			return Response.ok(entity, MediaType.APPLICATION_XML).build();
		}
		// ok, der benutzer hat eine Datei eingegeben, oder er hat sich vertippt und die Datei existiert gar nicht.
		if (currentFile.exists()) {
			StreamingOutput output = new StreamingOutput() {

				@Override
				public void write(OutputStream outputStream) throws IOException {
					FileInputStream inputStream = null;
					try {
						inputStream = createInputStream(currentFile);
						byte[] buffer = new byte[1024];
						int rc = inputStream.read();
						while (rc != -1) {
							outputStream.write(buffer, 0, rc);
							rc = inputStream.read(buffer);
						}
					} catch (FileNotFoundException e) {
						System.out.println("File " + currentFile.getAbsolutePath()
											+ " could not be found on filesystem");
					} catch (IOException e) {
						System.out.println("Exception while reading the file" + e);
					} finally {
						closeInputStream(inputStream);
					}
				}
			};
			return Response.ok(output, MediaType.APPLICATION_OCTET_STREAM).build();
		}
		System.out.println("not found:" + currentFile);
		return Response.status(404).entity(subdirectory).build();
	}

	FileInputStream createInputStream(final File file) throws FileNotFoundException {
		return new FileInputStream(file);
	}

	void closeInputStream(FileInputStream inputStream) {
		try {
			inputStream.close();
		} catch (IOException e) {
			String msg = "InputStream konnte nicht geschlossen werden!";
			throw new RuntimeException(msg, e);
		}

	}
}
