@hello 
Feature: hello (Function to validate the environment.) 

	Scenario Outline: Function to validate the environment.
    Given I check that author '<author>' is not empty.
    
    Given I check mandatory fields:
        |author|<author>|
        |city|<city>|
        |element|<element>|
        
    Then I wait '1' seconds.
         
    Given 'DEMO_HOME' is opened?
    Then The DEMO portal is displayed.
		
    Given me a hello, please. Best Regards '<author>'.
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
		
    When I clear text in 'demo.DemoPage-input_text_field'.
    When I update text 'demo.DemoPage-input_text_field' with '<author>'.
    When I update text 'demo.DemoPage-input_text_field' and type ENTER with '<author>'.
    And I check mandatory field 'demo.DemoPage-input_text_field' of type 'text'.
    And I check text 'demo.DemoPage-input_text_field' with '<author>'.
    When I update select list 'demo.DemoPage-input_select_field' with '<city>'.
    Then I update date 'demo.DemoPage-input_text_field' with a 'future' date '<date>'.
    
    And I update radio list 'demo.DemoPage-rate' with 'day'.
    Then I update checkboxes and check radio list 'DEMO_HOME-agree' with 'yes':
        |yes|true|
	      |no|false|
    
    And I save the value of 'demo.DemoPage-input_text_field'.
    
    When I click on 'demo.DemoPage-submit'.
		
    Given me a error if '<city>' is Paris.
    
    When I click by js on 'demo.DemoPage-smilejs'.
    And I check message 'OK' on alert
    
    When I click by js on xpath './/*[@name=\'smilejs\' and @title=\'smilejs\']' from 'demo.DemoPage' page.
    And I check message 'OK' on alert
    
    When I click on 'demo.DemoPage-<element>'.
    And I check message 'OK' on alert
		
    Given test for 'Annulée'
    
    Then If '' matches '', I do '2' times:
        |key|step|expected|actual|
        |1|I wait '3' seconds.|1|1|
        |2|I wait '4' seconds.|1|2|
    
    Then If '' matches '', I do until 'myOutLoopKey' respects 'Rennes' with '4' max tries: 
        |key|step|expected|actual|
        |1|I wait '3' seconds.|1|1|
        |2|I wait '4' seconds.|1|2|
        |3|I update select list 'demo.DemoPage-input_select_field' with '<city>'|||
        |4|I save the value of 'demo.DemoPage-input_select_field' in 'myOutLoopKey' context key.|||
    
  Examples:
    #DATA
    |id|author|zip|city|element|date|
    #END