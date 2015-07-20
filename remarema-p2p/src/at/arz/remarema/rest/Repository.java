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


/**
 * Die Klasse <code>Repository</code> dient zum Auslesen von Files und Verzeichnissen bzw. deren Unterverzeichnissen.
 * 
 * @author Rebecca van Langelaan
 * 
 */
public class Repository {

	public String DIRECTORY = "\\\\A8600681\\!homelw\\rpci343\\Documents";
	File file = new File(DIRECTORY);
	private FileRepository repository = new FileRepository(file);


	/**
	 * Listet alle Files und Unterverzeichnisse auf.
	 * 
	 * @return
	 */
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
			// existiert die Datei, wird mit "ok" der Status-Code 200 zurückgegeben.
			return Response.ok(output, MediaType.APPLICATION_OCTET_STREAM).build();
		}
		// Existiert die angebebene Datei nicht, wird der Status-Code 404 für "Not Found" zurückgegeben.
		System.out.println("not found:" + currentFile);
		return Response.status(404).entity(subdirectory).build();
	}

	/**
	 * Diese Methode erstellt einen neuen FileInputStream und liefert diesen zurück.
	 * 
	 * @param file
	 * @return FileInputStream
	 * @throws FileNotFoundException
	 */
	FileInputStream createInputStream(final File file) throws FileNotFoundException {
		return new FileInputStream(file);
	}

	/**
	 * Die Methode <code>closeInputStream</code> dient zum Schließen eines InputStreams.
	 * 
	 * @param inputStream
	 */
	void closeInputStream(FileInputStream inputStream) {
		try {
			inputStream.close();
		} catch (IOException e) {
			String msg = "InputStream konnte nicht geschlossen werden!";
			throw new RuntimeException(msg, e);
		}

	}
}
