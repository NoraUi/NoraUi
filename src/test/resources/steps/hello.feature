@hello 
Feature: hello (Function to validate the environment.) 

	Scenario Outline: Function to validate the environment.
    Given I check that author '<author>' is not empty?
    
    Given I check mandatory fields:
        |author|<author>|
        |city|<city>|
        |element|<element>|
        
    Then I wait '1' seconds.
         
    Given 'DEMO_HOME' is opened?
    Then The DEMO portal is displayed.
		
    Given me a hello, please. Best Regards '<author>'?
    Given me a hello, please. Best Regards '<author>'?
		    |key|expected|actual|
    Given me a hello, please. Best Regards '<author>'?
		    |key|expected|actual|
			  |zip|35000|<zip>|
			  |city|Rennes|<city>|
			  
    Given me a bye, please. Best Regards '<author>':
    Given me a bye, please. Best Regards '<author>':
			  |zip|<zip>|
			  |city|<city>|
			  
    Given me a cat, please. Best Regards '<author>'.
		
    When I update text 'DEMO_HOME-input_text_field' with '<author>'
    And I check text 'demo.DemoPage-input_text_field' with '<author>'?
    When I update select list 'DEMO_HOME-input_select_field' with '<city>'
    Then I update date 'demo.DemoPage-input_text_field' with a 'future' date '<date>'?
    
    And I update radio list 'demo.DemoPage-rate' with 'day'?
    Then I update checkboxes and check radio list 'DEMO_HOME-agree' with 'yes':
        |yes|true|
			  |no|false|
    
    When I click on 'demo.DemoPage-submit'?
		
    Given me a error if '<city>' is Paris.
    
    When I click by js on 'demo.DemoPage-smilejs'?
    And I check message 'OK' on alert
    
    When I click on 'demo.DemoPage-<element>'?
    And I check message 'OK' on alert
		
    Given test for 'Annulée'.
    
  Examples:
    #DATA
    |id|author|zip|city|element|date|
    |1|Jenkins T1|35000|Rennes|smile|16/01/2020|
    |2|Jenkins T2|75000|Paris|smile||
    |3|Jenkins T3|56100|Lorient|smile||
    |4|Jenkins T4|35000|Rennes|smile||
    |5|Jenkins T5|35000|Rennes|noExistElement||
    #END