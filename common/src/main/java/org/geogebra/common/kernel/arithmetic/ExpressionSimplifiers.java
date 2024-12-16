package org.geogebra.common.kernel.arithmetic;

import java.util.List;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.simplifiers.CancelGCDInFraction;
import org.geogebra.common.kernel.arithmetic.simplifiers.FactorOut;
import org.geogebra.common.kernel.arithmetic.simplifiers.PositiveDenominator;
import org.geogebra.common.kernel.arithmetic.simplifiers.ReduceRoot;
import org.geogebra.common.kernel.arithmetic.simplifiers.ReduceToIntegers;
import org.geogebra.common.kernel.arithmetic.simplifiers.SimplifyMultiplication;
import org.geogebra.common.kernel.arithmetic.simplifiers.SimplifyNode;
import org.geogebra.common.kernel.arithmetic.simplifiers.SimplifyToRadical;
import org.geogebra.common.util.debug.Log;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public class ExpressionSimplifiers {

	private final List<SimplifyNode> preItems;
	private final List<SimplifyNode> postItems;

	public ExpressionSimplifiers(@NonNull SimplifyUtils utils) {
		ReduceToIntegers reduceToIntegers = new ReduceToIntegers(utils);
		preItems = List.of(
				reduceToIntegers
		);

		postItems = List.of(
						new SimplifyToRadical(utils),
						new ReduceRoot(utils),
						new SimplifyMultiplication(utils),
						new PlusTagOrder(utils),
						new FactorOut(utils),
						new CancelGCDInFraction(utils),
						new PositiveDenominator(utils),
						new OperandOrder(utils), reduceToIntegers
				);
	}

	public ExpressionNode run(@Nullable ExpressionValue resolution) {
		if (resolution == null) {
			return null;
		}

		return simplifyWith(postItems, resolution.wrap());
	}

	private ExpressionNode simplifyWith(List<SimplifyNode> simplifiers, ExpressionNode node) {
		for (SimplifyNode simplifier : simplifiers) {
			if (simplifier.isAccepted(node)) {
				String before = node.toValueString(StringTemplate.defaultTemplate);
				node = simplifier.apply(node);
				String after = node.toValueString(StringTemplate.defaultTemplate);
				if (!after.equals(before)) {
					Log.debug(simplifier.name() + ": " + after
							+ "(=" + node.evaluateDouble() + ")");
				}
			}
		}
		return node;
	}

	public ExpressionNode runFirst(ExpressionNode root) {
		return simplifyWith(preItems, root);
	}
}
