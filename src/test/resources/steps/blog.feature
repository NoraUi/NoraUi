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
    |1|Blog 1|[{"title":"Article 1","text":"text 1","author":"Peter","note":7},{"title":"Article 2","text":"text 2","author":"Peter","note":10},{"title":"Article 5","text":"text 5","author":"Peter","note":9}]|
    |2|Blog 2|[{"title":"Article 3","text":"text 3","author":"Peter","note":8},{"title":"Article 4","text":"text 4","author":"anonymous","note":2}]|
    #END
