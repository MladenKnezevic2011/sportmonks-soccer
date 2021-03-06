package com.sportmonks.endpoints;

import com.mashape.unirest.http.HttpResponse;
import com.sportmonks.data.entity.VideoHighlight;
import com.sportmonks.data.structure.VideoHighlights;
import com.sportmonks.exceptions.HaveToDefineValidIdException;
import com.sportmonks.tools.RestTool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Proxy Teams
 */
public class VideoHighlightsEndPoint extends AbstractEndPoint {

    private static final String BASE_URL = AbstractEndPoint.API_URL + AbstractEndPoint.VERSION + "/highlights";
    private static final String BY_FIXTURE_URL = BASE_URL + "/fixture/{fixtureId}";

    private static VideoHighlightsEndPoint INSTANCE;
    private long lastCall = 0;

    private VideoHighlightsEndPoint(final Double hourRateLimit) {
        super(hourRateLimit);
    }

    /**
     * Creation d'une instance avec une limite d'appel par heure personnalisée
     *
     * @param customHourRateLimit : APIClient.FREE_PLAN_RATE_LIMIT, APIClient.CLASSIC_PLAN_RATE_LIMIT
     * @return VideoHighlightsEndPoint
     */
    public static VideoHighlightsEndPoint getInstance(final Double customHourRateLimit) {
        if (INSTANCE == null) {
            INSTANCE = new VideoHighlightsEndPoint(customHourRateLimit);
        }

        return INSTANCE;
    }


    /**
     * @param fixtureId id du mathc
     * @return List of {@link VideoHighlight}
     */
    public List<VideoHighlight> findByFixture(final Integer fixtureId) {
        final VideoHighlightsEndPointParams params = new VideoHighlightsEndPointParams();
        params.setFixtureId(fixtureId);
        return findByFixture(params);
    }

    /**
     * @param params d'appel de l'api
     * @return List of {@link VideoHighlight}
     */
    public List<VideoHighlight> findByFixture(final VideoHighlightsEndPointParams params) {

        if (!params.isValidId()) {
            throw new HaveToDefineValidIdException();
        }

        return findResults(BY_FIXTURE_URL, params);
    }

    /**
     * @return List of {@link VideoHighlight}
     */
    public List<VideoHighlight> findAll() {
        final VideoHighlightsEndPointParams params = new VideoHighlightsEndPointParams();
        params.setFixtureId(null);
        return findResults(BASE_URL, params);
    }

    /**
     * @param params d'appel de l'api
     * @return List of {@link VideoHighlight}
     */
    public List<VideoHighlight> findAll(final VideoHighlightsEndPointParams params) {
        return findResults(BASE_URL, params);
    }

    /**
     * Retourne une liste de résultat
     *
     * @param url    appelée
     * @param params d'appel de l'api
     * @return List of {@link VideoHighlight}
     */
    private List<VideoHighlight> findResults(final String url, final VideoHighlightsEndPointParams params) {

        lastCall = waitBeforeNextCall(lastCall);

        final Map<String, String> paramsMap = new HashMap<>();
        if (params != null) {
            paramsMap.put("includes", params.getRelations());
            if (params.isValidId()) {
                paramsMap.put("fixtureId", params.getFixtureId().toString());
            }
        }

        final HttpResponse<VideoHighlights> httpResponse = RestTool.get(url, paramsMap, VideoHighlights.class);

        final List<VideoHighlight> response = new ArrayList<>();
        response.addAll(httpResponse.getBody().getData());

        return response;
    }

    /**
     * VideoHighlight relations
     */
    public enum Relation {
        fixture
    }

}
