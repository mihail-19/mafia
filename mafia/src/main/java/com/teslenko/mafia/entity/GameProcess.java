package com.teslenko.mafia.entity;

import java.time.LocalTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Game process in separate Thread. Responses for game period changes - day/night and citizen/mafia votes.
 * @author Mykhailo Teslenko
 *
 */
public class GameProcess extends Thread {
	private static final Logger LOGGER = LoggerFactory.getLogger(GameProcess.class);
	private static final int SLEEP_TIME_PERIOD = 100;
	private Game game;

	public GameProcess(Game game) {
		this.game = game;
	}

	@Override
	public void run() {
		game.setIsNight(false);
		game.setStartTime(LocalTime.now());
		LOGGER.debug("starting separate thread for game={}, thread={}", game, Thread.currentThread().getName());
		while (!game.getIsFinished()) {
			LocalTime finishTime = game.periodFinishTime();
			while (LocalTime.now().isBefore(finishTime)) {
				sleepWithPeriod();
			}
			if (!game.getIsNight()) {
				startVoteCitizen(game);
			} else {
				startVoteMafia(game);
			}
			game.startNewPeriod();
			game.sendGame();
		}
	}
	
	private void startVoteCitizen(Game game) {
		LOGGER.info("starting citizen vote for game={} ", game);
		game.resetVoteCitizen();
		game.getVote().startVote();
		game.sendGame();
		while (!game.getVote().isFinished()) {
			sleepWithPeriod();
		}
		game.killPlayerByVoteResults();
		game.setIsNight(true);
		game.getVote().finish();
		LOGGER.debug("citizen vote is finished, game={}", game);
	}

	private void startVoteMafia(Game game) {
		LOGGER.info("starting mafia vote for game={} ", game);
		game.resetVoteMafia();
		game.getVote().startVote();
		game.sendGame();
		while (!game.getVote().isFinished()) {
			sleepWithPeriod();
		}
		game.killPlayerByVoteResults();
		game.setIsNight(false);
		LOGGER.debug("mafia vote is finished, game={}", game);
	}

	private void sleepWithPeriod() {
		try {
			Thread.sleep(SLEEP_TIME_PERIOD);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
