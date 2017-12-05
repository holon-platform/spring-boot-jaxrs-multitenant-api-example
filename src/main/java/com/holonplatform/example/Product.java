package com.holonplatform.example;

import com.holonplatform.core.Validator;
import com.holonplatform.core.datastore.DataTarget;
import com.holonplatform.core.property.PathProperty;
import com.holonplatform.core.property.PropertySet;

/**
 * Product model
 */
public interface Product {

	static final PathProperty<Long> ID = PathProperty.create("id", Long.class);

	static final PathProperty<String> SKU = PathProperty.create("sku", String.class);

	static final PathProperty<String> DESCRIPTION = PathProperty.create("description", String.class);

	static final PathProperty<String> CATEGORY = PathProperty.create("category", String.class);

	static final PathProperty<Double> UNIT_PRICE = PathProperty.create("price", Double.class)
			// not negative value validator
			.validator(Validator.notNegative());

	// Product property set
	static final PropertySet<?> PRODUCT = PropertySet.of(ID, SKU, DESCRIPTION, CATEGORY, UNIT_PRICE);

	// "products" table DataTarget
	static final DataTarget<String> TARGET = DataTarget.named("products");

}
