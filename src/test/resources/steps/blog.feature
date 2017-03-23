@blog 
Feature: blog (Function to validate the environment.) 

	Scenario Outline: Function to validate the environment.
    Given 'DEMO_HOME' is opened.
    Then The DEMO portal is displayed.
		
    Given me any article, please. '<DataJson>' of '<Blog>'.
    
    Given test for 'Annulée'.
		
  Examples:
    #DATA
    |id|Blog|DataJson|
    #END