/**
 * Copyright (C) 2009 - 2009 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.security.db;

import javax.persistence.Entity;

@Entity
public class CommodityFutureTypeBean extends EnumBean {
  
  protected CommodityFutureTypeBean() {
  }

  public CommodityFutureTypeBean(String commodityType) {
    super(commodityType);
  }
  
}
