/**
 * Copyright (C) 2011 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.core.marketdatasnapshot;

import org.apache.commons.lang.ObjectUtils;
import org.fudgemsg.FudgeMsg;
import org.fudgemsg.MutableFudgeMsg;
import org.fudgemsg.mapping.FudgeDeserializer;
import org.fudgemsg.mapping.FudgeSerializer;

import com.opengamma.id.ExternalId;
import com.opengamma.id.ExternalIdBundle;
import com.opengamma.util.ArgumentChecker;

/**
 * An immutable specification of an individual piece of unstructured market data.
 * <p>
 * This class is immutable and thread-safe.
 */
public final class MarketDataValueSpecification {
  //TODO This is a whole lot like LiveDataSpecification, but decoupled.  We may want to unify them

  // REVIEW 2013-02-05 Andrew -- Nothing uses the type; types are used to provide resolution strategies for alternative identifiers for a requirement. A specification
  // like this just needs the identifier. This whole class can then be removed; only the identifier is needed for its purpose.

  /**
   * The identifier(s) of the target.
   */
  private final ExternalIdBundle _identifiers;

  /**
   * Creates an instance for a type of market data and an external identifier.
   * 
   * @param identifier an identifier of the data this refers to, for example a ticker, not null
   */
  public MarketDataValueSpecification(final ExternalId identifier) {
    ArgumentChecker.notNull(identifier, "identifier");
    _identifiers = identifier.toBundle();
  }

  public MarketDataValueSpecification(final ExternalIdBundle identifiers) {
    ArgumentChecker.notNull(identifiers, "identifiers");
    _identifiers = identifiers;
  }

  /**
   * Gets the identifier(s) of the data.
   * 
   * @return the identifiers
   */
  public ExternalIdBundle getIdentifiers() {
    return _identifiers;
  }

  //-------------------------------------------------------------------------
  /**
   * Checks if this specification equals another.
   * <p>
   * This checks the type and unique identifier.
   * 
   * @param object the object to compare to, null returns false
   * @return true if equal
   */
  @Override
  public boolean equals(final Object object) {
    if (object == this) {
      return true;
    }
    if (object instanceof MarketDataValueSpecification) {
      final MarketDataValueSpecification other = (MarketDataValueSpecification) object;
      return ObjectUtils.equals(getIdentifiers(), other.getIdentifiers());
    }
    return false;
  }

  /**
   * Returns a suitable hash code.
   * 
   * @return the hash code
   */
  @Override
  public int hashCode() {
    return ObjectUtils.hashCode(getIdentifiers());
  }

  /**
   * Creates a Fudge representation of the value specification:
   * 
   * <pre>
   *   message {
   *     ExternalIdBundle identifiers;
   *   }
   * </pre>
   * 
   * @param serializer Fudge serialization context, not null
   * @return the message representation of this value specification
   */
  public MutableFudgeMsg toFudgeMsg(final FudgeSerializer serializer) {
    final MutableFudgeMsg msg = serializer.newMessage();
    serializer.addToMessage(msg, "identifiers", null, _identifiers);
    return msg;
  }

  public static MarketDataValueSpecification fromFudgeMsg(final FudgeDeserializer deserializer, final FudgeMsg msg) {
    final ExternalIdBundle identifiers = deserializer.fieldValueToObject(ExternalIdBundle.class, msg.getByName("identifiers"));
    return new MarketDataValueSpecification(identifiers);
  }

}
