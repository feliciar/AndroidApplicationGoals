package com.curious.dina.goals.Model;

/**
 * The Goal Planner model is responsible for everything that
 * concerns goal planning, that is creating new goals,
 * viewing goals for a specific time period, changing the time
 * period, and checking a goal as completed.
 *
 * FetchGoalPlannerData will be called automatically when the
 * model is changed, but it must be called by the model the
 * first time it should be collected. The fetched data will be
 * given to all observers of the model (the views).
 */

public interface iGoalPlannerModel {
    int LIFE=0, MONTH=1, WEEK=2, DAY=3;
    String TAG_NUM_COMPLETED_GOALS="numberOfCompletedGoals";
    String TAG_NUM_UNCOMPLETED_GOALS="numberOfUncompletedGoals";
    String TAG_UNCOMPLETED_GOALS="listOfUncompletedGoals";
    String TAG_COMPLETED_GOALS="listOfCompletedGoals";

     /**
     * Call this function, the first time only, when you want to fetch
     * data from the database (or call the setters).
     *
     * This function does not return anything, but
     * informs its observers that data has been changed.
     * The data is sent back through an update
     * as a JSON-object.
     * The object has the form
     *
     * {"tag":"GoalPlannerData","life":{"goals":[{"name":"Do Fun Stuff","completed":false},{"name":"go to Disney Land","completed":true},{"name":"go to Disney land","completed":true}]},"month":{"monthNumber":"4","goals":[{"name":"exercise","completed":true}]},"week":{"weekNumber":"14","goals":[{"name":"see movies","completed":true}]},"weekDays":[{"dayOfWeek":"0","month":4,"monthDay":8,"goals":[{"name":"Watch TV","completed":false}]},{"dayOfWeek":"1","month":4,"monthDay":9,"goals":[{"name":"clap your hands","completed":true}]},{"dayOfWeek":"3","month":4,"monthDay":11,"goals":[{"name":"Do Fun Stuff","completed":false}]}]}
     * Use http://jsonprettyprint.com/ to see the data
     */
     void fetchGoalPlannerData();

    /**
     * Sets the specified goal as completed.
     * Updates the database. (FetchGoalPlannerData is called automatically afterwards).
     * If the goal is a day goal, then it is important that the setWeekDay method has been called,
     * so the correct day goal can be accessed
     *
     * @param goalName the name of the goal that should be set as completed.
     * @param goalType The goaltype can be GoalPlannerModel.DAY, GoalPlannerModel.MONTH,
     *                 GoalPlannerModel.WEEK, or GoalPlannerModel.LIFE
     * @param year If the goal is not a life goal, specify in which year the goal is
     * @param month If the goal is a month-goal or day-goal, specify in which month (if not, it will be ignored)
     * @param week If the goal is a week goal, specify in which week (if not, it will be ignored)
     * @param day If the goal is a day goal, specify in which day (if not, it will be ignored)
     *
     *
     */
     void setGoalAsCompleted(String goalName, int goalType, int year, int month, int week, int day);

    /**
     * Adds this goal to the database.
     * (FetchGoalPlannerData is called automatically afterwards).
     * If the goal is a day goal, then it is important that the setWeekDay method has been called,
     * so the correct day goal can be accessed
     *
     * @param name the name of the goal that should be added.
     * @param goalType The goaltype can be GoalPlannerModel.DAY, GoalPlannerModel.MONTH,
     *                 GoalPlannerModel.WEEK, or GoalPlannerModel.LIFE
     * @param year If the goal is not a life goal, specify in which year the goal is
     * @param month If the goal is a month-goal or day-goal, specify in which month (if not, it will be ignored)
     * @param week If the goal is a week goal, specify in which week (if not, it will be ignored)
     * @param day If the goal is a day goal, specify in which day (if not, it will be ignored)
     *
     *
     */
     void addNewGoal(String name, int goalType, int year, int month, int week, int day );
}
