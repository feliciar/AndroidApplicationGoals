package com.curious.dina.goals.Model;

/**
 * The functions in this model are used in the first page of the
 * application - the progress view.
 * Progress model is responsible for collecting all completed goals
 * from the database, and all non-completed goals.
 * It is also responsible for getting and setting the users points and handling rewards.
 *
 * The goals are collected from the database when the respective method is called
 * (e.g. fetchCompletedGoals) and then returned with the update method to the observers.
 * The returned data is on the form:
 *
 * {"tag":"listOfUncompletedGoals", "life":[{"name":"Do Fun Stuff","completed":false}],"months":[{"year":"2016","monthNumber":"5","goals":[{"name":"Do Fun Stuff","completed":false},{"name":"exercise","completed":false}]}],"days":[{"year":"2016","weekNumber":"14","month":"4","dayOfMonth":"8","goals":[{"name":"Watch TV","completed":false}]},{"year":"2016","weekNumber":"14","month":"4","dayOfMonth":"11","goals":[{"name":"Do Fun Stuff","completed":false}]}],"weeks":[{"year":"2016","weekNumber":"15","goals":[{"name":"Do Fun Stuff","completed":false}]}]}
 *
 * use http://jsonprettyprint.com/ to view the JSON object
 *
 * the tag corresponds to the appropriate string constant defined in this class
 */
public interface iRewardsModel {
    String TAG_POINTS="numberOfPoints";
    String TAG_REWARDS="listOfRewards";
    String TAG_QUOTE_OF_THE_DAY="quoteOfTheDay";

    /**
     * Fetches the number of points and sends with the correct tag to
     * the observers
     */
    void fetchPoints();

    /**
     * Sets the number of points, updates the database and then calls
     * fetchPoints
     */
    void setPoints(int points);

    /**
     * Fetches all possible rewards from the database and returns with an update
     * and the correct tag.
     */
    void fetchRewards();

    /**
     * Fetches quote of the day from the database and returns with an update
     * and the correct tag.
     */
    void fetchQuoteOfTheDay();
}
