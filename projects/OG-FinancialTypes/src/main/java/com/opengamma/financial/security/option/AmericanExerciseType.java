/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.security.option;

import java.util.Map;

import org.joda.beans.BeanBuilder;
import org.joda.beans.BeanDefinition;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.impl.direct.DirectBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

/**
 * The American exercise type.
 */
@BeanDefinition
public class AmericanExerciseType extends ExerciseType {

  /** Serialization version. */
  private static final long serialVersionUID = 1L;

  static {
    ExerciseType.register(new AmericanExerciseType());
  }

  /**
   * Creates an empty instance.
   */
  public AmericanExerciseType() {
  }
  
  public String getName() {
    return "American";
  }

  //-------------------------------------------------------------------------
  @Override
  public <T> T accept(ExerciseTypeVisitor<T> visitor) {
    return visitor.visitAmericanExerciseType(this);
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code AmericanExerciseType}.
   * @return the meta-bean, not null
   */
  public static AmericanExerciseType.Meta meta() {
    return AmericanExerciseType.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(AmericanExerciseType.Meta.INSTANCE);
  }

  @Override
  public AmericanExerciseType.Meta metaBean() {
    return AmericanExerciseType.Meta.INSTANCE;
  }

  @Override
  protected Object propertyGet(String propertyName, boolean quiet) {
    return super.propertyGet(propertyName, quiet);
  }

  @Override
  protected void propertySet(String propertyName, Object newValue, boolean quiet) {
    super.propertySet(propertyName, newValue, quiet);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      return super.equals(obj);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    return hash ^ super.hashCode();
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code AmericanExerciseType}.
   */
  public static class Meta extends ExerciseType.Meta {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
        this, (DirectMetaPropertyMap) super.metaPropertyMap());

    /**
     * Restricted constructor.
     */
    protected Meta() {
    }

    @Override
    public BeanBuilder<? extends AmericanExerciseType> builder() {
      return new DirectBeanBuilder<AmericanExerciseType>(new AmericanExerciseType());
    }

    @Override
    public Class<? extends AmericanExerciseType> beanType() {
      return AmericanExerciseType.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return _metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
