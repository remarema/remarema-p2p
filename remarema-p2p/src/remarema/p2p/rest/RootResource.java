package remarema.p2p.rest;

import javax.ws.rs.Path;


public class RootResource {

	@Path("/repository")
	public RestRepository restRepository() {
		return new RestRepository();
	}

}
