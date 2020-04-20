package com.sportmonks.endpoints;

import com.mashape.unirest.http.HttpResponse;
import com.sportmonks.data.entity.FixtureTvStation;
import com.sportmonks.data.structure.FixtureTvStations;
import com.sportmonks.exceptions.HaveToDefineValidIdException;
import com.sportmonks.tools.RestTool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Proxy Teams
 */
public class TvStationsEndPoint extends AbstractEndPoint {

    private static final String BASE_URL = AbstractEndPoint.API_URL + AbstractEndPoint.VERSION + "/tvstations";
    private static final String BY_FIXTURE_URL = BASE_URL + "/fixture/{fixtureId}";

    private static TvStationsEndPoint INSTANCE;
    private long lastCall = 0;

    private TvStationsEndPoint(final Double hourRateLimit) {
        super(hourRateLimit);
    }

    /**
     * Creation d'une instance avec une limite d'appel par heure personnalisée
     *
     * @param customHourRateLimit : APIClient.FREE_PLAN_RATE_LIMIT, APIClient.CLASSIC_PLAN_RATE_LIMIT
     * @return TvStationsEndPoint
     */
    public static TvStationsEndPoint getInstance(final Double customHourRateLimit) {
        if (INSTANCE == null) {
            INSTANCE = new TvStationsEndPoint(customHourRateLimit);
        }

        return INSTANCE;
    }

    /**
     * @param fixtureId id fixture
     * @return List of {@link FixtureTvStation}
     */
    public List<FixtureTvStation> findByFixture(final Integer fixtureId) {
        final TvStationsEndPointParams params = new TvStationsEndPointParams();
        params.setFixtureId(fixtureId);
        return findByFixture(params);
    }

    /**
     * @param params d'appel de l'api
     * @return List of {@link FixtureTvStation}
     */
    public List<FixtureTvStation> findByFixture(final TvStationsEndPointParams params) {

        if (!params.isValidId()) {
            throw new HaveToDefineValidIdException();
        }

        return findResults(BY_FIXTURE_URL, params);
    }

    /**
     * Retourne une liste de résultat
     *
     * @param url    appelée
     * @param params d'appel
     * @return List of {@link FixtureTvStation}
     */
    private List<FixtureTvStation> findResults(final String url, final TvStationsEndPointParams params) {

        lastCall = waitBeforeNextCall(lastCall);

        final Map<String, String> paramsMap = new HashMap<>();
        if (params != null) {
            paramsMap.put("includes", params.getRelations());
            if (params.isValidId()) {
                paramsMap.put("fixtureId", params.getFixtureId().toString());
            }
        }

        final HttpResponse<FixtureTvStations> httpResponse = RestTool.get(url, paramsMap, FixtureTvStations.class);

        final List<FixtureTvStation> response = new ArrayList<>();
        response.addAll(httpResponse.getBody().getData());

        return response;
    }

    /**
     * TvStation relations
     */
    public enum Relation {
        fixture
    }

}
