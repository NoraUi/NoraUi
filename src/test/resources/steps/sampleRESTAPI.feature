@sampleRESTAPI 
Feature: sampleRESTAPI (Function to validate REST API features) 

  Scenario Outline: Function to validate REST API features.
  
    Given 'GITHUBAPI_HOME' is opened
    Then The GITHUBAPI portal is displayed.
		
    And I save the value of REST API 'GET' 'GITHUBAPI_HOME' '/search/users?q=location:rennes+language:java&page=1&per_page=10' in 'data' context key
    And I save the value of REST API 'GET' 'GITHUBAPI_HOME' '/search/users?q=location:rennes+language:java&page=1&per_page=10' in 'data' column of data output provider

    And I go back to 'GITHUBAPI_HOME'        

  Examples:
    #DATA
    |id|location|language|data|
    |1|rennes|java||
    |2|rennes|java||
    #END
