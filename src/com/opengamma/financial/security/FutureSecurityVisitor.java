/**
 * Copyright (C) 2009 - 2009 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.security;

public interface FutureSecurityVisitor<T> {
  public T visitAgricultureFutureSecurity (AgricultureFutureSecurity security);
  public T visitBondFutureSecurity (BondFutureSecurity security);
  public T visitEnergyFutureSecurity (EnergyFutureSecurity security);
  public T visitFXFutureSecurity (FXFutureSecurity security);
  public T visitInterestRateFutureSecurity (InterestRateFutureSecurity security);
  public T visitMetalFutureSecurity (MetalFutureSecurity security);
}
