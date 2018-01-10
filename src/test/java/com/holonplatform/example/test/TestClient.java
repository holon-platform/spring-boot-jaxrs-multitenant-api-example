package com.holonplatform.example.test;

import static com.holonplatform.example.Product.DESCRIPTION;
import static com.holonplatform.example.Product.PRODUCT;
import static com.holonplatform.example.Product.SKU;

import java.net.URI;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.http.rest.RequestEntity;
import com.holonplatform.http.rest.RestClient;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class TestClient {

	@LocalServerPort
	private int serverPort;

	@Test
	public void testMultiTenancy() {

		RestClient client = RestClient.forTarget("http://localhost:" + serverPort + "/api/");

		final PropertyBox product1 = PropertyBox.builder(PRODUCT).set(DESCRIPTION, "Product 1")
				.set(SKU, "tenant1-product1").build();

		// [tenant1] add using POST
		URI location = client.request().path("products") //
				.header("X-TENANT-ID", "tenant1") // set custom tenant header
				.postForLocation(RequestEntity.json(product1)).orElseThrow(() -> new RuntimeException("Missing URI"));

		// [tenant1] get the product
		PropertyBox created = client.request().target(location) //
				.header("X-TENANT-ID", "tenant1") // set custom tenant header
				.propertySet(PRODUCT).getForEntity(PropertyBox.class)
				.orElseThrow(() -> new RuntimeException("Missing product"));

		Assert.assertEquals("tenant1-product1", created.getValue(SKU));

		// [tenant2] get all products
		List<PropertyBox> values = client.request().path("products") //
				.header("X-TENANT-ID", "tenant2") // set custom tenant header
				.propertySet(PRODUCT).getAsList(PropertyBox.class);

		Assert.assertEquals(1, values.size());

		final PropertyBox product2 = PropertyBox.builder(PRODUCT).set(DESCRIPTION, "Product 1")
				.set(SKU, "tenant2-product1").build();

		// [tenant2] add using POST
		location = client.request().path("products") //
				.header("X-TENANT-ID", "tenant2") // set custom tenant header
				.postForLocation(RequestEntity.json(product2)).orElseThrow(() -> new RuntimeException("Missing URI"));

		// [tenant2] get the product
		created = client.request().target(location) //
				.header("X-TENANT-ID", "tenant2") // set custom tenant header
				.propertySet(PRODUCT).getForEntity(PropertyBox.class)
				.orElseThrow(() -> new RuntimeException("Missing product"));

		Assert.assertEquals("tenant2-product1", created.getValue(SKU));

	}

}
