@hello @AC001 @AC002 @AC003
Feature: hello (Function to validate the environment.) 

  Scenario Outline: Function to validate the environment.

    Given I check that 'author' '<author>' is not empty
    Given I check that 'city' '<city>' is not empty

    Given I start video capture in 'hello'?
        |key|expected|actual|
        |id|1|<id>|

    Given I check mandatory fields:
        |author|<author>|
        |city|<city>|
        |element|<element>|
        |element2|<element2>|
        
    Then I wait 1 second

    Given 'BAKERY_DEMO' is opened
    Then The DEMO portal is displayed

    Given me a hello, please. Best Regards '<author>'
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

    When I use $bakery.DemoPage-file_input_text_field element to upload 'test.txt' file
    And I click on $bakery.DemoPage-send
    Then I check text $bakery.DemoPage-input_text_field with 'test.txt'
    And I clean download directory
		
    Given My fields are ready to use
    When I clear text in $bakery.DemoPage-input_text_field
    When I update text $bakery.DemoPage-input_text_field with '<author>'
    When I update text $bakery.DemoPage-input_text_field and type ENTER with '<author>'
    And I check mandatory field $bakery.DemoPage-input_text_field of type 'text'
    And I check text $bakery.DemoPage-input_text_field with '<author>'
    When I update select list $bakery.DemoPage-input_select_field with '<city>'
    Then I update date $bakery.DemoPage-input_text_field with a 'future' date '<date>'

    And I update radio list $bakery.DemoPage-rate with 'day'

    Then I update checkboxes and check radio list 'DEMO_HOME-agree' with 'yes':
        |yes|true|
        |no|false|

    And I save the value of $bakery.DemoPage-input_text_field

    When I click on $bakery.DemoPage-submit

    Given me a error if '<city>' is Paris.

    And I check text $bakery.DemoPage-input_current_date with '0'
    When I click by js on $bakery.DemoPage-smilejs
    And I check message 'OK' on alert
    And I check text $bakery.DemoPage-input_current_date with '1'

    When I click by js on xpath './/*[@name=\'smilejs\' and @title=\'smilejs\']' from $bakery.DemoPage page
    And I check message 'OK' on alert
    And I check text $bakery.DemoPage-input_current_date with '2'

    When I click by js on $bakery.DemoPage-smilejsIssue80
    And I check message 'OK' on alert
    And I check text $bakery.DemoPage-input_current_date with '3'

    When I click on $bakery.DemoPage-<element>
    And I check message 'OK' on alert
    And I check text $bakery.DemoPage-input_current_date with '4'

    Given test for 'Annulée'

    Then If '' matches '', I do 2 times:
        |key|step|expected|actual|
        |1|I wait 3 seconds?|1|1|
        |2|I wait 4 seconds?|1|2|

    Then If '' matches '', I do until 'myOutLoopKey' respects 'Rennes' with 4 max tries: 
        |key|step|expected|actual|
        |1|I wait 3 seconds?|1|1|
        |2|I wait 4 seconds?|1|2|
        |3|I update select list $bakery.DemoPage-input_select_field with '<city>'|||
        |4|I save the value of $bakery.DemoPage-input_select_field in 'myOutLoopKey' context key|||

    Then If '<author>' matches '.+', I do until 'bakery.DemoPage-big_title' respects 'This is a demo for NORAUI.*' with 3 max tries:
        |key|step|expected|actual|
        |1|I wait 1 second|||
        |2|I wait 1 second?|(Rennes\|Paris\|New York)|<city>|

    Then If '<author>' matches '.+', While 'bakery.DemoPage-big_title' respects 'This is a demo for NORAUI.*' I do with 3 max tries:    
        |key|step|expected|actual|
        |1|I wait 1 second|||
        |2|I wait 1 second?|(Rennes\|Paris\|New York)|<city>|

    And I save the value of $bakery.DemoPage-big_title in 'title' column of data output provider

    And The element $bakery.DemoPage-big_title should be present
    And The element $bakery.DemoPage-noExistElement should not be present

    And The element $bakery.DemoPage-big_title is visible
    And The element $bakery.DemoPage-visibility_hidden_title is not visible
    And The element $bakery.DemoPage-display_none_title is not visible

    When I clear text in $bakery.DemoPage-input_text_field
    When I update text $bakery.DemoPage-input_text_field and type ENTER with '<zip>'
    And I check mandatory field $bakery.DemoPage-input_text_field of type 'text'

    And I update radio list $bakery.DemoPage-rate with '<author>' from these values:
        |Jenkins T1|week-end|
        |Jenkins T2|night|
        |Default|day|

    Then I update checkbox $bakery.DemoPage-iagree with 'true'
    Then I update checkbox $bakery.DemoPage-iagree with 'true'?
        |key|expected|actual|
        |wid|.+|<author>|
    Then I update checkbox $bakery.DemoPage-iagree with 'yes' from these values:
        |yes|true|
        |Default|false|
    Then I update checkbox $bakery.DemoPage-iagree with '<author>' from these values:
        |Jenkins T1|true|
        |Default|false|    

    When I click on $bakery.DemoPage-open_popup_button1 and switch to 'bakery.Popup1DemoPage' window
    Then I close current window and switch to 'bakery' window

    When I click on $bakery.DemoPage-open_popup_button1 and switch to 'bakery.Popup1DemoPage' window
    When I switch to 'bakery' window
    When I click on $bakery.DemoPage-open_popup_button2 and switch to 'bakery.Popup2DemoPage' window
    When I switch to 'bakery' window
    When I click on $bakery.DemoPage-open_popup_button3 and switch to 'bakery.Popup3DemoPage' window
    When I open a new window
    Then I close all windows except 'bakery'
    When I switch to 'bakery' window

    When I clear text in $bakery.DemoPage-input_text_field
    When I update text $bakery.DemoPage-input_text_field with '01234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789'
    And I check text $bakery.DemoPage-input_text_field with '01234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789'

    When I click on $bakery.DemoPage-changeValueButton
    Then I expect to have $bakery.DemoPage-changeValueButton with the text 'My value has changed'

    And I take a screenshot
    And I save a screenshot in 'myScreenshot-<id>'
    And I save a screenshot of $bakery.DemoPage-changeValueButton in 'changeValueButton<id>'

    Then I click on $bakery.DemoPage-disappearButton
    And The element $bakery.DemoPage-disappearButton should be invisible within 10 seconds

    Then I click on $bakery.DemoPage-staleButton
    And The element $bakery.DemoPage-disappearButton should become stale within the 10 seconds

    And I update text $bakery.DemoPage-input_text_field with ramdom match 'noraui[a-zA-Z0-9]{6}'

    When I clear text in $bakery.DemoPage-input_text_field
    When I update text $bakery.DemoPage-input_text_field with '℗:AMAapQjwjKaAGUkO6rbttg=='
    And I check text $bakery.DemoPage-input_text_field with 'foot'
    And I check text $bakery.DemoPage-input_text_field with '℗:AMAapQjwjKaAGUkO6rbttg=='

    Then I click on $bakery.DemoPage-navbarDropdownMenuLink
    And I pass over $bakery.DemoPage-navbarDropdownSubMenu
    And I pass over $bakery.DemoPage-navbarDropdownSubSubMenu1
    And I click on $bakery.DemoPage-navbarDropdownSubSubMenu2

    And I save the value of REST API 'GET' 'GITHUBAPI_HOME' '/search/users?q=location:rennes+language:java&page=1&per_page=10' in 'title' context key
    And I save the value of REST API 'GET' 'GITHUBAPI_HOME' '/search/users?q=location:rennes+language:java&page=1&per_page=10' in 'title' column of data output provider

    Given I stop video capture?
        |key|expected|actual|
        |id|1|<id>|

    And I click on $bakery.DemoPage-exportCSV
    And I wait file named 'stock-data.csv' to be downloaded with timeout of 3 seconds
    And The file 'stock-data.csv' encoded in 'ISO-8859-1' matches 'Symbol;Company;Price'
    And I remove 'stock-data.csv' file in download directory
    
    And I update text $bakery.DemoPage-input_text_field and type ESCAPE with '<author>'

    And I go back to 'BAKERY_DEMO'        

  Examples:
    #DATA
    |id|author|zip|city|element|element2|date|title|
    |1|Jenkins T1|35000|Rennes|smile|smile|2050-01-16||
    |2|Jenkins T2|75000|Paris|smile|smile|||
    |3|Jenkins T3|56100|Lorient|smile|smile|||
    |4|Jenkins T4|35000|Rennes|smile|smile|||
    |5|Jenkins T5|35000|Rennes|noExistElement|noExistElement|||
    |6|Jenkins T6|35000||smile|smile|||
    |7|Jenkins T7|35000|Rennes|||||
    |8|Jenkins T8||Rennes|smile|smile|||
    #END
