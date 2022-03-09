package com.teslenko.mafia.services;

import java.util.Deque;
import java.util.LinkedList;
import java.util.SortedSet;
import java.util.TreeSet;

import org.springframework.stereotype.Component;

/**
 * Generates ID for {@link Game}. New ID is min from 
 * free int numbers. When game is removed, ID became free.
 * @author Mykhailo Teslenko
 *
 */
@Component
public class GameIdGenerator {
	private TreeSet<Integer> releasedId = new TreeSet<>();
	private int maxId = 1;
	
	/**
	 * Generates ID for game, min is 1. 
	 * Minimum available id will be used.
	 * If id is released, will be reused. 
	 * @return
	 */
	public int generateId() {
		int id;
		if(releasedId.size() != 0) {
			id  = releasedId.pollFirst();
		} else {
			id = maxId++;
		}
		return id;
	}
	
	/**
	 * Releases given id for it could be used again for new game creation.
	 * @param id - game ID to release.
	 */
	public void releaseId(int id) {
		validateIdOrThrow(id);
		if(id == maxId - 1) {
			maxId--;
		} else {
			releasedId.add(id);
		}
	}
	
	/*
	 * Throws IllegalArgumentException if ID is lesser then lowest,
	 * bigger than actual set, or contains in set of released ID's. 
	 * 
	 */
	private void validateIdOrThrow(int id) throws IllegalArgumentException {
		if(id > maxId - 1) {
			throw new IllegalArgumentException("trying to remove ID that is greater than max id generated");
		}
		if(id < 1) {
			throw new IllegalArgumentException("triyng to remove ID that is lesser than minimum value 1");
		}
		if(releasedId.contains(id)) {
			throw new IllegalArgumentException("trying to remove ID that was already released, id=" + id);
		}
	}
}
