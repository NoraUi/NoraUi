@tmp 
Feature: tmp (desc) 

	Scenario Outline: desc
        
    Given 'DEMO_HOME' is opened?
    Then The DEMO portal is displayed.
      
    Then I do '2' times:
        |key|step|expected|actual|
        |1|I wait '3' seconds.|1|1|
        |2|I wait '4' seconds.|1|2|
        |3|I toto|||
        |3|I tutu 'true'|||
    
    Then I do until 'myOutLoopKey' respects 'Rennes' with '4' max tries:
        |key|step|expected|actual|
        |1|I wait '3' seconds.|1|1|
        |2|I wait '4' seconds.|1|2|
        |3|I toto|||
        |4|I tutu 'true'|||
        |5|I update select list 'DEMO_HOME-input_select_field' with '<city>'|||
        |6|I save the value of 'demo.DemoPage-input_select_field' in 'myOutLoopKey' context key.|||
        
    Examples:
    #DATA
    |id|author|zip|city|element|date|
    |1|Jenkins T1|35000|Rennes|smile|16/01/2020|
    #END