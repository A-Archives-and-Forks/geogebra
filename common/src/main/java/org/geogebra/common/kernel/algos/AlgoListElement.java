/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.Inspecting;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.geos.TestGeo;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.util.debug.Log;

/**
 * n-th element of a GeoList object.
 * 
 * Note: the type of the returned GeoElement object is determined by the type of
 * the first list element. If the list is initially empty, a GeoNumeric object
 * is created for element.
 * 
 * @author Markus Hohenwarter
 * @version 15-07-2007
 */

public class AlgoListElement extends AlgoElement {

	private GeoList geoList; // input
	private GeoNumberValue num;
	private GeoNumberValue[] indexes = null; // input
	private final boolean topLevelCommand;
	private GeoElement numGeo;
	private GeoElement element; // output
	private String elementLabel;

	/**
	 * Creates new labeled element algo
	 *
	 * @param cons
	 *            construction
	 * @param geoList
	 *            haystack
	 * @param num
	 *            index
	 */
	public AlgoListElement(Construction cons, GeoList geoList,
			GeoNumberValue num) {
		super(cons);
		this.geoList = geoList;
		this.num = num;
		numGeo = num.toGeoElement();
		topLevelCommand = false;

		element = createElementForList(geoList, getInitIndex(num));

		if (element.isGeoPolygon()) { // ensure type will not be sticked to e.g.
										// "triangle"
			((GeoPolygon) element).setNotFixedPointsLength(true);
		}

		setInputOutput();
		compute();
	}

	private GeoElement createElementForList(GeoElement source, int index) {
		if (source == null || !source.isGeoList()) {
			return createDefaultElement();
		}

		GeoList list = ((GeoList) source);
		if (index < list.size()) {
			return getGenericElement(list, index).copyInternal(cons);
		}

		if (!list.isEmptyList()) {
			return getGenericElement(list, 0).copyInternal(cons);
		}

		if (list.getTypeStringForXML() != null) {
			return kernel.createGeoElement(cons, list.getTypeStringForXML());
		}

		return createDefaultElement();
	}

	private GeoElement createDefaultElement() {
		return topLevelCommand ? new GeoNumeric(cons) : cons.getOutputGeo();
	}

	private static int getInitIndex(GeoNumberValue number) {
		return Math.max(0, (int) Math.round(number.getDouble()) - 1);
	}

	private static GeoElement getGenericElement(GeoList geoList, int index) {
		GeoElement toCopy = geoList.get(index);
		if (geoList.getElementType() == GeoClass.DEFAULT
		// we have list {2,x}, not eg Factors[2x]
				&& (geoList.getParentAlgorithm() == null || geoList
						.getParentAlgorithm() instanceof AlgoDependentList)
				// for {a,x} also return number a, not function
				&& !Inspecting.dynamicGeosFinder.check(toCopy)) {
			for (int i = 0; i < geoList.size(); i++) {
				if (TestGeo.canSet(geoList.get(i), toCopy)) {
					toCopy = geoList.get(i);
				}
			}
		}
		return toCopy;
	}

	/**
	 * @param cons
	 *            construction
	 * @param geoList
	 *            list
	 * @param nums
	 *            element coordinates
	 */
	public AlgoListElement(Construction cons, GeoList geoList,
			GeoNumberValue[] nums) {
		super(cons);
		this.geoList = geoList;
		this.indexes = nums;
		this.topLevelCommand = geoList.isLabelSet();

		element = null;
		GeoElement parent = null;
		GeoElement current = geoList;
		int depth = 0;
		try {
			do {
				parent = current;
				current = getNthElement(current, depth);
				depth++;
			} while (current.isGeoList() && depth < maxDepth());
			element = depth == maxDepth() - 1 ? current.copyInternal(cons)
					: createElementForList(parent, depth);
		} catch (Exception e) {
			Log.debug("error initialising list");
		}

		// desperate case: empty list, or malformed 2D array
		if (element == null) {
			// saved in XML from 4.0.18.0
			element = cons.getOutputGeo();
		}
		setInputOutput();
		compute();

	}

	private int maxDepth() {
		return indexes.length;
	}

	private GeoElement getNthElement(GeoElement current, int depth) {
		int initIndex = getInitIndex(indexes[depth]);
		GeoList currentList = (GeoList) current;
		// init return element as copy of initIndex list element
		if (currentList.size() > initIndex) {
			// create copy of initIndex GeoElement in list
			current = getCurrent(depth, currentList, initIndex);
		}

		// if not enough elements in list:
		// init return element as copy of first list element
		else if (geoList.size() > 0) {
			// create copy of first GeoElement in list
			current = getCurrent(depth, currentList, 0);
		}
		return current;
	}

	private GeoElement getCurrent(int depth, GeoList currentList, int initIndex) {
		return depth == maxDepth() - 1
				? getGenericElement(currentList, initIndex)
				: currentList.get(initIndex);
	}

	@Override
	public Commands getClassName() {
		return Commands.Element;
	}

	@Override
	protected void setInputOutput() {
		if (isFlatList()) {
			setInputForFlatList();
		} else {
			setInputForListOfLists();
		}

		setOnlyOutput(element);
		setDependencies(); // done by AlgoElement
	}

	private boolean isFlatList() {
		return indexes == null;
	}

	private void setInputForFlatList() {
		input = new GeoElement[2];
		input[0] = geoList;
		input[1] = numGeo;
	}

	private void setInputForListOfLists() {
		input = new GeoElement[maxDepth() + 1];
		input[0] = geoList;
		for (int i = 0; i < maxDepth(); i++) {
			input[i + 1] = indexes[i].toGeoElement();
		}
	}

	/**
	 * Returns chosen element
	 * 
	 * @return chosen element
	 */
	public GeoElement getElement() {
		return element;
	}

	@Override
	public final void compute() {
		if ((numGeo != null && !numGeo.isDefined()) || !geoList.isDefined()) {
			element.setUndefined();
			return;
		}

		if (isFlatList()) {
			// index of wanted element
			int n = (int) Math.round(num.getDouble()) - 1;
			if (n >= 0 && n < geoList.size()) {
				GeoElement nth = geoList.get(n);
				setElement(nth);
			} else {
				element.setUndefined();
			}

		} else {

			for (int k = 0; k < maxDepth(); k++) {
				if (!indexes[k].toGeoElement().isDefined()) {
					element.setUndefined();
					return;
				}
			}

			int m = (int) Math.round(indexes[maxDepth() - 1].getDouble()) - 1;
			GeoElement current = geoList;
			for (int k = 0; k < maxDepth() - 1; k++) {
				int index = (int) Math.round(indexes[k].getDouble() - 1);
				if (index >= 0 && current.isGeoList()
						&& index < ((GeoList) current).size()) {
					current = ((GeoList) current).get(index);
				} else {
					element.setUndefined();
					return;
				}
			}
			if (!(current instanceof GeoList)) { // not deep enough
				element.setUndefined();
				return;
			}
			GeoList list = (GeoList) current;

			if (m >= 0 && m < list.size()) {
				current = list.get(m);
			} else {
				element.setUndefined();
				return;
			}

			setElement(current);

		}
	}

	private void setElement(GeoElement nth) {
		// check type:
		if (nth.getGeoClassType() == element.getGeoClassType()
				|| TestGeo.canSet(element, nth)) {
			element.set(nth);
			elementLabel = nth.getLabel(StringTemplate.realTemplate);
			if (nth.getDrawAlgorithm() instanceof DrawInformationAlgo) {
				element.setDrawAlgorithm(
						((DrawInformationAlgo) nth.getDrawAlgorithm()).copy());
			}

		} else {
			element.setUndefined();
		}

	}

	/**
	 * So that Name(Element(list1,1)) works
	 * 
	 * @return label
	 */
	public String getLabel() {
		return elementLabel;
	}

	/*
	 * @Override public String getCommandDescription(StringTemplate tpl,boolean
	 * real) {
	 * 
	 * return super.getCommandDescription(tpl,real);
	 * 
	 * TODO re enable this for shortSyntax flag true for 5.0 sb.setLength(0);
	 * 
	 * 
	 * int length = input.length;
	 * 
	 * sb.append(geoList.getLabel()+"("); // input
	 * sb.append(real?input[1].getRealLabel():input[1].getLabel()); // Michael
	 * Borcherds 2008-05-15 added input.length>0 for Step[] for (int i = 2; i <
	 * length; ++i) { sb.append(", "); sb.append(real?
	 * input[i].getRealLabel():input[i].getLabel()); } sb.append(")"); return
	 * sb.toString();
	 * 
	 * }
	 */

}
