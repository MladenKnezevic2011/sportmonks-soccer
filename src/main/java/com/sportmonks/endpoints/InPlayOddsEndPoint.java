package com.sportmonks.endpoints;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mashape.unirest.http.HttpResponse;
import com.sportmonks.data.entity.Offer;
import com.sportmonks.data.structure.InPlayOdds;
import com.sportmonks.exceptions.HaveToDefineValidIdException;
import com.sportmonks.tools.RestTool;

/**
 * Proxy Teams
 */
public class InPlayOddsEndPoint extends AbstractEndPoint {

	private static final String BASE_URL = AbstractEndPoint.API_URL + AbstractEndPoint.VERSION + "/odds/inplay";
	private static final String BY_FIXTURE_URL = BASE_URL + "/fixture/{fixtureId}";

	private static InPlayOddsEndPoint INSTANCE;
	private long lastOddProxyCall = 0;

	private InPlayOddsEndPoint() {
		// Hide constructor
	}

	/**
	 * Singleton
	 *
	 * @return
	 */
	public static InPlayOddsEndPoint getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new InPlayOddsEndPoint();
		}

		return INSTANCE;
	}

	/**
	 *
	 * @param fixtureId
	 * @return
	 */
	public List<Offer> findByFixture(final Integer fixtureId) {
		final InPlayOddsEndPointParams params = new InPlayOddsEndPointParams();
		params.setFixtureId(fixtureId);
		return findByFixture(params);
	}

	/**
	 *
	 * @param params
	 * @return
	   */
	public List<Offer> findByFixture(final InPlayOddsEndPointParams params) {

		if (!params.isValidFixtureId()) {
			throw new HaveToDefineValidIdException();
		}

		return findResults(BY_FIXTURE_URL, params);
	}

	/**
	 *
	 * @param url
	 * @param params
	   * @return
	   */
	private List<Offer> findResults(final String url, final InPlayOddsEndPointParams params) {

		lastOddProxyCall = waitBeforeNextCall(lastOddProxyCall);

		final List<Offer> response = new ArrayList<>();

		final Map<String, String> paramsMap = new HashMap<>();
		if (params != null) {
			paramsMap.put("includes", "");
			if (params.isValidFixtureId()) {
				paramsMap.put("fixtureId", params.getFixtureId().toString());
			}
		}

		final HttpResponse<InPlayOdds> httpResponse = RestTool.get(url, paramsMap, InPlayOdds.class);
		final InPlayOdds body = httpResponse.getBody();
		if (body != null) {
			return body.getData();
		}

		return response;
	}

}
