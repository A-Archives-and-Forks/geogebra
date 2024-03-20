package org.geogebra.common.properties.factory;

import java.util.List;

import org.geogebra.common.properties.Property;

/**
 * Holds a reference to the array of the properties and to the name of this properties collection.
 */
public class PropertiesArray {

	private String name;
	private Property[] properties;

	/**
	 * @param name name
	 * @param properties properties
	 */
	public PropertiesArray(String name, Property... properties) {
		this.name = name;
		this.properties = properties;
	}

	public PropertiesArray(String name, List<Property> properties) {
		this.name = name;
		this.properties = properties.toArray(new Property[0]);
	}

	public String getName() {
		return name;
	}

	public Property[] getProperties() {
		return properties;
	}
}
