package at.arz.remarema.rest;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/rest")
public class ResourceCatalog
		extends Application {

	public Set<java.lang.Class<?>> getClasses() {
		return new HashSet<Class<?>>(Arrays.asList(RootResource.class));
	};
}
