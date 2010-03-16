/**
 * Copyright (C) 2009 - 2009 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.security.db;

import javax.persistence.Entity;

@Entity
public class UnitBean extends EnumBean {
  
  protected UnitBean() {
  }

  public UnitBean(String unitName) {
    super(unitName);
  }
  
}
