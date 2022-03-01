package com.teslenko.mafia.entity;

public final class GameCreateParams {
	private int dayTimeSeconds = 10;
	private int nightTimeSeconds = 10;
	private int voteTimeSeconds = 10;
	private int mafiaNum = 1;
	
	private GameCreateParams() {}
	
	public int getDayTimeSeconds() {
		return dayTimeSeconds;
	}
	public void setDayTimeSeconds(int dayTimeSeconds) {
		this.dayTimeSeconds = dayTimeSeconds;
	}
	public int getNightTimeSeconds() {
		return nightTimeSeconds;
	}
	public void setNightTimeSeconds(int nightTimeSeconds) {
		this.nightTimeSeconds = nightTimeSeconds;
	}

	public int getVoteTimeSeconds() {
		return voteTimeSeconds;
	}
	public void setVoteTimeSeconds(int voteTimeSeconds) {
		this.voteTimeSeconds = voteTimeSeconds;
	}
	public int getMafiaNum() {
		return mafiaNum;
	}
	public void setMafiaNum(int mafiaNum) {
		this.mafiaNum = mafiaNum;
	}


	public static GameCreateParamsBuilder builder() {
		GameCreateParams gameParams = new GameCreateParams();
		return gameParams.new GameCreateParamsBuilder();
	}
	
	public class GameCreateParamsBuilder{
		private GameCreateParamsBuilder() {}
		public GameCreateParamsBuilder dayTimeSeconds(Integer dayTime) {
			if(isValidTime(dayTime)) {
				dayTimeSeconds = dayTime;
			}
			return this;
		}
		public GameCreateParamsBuilder nightTimeSeconds(Integer nightTime) {
			if(isValidTime(nightTime)) {
				nightTimeSeconds = nightTime;
			}
			return this;
		}
		public GameCreateParamsBuilder voteTimeSeconds(Integer voteTime) {
			if(isValidTime(voteTime)) {
				voteTimeSeconds = voteTime;
			}
			return this;
		}
		public GameCreateParamsBuilder mafiaNum(Integer mafiaNum) {
			if(mafiaNum != null && mafiaNum >= 0) {
				GameCreateParams.this.mafiaNum = mafiaNum;
			}
			return this;
		}
		
		public GameCreateParams build() {
			return GameCreateParams.this;
		}
		
		private boolean isValidTime(Integer value) {
			if(value == null) {
				return false;
			}
			if(value < 1) {
				return false;
			}
			return true;
		}
	}
}
