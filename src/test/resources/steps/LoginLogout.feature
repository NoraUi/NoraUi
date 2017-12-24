@LoginLogout
Feature: LoginLogout (Scenario that login and logout with any good user.) 

	Scenario Outline:  Scenario that login and logout with any good user.
	
    Given I check that user '<user>' is not empty.
    Given I check that password '<password>' is not empty.
    
    Given 'COUNTRIES_HOME' is opened.
    Then The COUNTRIES home page is displayed
      
    When I log in to COUNTRIES as '<user>' '<password>'
    Then The COUNTRIES portal is displayed
    
    And I wait '3' seconds.
    
    When I log out of COUNTRIES
    Then The COUNTRIES logout page is displayed

    And I go back to 'COUNTRIES_HOME'
		
	Examples:
    #DATA
    |id|user|password|
    |1|demo|demo|
    |2|demo|demo|
    |3|demo|demo|
    #END