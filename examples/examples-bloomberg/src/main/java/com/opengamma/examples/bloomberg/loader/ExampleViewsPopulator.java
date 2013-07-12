/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.examples.bloomberg.loader;

import static com.opengamma.engine.value.ValuePropertyNames.AGGREGATION;
import static com.opengamma.engine.value.ValuePropertyNames.CALCULATION_METHOD;
import static com.opengamma.engine.value.ValuePropertyNames.CURRENCY;
import static com.opengamma.engine.value.ValuePropertyNames.CURVE;
import static com.opengamma.engine.value.ValuePropertyNames.CURVE_CALCULATION_CONFIG;
import static com.opengamma.engine.value.ValuePropertyNames.CURVE_CALCULATION_METHOD;
import static com.opengamma.engine.value.ValuePropertyNames.CURVE_CURRENCY;
import static com.opengamma.engine.value.ValuePropertyNames.SURFACE;
import static com.opengamma.engine.value.ValueRequirementNames.CAPM_BETA;
import static com.opengamma.engine.value.ValueRequirementNames.CARRY_RHO;
import static com.opengamma.engine.value.ValueRequirementNames.DELTA;
import static com.opengamma.engine.value.ValueRequirementNames.DELTA_BLEED;
import static com.opengamma.engine.value.ValueRequirementNames.DRIFTLESS_THETA;
import static com.opengamma.engine.value.ValueRequirementNames.DVANNA_DVOL;
import static com.opengamma.engine.value.ValueRequirementNames.DZETA_DVOL;
import static com.opengamma.engine.value.ValueRequirementNames.ELASTICITY;
import static com.opengamma.engine.value.ValueRequirementNames.FAIR_VALUE;
import static com.opengamma.engine.value.ValueRequirementNames.FX_CURRENCY_EXPOSURE;
import static com.opengamma.engine.value.ValueRequirementNames.GAMMA;
import static com.opengamma.engine.value.ValueRequirementNames.GAMMA_BLEED;
import static com.opengamma.engine.value.ValueRequirementNames.GAMMA_P;
import static com.opengamma.engine.value.ValueRequirementNames.GAMMA_P_BLEED;
import static com.opengamma.engine.value.ValueRequirementNames.HISTORICAL_VAR;
import static com.opengamma.engine.value.ValueRequirementNames.HISTORICAL_VAR_STDDEV;
import static com.opengamma.engine.value.ValueRequirementNames.JENSENS_ALPHA;
import static com.opengamma.engine.value.ValueRequirementNames.PAR_RATE;
import static com.opengamma.engine.value.ValueRequirementNames.PHI;
import static com.opengamma.engine.value.ValueRequirementNames.PNL_SERIES;
import static com.opengamma.engine.value.ValueRequirementNames.PRESENT_VALUE;
import static com.opengamma.engine.value.ValueRequirementNames.PRESENT_VALUE_SABR_ALPHA_SENSITIVITY;
import static com.opengamma.engine.value.ValueRequirementNames.PRESENT_VALUE_SABR_NU_SENSITIVITY;
import static com.opengamma.engine.value.ValueRequirementNames.PRESENT_VALUE_SABR_RHO_SENSITIVITY;
import static com.opengamma.engine.value.ValueRequirementNames.PV01;
import static com.opengamma.engine.value.ValueRequirementNames.RHO;
import static com.opengamma.engine.value.ValueRequirementNames.SECURITY_IMPLIED_VOLATILITY;
import static com.opengamma.engine.value.ValueRequirementNames.SECURITY_MARKET_PRICE;
import static com.opengamma.engine.value.ValueRequirementNames.SHARPE_RATIO;
import static com.opengamma.engine.value.ValueRequirementNames.SPEED;
import static com.opengamma.engine.value.ValueRequirementNames.SPEED_P;
import static com.opengamma.engine.value.ValueRequirementNames.STRIKE_GAMMA;
import static com.opengamma.engine.value.ValueRequirementNames.THETA;
import static com.opengamma.engine.value.ValueRequirementNames.TOTAL_RISK_ALPHA;
import static com.opengamma.engine.value.ValueRequirementNames.TREYNOR_RATIO;
import static com.opengamma.engine.value.ValueRequirementNames.ULTIMA;
import static com.opengamma.engine.value.ValueRequirementNames.VALUE_DELTA;
import static com.opengamma.engine.value.ValueRequirementNames.VALUE_GAMMA;
import static com.opengamma.engine.value.ValueRequirementNames.VALUE_GAMMA_P;
import static com.opengamma.engine.value.ValueRequirementNames.VALUE_PHI;
import static com.opengamma.engine.value.ValueRequirementNames.VALUE_RHO;
import static com.opengamma.engine.value.ValueRequirementNames.VALUE_THETA;
import static com.opengamma.engine.value.ValueRequirementNames.VALUE_VANNA;
import static com.opengamma.engine.value.ValueRequirementNames.VALUE_VEGA;
import static com.opengamma.engine.value.ValueRequirementNames.VALUE_VOMMA;
import static com.opengamma.engine.value.ValueRequirementNames.VANNA;
import static com.opengamma.engine.value.ValueRequirementNames.VARIANCE_ULTIMA;
import static com.opengamma.engine.value.ValueRequirementNames.VARIANCE_VANNA;
import static com.opengamma.engine.value.ValueRequirementNames.VARIANCE_VEGA;
import static com.opengamma.engine.value.ValueRequirementNames.VARIANCE_VOMMA;
import static com.opengamma.engine.value.ValueRequirementNames.VEGA;
import static com.opengamma.engine.value.ValueRequirementNames.VEGA_BLEED;
import static com.opengamma.engine.value.ValueRequirementNames.VEGA_MATRIX;
import static com.opengamma.engine.value.ValueRequirementNames.VEGA_P;
import static com.opengamma.engine.value.ValueRequirementNames.VEGA_QUOTE_MATRIX;
import static com.opengamma.engine.value.ValueRequirementNames.VOLATILITY_SURFACE_DATA;
import static com.opengamma.engine.value.ValueRequirementNames.VOMMA;
import static com.opengamma.engine.value.ValueRequirementNames.VOMMA_P;
import static com.opengamma.engine.value.ValueRequirementNames.YIELD_CURVE;
import static com.opengamma.engine.value.ValueRequirementNames.YIELD_CURVE_JACOBIAN;
import static com.opengamma.engine.value.ValueRequirementNames.YIELD_CURVE_NODE_SENSITIVITIES;
import static com.opengamma.financial.analytics.model.curve.interestrate.MultiYieldCurvePropertiesAndDefaults.PAR_RATE_STRING;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opengamma.OpenGammaRuntimeException;
import com.opengamma.component.tool.AbstractTool;
import com.opengamma.core.config.impl.ConfigItem;
import com.opengamma.engine.ComputationTargetSpecification;
import com.opengamma.engine.value.ValueProperties;
import com.opengamma.engine.value.ValuePropertyNames;
import com.opengamma.engine.value.ValueRequirement;
import com.opengamma.engine.view.ViewCalculationConfiguration;
import com.opengamma.engine.view.ViewDefinition;
import com.opengamma.financial.analytics.MissingInputsFunction;
import com.opengamma.financial.analytics.model.CalculationPropertyNamesAndValues;
import com.opengamma.financial.analytics.model.InstrumentTypeProperties;
import com.opengamma.financial.analytics.model.sabrcube.SABRFunction;
import com.opengamma.financial.currency.CurrencyConversionFunction;
import com.opengamma.financial.security.capfloor.CapFloorCMSSpreadSecurity;
import com.opengamma.financial.security.capfloor.CapFloorSecurity;
import com.opengamma.financial.security.equity.EquitySecurity;
import com.opengamma.financial.security.fx.FXForwardSecurity;
import com.opengamma.financial.security.option.EquityOptionSecurity;
import com.opengamma.financial.security.option.FXOptionSecurity;
import com.opengamma.financial.security.option.SwaptionSecurity;
import com.opengamma.financial.security.swap.SwapSecurity;
import com.opengamma.id.UniqueId;
import com.opengamma.integration.tool.IntegrationToolContext;
import com.opengamma.livedata.UserPrincipal;
import com.opengamma.master.config.ConfigMasterUtils;
import com.opengamma.master.portfolio.PortfolioSearchRequest;
import com.opengamma.master.portfolio.PortfolioSearchResult;
import com.opengamma.scripts.Scriptable;
import com.opengamma.util.money.Currency;
import com.opengamma.util.money.UnorderedCurrencyPair;
import com.opengamma.util.tuple.Pair;

/**
 * Example code to create a set of example views.
 * <p>
 * It is designed to run against the HSQLDB example database.
 */
@Scriptable
public class ExampleViewsPopulator extends AbstractTool<IntegrationToolContext> {
  private static final String DEFAULT_CALC_CONFIG = "Default";
  /** Logger. */
  private static final Logger s_logger = LoggerFactory.getLogger(ExampleViewsPopulator.class);
  private static final String MISSING_INPUTS = MissingInputsFunction.AGGREGATION_STYLE_MISSING;
  /** Map of currencies to curves */
  public static final Map<Currency, Pair<String, String>> CURVES_FOR_CURRENCY = new HashMap<>();
  /** Map of currencies to curve calculation configurations */
  public static final Map<Currency, String> CONFIGS_FOR_CURRENCY = new HashMap<>();

  static {
    CURVES_FOR_CURRENCY.put(Currency.USD, Pair.of("Discounting", "Forward3M"));
    CURVES_FOR_CURRENCY.put(Currency.EUR, Pair.of("Discounting", "Forward6M"));
    CURVES_FOR_CURRENCY.put(Currency.JPY, Pair.of("Discounting", "Forward6M"));
    CURVES_FOR_CURRENCY.put(Currency.CHF, Pair.of("Discounting", "Forward6M"));
    CURVES_FOR_CURRENCY.put(Currency.GBP, Pair.of("Discounting", "Forward6M"));
    CONFIGS_FOR_CURRENCY.put(Currency.USD, "DefaultTwoCurveUSDConfig");
    CONFIGS_FOR_CURRENCY.put(Currency.EUR, "DefaultTwoCurveEURConfig");
    CONFIGS_FOR_CURRENCY.put(Currency.JPY, "DefaultTwoCurveJPYConfig");
    CONFIGS_FOR_CURRENCY.put(Currency.CHF, "DefaultTwoCurveCHFConfig");
    CONFIGS_FOR_CURRENCY.put(Currency.GBP, "DefaultTwoCurveGBPConfig");
  }
  //-------------------------------------------------------------------------
  /**
   * Main method to run the tool. No arguments are needed.
   *
   * @param args the arguments, unused
   */
  public static void main(final String[] args) { // CSIGNORE
    new ExampleViewsPopulator().initAndRun(args, IntegrationToolContext.class);
    System.exit(0);
  }

  //-------------------------------------------------------------------------
  @Override
  protected void doRun() {
    storeViewDefinition(getEquityViewDefinition(ExampleEquityPortfolioLoader.PORTFOLIO_NAME, "Equity View"));
    storeViewDefinition(getMultiCurrencySwapViewDefinition(ExampleMultiCurrencySwapPortfolioLoader.PORTFOLIO_NAME));
    storeViewDefinition(getEquityOptionViewDefinition(DemoEquityOptionCollarPortfolioLoader.PORTFOLIO_NAME, "Equity / Equity Option View"));
    storeViewDefinition(getFXOptionViewDefinition(ExampleVanillaFxOptionPortfolioLoader.PORTFOLIO_NAME, "FX Option View"));
    storeViewDefinition(getFXOptionGreeksViewDefinition(ExampleVanillaFxOptionPortfolioLoader.PORTFOLIO_NAME, "FX Option Greeks View"));
    storeViewDefinition(getAUDSwapView1Definition(ExampleAUDSwapPortfolioLoader.PORTFOLIO_NAME));
    storeViewDefinition(getAUDSwapView2Definition(ExampleAUDSwapPortfolioLoader.PORTFOLIO_NAME));
    storeViewDefinition(getAUDSwapView3Definition(ExampleAUDSwapPortfolioLoader.PORTFOLIO_NAME));
    storeViewDefinition(getBlackSwaptionViewDefinition(ExampleSwaptionPortfolioLoader.PORTFOLIO_NAME, "Black Swaption Pricing View"));
    storeViewDefinition(getSABRSwaptionViewDefinition(ExampleSwaptionPortfolioLoader.PORTFOLIO_NAME, "SABR Swaption Pricing View"));
    storeViewDefinition(getSABRExtrapolationViewDefinition(ExampleMixedCMCapFloorPortfolioLoader.PORTFOLIO_NAME, "Constant Maturity Swap / Cap-Floor View"));
    storeViewDefinition(getFXForwardViewDefinition(ExampleFxForwardPortfolioLoader.PORTFOLIO_NAME, "FX Forward View"));
  }

  private ViewDefinition getEquityOptionViewDefinition(final String portfolioName, final String viewName) {
    final UniqueId portfolioId = getPortfolioId(portfolioName).toLatest();
    final ViewDefinition viewDefinition = new ViewDefinition(viewName, portfolioId, UserPrincipal.getTestUser());
    viewDefinition.setDefaultCurrency(Currency.USD);
    viewDefinition.setMaxFullCalculationPeriod(30000L);
    viewDefinition.setMinFullCalculationPeriod(500L);
    viewDefinition.setMinDeltaCalculationPeriod(500L);
    viewDefinition.setMaxDeltaCalculationPeriod(30000L);
    final ViewCalculationConfiguration defaultCalConfig = new ViewCalculationConfiguration(viewDefinition, DEFAULT_CALC_CONFIG);
    addValueRequirements(defaultCalConfig, EquityOptionSecurity.SECURITY_TYPE, new String[]{
      CARRY_RHO,
      DELTA,
      DELTA_BLEED,
      DRIFTLESS_THETA,
      ELASTICITY,
      GAMMA,
      GAMMA_BLEED,
      GAMMA_P,
      GAMMA_P_BLEED,
      HISTORICAL_VAR,
      HISTORICAL_VAR_STDDEV,
      JENSENS_ALPHA,
      PNL_SERIES,
      PHI,
      RHO,
      SPEED,
      SPEED_P,
      STRIKE_GAMMA,
      THETA,
      ULTIMA,
      VALUE_DELTA,
      VALUE_GAMMA,
      VANNA,
      VARIANCE_ULTIMA,
      VARIANCE_VANNA,
      VARIANCE_VEGA,
      VARIANCE_VOMMA,
      VEGA,
      VEGA_BLEED,
      VEGA_P,
      VOMMA,
      VOMMA_P,
      DVANNA_DVOL,
      DZETA_DVOL
    }, ValueProperties.with(AGGREGATION, MISSING_INPUTS).withOptional(AGGREGATION).get());
    addValueRequirements(defaultCalConfig, EquitySecurity.SECURITY_TYPE, new String[]{
      CAPM_BETA,
      FAIR_VALUE,
      HISTORICAL_VAR,
      HISTORICAL_VAR_STDDEV,
      JENSENS_ALPHA,
      PNL_SERIES,
      SHARPE_RATIO,
      TOTAL_RISK_ALPHA,
      TREYNOR_RATIO,
      SECURITY_MARKET_PRICE
    }, ValueProperties.with(AGGREGATION, MISSING_INPUTS).withOptional(AGGREGATION).get());
    viewDefinition.addViewCalculationConfiguration(defaultCalConfig);
    return viewDefinition;
  }

  private ViewDefinition getEquityViewDefinition(final String portfolioName, final String viewName) {
    final UniqueId portfolioId = getPortfolioId(portfolioName).toLatest();
    final ViewDefinition viewDefinition = new ViewDefinition(viewName, portfolioId, UserPrincipal.getTestUser());
    viewDefinition.setDefaultCurrency(Currency.USD);
    viewDefinition.setMaxFullCalculationPeriod(30000L);
    viewDefinition.setMinFullCalculationPeriod(500L);
    viewDefinition.setMinDeltaCalculationPeriod(500L);
    viewDefinition.setMaxDeltaCalculationPeriod(30000L);
    final ViewCalculationConfiguration defaultCalConfig = new ViewCalculationConfiguration(viewDefinition, DEFAULT_CALC_CONFIG);

    addValueRequirements(defaultCalConfig, EquitySecurity.SECURITY_TYPE, new String[]{
      CAPM_BETA,
      FAIR_VALUE,
      HISTORICAL_VAR,
      HISTORICAL_VAR_STDDEV,
      JENSENS_ALPHA,
      PNL_SERIES,
      SHARPE_RATIO,
      TOTAL_RISK_ALPHA,
      TREYNOR_RATIO,
      SECURITY_MARKET_PRICE
    }, ValueProperties.with(AGGREGATION, MISSING_INPUTS).withOptional(AGGREGATION).get());
    viewDefinition.addViewCalculationConfiguration(defaultCalConfig);
    return viewDefinition;
  }

  private void addValueRequirements(final ViewCalculationConfiguration calcConfiguration, final String securityType, final String[] valueRequirementNames,
      final ValueProperties properties) {
    for (final String valueRequirementName : valueRequirementNames) {
      calcConfiguration.addPortfolioRequirement(securityType, valueRequirementName, properties);
    }
  }

  private UniqueId getPortfolioId(final String portfolioName) {
    final PortfolioSearchRequest searchRequest = new PortfolioSearchRequest();
    searchRequest.setName(portfolioName);
    final PortfolioSearchResult searchResult = getToolContext().getPortfolioMaster().search(searchRequest);
    if (searchResult.getFirstPortfolio() == null) {
      s_logger.error("Couldn't find portfolio {}", portfolioName);
      throw new OpenGammaRuntimeException("Couldn't find portfolio" + portfolioName);
    }
    return searchResult.getFirstPortfolio().getUniqueId();
  }

  private ViewDefinition getMultiCurrencySwapViewDefinition(final String portfolioName) {
    final UniqueId portfolioId = getPortfolioId(portfolioName).toLatest();
    final ViewDefinition viewDefinition = new ViewDefinition(portfolioName + " View", portfolioId, UserPrincipal.getTestUser());
    viewDefinition.setDefaultCurrency(Currency.USD);
    viewDefinition.setMaxDeltaCalculationPeriod(500L);
    viewDefinition.setMaxFullCalculationPeriod(500L);
    viewDefinition.setMinDeltaCalculationPeriod(500L);
    viewDefinition.setMinFullCalculationPeriod(500L);
    final ViewCalculationConfiguration defaultCalConfig = new ViewCalculationConfiguration(viewDefinition, DEFAULT_CALC_CONFIG);
    defaultCalConfig.addPortfolioRequirementName(SwapSecurity.SECURITY_TYPE, PAR_RATE);
    // The name "Default" has no special meaning, but means that the currency conversion function can never be used and so we get the instrument's natural currency
    defaultCalConfig.addPortfolioRequirement(SwapSecurity.SECURITY_TYPE, PRESENT_VALUE,
        ValueProperties.with(CurrencyConversionFunction.ORIGINAL_CURRENCY, "Default").withOptional(CurrencyConversionFunction.ORIGINAL_CURRENCY).get());
    defaultCalConfig.addPortfolioRequirement(SwapSecurity.SECURITY_TYPE, PRESENT_VALUE,
        ValueProperties.with(CURRENCY, "USD").get());
    for (final Map.Entry<Currency, String> entry : CONFIGS_FOR_CURRENCY.entrySet()) {
      final String ccyName = entry.getKey().getCode();
      final ComputationTargetSpecification ccyTarget = ComputationTargetSpecification.of(entry.getKey());
      final Pair<String, String> curveNames = CURVES_FOR_CURRENCY.get(entry.getKey());
      final String discountingCurve = curveNames.getFirst();
      final String forwardCurve = curveNames.getSecond();
      defaultCalConfig.addPortfolioRequirement(SwapSecurity.SECURITY_TYPE, PV01,
          ValueProperties.with(CURVE, discountingCurve).with(CURVE_CURRENCY, ccyName)
              .with(ValuePropertyNames.AGGREGATION, MISSING_INPUTS).withOptional(ValuePropertyNames.AGGREGATION).get());
      defaultCalConfig.addPortfolioRequirement(SwapSecurity.SECURITY_TYPE, YIELD_CURVE_NODE_SENSITIVITIES,
          ValueProperties.with(CURVE, discountingCurve).with(CURVE_CURRENCY, ccyName)
              .with(ValuePropertyNames.AGGREGATION, MISSING_INPUTS).withOptional(ValuePropertyNames.AGGREGATION).get());
      defaultCalConfig.addSpecificRequirement(new ValueRequirement(YIELD_CURVE, ccyTarget,
          ValueProperties.with(CURVE, discountingCurve).with(CURVE_CALCULATION_CONFIG, entry.getValue())
              .with(ValuePropertyNames.AGGREGATION, MISSING_INPUTS).withOptional(ValuePropertyNames.AGGREGATION).get()));
      defaultCalConfig.addPortfolioRequirement(SwapSecurity.SECURITY_TYPE, YIELD_CURVE_NODE_SENSITIVITIES,
          ValueProperties.with(CURVE, forwardCurve).with(CURVE_CURRENCY, ccyName)
              .with(ValuePropertyNames.AGGREGATION, MISSING_INPUTS).withOptional(ValuePropertyNames.AGGREGATION).get());
      defaultCalConfig.addPortfolioRequirement(SwapSecurity.SECURITY_TYPE, PV01,
          ValueProperties.with(CURVE, forwardCurve).with(CURVE_CURRENCY, ccyName)
              .with(ValuePropertyNames.AGGREGATION, MISSING_INPUTS).withOptional(ValuePropertyNames.AGGREGATION).get());
      defaultCalConfig.addSpecificRequirement(new ValueRequirement(YIELD_CURVE, ccyTarget,
          ValueProperties.with(CURVE, forwardCurve).with(CURVE_CALCULATION_CONFIG, entry.getValue())
              .with(ValuePropertyNames.AGGREGATION, MISSING_INPUTS).withOptional(ValuePropertyNames.AGGREGATION).get()));
    }
    viewDefinition.addViewCalculationConfiguration(defaultCalConfig);
    return viewDefinition;
  }

  private ViewDefinition getFXOptionViewDefinition(final String portfolioName, final String viewName) {
    final UniqueId portfolioId = getPortfolioId(portfolioName).toLatest();
    final ViewDefinition viewDefinition = new ViewDefinition(viewName, portfolioId, UserPrincipal.getTestUser());
    viewDefinition.setDefaultCurrency(Currency.USD);
    viewDefinition.setMaxDeltaCalculationPeriod(500L);
    viewDefinition.setMaxFullCalculationPeriod(500L);
    viewDefinition.setMinDeltaCalculationPeriod(500L);
    viewDefinition.setMinFullCalculationPeriod(500L);
    final ViewCalculationConfiguration defaultCalculationConfig = new ViewCalculationConfiguration(viewDefinition, DEFAULT_CALC_CONFIG);
    final Set<Currency> ccysAdded = new HashSet<>();
    for (final UnorderedCurrencyPair pair : ExampleVanillaFxOptionPortfolioLoader.CCYS) {
      final ComputationTargetSpecification target = ComputationTargetSpecification.of(pair.getUniqueId());
      final ValueProperties properties = ValueProperties.builder()
          .with(SURFACE, "DEFAULT")
          .with(InstrumentTypeProperties.PROPERTY_SURFACE_INSTRUMENT_TYPE, InstrumentTypeProperties.FOREX)
          .get();
      defaultCalculationConfig.addSpecificRequirement(new ValueRequirement(VOLATILITY_SURFACE_DATA, target, properties));
      defaultCalculationConfig.addPortfolioRequirement(FXOptionSecurity.SECURITY_TYPE, VEGA_QUOTE_MATRIX, properties);
      defaultCalculationConfig.addPortfolioRequirement(FXOptionSecurity.SECURITY_TYPE, VEGA_MATRIX, properties);
      if (!ccysAdded.contains(pair.getFirstCurrency())) {
        final String ccy = pair.getFirstCurrency().getCode();
        final String discountingCurve = CURVES_FOR_CURRENCY.get(pair.getFirstCurrency()).getFirst();
        final ValueProperties curveProperties = ValueProperties.builder()
            .with(CURVE, discountingCurve)
            .with(CURVE_CURRENCY, ccy)
            .get();
        defaultCalculationConfig.addPortfolioRequirement(FXOptionSecurity.SECURITY_TYPE, YIELD_CURVE_NODE_SENSITIVITIES, curveProperties);
        ccysAdded.add(pair.getFirstCurrency());
      }
      if (!ccysAdded.contains(pair.getSecondCurrency())) {
        final String ccy = pair.getSecondCurrency().getCode();
        final String discountingCurve = CURVES_FOR_CURRENCY.get(pair.getSecondCurrency()).getFirst();
        final ValueProperties curveProperties = ValueProperties.builder()
            .with(CURVE, discountingCurve)
            .with(CURVE_CURRENCY, ccy)
            .get();
        defaultCalculationConfig.addPortfolioRequirement(FXOptionSecurity.SECURITY_TYPE, YIELD_CURVE_NODE_SENSITIVITIES, curveProperties);
        ccysAdded.add(pair.getSecondCurrency());
      }
    }
    final ValueProperties currencyProperty = ValueProperties.builder()
        .with(ValuePropertyNames.CURRENCY, "USD")
        .get();
    defaultCalculationConfig.addPortfolioRequirement(FXOptionSecurity.SECURITY_TYPE, PRESENT_VALUE, currencyProperty);
    defaultCalculationConfig.addPortfolioRequirement(FXOptionSecurity.SECURITY_TYPE, FX_CURRENCY_EXPOSURE, ValueProperties.builder().get());
    viewDefinition.addViewCalculationConfiguration(defaultCalculationConfig);
    return viewDefinition;
  }

  private ViewDefinition getFXOptionGreeksViewDefinition(final String portfolioName, final String viewName) {
    final UniqueId portfolioId = getPortfolioId(portfolioName).toLatest();
    final ViewDefinition viewDefinition = new ViewDefinition(viewName, portfolioId, UserPrincipal.getTestUser());
    viewDefinition.setDefaultCurrency(Currency.USD);
    viewDefinition.setMaxDeltaCalculationPeriod(500L);
    viewDefinition.setMaxFullCalculationPeriod(500L);
    viewDefinition.setMinDeltaCalculationPeriod(500L);
    viewDefinition.setMinFullCalculationPeriod(500L);
    final ViewCalculationConfiguration defaultCalculationConfig = new ViewCalculationConfiguration(viewDefinition, DEFAULT_CALC_CONFIG);
    final ValueProperties currencyProperty = ValueProperties.builder()
        .with(ValuePropertyNames.CURRENCY, "USD")
        .get();
    defaultCalculationConfig.addPortfolioRequirement(FXOptionSecurity.SECURITY_TYPE, PRESENT_VALUE, currencyProperty);
    defaultCalculationConfig.addPortfolioRequirement(FXOptionSecurity.SECURITY_TYPE, VALUE_DELTA, currencyProperty);
    defaultCalculationConfig.addPortfolioRequirement(FXOptionSecurity.SECURITY_TYPE, VALUE_VEGA, currencyProperty);
    defaultCalculationConfig.addPortfolioRequirement(FXOptionSecurity.SECURITY_TYPE, VALUE_GAMMA, currencyProperty);
    defaultCalculationConfig.addPortfolioRequirement(FXOptionSecurity.SECURITY_TYPE, VALUE_GAMMA_P, currencyProperty);
    defaultCalculationConfig.addPortfolioRequirement(FXOptionSecurity.SECURITY_TYPE, VALUE_RHO, currencyProperty);
    defaultCalculationConfig.addPortfolioRequirement(FXOptionSecurity.SECURITY_TYPE, VALUE_PHI, currencyProperty);
    defaultCalculationConfig.addPortfolioRequirement(FXOptionSecurity.SECURITY_TYPE, VALUE_VOMMA, currencyProperty);
    defaultCalculationConfig.addPortfolioRequirement(FXOptionSecurity.SECURITY_TYPE, VALUE_VANNA, currencyProperty);
    defaultCalculationConfig.addPortfolioRequirement(FXOptionSecurity.SECURITY_TYPE, VALUE_THETA, currencyProperty);
    defaultCalculationConfig.addPortfolioRequirement(FXOptionSecurity.SECURITY_TYPE, SECURITY_IMPLIED_VOLATILITY, ValueProperties.builder().get());
    viewDefinition.addViewCalculationConfiguration(defaultCalculationConfig);
    return viewDefinition;
  }

  private ViewDefinition getAUDSwapView1Definition(final String portfolioName) {
    final UniqueId portfolioId = getPortfolioId(portfolioName).toLatest();
    final ViewDefinition viewDefinition = new ViewDefinition("AUD Swaps (3m / 6m basis) (1)", portfolioId, UserPrincipal.getTestUser());
    viewDefinition.setDefaultCurrency(Currency.AUD);
    viewDefinition.setMaxDeltaCalculationPeriod(500L);
    viewDefinition.setMaxFullCalculationPeriod(500L);
    viewDefinition.setMinDeltaCalculationPeriod(500L);
    viewDefinition.setMinFullCalculationPeriod(500L);
    final String curveConfig = "DefaultThreeCurveAUDConfig";
    final ViewCalculationConfiguration defaultCalConfig = new ViewCalculationConfiguration(viewDefinition, DEFAULT_CALC_CONFIG);
    defaultCalConfig.addPortfolioRequirement(SwapSecurity.SECURITY_TYPE, PRESENT_VALUE,
        ValueProperties.with(CURVE_CALCULATION_CONFIG, curveConfig).get());
    defaultCalConfig.addPortfolioRequirement(SwapSecurity.SECURITY_TYPE, YIELD_CURVE_NODE_SENSITIVITIES,
        ValueProperties.with(CURVE, "Discounting").with(CURVE_CALCULATION_CONFIG, curveConfig).get());
    defaultCalConfig.addPortfolioRequirement(SwapSecurity.SECURITY_TYPE, YIELD_CURVE_NODE_SENSITIVITIES,
        ValueProperties.with(CURVE, "ForwardBasis3M").with(CURVE_CALCULATION_CONFIG, curveConfig).get());
    defaultCalConfig.addPortfolioRequirement(SwapSecurity.SECURITY_TYPE, YIELD_CURVE_NODE_SENSITIVITIES,
        ValueProperties.with(CURVE, "ForwardBasis6M").with(CURVE_CALCULATION_CONFIG, curveConfig).get());
    defaultCalConfig.addSpecificRequirement(new ValueRequirement(YIELD_CURVE, ComputationTargetSpecification.of(Currency.AUD),
        ValueProperties.with(CURVE, "Discounting").with(CURVE_CALCULATION_METHOD, PAR_RATE_STRING).with(CURVE_CALCULATION_CONFIG, curveConfig).get()));
    defaultCalConfig.addSpecificRequirement(new ValueRequirement(YIELD_CURVE, ComputationTargetSpecification.of(Currency.AUD),
        ValueProperties.with(CURVE, "ForwardBasis3M").with(CURVE_CALCULATION_METHOD, PAR_RATE_STRING).with(CURVE_CALCULATION_CONFIG, curveConfig).get()));
    defaultCalConfig.addSpecificRequirement(new ValueRequirement(YIELD_CURVE, ComputationTargetSpecification.of(Currency.AUD),
        ValueProperties.with(CURVE, "ForwardBasis6M").with(CURVE_CALCULATION_METHOD, PAR_RATE_STRING).with(CURVE_CALCULATION_CONFIG, curveConfig).get()));
    defaultCalConfig.addSpecificRequirement(new ValueRequirement(YIELD_CURVE_JACOBIAN, ComputationTargetSpecification.of(Currency.AUD),
        ValueProperties.with(CURVE_CALCULATION_METHOD, PAR_RATE_STRING).with(CURVE_CALCULATION_CONFIG, curveConfig).get()));
    viewDefinition.addViewCalculationConfiguration(defaultCalConfig);
    return viewDefinition;
  }

  private ViewDefinition getAUDSwapView2Definition(final String portfolioName) {
    final UniqueId portfolioId = getPortfolioId(portfolioName).toLatest();
    final ViewDefinition viewDefinition = new ViewDefinition("AUD Swaps (3m / 6m basis) (2)", portfolioId, UserPrincipal.getTestUser());
    viewDefinition.setDefaultCurrency(Currency.AUD);
    viewDefinition.setMaxDeltaCalculationPeriod(500L);
    viewDefinition.setMaxFullCalculationPeriod(500L);
    viewDefinition.setMinDeltaCalculationPeriod(500L);
    viewDefinition.setMinFullCalculationPeriod(500L);
    final String curveConfig = "ForwardFromDiscountingAUDConfig";
    final ViewCalculationConfiguration defaultCalConfig = new ViewCalculationConfiguration(viewDefinition, DEFAULT_CALC_CONFIG);
    defaultCalConfig.addPortfolioRequirement(SwapSecurity.SECURITY_TYPE, PRESENT_VALUE,
        ValueProperties.with(CURVE_CALCULATION_CONFIG, curveConfig).get());
    defaultCalConfig.addPortfolioRequirement(SwapSecurity.SECURITY_TYPE, YIELD_CURVE_NODE_SENSITIVITIES,
        ValueProperties.with(CURVE, "ForwardBasis3M").with(CURVE_CALCULATION_CONFIG, curveConfig).get());
    defaultCalConfig.addPortfolioRequirement(SwapSecurity.SECURITY_TYPE, YIELD_CURVE_NODE_SENSITIVITIES,
        ValueProperties.with(CURVE, "ForwardBasis6M").with(CURVE_CALCULATION_CONFIG, curveConfig).get());
    defaultCalConfig.addSpecificRequirement(new ValueRequirement(YIELD_CURVE, ComputationTargetSpecification.of(Currency.AUD),
        ValueProperties.with(CURVE, "Discounting").with(CURVE_CALCULATION_METHOD, PAR_RATE_STRING).with(CURVE_CALCULATION_CONFIG, curveConfig).get()));
    defaultCalConfig.addSpecificRequirement(new ValueRequirement(YIELD_CURVE, ComputationTargetSpecification.of(Currency.AUD),
        ValueProperties.with(CURVE, "ForwardBasis3M").with(CURVE_CALCULATION_METHOD, PAR_RATE_STRING).with(CURVE_CALCULATION_CONFIG, curveConfig).get()));
    defaultCalConfig.addSpecificRequirement(new ValueRequirement(YIELD_CURVE, ComputationTargetSpecification.of(Currency.AUD),
        ValueProperties.with(CURVE, "ForwardBasis6M").with(CURVE_CALCULATION_METHOD, PAR_RATE_STRING).with(CURVE_CALCULATION_CONFIG, curveConfig).get()));
    defaultCalConfig.addSpecificRequirement(new ValueRequirement(YIELD_CURVE_JACOBIAN, ComputationTargetSpecification.of(Currency.AUD),
        ValueProperties.with(CURVE_CALCULATION_METHOD, PAR_RATE_STRING).with(CURVE_CALCULATION_CONFIG, curveConfig).get()));
    viewDefinition.addViewCalculationConfiguration(defaultCalConfig);
    return viewDefinition;
  }

  private ViewDefinition getAUDSwapView3Definition(final String portfolioName) {
    final UniqueId portfolioId = getPortfolioId(portfolioName).toLatest();
    final ViewDefinition viewDefinition = new ViewDefinition("AUD Swaps (no basis)", portfolioId, UserPrincipal.getTestUser());
    viewDefinition.setDefaultCurrency(Currency.AUD);
    viewDefinition.setMaxDeltaCalculationPeriod(500L);
    viewDefinition.setMaxFullCalculationPeriod(500L);
    viewDefinition.setMinDeltaCalculationPeriod(500L);
    viewDefinition.setMinFullCalculationPeriod(500L);
    final String curveConfig = "SingleAUDConfig";
    final ViewCalculationConfiguration defaultCalConfig = new ViewCalculationConfiguration(viewDefinition, DEFAULT_CALC_CONFIG);
    defaultCalConfig.addPortfolioRequirement(SwapSecurity.SECURITY_TYPE, PRESENT_VALUE,
        ValueProperties.with(CURVE_CALCULATION_CONFIG, curveConfig).get());
    defaultCalConfig.addPortfolioRequirement(SwapSecurity.SECURITY_TYPE, YIELD_CURVE_NODE_SENSITIVITIES,
        ValueProperties.with(CURVE, "Single").with(CURVE_CALCULATION_CONFIG, curveConfig).get());
    defaultCalConfig.addSpecificRequirement(new ValueRequirement(YIELD_CURVE, ComputationTargetSpecification.of(Currency.AUD),
        ValueProperties.with(CURVE, "Single").with(CURVE_CALCULATION_METHOD, PAR_RATE_STRING).with(CURVE_CALCULATION_CONFIG, curveConfig).get()));
    defaultCalConfig.addSpecificRequirement(new ValueRequirement(YIELD_CURVE_JACOBIAN, ComputationTargetSpecification.of(Currency.AUD),
        ValueProperties.with(CURVE_CALCULATION_METHOD, PAR_RATE_STRING).with(CURVE_CALCULATION_CONFIG, curveConfig).get()));
    viewDefinition.addViewCalculationConfiguration(defaultCalConfig);
    return viewDefinition;
  }

  private ViewDefinition getBlackSwaptionViewDefinition(final String portfolioName, final String viewName) {
    final UniqueId portfolioId = getPortfolioId(portfolioName).toLatest();
    final ViewDefinition viewDefinition = new ViewDefinition(viewName, portfolioId, UserPrincipal.getTestUser());
    viewDefinition.setDefaultCurrency(Currency.USD);
    viewDefinition.setMaxDeltaCalculationPeriod(500L);
    viewDefinition.setMaxFullCalculationPeriod(500L);
    viewDefinition.setMinDeltaCalculationPeriod(500L);
    viewDefinition.setMinFullCalculationPeriod(500L);
    final ViewCalculationConfiguration defaultCalculationConfig = new ViewCalculationConfiguration(viewDefinition, DEFAULT_CALC_CONFIG);
    for (final Currency currency : ExampleSwaptionPortfolioLoader.CCYS) {
      final ComputationTargetSpecification target = ComputationTargetSpecification.of(currency.getUniqueId());
      ValueProperties properties = ValueProperties.builder()
          .with(SURFACE, "DEFAULT")
          .with(InstrumentTypeProperties.PROPERTY_SURFACE_INSTRUMENT_TYPE, InstrumentTypeProperties.SWAPTION_ATM)
          .get();
      defaultCalculationConfig.addSpecificRequirement(new ValueRequirement(VOLATILITY_SURFACE_DATA, target, properties));
      properties = ValueProperties.builder()
          .with(ValuePropertyNames.CURVE, CURVES_FOR_CURRENCY.get(currency).getFirst())
          .with(ValuePropertyNames.CURVE_CURRENCY, currency.getCode())
          .with(ValuePropertyNames.CALCULATION_METHOD, CalculationPropertyNamesAndValues.BLACK_METHOD)
          .get();
      defaultCalculationConfig.addPortfolioRequirement(SwaptionSecurity.SECURITY_TYPE, YIELD_CURVE_NODE_SENSITIVITIES, properties);
      defaultCalculationConfig.addPortfolioRequirement(SwaptionSecurity.SECURITY_TYPE, PV01, properties);
      properties = ValueProperties.builder()
          .with(ValuePropertyNames.CURVE, CURVES_FOR_CURRENCY.get(currency).getSecond())
          .with(ValuePropertyNames.CURVE_CURRENCY, currency.getCode())
          .with(ValuePropertyNames.CALCULATION_METHOD, CalculationPropertyNamesAndValues.BLACK_METHOD)
          .get();
      defaultCalculationConfig.addPortfolioRequirement(SwaptionSecurity.SECURITY_TYPE, YIELD_CURVE_NODE_SENSITIVITIES, properties);
      defaultCalculationConfig.addPortfolioRequirement(SwaptionSecurity.SECURITY_TYPE, PV01, properties);
    }
    final ValueProperties calculationMethodProperty = ValueProperties.builder()
        .with(ValuePropertyNames.CALCULATION_METHOD, CalculationPropertyNamesAndValues.BLACK_METHOD)
        .get();
    defaultCalculationConfig.addPortfolioRequirement(SwaptionSecurity.SECURITY_TYPE, PRESENT_VALUE, calculationMethodProperty);
    defaultCalculationConfig.addPortfolioRequirement(SwaptionSecurity.SECURITY_TYPE, VALUE_VEGA, calculationMethodProperty);
    defaultCalculationConfig.addPortfolioRequirement(SwaptionSecurity.SECURITY_TYPE, SECURITY_IMPLIED_VOLATILITY, ValueProperties.builder().get());
    viewDefinition.addViewCalculationConfiguration(defaultCalculationConfig);
    return viewDefinition;
  }

  private ViewDefinition getSABRSwaptionViewDefinition(final String portfolioName, final String viewName) {
    final UniqueId portfolioId = getPortfolioId(portfolioName).toLatest();
    final ViewDefinition viewDefinition = new ViewDefinition(viewName, portfolioId, UserPrincipal.getTestUser());
    viewDefinition.setDefaultCurrency(Currency.USD);
    viewDefinition.setMaxDeltaCalculationPeriod(500L);
    viewDefinition.setMaxFullCalculationPeriod(500L);
    viewDefinition.setMinDeltaCalculationPeriod(500L);
    viewDefinition.setMinFullCalculationPeriod(500L);
    final String curveConfig = "DefaultTwoCurveUSDConfig";
    final ViewCalculationConfiguration noExtrapolationConfig = new ViewCalculationConfiguration(viewDefinition, "No Extrapolation");
    final ViewCalculationConfiguration rightExtrapolationConfig = new ViewCalculationConfiguration(viewDefinition, "Right Extrapolation");
    final String[] securityTypes = new String[] {CapFloorCMSSpreadSecurity.SECURITY_TYPE, CapFloorSecurity.SECURITY_TYPE, SwapSecurity.SECURITY_TYPE };
    for (final String securityType : securityTypes) {
      noExtrapolationConfig.addPortfolioRequirement(securityType, PRESENT_VALUE,
          ValueProperties.with(CURVE_CALCULATION_CONFIG, curveConfig).with(CALCULATION_METHOD, SABRFunction.SABR_NO_EXTRAPOLATION).get());
      noExtrapolationConfig.addPortfolioRequirement(securityType, PRESENT_VALUE_SABR_ALPHA_SENSITIVITY,
          ValueProperties.with(CURVE_CALCULATION_CONFIG, curveConfig).with(CALCULATION_METHOD, SABRFunction.SABR_NO_EXTRAPOLATION).get());
      noExtrapolationConfig.addPortfolioRequirement(securityType, PRESENT_VALUE_SABR_NU_SENSITIVITY,
          ValueProperties.with(CURVE_CALCULATION_CONFIG, curveConfig).with(CALCULATION_METHOD, SABRFunction.SABR_NO_EXTRAPOLATION).get());
      noExtrapolationConfig.addPortfolioRequirement(securityType, PRESENT_VALUE_SABR_RHO_SENSITIVITY,
          ValueProperties.with(CURVE_CALCULATION_CONFIG, curveConfig).with(CALCULATION_METHOD, SABRFunction.SABR_NO_EXTRAPOLATION).get());
      noExtrapolationConfig.addPortfolioRequirement(securityType, YIELD_CURVE_NODE_SENSITIVITIES,
          ValueProperties.with(CURVE, "Discounting").with(CURVE_CALCULATION_CONFIG, curveConfig).with(CALCULATION_METHOD, SABRFunction.SABR_NO_EXTRAPOLATION).get());
      noExtrapolationConfig.addPortfolioRequirement(securityType, YIELD_CURVE_NODE_SENSITIVITIES,
          ValueProperties.with(CURVE, "Forward3M").with(CURVE_CALCULATION_CONFIG, curveConfig).with(CALCULATION_METHOD, SABRFunction.SABR_NO_EXTRAPOLATION).get());
      rightExtrapolationConfig.addPortfolioRequirement(securityType, PRESENT_VALUE,
          ValueProperties.with(CURVE_CALCULATION_CONFIG, curveConfig).with(CALCULATION_METHOD, SABRFunction.SABR_RIGHT_EXTRAPOLATION).get());
      rightExtrapolationConfig.addPortfolioRequirement(securityType, PRESENT_VALUE_SABR_ALPHA_SENSITIVITY,
          ValueProperties.with(CURVE_CALCULATION_CONFIG, curveConfig).with(CALCULATION_METHOD, SABRFunction.SABR_RIGHT_EXTRAPOLATION).get());
      rightExtrapolationConfig.addPortfolioRequirement(securityType, PRESENT_VALUE_SABR_NU_SENSITIVITY,
          ValueProperties.with(CURVE_CALCULATION_CONFIG, curveConfig).with(CALCULATION_METHOD, SABRFunction.SABR_RIGHT_EXTRAPOLATION).get());
      rightExtrapolationConfig.addPortfolioRequirement(securityType, PRESENT_VALUE_SABR_RHO_SENSITIVITY,
          ValueProperties.with(CURVE_CALCULATION_CONFIG, curveConfig).with(CALCULATION_METHOD, SABRFunction.SABR_RIGHT_EXTRAPOLATION).get());
      rightExtrapolationConfig.addPortfolioRequirement(securityType, YIELD_CURVE_NODE_SENSITIVITIES,
          ValueProperties.with(CURVE, "Discounting").with(CURVE_CALCULATION_CONFIG, curveConfig).with(CALCULATION_METHOD, SABRFunction.SABR_RIGHT_EXTRAPOLATION).get());
      rightExtrapolationConfig.addPortfolioRequirement(securityType, YIELD_CURVE_NODE_SENSITIVITIES,
          ValueProperties.with(CURVE, "Forward3M").with(CURVE_CALCULATION_CONFIG, curveConfig).with(CALCULATION_METHOD, SABRFunction.SABR_RIGHT_EXTRAPOLATION).get());
    }
    viewDefinition.addViewCalculationConfiguration(noExtrapolationConfig);
    viewDefinition.addViewCalculationConfiguration(rightExtrapolationConfig);
    return viewDefinition;
  }

  private ViewDefinition getSABRExtrapolationViewDefinition(final String portfolioName, final String viewName) {
    final UniqueId portfolioId = getPortfolioId(portfolioName).toLatest();
    final ViewDefinition viewDefinition = new ViewDefinition(viewName, portfolioId, UserPrincipal.getTestUser());
    viewDefinition.setDefaultCurrency(Currency.USD);
    viewDefinition.setMaxDeltaCalculationPeriod(500L);
    viewDefinition.setMaxFullCalculationPeriod(500L);
    viewDefinition.setMinDeltaCalculationPeriod(500L);
    viewDefinition.setMinFullCalculationPeriod(500L);
    final String curveConfig = "DefaultTwoCurveUSDConfig";
    final ViewCalculationConfiguration noExtrapolationConfig = new ViewCalculationConfiguration(viewDefinition, "No Extrapolation");
    final ViewCalculationConfiguration rightExtrapolationConfig = new ViewCalculationConfiguration(viewDefinition, "Right Extrapolation");
    final String[] securityTypes = new String[] {CapFloorCMSSpreadSecurity.SECURITY_TYPE, CapFloorSecurity.SECURITY_TYPE, SwapSecurity.SECURITY_TYPE };
    for (final String securityType : securityTypes) {
      noExtrapolationConfig.addPortfolioRequirement(securityType, PRESENT_VALUE,
          ValueProperties.with(CURVE_CALCULATION_CONFIG, curveConfig).with(CALCULATION_METHOD, SABRFunction.SABR_NO_EXTRAPOLATION).get());
      noExtrapolationConfig.addPortfolioRequirement(securityType, PRESENT_VALUE_SABR_ALPHA_SENSITIVITY,
          ValueProperties.with(CURVE_CALCULATION_CONFIG, curveConfig).with(CALCULATION_METHOD, SABRFunction.SABR_NO_EXTRAPOLATION).get());
      noExtrapolationConfig.addPortfolioRequirement(securityType, PRESENT_VALUE_SABR_NU_SENSITIVITY,
          ValueProperties.with(CURVE_CALCULATION_CONFIG, curveConfig).with(CALCULATION_METHOD, SABRFunction.SABR_NO_EXTRAPOLATION).get());
      noExtrapolationConfig.addPortfolioRequirement(securityType, PRESENT_VALUE_SABR_RHO_SENSITIVITY,
          ValueProperties.with(CURVE_CALCULATION_CONFIG, curveConfig).with(CALCULATION_METHOD, SABRFunction.SABR_NO_EXTRAPOLATION).get());
      noExtrapolationConfig.addPortfolioRequirement(securityType, YIELD_CURVE_NODE_SENSITIVITIES,
          ValueProperties.with(CURVE, "Discounting").with(CURVE_CALCULATION_CONFIG, curveConfig).with(CALCULATION_METHOD, SABRFunction.SABR_NO_EXTRAPOLATION).get());
      noExtrapolationConfig.addPortfolioRequirement(securityType, YIELD_CURVE_NODE_SENSITIVITIES,
          ValueProperties.with(CURVE, "Forward3M").with(CURVE_CALCULATION_CONFIG, curveConfig).with(CALCULATION_METHOD, SABRFunction.SABR_NO_EXTRAPOLATION).get());
      rightExtrapolationConfig.addPortfolioRequirement(securityType, PRESENT_VALUE,
          ValueProperties.with(CURVE_CALCULATION_CONFIG, curveConfig).with(CALCULATION_METHOD, SABRFunction.SABR_RIGHT_EXTRAPOLATION).get());
      rightExtrapolationConfig.addPortfolioRequirement(securityType, PRESENT_VALUE_SABR_ALPHA_SENSITIVITY,
          ValueProperties.with(CURVE_CALCULATION_CONFIG, curveConfig).with(CALCULATION_METHOD, SABRFunction.SABR_RIGHT_EXTRAPOLATION).get());
      rightExtrapolationConfig.addPortfolioRequirement(securityType, PRESENT_VALUE_SABR_NU_SENSITIVITY,
          ValueProperties.with(CURVE_CALCULATION_CONFIG, curveConfig).with(CALCULATION_METHOD, SABRFunction.SABR_RIGHT_EXTRAPOLATION).get());
      rightExtrapolationConfig.addPortfolioRequirement(securityType, PRESENT_VALUE_SABR_RHO_SENSITIVITY,
          ValueProperties.with(CURVE_CALCULATION_CONFIG, curveConfig).with(CALCULATION_METHOD, SABRFunction.SABR_RIGHT_EXTRAPOLATION).get());
      rightExtrapolationConfig.addPortfolioRequirement(securityType, YIELD_CURVE_NODE_SENSITIVITIES,
          ValueProperties.with(CURVE, "Discounting").with(CURVE_CALCULATION_CONFIG, curveConfig).with(CALCULATION_METHOD, SABRFunction.SABR_RIGHT_EXTRAPOLATION).get());
      rightExtrapolationConfig.addPortfolioRequirement(securityType, YIELD_CURVE_NODE_SENSITIVITIES,
          ValueProperties.with(CURVE, "Forward3M").with(CURVE_CALCULATION_CONFIG, curveConfig).with(CALCULATION_METHOD, SABRFunction.SABR_RIGHT_EXTRAPOLATION).get());
    }
    viewDefinition.addViewCalculationConfiguration(noExtrapolationConfig);
    viewDefinition.addViewCalculationConfiguration(rightExtrapolationConfig);
    return viewDefinition;
  }

  private ViewDefinition getFXForwardViewDefinition(final String portfolioName, final String viewName) {
    final UniqueId portfolioId = getPortfolioId(portfolioName).toLatest();
    final ViewDefinition viewDefinition = new ViewDefinition(viewName, portfolioId, UserPrincipal.getTestUser());
    viewDefinition.setDefaultCurrency(Currency.USD);
    viewDefinition.setMaxDeltaCalculationPeriod(500L);
    viewDefinition.setMaxFullCalculationPeriod(500L);
    viewDefinition.setMinDeltaCalculationPeriod(500L);
    viewDefinition.setMinFullCalculationPeriod(500L);
    final ViewCalculationConfiguration calculationConfig1 = new ViewCalculationConfiguration(viewDefinition, "FX Implied Curves");
    final ViewCalculationConfiguration calculationConfig2 = new ViewCalculationConfiguration(viewDefinition, "FX Forward Points");
    calculationConfig1.addPortfolioRequirement(FXForwardSecurity.SECURITY_TYPE, PRESENT_VALUE,
            ValueProperties.with(CALCULATION_METHOD, CalculationPropertyNamesAndValues.DISCOUNTING)
                    .with(CURRENCY, Currency.USD.getCode()).get());
    calculationConfig2.addPortfolioRequirement(FXForwardSecurity.SECURITY_TYPE, PRESENT_VALUE,
            ValueProperties.with(CALCULATION_METHOD, CalculationPropertyNamesAndValues.FORWARD_POINTS)
                    .with(CURRENCY, Currency.USD.getCode()).get());
    calculationConfig1.addPortfolioRequirement(FXForwardSecurity.SECURITY_TYPE, FX_CURRENCY_EXPOSURE,
            ValueProperties.with(CALCULATION_METHOD, CalculationPropertyNamesAndValues.DISCOUNTING).get());
    calculationConfig2.addPortfolioRequirement(FXForwardSecurity.SECURITY_TYPE, FX_CURRENCY_EXPOSURE,
            ValueProperties.with(CALCULATION_METHOD, CalculationPropertyNamesAndValues.FORWARD_POINTS).get());
    viewDefinition.addViewCalculationConfiguration(calculationConfig1);
    viewDefinition.addViewCalculationConfiguration(calculationConfig2);
    return viewDefinition;
  }

  private void storeViewDefinition(final ViewDefinition viewDefinition) {
    final ConfigItem<ViewDefinition> configItem = ConfigItem.of(viewDefinition, viewDefinition.getName(), ViewDefinition.class);
    ConfigMasterUtils.storeByName(getToolContext().getConfigMaster(), configItem);
  }

}