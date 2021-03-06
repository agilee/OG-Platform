/**
 * Copyright (C) 2013 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.component.factory.infrastructure;

import java.util.LinkedHashMap;
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

import com.opengamma.component.ComponentInfo;
import com.opengamma.component.ComponentRepository;
import com.opengamma.component.factory.AbstractComponentFactory;
import com.opengamma.util.redis.RedisConnector;

/**
 * 
 */
@BeanDefinition
public class RedisConnectorComponentFactory extends AbstractComponentFactory {

  @PropertyDefinition(validate = "notEmpty")
  private String _classifier;
  
  @PropertyDefinition(validate = "notEmpty")
  private String _hostName;
  
  @PropertyDefinition
  private int _redisPort = 6379;
  
  @PropertyDefinition
  private String _password;
  
  @Override
  public void init(ComponentRepository repo, LinkedHashMap<String, String> configuration) throws Exception {
    RedisConnector redisConnector = new RedisConnector(getClassifier(), getHostName(), getRedisPort(), getPassword());
    
    ComponentInfo info = new ComponentInfo(RedisConnector.class, getClassifier());
    repo.registerComponent(info, redisConnector);
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code RedisConnectorComponentFactory}.
   * @return the meta-bean, not null
   */
  public static RedisConnectorComponentFactory.Meta meta() {
    return RedisConnectorComponentFactory.Meta.INSTANCE;
  }
  static {
    JodaBeanUtils.registerMetaBean(RedisConnectorComponentFactory.Meta.INSTANCE);
  }

  @Override
  public RedisConnectorComponentFactory.Meta metaBean() {
    return RedisConnectorComponentFactory.Meta.INSTANCE;
  }

  @Override
  protected Object propertyGet(String propertyName, boolean quiet) {
    switch (propertyName.hashCode()) {
      case -281470431:  // classifier
        return getClassifier();
      case -300756909:  // hostName
        return getHostName();
      case 1709620380:  // redisPort
        return getRedisPort();
      case 1216985755:  // password
        return getPassword();
    }
    return super.propertyGet(propertyName, quiet);
  }

  @Override
  protected void propertySet(String propertyName, Object newValue, boolean quiet) {
    switch (propertyName.hashCode()) {
      case -281470431:  // classifier
        setClassifier((String) newValue);
        return;
      case -300756909:  // hostName
        setHostName((String) newValue);
        return;
      case 1709620380:  // redisPort
        setRedisPort((Integer) newValue);
        return;
      case 1216985755:  // password
        setPassword((String) newValue);
        return;
    }
    super.propertySet(propertyName, newValue, quiet);
  }

  @Override
  protected void validate() {
    JodaBeanUtils.notEmpty(_classifier, "classifier");
    JodaBeanUtils.notEmpty(_hostName, "hostName");
    super.validate();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      RedisConnectorComponentFactory other = (RedisConnectorComponentFactory) obj;
      return JodaBeanUtils.equal(getClassifier(), other.getClassifier()) &&
          JodaBeanUtils.equal(getHostName(), other.getHostName()) &&
          JodaBeanUtils.equal(getRedisPort(), other.getRedisPort()) &&
          JodaBeanUtils.equal(getPassword(), other.getPassword()) &&
          super.equals(obj);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash += hash * 31 + JodaBeanUtils.hashCode(getClassifier());
    hash += hash * 31 + JodaBeanUtils.hashCode(getHostName());
    hash += hash * 31 + JodaBeanUtils.hashCode(getRedisPort());
    hash += hash * 31 + JodaBeanUtils.hashCode(getPassword());
    return hash ^ super.hashCode();
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the classifier.
   * @return the value of the property, not null
   */
  public String getClassifier() {
    return _classifier;
  }

  /**
   * Sets the classifier.
   * @param classifier  the new value of the property, not null
   */
  public void setClassifier(String classifier) {
    JodaBeanUtils.notEmpty(classifier, "classifier");
    this._classifier = classifier;
  }

  /**
   * Gets the the {@code classifier} property.
   * @return the property, not null
   */
  public final Property<String> classifier() {
    return metaBean().classifier().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the hostName.
   * @return the value of the property, not null
   */
  public String getHostName() {
    return _hostName;
  }

  /**
   * Sets the hostName.
   * @param hostName  the new value of the property, not null
   */
  public void setHostName(String hostName) {
    JodaBeanUtils.notEmpty(hostName, "hostName");
    this._hostName = hostName;
  }

  /**
   * Gets the the {@code hostName} property.
   * @return the property, not null
   */
  public final Property<String> hostName() {
    return metaBean().hostName().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the redisPort.
   * @return the value of the property
   */
  public int getRedisPort() {
    return _redisPort;
  }

  /**
   * Sets the redisPort.
   * @param redisPort  the new value of the property
   */
  public void setRedisPort(int redisPort) {
    this._redisPort = redisPort;
  }

  /**
   * Gets the the {@code redisPort} property.
   * @return the property, not null
   */
  public final Property<Integer> redisPort() {
    return metaBean().redisPort().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the password.
   * @return the value of the property
   */
  public String getPassword() {
    return _password;
  }

  /**
   * Sets the password.
   * @param password  the new value of the property
   */
  public void setPassword(String password) {
    this._password = password;
  }

  /**
   * Gets the the {@code password} property.
   * @return the property, not null
   */
  public final Property<String> password() {
    return metaBean().password().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code RedisConnectorComponentFactory}.
   */
  public static class Meta extends AbstractComponentFactory.Meta {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code classifier} property.
     */
    private final MetaProperty<String> _classifier = DirectMetaProperty.ofReadWrite(
        this, "classifier", RedisConnectorComponentFactory.class, String.class);
    /**
     * The meta-property for the {@code hostName} property.
     */
    private final MetaProperty<String> _hostName = DirectMetaProperty.ofReadWrite(
        this, "hostName", RedisConnectorComponentFactory.class, String.class);
    /**
     * The meta-property for the {@code redisPort} property.
     */
    private final MetaProperty<Integer> _redisPort = DirectMetaProperty.ofReadWrite(
        this, "redisPort", RedisConnectorComponentFactory.class, Integer.TYPE);
    /**
     * The meta-property for the {@code password} property.
     */
    private final MetaProperty<String> _password = DirectMetaProperty.ofReadWrite(
        this, "password", RedisConnectorComponentFactory.class, String.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
        this, (DirectMetaPropertyMap) super.metaPropertyMap(),
        "classifier",
        "hostName",
        "redisPort",
        "password");

    /**
     * Restricted constructor.
     */
    protected Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case -281470431:  // classifier
          return _classifier;
        case -300756909:  // hostName
          return _hostName;
        case 1709620380:  // redisPort
          return _redisPort;
        case 1216985755:  // password
          return _password;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends RedisConnectorComponentFactory> builder() {
      return new DirectBeanBuilder<RedisConnectorComponentFactory>(new RedisConnectorComponentFactory());
    }

    @Override
    public Class<? extends RedisConnectorComponentFactory> beanType() {
      return RedisConnectorComponentFactory.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return _metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code classifier} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<String> classifier() {
      return _classifier;
    }

    /**
     * The meta-property for the {@code hostName} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<String> hostName() {
      return _hostName;
    }

    /**
     * The meta-property for the {@code redisPort} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Integer> redisPort() {
      return _redisPort;
    }

    /**
     * The meta-property for the {@code password} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<String> password() {
      return _password;
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
