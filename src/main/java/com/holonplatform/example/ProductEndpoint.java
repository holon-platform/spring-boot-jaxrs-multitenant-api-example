package com.holonplatform.example;

import static com.holonplatform.example.Product.ID;
import static com.holonplatform.example.Product.PRODUCT;
import static com.holonplatform.example.Product.TARGET;

import java.net.URI;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySetRef;

@Component
@Path("/api")
public class ProductEndpoint {

	@Autowired
	private Datastore datastore;

	/*
	 * Get a list of products PropertyBox in JSON.
	 */
	@GET
	@Path("/products")
	@Produces(MediaType.APPLICATION_JSON)
	public List<PropertyBox> getProducts() {
		return datastore.query().target(TARGET).list(PRODUCT);
	}

	/*
	 * Get a product PropertyBox in JSON.
	 */
	@GET
	@Path("/products/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getProduct(@PathParam("id") Long id) {
		return datastore.query().target(TARGET).filter(ID.eq(id)).findOne(PRODUCT).map(p -> Response.ok(p).build())
				.orElse(Response.status(Status.NOT_FOUND).build());
	}

	/*
	 * Create a product. The @PropertySetRef must be used to declare the request
	 * PropertyBox property set.
	 */
	@POST
	@Path("/products")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addProduct(@PropertySetRef(Product.class) PropertyBox product) {
		// set id
		long nextId = datastore.query().target(TARGET).findOne(ID.max()).orElse(0L) + 1;
		product.setValue(ID, nextId);
		// save
		datastore.save(TARGET, product);
		return Response.created(URI.create("/api/products/" + nextId)).build();
	}

	/*
	 * Update a product. The @PropertySetRef must be used to declare the request
	 * PropertyBox property set.
	 */
	@PUT
	@Path("/products/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateProduct(@PropertySetRef(Product.class) PropertyBox product) {
		return datastore.query().target(TARGET).filter(ID.eq(product.getValue(ID))).findOne(PRODUCT).map(p -> {
			datastore.save(TARGET, product);
			return Response.noContent().build();
		}).orElse(Response.status(Status.NOT_FOUND).build());
	}

	/*
	 * Delete a product by id.
	 */
	@DELETE
	@Path("/products/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteProduct(@PathParam("id") Long id) {
		datastore.bulkDelete(TARGET).filter(ID.eq(id)).execute();
		return Response.noContent().build();
	}

}
