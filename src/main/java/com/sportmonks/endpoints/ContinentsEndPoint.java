package com.sportmonks.endpoints;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mashape.unirest.http.HttpResponse;
import com.sportmonks.data.entity.Continent;
import com.sportmonks.data.structure.Continents;
import com.sportmonks.data.structure.OneContinent;
import com.sportmonks.exceptions.HaveToDefineValidIdException;
import com.sportmonks.exceptions.NotFoundException;
import com.sportmonks.tools.RestTool;

/**
 * Created by kevin on 28/05/2016.
 */
public class ContinentsEndPoint extends AbstractEndPoint {

	private static final String BASE_URL = AbstractEndPoint.API_URL + AbstractEndPoint.VERSION + "/continents";
	private static final String BY_ID_URL = BASE_URL + "/{id}";
	private static ContinentsEndPoint INSTANCE;

	private long lastCall = 0;

	private ContinentsEndPoint(final Double hourRateLimit) {
		super(hourRateLimit);
	}


	/**
	 * Creation d'une instance avec une limite d'appel par heure personnalisée
	 *
	 * @param customHourRateLimit : APIClient.FREE_PLAN_RATE_LIMIT, APIClient.CLASSIC_PLAN_RATE_LIMIT
	 *
	 * @return
	 */
	public static ContinentsEndPoint getInstance(final Double customHourRateLimit) {
		if (INSTANCE == null)
			INSTANCE = new ContinentsEndPoint(customHourRateLimit);

		return INSTANCE;
	}

	/**
	 * Retourne la liste de toutes les compétitions autorisées
	 *
	 * @return {@List<@Competition>}
	 */
	public List<Continent> findAll() {
		return findAll(null);
	}

	/**
	 * Liste de toutes les competitions autorisées avec les relations définies
	 */
	public List<Continent> findAll(final ContinentsEndPointParams params) {
		if (null != params) {
			params.setContinentId(null);
		}
		return findSeverals(BASE_URL, params);
	}

	private List<Continent> findSeverals(final String url, final ContinentsEndPointParams params) {

		lastCall = waitBeforeNextCall(lastCall);

		final List<Continent> response = new ArrayList<>();

		final Map<String, String> paramsMap = new HashMap<>();
		if (params != null) {
			paramsMap.put("includes", params.getRelations());
			if (params.isValidId()) {
				paramsMap.put("id", String.valueOf(params.getContinentId()));
			}
		}

		final HttpResponse<Continents> httpResponse = RestTool.get(url, paramsMap, Continents.class);
		final Continents body = httpResponse.getBody();
		if (body != null) {
			response.addAll(body.getData());
		}

		return response;
	}

	private Continent findUnique(final ContinentsEndPointParams params) throws NotFoundException {

		lastCall = waitBeforeNextCall(lastCall);

		final Map<String, String> paramsMap = new HashMap<>();
		if (params != null) {
			paramsMap.put("includes", params.getRelations());
			if (params.isValidId()) {
				paramsMap.put("id", String.valueOf(params.getContinentId()));
			}
		}

		final HttpResponse<OneContinent> httpResponse = RestTool.get(BY_ID_URL, paramsMap, OneContinent.class);
		final OneContinent body = httpResponse.getBody();
		if (body == null) {
			throw new NotFoundException();
		}
		final Continent continent = body.getData();
		if (continent == null) {
			throw new NotFoundException();
		}

		return continent;
	}

	/**
	 * @param ContinentId
	 * @return
	 * @throws NotFoundException
	 */
	public Continent findOne(final Integer ContinentId) throws NotFoundException {
		final ContinentsEndPointParams params = new ContinentsEndPointParams();
		params.setContinentId(ContinentId);
		return findOne(params);
	}

	/**
	 * Liste de toutes les competitions autorisées avec les relations définies
	 */
	public Continent findOne(final ContinentsEndPointParams params) throws NotFoundException {

		if (!params.isValidId()) {
			throw new HaveToDefineValidIdException();
		}

		return findUnique(params);
	}

	/**
	 * Competitions relations
	 */
	public enum Relation {
		countries
	}
}
