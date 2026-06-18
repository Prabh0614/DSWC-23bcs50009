// Problem 1 - Trading Engine Initialization
// Contains TradingStrategy, AbstractStrategy, MomentumStrategy, ArbitrageStrategy, TradingEngine skeleton
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

interface TradingStrategy {
    void executeTrade();
}

abstract class AbstractStrategy implements TradingStrategy {
    protected String assetClass;
}

@Component
class MomentumStrategy extends AbstractStrategy {

    public MomentumStrategy() {
        assetClass = "Equity";
    }

    @Override
    public void executeTrade() {
        System.out.println("Momentum Trade Executed");
    }
}

@Component
class ArbitrageStrategy extends AbstractStrategy {

    public ArbitrageStrategy() {
        assetClass = "Forex";
    }

    @Override
    public void executeTrade() {
        System.out.println("Arbitrage Trade Executed");
    }
}

@Component
class MarketDataService {
}

@Component
class AlertService {

    public void sendAlert(String msg) {
        System.out.println(msg);
    }
}

@Component
public class TradingEngine
        implements BeanNameAware, InitializingBean {

    private final MarketDataService marketDataService;
    private final List<TradingStrategy> strategies;

    private AlertService alertService;

    public TradingEngine(
            MarketDataService marketDataService,
            List<TradingStrategy> strategies) {

        this.marketDataService = marketDataService;
        this.strategies = strategies;
    }

    @Autowired(required = false)
    public void setAlertService(AlertService alertService) {
        this.alertService = alertService;
    }

    @Override
    public void setBeanName(String name) {
        System.out.println("Bean Name : " + name);
    }

    @PostConstruct
    public void warmUpCache() {
        System.out.println("Cache Warmed Up");
    }

    @Override
    public void afterPropertiesSet() {
        if (marketDataService == null || strategies.isEmpty()) {
            throw new RuntimeException("Validation Failed");
        }

        System.out.println("Safety Validation Passed");
    }

    @PreDestroy
    public void closePositions() {
        System.out.println("Closing Open Positions");
    }
}