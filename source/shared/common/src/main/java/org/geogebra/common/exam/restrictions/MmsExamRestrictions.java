package org.geogebra.common.exam.restrictions;

import static org.geogebra.common.SuiteSubApp.CAS;
import static org.geogebra.common.SuiteSubApp.G3D;
import static org.geogebra.common.SuiteSubApp.GEOMETRY;
import static org.geogebra.common.SuiteSubApp.GRAPHING;
import static org.geogebra.common.SuiteSubApp.PROBABILITY;
import static org.geogebra.common.SuiteSubApp.SCIENTIFIC;
import static org.geogebra.common.exam.restrictions.visibility.VisibilityRestriction.Effect.HIDE;
import static org.geogebra.common.exam.restrictions.visibility.VisibilityRestriction.Effect.IGNORE;

import java.util.Set;

import javax.annotation.Nonnull;

import org.geogebra.common.contextmenu.AlgebraContextMenuItem;
import org.geogebra.common.contextmenu.ContextMenuItemFilter;
import org.geogebra.common.exam.ExamType;
import org.geogebra.common.exam.restrictions.visibility.HiddenInequalityVisibilityRestriction;
import org.geogebra.common.exam.restrictions.visibility.HiddenVectorVisibilityRestriction;
import org.geogebra.common.exam.restrictions.visibility.VisibilityRestriction;
import org.geogebra.common.kernel.arithmetic.filter.ComplexExpressionFilter;
import org.geogebra.common.kernel.arithmetic.filter.ExpressionFilter;
import org.geogebra.common.kernel.cas.AlgoIntegralDefinite;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.commands.selector.CommandFilter;
import org.geogebra.common.kernel.commands.selector.CommandFilterFactory;
import org.geogebra.common.kernel.commands.selector.CommandNameFilter;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoSymbolic;

public class MmsExamRestrictions extends ExamRestrictions {

	/**
	 * Restrictions for IQB MMS
	 */
	protected MmsExamRestrictions() {
		super(ExamType.MMS,
				Set.of(GRAPHING, GEOMETRY, G3D, PROBABILITY, SCIENTIFIC),
				CAS,
				createFeatureRestrictions(),
				createInputExpressionFilters(),
				createOutputExpressionFilters(),
				Set.of(createCommandFilter()),
				null,
				null,
				createContextMenuItemFilters(),
				null,
				null,
				null,
				createVisibilityRestrictions(),
				null,
				null);
	}

	private static Set<ExamFeatureRestriction> createFeatureRestrictions() {
		return Set.of(ExamFeatureRestriction.DATA_TABLE_REGRESSION,
				ExamFeatureRestriction.HIDE_SPECIAL_POINTS,
				ExamFeatureRestriction.SPREADSHEET,
				ExamFeatureRestriction.SURD,
				ExamFeatureRestriction.RATIONALIZATION);
	}

	private static Set<ExpressionFilter> createInputExpressionFilters() {
		return Set.of(new ComplexExpressionFilter());
	}

	private static Set<ExpressionFilter> createOutputExpressionFilters() {
		return Set.of(new ComplexExpressionFilter());
	}

	/**
	 * @return filer for IQB MMS exam
	 */
	public static CommandFilter createCommandFilter() {
		CommandNameFilter nameFilter = new CommandNameFilter(true);
		nameFilter.addCommands(Commands.Axes, Commands.Focus,
				Commands.DelauneyTriangulation, Commands.Difference,
				Commands.Rotate, Commands.TriangleCenter,
				Commands.Envelope, Commands.Ends,
				Commands.FirstAxisLength, Commands.SecondAxisLength,
				Commands.FirstAxis, Commands.Height, Commands.InteriorAngles,
				Commands.InverseBinomial, Commands.InverseBinomialMinimumTrials,
				Commands.InverseCauchy, Commands.InverseChiSquared,
				Commands.InverseExponential, Commands.InverseFDistribution,
				Commands.InverseGamma, Commands.InverseHyperGeometric,
				Commands.InverseLaplace, Commands.InverseLogistic, Commands.InverseLogNormal,
				Commands.InverseNormal, Commands.InversePascal, Commands.InversePoisson,
				Commands.InverseTDistribution, Commands.InverseWeibull, Commands.InverseZipf,
				Commands.Diameter, Commands.ConvexHull, Commands.ShortestDistance,
				Commands.CurveCartesian, Commands.Barycenter, Commands.MinimumSpanningTree,
				Commands.Center, Commands.SecondAxis, Commands.Net, Commands.Top,
				Commands.Surface, Commands.Prove, Commands.ProveDetails,
				Commands.Point, Commands.PointIn, Commands.IntersectPath,
				Commands.IntersectConic, Commands.Centroid, Commands.QuadricSide,
				Commands.Sector, Commands.Mirror, Commands.Spline,
				Commands.Stretch, Commands.Dilate, Commands.Bottom,
				Commands.Translate, Commands.Shear, Commands.Polygon,
				Commands.Arc, Commands.Circle, Commands.CircleSector,
				Commands.CircleArc, Commands.OsculatingCircle, Commands.Cubic,
				Commands.Polygon, Commands.Line, Commands.Segment, Commands.Ray,
				Commands.Ellipse, Commands.LineBisector, Commands.OrthogonalLine,
				Commands.Asymptote, Commands.RigidPolygon, Commands.Tangent,
				Commands.AngularBisector,
				Commands.Pyramid, Commands.Prism, Commands.Cone, Commands.Cylinder,
				Commands.Sphere, Commands.TriangleCurve, Commands.Semicircle,
				Commands.ImplicitCurve, Commands.Conic, Commands.Icosahedron, Commands.Hyperbola,
				Commands.Parabola, Commands.Incircle, Commands.Directrix, Commands.Octahedron,
				Commands.Locus, Commands.LocusEquation, Commands.Polar,
				Commands.PolyLine, Commands.ConeInfinite, Commands.CylinderInfinite,
				Commands.Tetrahedron, Commands.CircumcircleArc, Commands.CircumcircleSector,
				Commands.Cube, Commands.Roots, Commands.ComplexRoot, Commands.Root,
				Commands.RootList, Commands.Volume, Commands.Plane,
				Commands.OrthogonalPlane,
				Commands.PlaneBisector, Commands.Angle, Commands.Distance, Commands.Relation,
				Commands.IsInRegion, Commands.AffineRatio, Commands.Angle,
				Commands.AngleBisector, Commands.ANOVA, Commands.ApplyMatrix, Commands.Arc,
				Commands.Area, Commands.Asymptote,
				Commands.AttachCopyToView, Commands.Axes, Commands.AxisStepX, Commands.AxisStepY,
				Commands.Barycenter, Commands.Bernoulli, Commands.Bottom, Commands.Button,
				Commands.CASLoaded, Commands.Cauchy, Commands.Cell, Commands.CellRange,
				Commands.CenterView, Commands.Centroid, Commands.Checkbox, Commands.ChiSquared,
				Commands.ChiSquaredTest, Commands.Circle, Commands.CircularArc,
				Commands.CircleSector, Commands.CircumcircularArc, Commands.CircumcircularSector,
				Commands.Classes, Commands.ClosestPoint, Commands.ClosestPointRegion,
				Commands.Column, Commands.ColumnName, Commands.CompleteSquare, Commands.ComplexRoot,
				Commands.Cone, Commands.Conic, Commands.ConjugateDiameter,
				Commands.ConstructionStep, Commands.ContingencyTable, Commands.ContinuedFraction,
				Commands.ConvexHull, Commands.CopyFreeObject, Commands.Corner, Commands.CrossRatio,
				Commands.Cube, Commands.Cubic, Commands.Curvature, Commands.CurvatureVector,
				Commands.Curve, Commands.Cylinder, Commands.DelauneyTriangulation,
				Commands.Difference, Commands.Dilate, Commands.Direction, Commands.Directrix,
				Commands.Distance, Commands.Div, Commands.Divisors, Commands.DivisorsList,
				Commands.DivisorsSum, Commands.Dodecahedron, Commands.DotPlot,
				Commands.DynamicCoordinates, Commands.Eccentricity, Commands.Ellipse,
				Commands.Ends, Commands.Envelope, Commands.Erlang, Commands.Execute,
				Commands.Exponential, Commands.ExportImage, Commands.Extremum,
				Commands.FDistribution, Commands.FillCells, Commands.FillColumn, Commands.FillRow,
				Commands.Fit, Commands.FitExp, Commands.FitGrowth, Commands.FitImplicit,
				Commands.FitLineY, Commands.FitLineX, Commands.FitLog, Commands.FitLogistic,
				Commands.FitPoly, Commands.FitPow, Commands.FitSin, Commands.Focus,
				Commands.FormulaText, Commands.FractionText, Commands.FrequencyTable,
				Commands.FromBase, Commands.Function, Commands.FutureValue, Commands.Gamma,
				Commands.GeometricMean, Commands.GetTime, Commands.GroebnerDegRevLex,
				Commands.GroebnerLex, Commands.GroebnerLexDeg, Commands.HarmonicMean,
				Commands.Height, Commands.HideLayer, Commands.Hyperbola, Commands.Icosahedron,
				Commands.ImplicitCurve, Commands.ImplicitDerivative, Commands.Incircle,
				Commands.InfiniteCone, Commands.TurningPoint, Commands.InputBox,
				Commands.IntegralBetween, Commands.InteriorAngles, Commands.Intersect,
				Commands.IntersectConic, Commands.IntersectPath, Commands.InverseBinomial,
				Commands.InverseCauchy, Commands.InverseChiSquared, Commands.InverseExponential,
				Commands.InverseFDistribution, Commands.InverseGamma,
				Commands.InverseHyperGeometric, Commands.InverseLogistic,
				Commands.InverseLogNormal, Commands.InverseNormal, Commands.InversePascal,
				Commands.InversePoisson, Commands.InverseTDistribution, Commands.InverseWeibull,
				Commands.InverseZipf, Commands.IsDefined, Commands.IsFactored, Commands.IsInRegion,
				Commands.IsInteger, Commands.IsPrime, Commands.IsVertexForm,
				Commands.Iteration, Commands.IterationList, Commands.Laplace, Commands.LeftSum,
				Commands.Length, Commands.LetterToUnicode, Commands.Line,
				Commands.Excentricity, Commands.Locus, Commands.LocusEquation,
				Commands.Logistic, Commands.LogNormal, Commands.LowerSum, Commands.mad,
				Commands.MajorAxis, Commands.Maximize, Commands.Midpoint, Commands.Minimize,
				Commands.MinimumSpanningTree, Commands.MinorAxis, Commands.Name, Commands.Net,
				Commands.NextPrime, Commands.NormalQuantilePlot, Commands.NSolveODE,
				Commands.Object, Commands.Octahedron, Commands.Ordinal, Commands.OsculatingCircle,
				Commands.Pan, Commands.Parabola, Commands.Parameter, Commands.ParametricDerivative,
				Commands.ParseToFunction, Commands.ParseToNumber, Commands.Pascal,
				Commands.PathParameter, Commands.Payment, Commands.Perimeter, Commands.Periods,
				Commands.PerpendicularBisector, Commands.PerpendicularLine,
				Commands.OrthogonalVector, Commands.Plane, Commands.PlaneBisector,
				Commands.PlaySound, Commands.PlotSolve, Commands.Point, Commands.PointIn,
				Commands.PointList, Commands.Poisson, Commands.Polar, Commands.Polygon,
				Commands.Polyline, Commands.Polynomial, Commands.PresentValue,
				Commands.PreviousPrime, Commands.Prism, Commands.Prove, Commands.ProveDetails,
				Commands.Pyramid, Commands.Radius, Commands.RandomBinomial,
				Commands.RandomDiscrete, Commands.RandomNormal, Commands.RandomPointIn,
				Commands.RandomPoisson, Commands.RandomPolynomial, Commands.RandomUniform,
				Commands.Rate, Commands.Rationalize, Commands.Ray, Commands.ReadText,
				Commands.RectangleSum, Commands.ReducedRowEchelonForm, Commands.Reflect,
				Commands.Relation, Commands.RemovableDiscontinuity, Commands.Rename,
				Commands.Repeat, Commands.ResidualPlot, Commands.RigidPolygon, Commands.Root,
				Commands.RootList, Commands.RootMeanSquare, Commands.Roots, Commands.Rotate,
				Commands.RotateText, Commands.Row, Commands.RunClickScript,
				Commands.RunUpdateScript, Commands.SampleSD, Commands.stdev, Commands.SampleSDX,
				Commands.SampleSDY, Commands.SampleVariance, Commands.ScientificText, Commands.SD,
				Commands.stdevp, Commands.SDX, Commands.SDY, Commands.Sector, Commands.Segment,
				Commands.SelectedElement, Commands.SelectedIndex, Commands.Semicircle,
				Commands.SemiMajorAxisLength, Commands.SemiMinorAxisLength, Commands.SetActiveView,
				Commands.SetAxesRatio, Commands.SetBackgroundColor, Commands.SetCaption,
				Commands.SetColor, Commands.SetConditionToShowObject, Commands.SetConstructionStep,
				Commands.SetCoords, Commands.SetDecoration, Commands.SetDynamicColor,
				Commands.SetFilling, Commands.SetFixed, Commands.SetImage, Commands.SetLabelMode,
				Commands.SetLayer, Commands.SetLevelOfDetail, Commands.SetLineStyle,
				Commands.SetLineThickness, Commands.SetPerspective, Commands.SetPointSize,
				Commands.SetPointStyle, Commands.SetSeed, Commands.SetSpinSpeed,
				Commands.SetTooltipMode, Commands.SetTrace, Commands.SetValue,
				Commands.SetViewDirection, Commands.SetVisibleInView, Commands.Shear,
				Commands.ShortestDistance, Commands.ShowAxes, Commands.ShowGrid,
				Commands.ShowLabel, Commands.ShowLayer, Commands.Side, Commands.SigmaXX,
				Commands.SigmaXY, Commands.SigmaYY, Commands.Slope, Commands.SlopeField,
				Commands.SlowPlot, Commands.SolveCubic, Commands.SolveODE, Commands.SolveQuartic,
				Commands.Spearman, Commands.Sphere, Commands.Spline, Commands.Split,
				Commands.StartAnimation, Commands.StartRecord, Commands.Stretch,
				Commands.SumSquaredErrors, Commands.SurdText, Commands.Surface, Commands.SVD,
				Commands.SXX, Commands.SXY, Commands.SYY, Commands.TableText, Commands.Tangent,
				Commands.TaylorSeries, Commands.TDistribution, Commands.Tetrahedron,
				Commands.TextToUnicode, Commands.TMean2Estimate, Commands.TMeanEstimate,
				Commands.ToBase, Commands.ToolImage, Commands.Top, Commands.ToPoint,
				Commands.ToPolar, Commands.Translate, Commands.TrapezoidalSum,
				Commands.TravelingSalesman, Commands.TriangleCenter, Commands.TriangleCurve,
				Commands.Triangular, Commands.TrigCombine, Commands.TrigExpand,
				Commands.TrigSimplify, Commands.Trilinear, Commands.TTest, Commands.TTest2,
				Commands.TTestPaired, Commands.Turtle, Commands.TurtleBack, Commands.TurtleDown,
				Commands.TurtleForward, Commands.TurtleLeft, Commands.TurtleRight,
				Commands.TurtleUp, Commands.Type, Commands.UnicodeToLetter, Commands.UnicodeToText,
				Commands.Uniform, Commands.UnitOrthogonalVector, Commands.UnitVector,
				Commands.UpperSum, Commands.Variance, Commands.Vector, Commands.Vertex,
				Commands.VerticalText, Commands.Volume, Commands.Voronoi, Commands.Weibull,
				Commands.Zip, Commands.Zipf, Commands.ZMean2Estimate, Commands.ZMean2Test,
				Commands.ZMeanEstimate, Commands.ZMeanTest, Commands.ZoomIn, Commands.ZoomOut,
				Commands.ZProportion2Estimate, Commands.ZProportion2Test,
				Commands.ZProportionEstimate, Commands.ZProportionTest);
		CommandFilterFactory.addBooleanCommands(nameFilter);
		return nameFilter;
	}

	private static Set<ContextMenuItemFilter> createContextMenuItemFilters() {
		return Set.of(contextMenuItem -> {
			return contextMenuItem != AlgebraContextMenuItem.Statistics
					&& contextMenuItem != AlgebraContextMenuItem.SpecialPoints;
		});
	}

	/**
	 * Creates a set of visibility restrictions for exam mode.
	 * <p> This method is used and exposed as {@code public} only for unit tests. </p>
	 * @return the set of visibility restrictions
	 */
	public static Set<VisibilityRestriction> createVisibilityRestrictions() {
		return Set.of(
				new HiddenIntegralAreaVisibilityRestriction(),
				new HiddenInequalityVisibilityRestriction(),
				new HiddenVectorVisibilityRestriction());
	}

	/**
	 * Restricts the visibility of integrals with area.
	 * <p>Examples: </p>
	 * <ul>
	 *     <li>Integral(f, -5, 5)</li>
	 *     <li>Integral(f, x, -5, 5)</li>
	 *     <li>NIntegral(f, -5, 5)</li>
	 * </ul>
	 */
	private static final class HiddenIntegralAreaVisibilityRestriction
			implements VisibilityRestriction {
		@Nonnull
		@Override
		public Effect getEffect(GeoElement geoElement) {
			return (geoElement instanceof GeoSymbolic && ((GeoSymbolic) geoElement).getTwinGeo()
					.getParentAlgorithm() instanceof AlgoIntegralDefinite) ? HIDE : IGNORE;
		}
	}
}
