package business.stockmarket.simulation;

public class MarketPercentConstants
{
    public static final double STEADY_MAX_ABS = 0.01;

    public static final double GROW_DRIFT_MIN = 0.002;
    public static final double GROW_DRIFT_RANGE = 0.028;
    public static final double GROW_NOISE_MAX_ABS = 0.004;

    public static final double DECL_DRIFT_MIN = 0.002;
    public static final double DECL_DRIFT_RANGE = 0.028;
    public static final double DECL_NOISE_MAX_ABS = 0.004;

    public static final double HF_BASE_MAX_ABS = 0.06;
    public static final double HF_SPIKE_CHANCE = 0.15;
    public static final double HF_SPIKE_MIN = 0.08;
    public static final double HF_SPIKE_RANGE = 0.12;

    public static final double RG_DRIFT_MIN = 0.03;
    public static final double RG_DRIFT_RANGE = 0.12;
    public static final double RG_NOISE_MAX_ABS = 0.03;

    public static final double RD_DRIFT_MIN = 0.03;
    public static final double RD_DRIFT_RANGE = 0.12;
    public static final double RD_NOISE_MAX_ABS = 0.03;

    public static final double RC_REBOUND_CHANCE = 0.15;
    public static final double RC_REBOUND_MIN = 0.08;
    public static final double RC_REBOUND_RANGE = 0.12;
    public static final double RC_DROP_MIN = 0.15;
    public static final double RC_DROP_RANGE = 0.20;
}
