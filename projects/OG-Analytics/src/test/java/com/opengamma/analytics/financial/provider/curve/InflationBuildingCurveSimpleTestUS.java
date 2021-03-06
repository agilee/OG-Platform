/**
 * Copyright (C) 2013 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.analytics.financial.provider.curve;

import static org.testng.AssertJUnit.assertEquals;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import org.threeten.bp.Period;
import org.threeten.bp.ZonedDateTime;

import com.opengamma.analytics.financial.curve.inflation.generator.GeneratorPriceIndexCurve;
import com.opengamma.analytics.financial.curve.inflation.generator.GeneratorPriceIndexCurveInterpolated;
import com.opengamma.analytics.financial.instrument.InstrumentDefinition;
import com.opengamma.analytics.financial.instrument.cash.CashDefinition;
import com.opengamma.analytics.financial.instrument.fra.ForwardRateAgreementDefinition;
import com.opengamma.analytics.financial.instrument.index.GeneratorAttribute;
import com.opengamma.analytics.financial.instrument.index.GeneratorAttributeIR;
import com.opengamma.analytics.financial.instrument.index.GeneratorInstrument;
import com.opengamma.analytics.financial.instrument.index.GeneratorSwapFixedInflationMaster;
import com.opengamma.analytics.financial.instrument.index.GeneratorSwapFixedInflationZeroCoupon;
import com.opengamma.analytics.financial.instrument.index.IndexPrice;
import com.opengamma.analytics.financial.instrument.inflation.CouponInflationZeroCouponInterpolationDefinition;
import com.opengamma.analytics.financial.instrument.inflation.CouponInflationZeroCouponMonthlyDefinition;
import com.opengamma.analytics.financial.instrument.swap.SwapFixedIborDefinition;
import com.opengamma.analytics.financial.instrument.swap.SwapFixedInflationZeroCouponDefinition;
import com.opengamma.analytics.financial.instrument.swap.SwapFixedONDefinition;
import com.opengamma.analytics.financial.interestrate.InstrumentDerivative;
import com.opengamma.analytics.financial.interestrate.InstrumentDerivativeVisitor;
import com.opengamma.analytics.financial.interestrate.annuity.derivative.Annuity;
import com.opengamma.analytics.financial.interestrate.payments.derivative.Payment;
import com.opengamma.analytics.financial.interestrate.swap.derivative.Swap;
import com.opengamma.analytics.financial.provider.calculator.generic.LastTimeCalculator;
import com.opengamma.analytics.financial.provider.calculator.inflation.ParSpreadInflationMarketQuoteCurveSensitivityDiscountingCalculator;
import com.opengamma.analytics.financial.provider.calculator.inflation.ParSpreadInflationMarketQuoteDiscountingCalculator;
import com.opengamma.analytics.financial.provider.calculator.inflation.PresentValueDiscountingInflationCalculator;
import com.opengamma.analytics.financial.provider.curve.inflation.InflationDiscountBuildingRepository;
import com.opengamma.analytics.financial.provider.description.MulticurveProviderDiscountDataSets;
import com.opengamma.analytics.financial.provider.description.inflation.InflationProviderDiscount;
import com.opengamma.analytics.financial.provider.description.inflation.InflationProviderInterface;
import com.opengamma.analytics.financial.provider.description.interestrate.MulticurveProviderDiscount;
import com.opengamma.analytics.financial.provider.sensitivity.inflation.InflationSensitivity;
import com.opengamma.analytics.math.interpolation.CombinedInterpolatorExtrapolatorFactory;
import com.opengamma.analytics.math.interpolation.Interpolator1D;
import com.opengamma.analytics.math.interpolation.Interpolator1DFactory;
import com.opengamma.timeseries.DoubleTimeSeries;
import com.opengamma.timeseries.precise.zdt.ImmutableZonedDateTimeDoubleTimeSeries;
import com.opengamma.timeseries.precise.zdt.ZonedDateTimeDoubleTimeSeries;
import com.opengamma.util.money.Currency;
import com.opengamma.util.time.DateUtils;
import com.opengamma.util.tuple.Pair;

/**
 * Build of inflation curve in several blocks with relevant Jacobian matrice.
 */
public class InflationBuildingCurveSimpleTestUS {

  private static final Interpolator1D INTERPOLATOR_LOG_LINEAR = CombinedInterpolatorExtrapolatorFactory.getInterpolator(Interpolator1DFactory.LOG_LINEAR, Interpolator1DFactory.FLAT_EXTRAPOLATOR,
      Interpolator1DFactory.FLAT_EXTRAPOLATOR);

  private static final LastTimeCalculator MATURITY_CALCULATOR = LastTimeCalculator.getInstance();
  private static final double TOLERANCE_ROOT = 1.0E-10;
  private static final int STEP_MAX = 100;

  private static final Currency USD = Currency.USD;

  private static final double NOTIONAL = 1.0;

  private static final GeneratorSwapFixedInflationZeroCoupon GENERATOR_INFLATION_SWAP = GeneratorSwapFixedInflationMaster.getInstance().getGenerator("USCPI");
  private static final IndexPrice US_CPI = GENERATOR_INFLATION_SWAP.getIndexPrice();

  private static final ZonedDateTime NOW = DateUtils.getUTCDate(2012, 9, 28);

  private static final ZonedDateTimeDoubleTimeSeries TS_PRICE_INDEX_USD_WITH_TODAY = ImmutableZonedDateTimeDoubleTimeSeries.ofUTC(new ZonedDateTime[] {DateUtils.getUTCDate(2011, 9, 27),
      DateUtils.getUTCDate(2011, 9, 28) }, new double[] {200, 200 });
  private static final ZonedDateTimeDoubleTimeSeries TS_PRICE_INDEX_USD_WITHOUT_TODAY = ImmutableZonedDateTimeDoubleTimeSeries.ofUTC(new ZonedDateTime[] {DateUtils.getUTCDate(2011, 9, 27) },
      new double[] {100 });

  @SuppressWarnings("rawtypes")
  private static final DoubleTimeSeries[] TS_FIXED_PRICE_INDEX_USD_WITH_TODAY = new DoubleTimeSeries[] {TS_PRICE_INDEX_USD_WITH_TODAY };
  @SuppressWarnings("rawtypes")
  private static final DoubleTimeSeries[] TS_FIXED_PRICE_INDEX_USD_WITHOUT_TODAY = new DoubleTimeSeries[] {TS_PRICE_INDEX_USD_WITHOUT_TODAY };

  private static final String CURVE_NAME_CPI_USD = "USD CPI";

  /** Market values for the CPI USD curve */
  public static final double[] CPI_USD_MARKET_QUOTES = new double[] {0.0200, 0.0200, 0.0250, 0.0260, 0.0200, 0.0270, 0.0280, 0.0290, 0.0300, 0.0310, 0.0320, 0.0330, 0.0330, 0.0330, 0.0330 };
  /** Generators for the CPI USD curve */
  public static final GeneratorInstrument<? extends GeneratorAttribute>[] CPI_USD_GENERATORS = new GeneratorInstrument<?>[] {GENERATOR_INFLATION_SWAP, GENERATOR_INFLATION_SWAP,
      GENERATOR_INFLATION_SWAP,
      GENERATOR_INFLATION_SWAP, GENERATOR_INFLATION_SWAP, GENERATOR_INFLATION_SWAP, GENERATOR_INFLATION_SWAP, GENERATOR_INFLATION_SWAP, GENERATOR_INFLATION_SWAP,
      GENERATOR_INFLATION_SWAP, GENERATOR_INFLATION_SWAP, GENERATOR_INFLATION_SWAP, GENERATOR_INFLATION_SWAP, GENERATOR_INFLATION_SWAP, GENERATOR_INFLATION_SWAP };
  /** Tenors for the CPI USD curve */
  public static final Period[] CPI_USD_TENOR = new Period[] {Period.ofYears(1),
      Period.ofYears(2), Period.ofYears(3), Period.ofYears(4), Period.ofYears(5), Period.ofYears(6), Period.ofYears(7),
      Period.ofYears(8), Period.ofYears(9), Period.ofYears(10), Period.ofYears(12), Period.ofYears(15), Period.ofYears(20),
      Period.ofYears(25), Period.ofYears(30) };
  public static final GeneratorAttributeIR[] CPI_USD_ATTR = new GeneratorAttributeIR[CPI_USD_TENOR.length];
  static {
    for (int loopins = 0; loopins < CPI_USD_TENOR.length; loopins++) {
      CPI_USD_ATTR[loopins] = new GeneratorAttributeIR(CPI_USD_TENOR[loopins]);
    }
  }

  /** Standard USD CPI curve instrument definitions */
  public static final InstrumentDefinition<?>[] DEFINITIONS_CPI_USD;

  /** Units of curves */
  public static final int[] NB_UNITS = new int[] {1 };
  public static final int NB_BLOCKS = NB_UNITS.length;
  public static final InstrumentDefinition<?>[][][][] DEFINITIONS_UNITS = new InstrumentDefinition<?>[NB_BLOCKS][][][];
  public static final GeneratorPriceIndexCurve[][][] GENERATORS_UNITS = new GeneratorPriceIndexCurve[NB_BLOCKS][][];
  public static final String[][][] NAMES_UNITS = new String[NB_BLOCKS][][];

  public static final MulticurveProviderDiscount usMulticurveProviderDiscount = MulticurveProviderDiscountDataSets.createMulticurveEurUsd().copy();
  public static final InflationProviderDiscount KNOWN_DATA = new InflationProviderDiscount(usMulticurveProviderDiscount);

  public static final LinkedHashMap<String, IndexPrice[]> US_CPI_MAP = new LinkedHashMap<String, IndexPrice[]>();

  static {
    DEFINITIONS_CPI_USD = getDefinitions(CPI_USD_MARKET_QUOTES, CPI_USD_GENERATORS, CPI_USD_ATTR);

    for (int loopblock = 0; loopblock < NB_BLOCKS; loopblock++) {
      DEFINITIONS_UNITS[loopblock] = new InstrumentDefinition<?>[NB_UNITS[loopblock]][][];
      GENERATORS_UNITS[loopblock] = new GeneratorPriceIndexCurve[NB_UNITS[loopblock]][];
      NAMES_UNITS[loopblock] = new String[NB_UNITS[loopblock]][];
    }
    DEFINITIONS_UNITS[0][0] = new InstrumentDefinition<?>[][] {DEFINITIONS_CPI_USD };

    final GeneratorPriceIndexCurve genIntLin = new GeneratorPriceIndexCurveInterpolated(MATURITY_CALCULATOR, INTERPOLATOR_LOG_LINEAR);
    GENERATORS_UNITS[0][0] = new GeneratorPriceIndexCurve[] {genIntLin };

    NAMES_UNITS[0][0] = new String[] {CURVE_NAME_CPI_USD };

    US_CPI_MAP.put(CURVE_NAME_CPI_USD, new IndexPrice[] {US_CPI });
  }

  public static final String NOT_USED = "Not used";
  public static final String[] NOT_USED_2 = {NOT_USED, NOT_USED };

  public static InstrumentDefinition<?>[] getDefinitions(final double[] marketQuotes, final GeneratorInstrument[] generators, final GeneratorAttribute[] attribute) {
    final InstrumentDefinition<?>[] definitions = new InstrumentDefinition<?>[marketQuotes.length];
    for (int loopmv = 0; loopmv < marketQuotes.length; loopmv++) {
      definitions[loopmv] = generators[loopmv].generateInstrument(NOW, marketQuotes[loopmv], NOTIONAL, attribute[loopmv]);
    }
    return definitions;
  }

  private static List<Pair<InflationProviderDiscount, CurveBuildingBlockBundle>> CURVES_PAR_SPREAD_MQ_WITHOUT_TODAY_BLOCK = new ArrayList<Pair<InflationProviderDiscount, CurveBuildingBlockBundle>>();

  // Calculator
  private static final PresentValueDiscountingInflationCalculator PVIC = PresentValueDiscountingInflationCalculator.getInstance();
  private static final ParSpreadInflationMarketQuoteDiscountingCalculator PSIMQC = ParSpreadInflationMarketQuoteDiscountingCalculator.getInstance();
  private static final ParSpreadInflationMarketQuoteCurveSensitivityDiscountingCalculator PSIMQCSC = ParSpreadInflationMarketQuoteCurveSensitivityDiscountingCalculator.getInstance();

  private static final InflationDiscountBuildingRepository CURVE_BUILDING_REPOSITORY = new InflationDiscountBuildingRepository(TOLERANCE_ROOT, TOLERANCE_ROOT, STEP_MAX);

  private static final double TOLERANCE_CAL = 1.0E-9;

  @BeforeSuite
  static void initClass() {
    for (int loopblock = 0; loopblock < NB_BLOCKS; loopblock++) {
      CURVES_PAR_SPREAD_MQ_WITHOUT_TODAY_BLOCK.add(makeCurvesFromDefinitions(DEFINITIONS_UNITS[loopblock], GENERATORS_UNITS[loopblock], NAMES_UNITS[loopblock], KNOWN_DATA, PSIMQC, PSIMQCSC, false));
    }
  }

  @Test(enabled = false)
  public void performance() {
    long startTime, endTime;
    final int nbTest = 1000;

    startTime = System.currentTimeMillis();
    for (int looptest = 0; looptest < nbTest; looptest++) {
      makeCurvesFromDefinitions(DEFINITIONS_UNITS[0], GENERATORS_UNITS[0], NAMES_UNITS[0], KNOWN_DATA, PSIMQC, PSIMQCSC, false);
    }
    endTime = System.currentTimeMillis();
    System.out.println("InflationBuildingCurveSimpleTestEUR - " + nbTest + " curve construction Price index EUR 1 units: " + (endTime - startTime) + " ms");
    // Performance note: curve construction Price index EUR 1 units: 27-Mar-13: On Dell Precision T1850 3.5 GHz Quad-Core Intel Xeon: 3564 ms for 1000 sets.
  }

  @Test
  public void curveConstructionGeneratorOtherBlocks() {
    for (int loopblock = 0; loopblock < NB_BLOCKS; loopblock++) {
      curveConstructionTest(DEFINITIONS_UNITS[loopblock], CURVES_PAR_SPREAD_MQ_WITHOUT_TODAY_BLOCK.get(loopblock).getFirst(), false, loopblock);
    }
  }

  public void curveConstructionTest(final InstrumentDefinition<?>[][][] definitions, final InflationProviderDiscount curves, final boolean withToday, final int block) {
    final int nbBlocks = definitions.length;
    for (int loopblock = 0; loopblock < nbBlocks; loopblock++) {
      final InstrumentDerivative[][] instruments = convert(definitions[loopblock], loopblock, withToday);
      final double[][] pv = new double[instruments.length][];
      for (int loopcurve = 0; loopcurve < instruments.length; loopcurve++) {
        pv[loopcurve] = new double[instruments[loopcurve].length];
        for (int loopins = 0; loopins < instruments[loopcurve].length; loopins++) {
          pv[loopcurve][loopins] = curves.getFxRates().convert(instruments[loopcurve][loopins].accept(PVIC, curves), USD).getAmount();
          assertEquals("Curve construction: block " + block + ", unit " + loopblock + " - instrument " + loopins, 0, pv[loopcurve][loopins], TOLERANCE_CAL);
        }
      }
    }
  }

  private static Pair<InflationProviderDiscount, CurveBuildingBlockBundle> makeCurvesFromDefinitions(final InstrumentDefinition<?>[][][] definitions,
      final GeneratorPriceIndexCurve[][] curveGenerators,
      final String[][] curveNames, final InflationProviderDiscount knownData, final InstrumentDerivativeVisitor<InflationProviderInterface, Double> calculator,
      final InstrumentDerivativeVisitor<InflationProviderInterface, InflationSensitivity> sensitivityCalculator, final boolean withToday)
  {
    final int nbUnits = curveGenerators.length;
    final double[][] parametersGuess = new double[nbUnits][];
    final GeneratorPriceIndexCurve[][] generatorFinal = new GeneratorPriceIndexCurve[nbUnits][];
    final InstrumentDerivative[][][] instruments = new InstrumentDerivative[nbUnits][][];
    for (int loopunit = 0; loopunit < nbUnits; loopunit++) {
      generatorFinal[loopunit] = new GeneratorPriceIndexCurve[curveGenerators[loopunit].length];
      int nbInsUnit = 0;
      for (int loopcurve = 0; loopcurve < curveGenerators[loopunit].length; loopcurve++) {
        nbInsUnit += definitions[loopunit][loopcurve].length;
      }
      parametersGuess[loopunit] = new double[nbInsUnit];
      int startCurve = 0; // First parameter index of the curve in the unit.
      instruments[loopunit] = convert(definitions[loopunit], loopunit, withToday);
      for (int loopcurve = 0; loopcurve < curveGenerators[loopunit].length; loopcurve++) {
        generatorFinal[loopunit][loopcurve] = curveGenerators[loopunit][loopcurve].finalGenerator(instruments[loopunit][loopcurve]);
        final double[] guessCurve = generatorFinal[loopunit][loopcurve].initialGuess(initialGuess(definitions[loopunit][loopcurve]));
        System.arraycopy(guessCurve, 0, parametersGuess[loopunit], startCurve, instruments[loopunit][loopcurve].length);
        startCurve += instruments[loopunit][loopcurve].length;
      }
    }
    return CURVE_BUILDING_REPOSITORY.makeCurvesFromDerivatives(instruments, generatorFinal, curveNames, parametersGuess, knownData, US_CPI_MAP, calculator,
        sensitivityCalculator);

  }

  @SuppressWarnings("unchecked")
  private static InstrumentDerivative[][] convert(final InstrumentDefinition<?>[][] definitions, final int unit, final boolean withToday) {
    //    int nbDef = 0;
    //    for (final InstrumentDefinition<?>[] definition : definitions) {
    //      nbDef += definition.length;
    //    }
    final InstrumentDerivative[][] instruments = new InstrumentDerivative[definitions.length][];
    for (int loopcurve = 0; loopcurve < definitions.length; loopcurve++) {
      instruments[loopcurve] = new InstrumentDerivative[definitions[loopcurve].length];
      int loopins = 0;
      for (final InstrumentDefinition<?> instrument : definitions[loopcurve]) {
        InstrumentDerivative ird;
        if (instrument instanceof SwapFixedInflationZeroCouponDefinition) {
          /* ird = ((SwapFixedInflationZeroCouponDefinition) instrument).toDerivative(NOW, getTSSwapFixedInflation(withToday, unit), NOT_USED_2);*/
          final Annuity<? extends Payment> ird1 = ((SwapFixedInflationZeroCouponDefinition) instrument).getFirstLeg().toDerivative(NOW, NOT_USED_2);
          final Annuity<? extends Payment> ird2 = ((SwapFixedInflationZeroCouponDefinition) instrument).getSecondLeg().toDerivative(NOW, TS_PRICE_INDEX_USD_WITH_TODAY, NOT_USED_2);
          ird = new Swap(ird1, ird2);
        }
        else {
          ird = instrument.toDerivative(NOW, NOT_USED_2);
        }
        instruments[loopcurve][loopins++] = ird;
      }
    }
    return instruments;
  }

  @SuppressWarnings("rawtypes")
  private static DoubleTimeSeries[] getTSSwapFixedInflation(final Boolean withToday, final Integer unit) {
    switch (unit) {
      case 0:
        return withToday ? TS_FIXED_PRICE_INDEX_USD_WITH_TODAY : TS_FIXED_PRICE_INDEX_USD_WITHOUT_TODAY;
      case 1:
        return withToday ? TS_FIXED_PRICE_INDEX_USD_WITH_TODAY : TS_FIXED_PRICE_INDEX_USD_WITHOUT_TODAY;
      default:
        throw new IllegalArgumentException(unit.toString());
    }
  }

  private static double[] initialGuess(final InstrumentDefinition<?>[] definitions) {
    final double[] result = new double[definitions.length];
    int loopr = 0;
    for (final InstrumentDefinition<?> definition : definitions) {
      result[loopr++] = initialGuess(definition);
    }
    return result;
  }

  private static double initialGuess(final InstrumentDefinition<?> instrument) {
    if (instrument instanceof SwapFixedONDefinition) {
      return ((SwapFixedONDefinition) instrument).getFixedLeg().getNthPayment(0).getRate();
    }
    if (instrument instanceof SwapFixedIborDefinition) {
      return ((SwapFixedIborDefinition) instrument).getFixedLeg().getNthPayment(0).getRate();
    }
    if (instrument instanceof SwapFixedInflationZeroCouponDefinition) {

      if (((SwapFixedInflationZeroCouponDefinition) instrument).getFirstLeg().getNthPayment(0) instanceof CouponInflationZeroCouponMonthlyDefinition) {
        return ((CouponInflationZeroCouponMonthlyDefinition) ((SwapFixedInflationZeroCouponDefinition) instrument).getFirstLeg().getNthPayment(0)).getIndexStartValue();
      }
      if (((SwapFixedInflationZeroCouponDefinition) instrument).getFirstLeg().getNthPayment(0) instanceof CouponInflationZeroCouponInterpolationDefinition) {
        return ((CouponInflationZeroCouponInterpolationDefinition) ((SwapFixedInflationZeroCouponDefinition) instrument).getFirstLeg().getNthPayment(0)).getIndexStartValue();
      }
      return 100;
    }
    if (instrument instanceof ForwardRateAgreementDefinition) {
      return ((ForwardRateAgreementDefinition) instrument).getRate();
    }
    if (instrument instanceof CashDefinition) {
      return ((CashDefinition) instrument).getRate();
    }
    return 1;
  }

}
