@LoginLogout
Feature: LoginLogout (Scenario that login and logout with any good user.) 

	Scenario Outline:  Scenario that login and logout with any good user.
	
    Given I check that user '<user>' is not empty
    Given I check that password '<password>' is not empty
    
    Given 'GEOBEER_HOME' is opened
    Then The GEOBEER home page is displayed
      
    When I log in to GEOBEER as '<user>' '<password>'
    Then The GEOBEER portal is displayed
    
    And I wait 3 seconds
    
    When I log out of GEOBEER
    Then The GEOBEER logout page is displayed

    And I go back to 'GEOBEER_HOME'
		
	Examples:
    #DATA
    |id|user|password|
    |1|demo|demo|
    |2|demo|demo|
    |3|demo|demo|
    #END