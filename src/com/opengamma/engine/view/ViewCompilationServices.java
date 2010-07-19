/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.engine.view;

import java.util.concurrent.ExecutorService;

import com.opengamma.engine.ComputationTargetResolver;
import com.opengamma.engine.function.FunctionCompilationContext;
import com.opengamma.engine.function.FunctionResolver;
import com.opengamma.engine.livedata.LiveDataAvailabilityProvider;
import com.opengamma.engine.position.PositionMaster;
import com.opengamma.engine.security.SecuritySource;
import com.opengamma.util.ArgumentChecker;

// REVIEW kirk 2010-05-22 -- I don't like this name but couldn't come up with a better
// one on the fly.

/**
 * All the injected services necessary for view compilation.
 */
public class ViewCompilationServices {
  private final LiveDataAvailabilityProvider _liveDataAvailabilityProvider;
  private final FunctionResolver _functionResolver;
  private final PositionMaster _positionMaster;
  private final SecuritySource _securitySource;
  private final ExecutorService _executorService;
  private final FunctionCompilationContext _compilationContext;
  private final ComputationTargetResolver _computationTargetResolver;
  
  public ViewCompilationServices(
      LiveDataAvailabilityProvider liveDataAvailabilityProvider,
      FunctionResolver functionResolver,
      PositionMaster positionMaster,
      SecuritySource securitySource,
      FunctionCompilationContext compilationContext,
      ComputationTargetResolver computationTargetResolver,
      ExecutorService executorService) {
    ArgumentChecker.notNull(liveDataAvailabilityProvider, "LiveDataAvailabilityProvider");
    ArgumentChecker.notNull(functionResolver, "FunctionResolver");
    ArgumentChecker.notNull(positionMaster, "PositionMaster");
    ArgumentChecker.notNull(securitySource, "SecuritySource");
    ArgumentChecker.notNull(compilationContext, "CompilationContext");
    ArgumentChecker.notNull(computationTargetResolver, "Computation target resolver");
    ArgumentChecker.notNull(executorService, "ExecutorService");
    
    _liveDataAvailabilityProvider = liveDataAvailabilityProvider;
    _functionResolver = functionResolver;
    _positionMaster = positionMaster;
    _securitySource = securitySource;
    _compilationContext = compilationContext;
    _executorService = executorService;
    _computationTargetResolver = computationTargetResolver;
  }

  //-------------------------------------------------------------------------
  /**
   * Gets the live data.
   * @return the live data availability provider, not null
   */
  public LiveDataAvailabilityProvider getLiveDataAvailabilityProvider() {
    return _liveDataAvailabilityProvider;
  }

  /**
   * Gets the function resolver.
   * @return the function resolver, not null
   */
  public FunctionResolver getFunctionResolver() {
    return _functionResolver;
  }

  /**
   * Gets the source of positions.
   * @return the source of positions, not null
   */
  public PositionMaster getPositionMaster() {
    return _positionMaster;
  }

  /**
   * Gets the source of securities.
   * @return the source of securities, not null
   */
  public SecuritySource getSecurityMaster() {
    return _securitySource;
  }

  /**
   * Gets the executor service.
   * @return the executor service, not null
   */
  public ExecutorService getExecutorService() {
    return _executorService;
  }

  /**
   * Gets the compilation context.
   * @return the compilation context, not null
   */
  public FunctionCompilationContext getCompilationContext() {
    return _compilationContext;
  }

  /**
   * Gets the computation target resolver.
   * @return the computation target resolver, not null
   */
  public ComputationTargetResolver getComputationTargetResolver() {
    return _computationTargetResolver;
  }

}
