package com.sportmonks.endpoints;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Created by kevin on 21/05/2016.
 */
public abstract class AbstractEndPoint {

    public static final String API_URL = "https://soccer.sportmonks.com/api/";
    public static final String COMMON_URL_PARAMS = "?api_token={api_token}&include={includes}";
    public static final String VERSION = "v2.0";
    private static final Logger LOGGER = Logger.getLogger(AbstractEndPoint.class.getName());
    private static final double SECOND_IN_MILLISECOND = 1000.0;
    private static final double HOUR_IN_SECOND = 3600.0;

    private final double timeBetweenTwoCalls;

    /**
     * @param hourRateLimit limite d'appel à l'API
     */
    public AbstractEndPoint(final Double hourRateLimit) {
        double hourRateLimit1 = hourRateLimit;
        this.timeBetweenTwoCalls = Math.ceil(HOUR_IN_SECOND / hourRateLimit1 * SECOND_IN_MILLISECOND);

        LOGGER.info("Rate limit : " + hourRateLimit + " calls/hour - Time between 2 calls : " + timeBetweenTwoCalls + "ms");
    }

    /**
     * Permet de respecter le delai entre chaque d'appel du end point
     *
     * @param lastProxyCall timestamp du dernier call
     * @return long representant le temps d'attente nécessaire entre chaque appel
     * pour respecter le refresh rate paramétré
     */
    protected long waitBeforeNextCall(long lastProxyCall) {
        synchronized (this) {
            try {
                while (System.currentTimeMillis() - lastProxyCall <= timeBetweenTwoCalls) {
                    TimeUnit.MILLISECONDS.sleep(100);

                }
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }

            notifyAll();
            return System.currentTimeMillis();
        }
    }
}
