/**
 * Copyright (C) 2013 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.analytics.financial.interestrate.inflation.method;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.opengamma.analytics.financial.interestrate.inflation.derivative.CapFloorInflationZeroCouponInterpolation;
import com.opengamma.analytics.financial.model.option.pricing.analytic.formula.BlackFunctionData;
import com.opengamma.analytics.financial.model.option.pricing.analytic.formula.BlackPriceFunction;
import com.opengamma.analytics.financial.model.option.pricing.analytic.formula.EuropeanVanillaOption;
import com.opengamma.analytics.financial.provider.description.inflation.BlackSmileCapInflationZeroCouponWithConvexityProviderInterface;
import com.opengamma.analytics.financial.provider.description.inflation.InflationProviderInterface;
import com.opengamma.analytics.financial.provider.sensitivity.inflation.InflationSensitivity;
import com.opengamma.analytics.financial.provider.sensitivity.inflation.MultipleCurrencyInflationSensitivity;
import com.opengamma.analytics.math.function.Function1D;
import com.opengamma.util.ArgumentChecker;
import com.opengamma.util.money.MultipleCurrencyAmount;
import com.opengamma.util.tuple.DoublesPair;

/**
 * Class used to compute the price and sensitivity of a Inflation Zero-Coupon cap/floor with Black model.
 * Black model for inflation assume a lognormal diffusion of the forward price index. 
 * A convexity adjustment is done for payment at non-standard dates.
 */
public final class CapFloorInflationZeroCouponInterpolationConvexityAdjustmentMethod {

  /**
   * The method unique instance.
   */
  private static final CapFloorInflationZeroCouponInterpolationConvexityAdjustmentMethod INSTANCE = new CapFloorInflationZeroCouponInterpolationConvexityAdjustmentMethod();

  /**
   * Private constructor.
   */
  private CapFloorInflationZeroCouponInterpolationConvexityAdjustmentMethod() {
  }

  /**
   * Return the unique instance of the class.
   * @return The instance.
   */
  public static CapFloorInflationZeroCouponInterpolationConvexityAdjustmentMethod getInstance() {
    return INSTANCE;
  }

  /**
   * The Black function used in the pricing.
   */
  private static final BlackPriceFunction BLACK_FUNCTION = new BlackPriceFunction();

  /**
   * The convexity adjustment function used in the pricing.
   */
  private static final InflationMarketModelConvexityAdjustementForCapFloor CONVEXITY_ADJUSTMENT_FUNCTION = new InflationMarketModelConvexityAdjustementForCapFloor();

  /**
   * Computes the net amount.
   * @param cap The caplet/floorlet.
   * @param black The Black implied volatility and multi-curve provider.
   * @return The present value.
   */
  public MultipleCurrencyAmount netAmount(final CapFloorInflationZeroCouponInterpolation cap, final BlackSmileCapInflationZeroCouponWithConvexityProviderInterface black) {
    ArgumentChecker.notNull(cap, "The cap/floor shoud not be null");
    ArgumentChecker.notNull(black, "Black provider");
    final double timeToMaturity = cap.getReferenceEndTime()[1] - cap.getLastKnownFixingTime();
    final EuropeanVanillaOption option = new EuropeanVanillaOption(Math.pow(1 + cap.getStrike(), cap.getMaturity()), timeToMaturity, cap.isCap());
    final double refenrenceEndTime = cap.getWeight() * cap.getReferenceEndTime()[0] + (1 - cap.getWeight()) * cap.getReferenceEndTime()[1];
    final double priceIndexEnd = black.getInflationProvider().getPriceIndex(cap.getPriceIndex(), refenrenceEndTime);
    final double priceIndexStart = cap.getIndexStartValue();
    final double convexityAdjustment = CONVEXITY_ADJUSTMENT_FUNCTION.getZeroCouponConvexityAdjustment(cap, black);
    final double forward = priceIndexEnd / priceIndexStart * convexityAdjustment;
    final double volatility = black.getBlackParameters().getVolatility(cap.getReferenceEndTime()[1], cap.getStrike());
    final BlackFunctionData dataBlack = new BlackFunctionData(forward, 1.0, volatility);
    final Function1D<BlackFunctionData, Double> func = BLACK_FUNCTION.getPriceFunction(option);
    final double price = func.evaluate(dataBlack) * cap.getNotional() * cap.getPaymentYearFraction();
    return MultipleCurrencyAmount.of(cap.getCurrency(), price);
  }

  /**
   * Computes the present value.
   * @param cap The caplet/floorlet.
   * @param black The Black implied volatility and multi-curve provider.
   * @return The present value.
   */
  public MultipleCurrencyAmount presentValue(final CapFloorInflationZeroCouponInterpolation cap, final BlackSmileCapInflationZeroCouponWithConvexityProviderInterface black) {
    final MultipleCurrencyAmount nonDiscountedPresentValue = netAmount(cap, black);
    final double df = black.getMulticurveProvider().getDiscountFactor(cap.getCurrency(), cap.getPaymentTime());
    return nonDiscountedPresentValue.multipliedBy(df);
  }

  /**
   * Computes the present value rate sensitivity to rates of a cap/floor in the Black model.
   * No smile impact is taken into account; equivalent to a sticky strike smile description.
   * @param cap The caplet/floorlet.
   * @param black The Black implied volatility and multi-curve provider.
   * @return The present value curve sensitivity.
   */
  public MultipleCurrencyInflationSensitivity presentValueCurveSensitivity(final CapFloorInflationZeroCouponInterpolation cap,
      final BlackSmileCapInflationZeroCouponWithConvexityProviderInterface black) {
    ArgumentChecker.notNull(cap, "The cap/floor shoud not be null");
    ArgumentChecker.notNull(black, "Black provider");
    InflationProviderInterface inflation = black.getInflationProvider();
    final double timeToMaturity = cap.getReferenceEndTime()[0] - cap.getLastKnownFixingTime();
    final EuropeanVanillaOption option = new EuropeanVanillaOption(Math.pow(1 + cap.getStrike(), cap.getMaturity()), timeToMaturity, cap.isCap());
    final double referenceEndTime = cap.getWeight() * cap.getReferenceEndTime()[0] + (1 - cap.getWeight()) * cap.getReferenceEndTime()[1];
    final double convexityAdjustment = CONVEXITY_ADJUSTMENT_FUNCTION.getZeroCouponConvexityAdjustment(cap, black);
    final double forward = black.getInflationProvider().getPriceIndex(cap.getPriceIndex(), referenceEndTime) / cap.getIndexStartValue() * convexityAdjustment;
    final double df = black.getMulticurveProvider().getDiscountFactor(cap.getCurrency(), cap.getPaymentTime());
    final Map<String, List<DoublesPair>> resultMapPrice = new HashMap<String, List<DoublesPair>>();
    final List<DoublesPair> listPrice = new ArrayList<DoublesPair>();
    listPrice.add(new DoublesPair(referenceEndTime, 1 / cap.getIndexStartValue() * convexityAdjustment));
    resultMapPrice.put(inflation.getName(cap.getPriceIndex()), listPrice);
    final InflationSensitivity forwardDi = InflationSensitivity.ofPriceIndex(resultMapPrice);
    final double dfDr = -cap.getPaymentTime() * df;
    final double volatility = black.getBlackParameters().getVolatility(cap.getReferenceEndTime()[1], cap.getStrike());
    final BlackFunctionData dataBlack = new BlackFunctionData(forward, 1.0, volatility);
    final double[] bsAdjoint = BLACK_FUNCTION.getPriceAdjoint(option, dataBlack);
    final List<DoublesPair> list = new ArrayList<DoublesPair>();
    list.add(new DoublesPair(cap.getPaymentTime(), dfDr));
    final Map<String, List<DoublesPair>> resultMap = new HashMap<String, List<DoublesPair>>();
    resultMap.put(inflation.getName(cap.getCurrency()), list);
    InflationSensitivity result = InflationSensitivity.ofYieldDiscounting(resultMap);
    result = result.multipliedBy(bsAdjoint[0]);
    result = result.plus(forwardDi.multipliedBy(df * bsAdjoint[1]));
    result = result.multipliedBy(cap.getNotional() * cap.getPaymentYearFraction());
    return MultipleCurrencyInflationSensitivity.of(cap.getCurrency(), result);
  }

}
