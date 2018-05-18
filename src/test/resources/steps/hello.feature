@hello 
Feature: hello (Function to validate the environment.) 

  Scenario Outline: Function to validate the environment.
    Given I check that author '<author>' is not empty.
    Given I check that city '<city>' is not empty.
    
    Given I start video capture in 'hello'?
        |key|expected|actual|
        |id|1|<id>|
    
    Given I check mandatory fields:
        |author|<author>|
        |city|<city>|
        |element|<element>|
        |element2|<element2>|
        
    Then I wait '1' seconds.
         
    Given 'DEMO_HOME' is opened.
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
    
    Given I create test.txt file in download directory
    When I use 'demo.DemoPage-file_input_text_field' element to upload 'test.txt' file.
    And I click on 'demo.DemoPage-send'.
    Then I check text 'demo.DemoPage-input_text_field' with 'test.txt'.
    And I clean download directory.

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
    
    And I check text 'demo.DemoPage-input_current_date' with '0'.
    When I click by js on 'demo.DemoPage-smilejs'.
    And I check message 'OK' on alert
    And I check text 'demo.DemoPage-input_current_date' with '1'.
    
    When I click by js on xpath './/*[@name=\'smilejs\' and @title=\'smilejs\']' from 'demo.DemoPage' page.
    And I check message 'OK' on alert
    And I check text 'demo.DemoPage-input_current_date' with '2'.
    
    When I click on 'demo.DemoPage-<element>'.
    And I check message 'OK' on alert
    And I check text 'demo.DemoPage-input_current_date' with '3'.
		
    Given test for 'Annulée'
    
    Then If '' matches '', I do '2' times:
        |key|step|expected|actual|
        |1|I wait '3' seconds.|1|1|
        |2|I wait '4' seconds.|1|2|
    
    Then If '' matches '', I do until 'myOutLoopKey' respects 'Rennes' with '4' max tries: 
        |key|step|expected|actual|
        |1|I wait '3' seconds?|1|1|
        |2|I wait '4' seconds?|1|2|
        |3|I update select list 'demo.DemoPage-input_select_field' with '<city>'.|||
        |4|I save the value of 'demo.DemoPage-input_select_field' in 'myOutLoopKey' context key.|||
        
    Then If '<author>' matches '.+', I do until 'demo.DemoPage-big_title' respects 'This is a demo for NORAUI.*' with '3' max tries:
        |key|step|expected|actual|
        |1|I wait '1' seconds?|||
        |2|I wait '1' seconds?|(Rennes\|Paris\|New York)|<city>|
        
    Then If '<author>' matches '.+', While 'demo.DemoPage-big_title' respects 'This is a demo for NORAUI.*' I do with '3' max tries:    
        |key|step|expected|actual|
        |1|I wait '1' seconds?|||
        |2|I wait '1' seconds?|(Rennes\|Paris\|New York)|<city>|

    And I save the value of 'demo.DemoPage-big_title' in 'title' column of data output provider.

    And I check that 'demo.DemoPage-big_title' is present.
    And I check that 'demo.DemoPage-noExistElement' is not present.

    And I check that 'demo.DemoPage-big_title' is visible.
    And I check that 'demo.DemoPage-visibility_hidden_title' is not visible.
    And I check that 'demo.DemoPage-display_none_title' is not visible.

    When I clear text in 'demo.DemoPage-input_text_field'.
    When I update text 'demo.DemoPage-input_text_field' and type ENTER with '<zip>'.
    And I check mandatory field 'demo.DemoPage-input_text_field' of type 'text'.
    
    And I update radio list 'demo.DemoPage-rate' with '<author>' from these values:
        |Jenkins T1|week-end|
        |Jenkins T2|night|
        |Default|day|
    
    Then I update checkbox 'demo.DemoPage-iagree' with 'true'.
    Then I update checkbox 'demo.DemoPage-iagree' with 'true'?
        |key|expected|actual|
        |wid|.+|<author>|
    Then I update checkbox 'demo.DemoPage-iagree' with 'yes' from these values:
        |yes|true|
        |Default|false|
    Then I update checkbox 'demo.DemoPage-iagree' with '<author>' from these values:
        |Jenkins T1|true|
        |Default|false|    

    When I click on 'demo.DemoPage-open_popup_button1' and switch to 'demo.Popup1DemoPage' window. 
    Then I close current window and switch to 'demo' window.

    When I click on 'demo.DemoPage-open_popup_button1' and switch to 'demo.Popup1DemoPage' window.
    When I switch to 'demo' window.
    When I click on 'demo.DemoPage-open_popup_button2' and switch to 'demo.Popup2DemoPage' window.
    When I switch to 'demo' window.
    When I click on 'demo.DemoPage-open_popup_button3' and switch to 'demo.Popup3DemoPage' window.
    When I open a new window.
    Then I close all windows except 'demo'.
    When I switch to 'demo' window.
    
    When I clear text in 'demo.DemoPage-input_text_field'.
    When I update text 'demo.DemoPage-input_text_field' with '01234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789'.
    And I check text 'demo.DemoPage-input_text_field' with '01234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789'.
    
    When I click on 'demo.DemoPage-changeValueButton'.
    Then I expect to have 'demo.DemoPage-changeValueButton' with the text 'My value has changed'.

    And I take a screenshot.
    And I save a screenshot in 'myScreenshot-<id>'.
    And I save a screenshot of 'demo.DemoPage-changeValueButton' in 'changeValueButton<id>'.

    Then I click on 'demo.DemoPage-disappearButton'.
    And I wait invisibility of 'demo.DemoPage-disappearButton' with timeout of '10' seconds.
    
    Then I click on 'demo.DemoPage-staleButton'.
    And I wait staleness of 'demo.DemoPage-disappearButton' with timeout of '10' seconds.

    When I clear text in 'demo.DemoPage-input_text_field'.
    When I update text 'demo.DemoPage-input_text_field' with '℗:AMAapQjwjKaAGUkO6rbttg=='.
    And I check text 'demo.DemoPage-input_text_field' with 'foot'.
    And I check text 'demo.DemoPage-input_text_field' with '℗:AMAapQjwjKaAGUkO6rbttg=='.
    
    Then I click on 'demo.DemoPage-navbarDropdownMenuLink'.
    And I pass over 'demo.DemoPage-navbarDropdownSubMenu'.
    And I pass over 'demo.DemoPage-navbarDropdownSubSubMenu1'.
    And I click on 'demo.DemoPage-navbarDropdownSubSubMenu2'.

    And I save the value of REST API 'GET' 'GITHUBAPI_HOME' '/search/users?q=location:rennes+language:java&page=1&per_page=10' in 'title' context key.
    And I save the value of REST API 'GET' 'GITHUBAPI_HOME' '/search/users?q=location:rennes+language:java&page=1&per_page=10' in 'title' column of data output provider.

    Given I stop video capture?
        |key|expected|actual|
        |id|1|<id>|

    And I click on 'demo.DemoPage-exportCSV'.
    And I wait file named 'stock-data.csv' to be downloaded with timeout of '3' seconds.
    And The file 'stock-data.csv' encoded in 'ISO-8859-1' matches 'Symbol;Company;Price'.
    And I remove 'stock-data.csv' file in download directory.

    And I go back to 'DEMO_HOME'        

  Examples:
    #DATA
    |id|author|zip|city|element|element2|date|title|
    |1|Jenkins T1|35000|Rennes|smile|smile|16/01/2020||
    |2|Jenkins T2|75000|Paris|smile|smile|||
    |3|Jenkins T3|56100|Lorient|smile|smile|||
    |4|Jenkins T4|35000|Rennes|smile|smile|||
    |5|Jenkins T5|35000|Rennes|noExistElement|noExistElement|||
    |6|Jenkins T6|35000||smile|smile|||
    |7|Jenkins T7|35000|Rennes|||||
    |8|Jenkins T8||Rennes|smile|smile|||
    #END
