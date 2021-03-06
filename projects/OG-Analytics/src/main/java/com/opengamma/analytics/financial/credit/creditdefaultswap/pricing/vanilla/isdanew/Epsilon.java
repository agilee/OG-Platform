/**
 * Copyright (C) 2013 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.analytics.financial.credit.creditdefaultswap.pricing.vanilla.isdanew;

/**
 * 
 */
public abstract class Epsilon {

  // Coefficients for the Taylor expansion of (e^x-1)/x and its first two derivatives
  private static final double[] COEFF1 = new double[] {1 / 24., 1 / 6., 1 / 2., 1};
  private static final double[] COEFF2 = new double[] {1 / 144., 1 / 30., 1 / 8., 1 / 3., 1 / 2.};
  private static final double[] COEFF3 = new double[] {1 / 168., 1 / 36., 1 / 10., 1 / 4., 1 / 3.};

  /**
   * This is the Taylor expansion of $$\frac{\exp(x)-1}{x}$$ - note for $$|x| > 10^{-10}$$ the expansion is note uesed 
   * @param x value
   * @return result 
   */
  public static double epsilon(final double x) {
    if (Math.abs(x) > 1e-10) {
      return Math.expm1(x) / x;
    }
    double sum = COEFF1[0];
    final int n = COEFF1.length;
    for (int i = 1; i < n; i++) {
      sum = COEFF1[i] + x * sum;
    }
    return sum;
  }

  /**
   * This is the Taylor expansion of the first derivative of $$\frac{\exp(x)-1}{x}$$
   * @param x value
   * @return result 
   */
  public static double epsilonP(final double x) {

    // if (Math.abs(x) > 1e-10) {
    // return ((x - 1) * Math.expm1(x) + x) / x / x;
    // }

    double sum = COEFF2[0];
    final int n = COEFF2.length;
    for (int i = 1; i < n; i++) {
      sum = COEFF2[i] + x * sum;
    }
    return sum;
  }

  /**
   * This is the Taylor expansion of the second derivative of $$\frac{\exp(x)-1}{x}$$
   * @param x value
   * @return result 
   */
  public static double epsilonPP(final double x) {

    // if (Math.abs(x) > 1e-10) {
    // final double x2 = x * x;
    // final double x3 = x * x2;
    // return (Math.expm1(x) * (x2 - 2 * x + 2) + x2 - 2 * x) / x3;
    // }

    double sum = COEFF3[0];
    final int n = COEFF3.length;
    for (int i = 1; i < n; i++) {
      sum = COEFF3[i] + x * sum;
    }
    return sum;
  }

}
