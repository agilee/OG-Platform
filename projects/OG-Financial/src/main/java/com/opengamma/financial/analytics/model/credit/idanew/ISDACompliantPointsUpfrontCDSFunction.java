/**
 * Copyright (C) 2013 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.analytics.model.credit.idanew;

import org.threeten.bp.ZonedDateTime;

import com.opengamma.analytics.financial.credit.BuySellProtection;
import com.opengamma.analytics.financial.credit.creditdefaultswap.definition.legacy.LegacyVanillaCreditDefaultSwapDefinition;
import com.opengamma.analytics.financial.credit.creditdefaultswap.pricing.vanilla.isdanew.CDSAnalytic;
import com.opengamma.analytics.financial.credit.creditdefaultswap.pricing.vanilla.isdanew.ISDACompliantCreditCurve;
import com.opengamma.analytics.financial.credit.creditdefaultswap.pricing.vanilla.isdanew.ISDACompliantYieldCurve;
import com.opengamma.analytics.financial.credit.creditdefaultswap.pricing.vanilla.isdanew.PointsUpFrontConverter;
import com.opengamma.engine.value.ValueRequirementNames;

/**
 *
 */
public class ISDACompliantPointsUpfrontCDSFunction extends ISDACompliantCDSFunction {

  private final PointsUpFrontConverter _puf = new PointsUpFrontConverter();

  public ISDACompliantPointsUpfrontCDSFunction() {
    super(ValueRequirementNames.POINTS_UPFRONT);
  }

  @Override
  protected Object compute(final ZonedDateTime valuationDate, final LegacyVanillaCreditDefaultSwapDefinition cds, final ISDACompliantCreditCurve creditCurve,
      final ISDACompliantYieldCurve yieldCurve, final CDSAnalytic analytic, CDSAnalytic[] creditAnalytics, final double[] spreads) {
    final double puf = 100.0 * _puf.quotedSpreadToPUF(analytic, cds.getParSpread() * s_tenminus4, yieldCurve, spreads[0]);
    // SELL protection reverses directions of legs
    return Double.valueOf(cds.getBuySellProtection() == BuySellProtection.SELL ? -puf : puf);
  }

}