@loginLogout
Feature: loginLogout (Scenario that login and logout with any good user.) 

	Scenario Outline:  Scenario that login and logout with any good user.
	
    Given I check that 'user' '<user>' is not empty
    Given I check that 'user' '<password>' is not empty
    
    Given 'BAKERY_HOME' is opened
    Then The BAKERY home page is displayed
      
    When I log in to BAKERY as '<user>' '<password>'
    Then The administrator part of the BAKERY portal is displayed?
        |key|expected|actual|
        |profile|admin|<profile>|
    Then The referencer part of the BAKERY portal is displayed?
        |key|expected|actual|
        |profile|referencer|<profile>|    
    
    And I wait 3 seconds
    
    When I log out of BAKERY
    Then The BAKERY logout page is displayed

    And I go back to 'BAKERY_HOME'
		
	Examples:
    #DATA
    |id|user|password|profile|
    |1|sgrillon|℗:qmTAYKS9UG87rNuUQ0Ao6Q==|admin|
    |2|sgrillon2|℗:qmTAYKS9UG87rNuUQ0Ao6Q==|referencer|
    #END