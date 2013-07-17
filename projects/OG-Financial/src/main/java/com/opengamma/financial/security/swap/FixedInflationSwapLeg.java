/**
 * Copyright (C) 2013 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.security.swap;

import java.util.Map;

import org.joda.beans.BeanBuilder;
import org.joda.beans.BeanDefinition;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.direct.DirectBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

import com.opengamma.financial.convention.businessday.BusinessDayConvention;
import com.opengamma.financial.convention.daycount.DayCount;
import com.opengamma.financial.convention.frequency.Frequency;
import com.opengamma.id.ExternalId;
import com.opengamma.util.ArgumentChecker;

/**
 * A fixed inflation swap leg.
 */
@BeanDefinition
public class FixedInflationSwapLeg extends InflationLeg {

  /** Serialization version */
  private static final long serialVersionUID = 1L;

  /**
   * The rate.
   */
  @PropertyDefinition
  private double _rate;

  /**
   * Whether to exchange notional.
   */
  @PropertyDefinition
  private boolean _isExchangeNotional;

  /**
   * For the builder.
   */
  /* package */ FixedInflationSwapLeg() {
    super();
  }

  /**
   * @param dayCount The day count, not null
   * @param frequency The frequency, not null
   * @param regionId The region id, not null
   * @param businessDayConvention The business day convention, not null
   * @param notional The notional, not null
   * @param isEOM True if dates follow the EOM convention
   * @param rate The fixed rate
   * @param isExchangeNotional True if notionals are exchanged
   */
  public FixedInflationSwapLeg(final DayCount dayCount, final Frequency frequency, final ExternalId regionId, final BusinessDayConvention businessDayConvention,
      final Notional notional, final boolean isEOM, final double rate, final boolean isExchangeNotional) {
    super(dayCount, frequency, regionId, businessDayConvention, notional, isEOM);
    setRate(rate);
    setIsExchangeNotional(isExchangeNotional);
  }

  @Override
  public <T> T accept(final SwapLegVisitor<T> visitor) {
    ArgumentChecker.notNull(visitor, "visitor");
    return visitor.visitFixedInflationSwapLeg(this);
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code FixedInflationSwapLeg}.
   * @return the meta-bean, not null
   */
  public static FixedInflationSwapLeg.Meta meta() {
    return FixedInflationSwapLeg.Meta.INSTANCE;
  }
  static {
    JodaBeanUtils.registerMetaBean(FixedInflationSwapLeg.Meta.INSTANCE);
  }

  @Override
  public FixedInflationSwapLeg.Meta metaBean() {
    return FixedInflationSwapLeg.Meta.INSTANCE;
  }

  @Override
  protected Object propertyGet(String propertyName, boolean quiet) {
    switch (propertyName.hashCode()) {
      case 3493088:  // rate
        return getRate();
      case 348962765:  // isExchangeNotional
        return isIsExchangeNotional();
    }
    return super.propertyGet(propertyName, quiet);
  }

  @Override
  protected void propertySet(String propertyName, Object newValue, boolean quiet) {
    switch (propertyName.hashCode()) {
      case 3493088:  // rate
        setRate((Double) newValue);
        return;
      case 348962765:  // isExchangeNotional
        setIsExchangeNotional((Boolean) newValue);
        return;
    }
    super.propertySet(propertyName, newValue, quiet);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      FixedInflationSwapLeg other = (FixedInflationSwapLeg) obj;
      return JodaBeanUtils.equal(getRate(), other.getRate()) &&
          JodaBeanUtils.equal(isIsExchangeNotional(), other.isIsExchangeNotional()) &&
          super.equals(obj);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash += hash * 31 + JodaBeanUtils.hashCode(getRate());
    hash += hash * 31 + JodaBeanUtils.hashCode(isIsExchangeNotional());
    return hash ^ super.hashCode();
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the rate.
   * @return the value of the property
   */
  public double getRate() {
    return _rate;
  }

  /**
   * Sets the rate.
   * @param rate  the new value of the property
   */
  public void setRate(double rate) {
    this._rate = rate;
  }

  /**
   * Gets the the {@code rate} property.
   * @return the property, not null
   */
  public final Property<Double> rate() {
    return metaBean().rate().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets whether to exchange notional.
   * @return the value of the property
   */
  public boolean isIsExchangeNotional() {
    return _isExchangeNotional;
  }

  /**
   * Sets whether to exchange notional.
   * @param isExchangeNotional  the new value of the property
   */
  public void setIsExchangeNotional(boolean isExchangeNotional) {
    this._isExchangeNotional = isExchangeNotional;
  }

  /**
   * Gets the the {@code isExchangeNotional} property.
   * @return the property, not null
   */
  public final Property<Boolean> isExchangeNotional() {
    return metaBean().isExchangeNotional().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code FixedInflationSwapLeg}.
   */
  public static class Meta extends InflationLeg.Meta {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code rate} property.
     */
    private final MetaProperty<Double> _rate = DirectMetaProperty.ofReadWrite(
        this, "rate", FixedInflationSwapLeg.class, Double.TYPE);
    /**
     * The meta-property for the {@code isExchangeNotional} property.
     */
    private final MetaProperty<Boolean> _isExchangeNotional = DirectMetaProperty.ofReadWrite(
        this, "isExchangeNotional", FixedInflationSwapLeg.class, Boolean.TYPE);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
        this, (DirectMetaPropertyMap) super.metaPropertyMap(),
        "rate",
        "isExchangeNotional");

    /**
     * Restricted constructor.
     */
    protected Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case 3493088:  // rate
          return _rate;
        case 348962765:  // isExchangeNotional
          return _isExchangeNotional;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends FixedInflationSwapLeg> builder() {
      return new DirectBeanBuilder<FixedInflationSwapLeg>(new FixedInflationSwapLeg());
    }

    @Override
    public Class<? extends FixedInflationSwapLeg> beanType() {
      return FixedInflationSwapLeg.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return _metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code rate} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Double> rate() {
      return _rate;
    }

    /**
     * The meta-property for the {@code isExchangeNotional} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Boolean> isExchangeNotional() {
      return _isExchangeNotional;
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}