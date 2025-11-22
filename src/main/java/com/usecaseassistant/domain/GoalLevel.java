package com.usecaseassistant.domain;

/**
 * Represents the scope level of a use case according to Cockburn's methodology.
 */
public enum GoalLevel {
    /**
     * Summary level - high-level business process spanning multiple user goals
     */
    SUMMARY,
    
    /**
     * User goal level - a goal that a primary actor can complete in one sitting
     */
    USER_GOAL,
    
    /**
     * Subfunction level - a step within a user goal use case
     */
    SUBFUNCTION
}
