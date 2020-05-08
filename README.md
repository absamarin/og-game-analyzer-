bayes-dota
==========

This is the [task](TASK.md).

Any additional information about your solution goes here.

Unanswered questions
====
    What will happen if a operator uploads a file twice?
    for game-ingest it's better to get the fileName as a parameter and store as a unique name in MatchTable.


  
  
Change Management
==== 
    gg.bayes.challenge.logfileutility.PayloadLogUtility: is a logUtility for reading and analyzing the log of a match
 
    predicted change is adding a new event without changing the existing class here is the steps:
    1- creating a new Event and implementing GameEvent interface.  
    3- adding keyword to PayloadLogUtility.lineAnalyzer

General features
====
    
*   Ingesting a log file is transactional in case of any record all the effects should be rolled back
* tested code coverage with Jacoco and coverage of 94 %

    
