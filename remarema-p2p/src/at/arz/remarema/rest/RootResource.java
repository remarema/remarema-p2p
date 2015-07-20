package at.arz.remarema.rest;

import javax.ws.rs.Path;

import remarema.p2p.Hello;


public class RootResource {

	@Path("/hello")
	public Hello helloWorld() {
		return new Hello();
	}

	@Path("/repository")
	public Repository repository() {
		return new Repository();
	}

}
