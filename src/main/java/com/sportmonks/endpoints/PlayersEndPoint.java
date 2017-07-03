package com.sportmonks.endpoints;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mashape.unirest.http.HttpResponse;
import com.sportmonks.data.entity.Player;
import com.sportmonks.data.structure.Players;
import com.sportmonks.exceptions.HaveToDefineValidIdException;
import com.sportmonks.exceptions.NotFoundException;
import com.sportmonks.tools.RestTool;

/**
 * Created by kevin on 28/05/2016.
 */
public class PlayersEndPoint extends AbstractEndPoint {

	private static final String BASE_URL = AbstractEndPoint.API_URL + AbstractEndPoint.VERSION + "/players";
	private static final String BY_ID_URL = BASE_URL + "/{playerId}";
	private static PlayersEndPoint INSTANCE;

	private long lastCall = 0;

	private PlayersEndPoint() {
		// Hide constructor
	}

	/**
	 * Singleton
	 *
	 * @return
	 */
	public static PlayersEndPoint getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new PlayersEndPoint();
		}

		return INSTANCE;
	}

	private Player findUnique(final String url, final PlayersEndPointParams params) {

		lastCall = waitBeforeNextCall(lastCall);

		final List<Player> response = new ArrayList<>();

		final Map<String, String> paramsMap = new HashMap<>();
		if (params != null) {
			paramsMap.put("includes", params.getRelations());
			if (params.isValidId()) {
				paramsMap.put("playerId", String.valueOf(params.getPlayerId()));
			}
		}

		final HttpResponse<Players> httpResponse = RestTool.get(url, paramsMap, Players.class);
		final Players body = httpResponse.getBody();
		if (body != null) {
			return body.getData();
		}

		return null;
	}

	/**
	 * @param playerId
	 * @return
	 * @throws NotFoundException
	 */
	public Player findById(final Long playerId) throws NotFoundException {
		final PlayersEndPointParams params = new PlayersEndPointParams();
		params.setPlayerId(playerId);
		return findOne(params);
	}

	/**
	 * Liste de toutes les competitions autorisées avec les relations définies
	 */
	public Player findOne(final PlayersEndPointParams params) throws NotFoundException {

		if (!params.isValidId()) {
			throw new HaveToDefineValidIdException();
		}

		final Player player = findUnique(BY_ID_URL, params);
		if (null == player) {
			throw new NotFoundException();
		}

		return player;
	}

	/**
	 * Competitions relations
	 */
	public enum Relation {
		team, stats
	}
}
