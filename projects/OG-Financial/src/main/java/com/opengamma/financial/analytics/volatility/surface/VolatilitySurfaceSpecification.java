/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.analytics.volatility.surface;

import com.opengamma.core.config.Config;
import com.opengamma.financial.security.option.EuropeanExerciseType;
import com.opengamma.financial.security.option.ExerciseType;
import com.opengamma.id.UniqueIdentifiable;
import com.opengamma.util.ArgumentChecker;
import com.opengamma.util.money.Currency;

/**
 * Specification for a volatility surface - contains all available points on the surface.
 */
@Config
public class VolatilitySurfaceSpecification {
  private final SurfaceInstrumentProvider<?, ?> _surfaceInstrumentProvider;
  private final String _name;
  private final String _surfaceQuoteType;
  private final String _quoteUnits;
  private final UniqueIdentifiable _target;
  private final ExerciseType _exerciseType;

  public VolatilitySurfaceSpecification(final String name, final UniqueIdentifiable target, final String surfaceQuoteType, final SurfaceInstrumentProvider<?, ?> surfaceInstrumentProvider) {
    this(name, target, surfaceQuoteType, SurfaceAndCubePropertyNames.VOLATILITY_QUOTE, new EuropeanExerciseType(), surfaceInstrumentProvider);
  }

  public VolatilitySurfaceSpecification(final String name, final UniqueIdentifiable target, final String surfaceQuoteType, final String quoteUnits, 
      final SurfaceInstrumentProvider<?, ?> surfaceInstrumentProvider) {
    this(name, target, surfaceQuoteType, quoteUnits, new EuropeanExerciseType(), surfaceInstrumentProvider);
  }

  public VolatilitySurfaceSpecification(final String name, final UniqueIdentifiable target, final String surfaceQuoteType, final String quoteUnits, 
      final ExerciseType exerciseType, final SurfaceInstrumentProvider<?, ?> surfaceInstrumentProvider) {
    ArgumentChecker.notNull(name, "name");
    ArgumentChecker.notNull(target, "target");
    ArgumentChecker.notNull(surfaceQuoteType, "surface quote type");
    ArgumentChecker.notNull(quoteUnits, "quote units");
    ArgumentChecker.notNull(exerciseType, "exerciseType");
    ArgumentChecker.notNull(surfaceInstrumentProvider, "surface instrument provider");
    _name = name;
    _target = target;
    _surfaceQuoteType = surfaceQuoteType;
    _quoteUnits = quoteUnits;
    _exerciseType = exerciseType;
    _surfaceInstrumentProvider = surfaceInstrumentProvider;
  }

  public SurfaceInstrumentProvider<?, ?> getSurfaceInstrumentProvider() {
    return _surfaceInstrumentProvider;
  }

  public String getName() {
    return _name;
  }

  public String getSurfaceQuoteType() {
    return _surfaceQuoteType;
  }

  public String getQuoteUnits() {
    return _quoteUnits;
  }

  /**
   * @deprecated use getTarget()
   * @throws ClassCastException if target not a currency
   * @return currency assuming that the target is a currency
   */
  @Deprecated
  public Currency getCurrency() {
    return (Currency) _target;
  }

  public UniqueIdentifiable getTarget() {
    return _target;
  }
  
  public ExerciseType getExerciseType() {
    return _exerciseType;
  }

  @Override
  public boolean equals(final Object o) {
    if (o == null) {
      return false;
    }
    if (!(o instanceof VolatilitySurfaceSpecification)) {
      return false;
    }
    final VolatilitySurfaceSpecification other = (VolatilitySurfaceSpecification) o;
    return other.getName().equals(getName()) &&
        other.getTarget().equals(getTarget()) &&
        other.getSurfaceInstrumentProvider().equals(getSurfaceInstrumentProvider()) &&
        other.getSurfaceQuoteType().equals(getSurfaceQuoteType()) &&
        other.getExerciseType().equals(getExerciseType()) &&
        other.getQuoteUnits().equals(getQuoteUnits());
  }

  @Override
  public int hashCode() {
    return getName().hashCode() * getTarget().hashCode() * getSurfaceQuoteType().hashCode() * getQuoteUnits().hashCode() * getExerciseType().hashCode();
  }


}
