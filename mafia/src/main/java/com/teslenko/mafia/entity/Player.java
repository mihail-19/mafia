package com.teslenko.mafia.entity;

public class Player {
	private int id;
	private String name;
	private MafiaRole mafiaRole;
	private boolean isAlive;
	private boolean isUntouchable;
	
	public Player(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public MafiaRole getMafiaRole() {
		return mafiaRole;
	}

	public void setMafiaRole(MafiaRole mafiaRole) {
		this.mafiaRole = mafiaRole;
	}

	public boolean isAlive() {
		return isAlive;
	}

	public void setAlive(boolean isAlive) {
		this.isAlive = isAlive;
	}
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Player other = (Player) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Player [id=" + id + ", name=" + name + ", mafiaRole=" + mafiaRole + ", isAlive=" + isAlive + "]";
	}

	public boolean isUntouchable() {
		return isUntouchable;
	}

	public void setUntouchable(boolean isUntouchable) {
		this.isUntouchable = isUntouchable;
	}
	
}
