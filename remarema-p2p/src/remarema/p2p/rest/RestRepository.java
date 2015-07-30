package remarema.p2p.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import remarema.client.filerepository.FileInfo;
import remarema.client.filerepository.FileRepository;


/**
 * Die Klasse <code>Repository</code> dient zum Auslesen von Files und Verzeichnissen bzw. deren Unterverzeichnissen.
 * 
 * @author Rebecca van Langelaan
 * 
 */
public class RestRepository {

	private static final Logger log = Logger.getLogger("RestRepository");
	public String DIRECTORY = "C:\\Users\\rpci343\\server_testordner";
	File file = new File(DIRECTORY);
	private FileRepository repository = new FileRepository(file);
	private String hostName;

	public RestRepository() {

	}

	public RestRepository(String hostName) {
		this.hostName = hostName;

	}

	public String getHostName() {
		return hostName;
	}

	/**
	 * Listet alle Files und Unterverzeichnisse auf.
	 * 
	 * @return
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * 
	 */
	@GET
	@Produces("application/xml")
	@Path("/")
	public Response listFiles() throws NoSuchAlgorithmException, IOException {
		return listFilesFromSubdirectory("/");
	}

	@GET
	@Path("/{subdirectory:.*}")
	public Response listFilesFromSubdirectory(@PathParam("subdirectory") String subdirectory)	throws NoSuchAlgorithmException,
																								IOException {
		final File currentFile = repository.makeFileFromPath(subdirectory);
		if (currentFile.isDirectory()) {
			return buildDirectoryResponse(subdirectory);
		}
		// ok, der benutzer hat eine Datei eingegeben, oder er hat sich vertippt und die Datei existiert gar nicht.
		if (currentFile.exists()) {
			return buildStreamResponse(currentFile);
		}
		// Existiert die angebebene Datei nicht, wird der Status-Code 404 für "Not Found" zurückgegeben.
		log.warning("not found:" + currentFile);
		return Response.status(404).entity(subdirectory).build();
	}

	private Response buildStreamResponse(final File currentFile) {
		StreamingOutput output = new StreamingOutput() {

			@Override
			public void write(OutputStream outputStream) throws IOException {
				copyFileToDestination(currentFile, outputStream);
			}
		};
		// existiert die Datei, wird mit "ok" der Status-Code 200 zurückgegeben.
		return Response.ok(output, MediaType.APPLICATION_OCTET_STREAM).build();
	}

	private Response buildDirectoryResponse(String subdirectory) throws NoSuchAlgorithmException, IOException {
		List<FileInfo> x = repository.listFiles(subdirectory);
		GenericEntity<List<FileInfo>> entity = new GenericEntity<List<FileInfo>>(x) {
			//
		};
		return Response.ok(entity, MediaType.APPLICATION_XML).build();
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

	void copyFileToDestination(File inputFile, OutputStream destination) throws IOException {
		FileInputStream inputStream = createInputStream(inputFile);
		try {
			byte[] buffer = new byte[1024];

			int len = inputStream.read(buffer);
			while (len != -1) {

				destination.write(buffer, 0, len);
				len = inputStream.read(buffer);
			}
			destination.flush();
		} catch (IOException e) {
			String message = "Beim Kopieren der Datei ist ein Fehler aufgetreten";
			log.log(Level.SEVERE, message);
			throw new IOException(message, e);
		} finally {
			closeInputStream(inputStream);
		}
	}

}
