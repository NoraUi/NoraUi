# language: fr
@bonjour
Fonctionnalité: bonjour (Fonction pour valider l environnement.) 

  Plan du Scénario: Function to validate the environment.
    Lorsque Je vérifie que (.*) '(.*)' n'est pas vide.
    
    Lorsque Je vérifie les champs obligatoires:
        |author|<author>|
        |city|<city>|
        |element|<element>|
        
    Alors J'attends '1' secondes.
    
    Lorsque 'DEMO_HOME' est ouvert?
    Alors Le portail DEMO est affiché.

    Lorsque j'ai un bonjour, s'il vous plaît. Cordialement '<author>'.
    Lorsque j'ai un bonjour, s'il vous plaît. Cordialement '<author>'?
        |key|expected|actual|
    Lorsque j'ai un bonjour, s'il vous plaît. Cordialement '<author>'?
        |key|expected|actual|
        |zip|35000|<zip>|
        |city|Rennes|<city>|
	  
    Lorsque j'ai un au revoir, s'il vous plaît. Cordialement '<author>':
    Lorsque j'ai un au revoir, s'il vous plaît. Cordialement '<author>':
        |zip|<zip>|
        |city|<city>|

    Lorsque moi un chat, s'il vous plaît, meilleures salutations '<author>'.
    
    Quand Je clarifie le texte dans 'demo.DemoPage-input_text_field'.
    Quand Je met à jour le texte 'demo.DemoPage-input_text_field' avec '<author>'.
    Quand Je met à jour le texte 'demo.DemoPage-input_text_field' et entre ENTRER avec '<author>'.
    Et Je vérifie le champ obligatoire 'demo.DemoPage-input_text_field' de type 'text'.
    Et Je vérifie le texte 'demo.DemoPage-input_text_field' avec '<author>'.
    Quand Je met à jour la liste déroulante 'demo.DemoPage-input_select_field' avec '<city>'.
    Alors Je met à jour la date 'demo.DemoPage-input_text_field' avec une 'future' date '<date>'.
    
    Et Je met à jour la liste radio 'demo.DemoPage-rate' avec 'day'.
    Alors Je met à jour les checkboxes et vérifie la liste radio 'DEMO_HOME-agree' avec 'yes':
        |yes|true|
        |no|false|
    
    Et Je sauvegarde la valeur de 'demo.DemoPage-input_text_field'.
    
    Quand Je clique sur 'demo.DemoPage-submit'.
    
    Lorsque moi une erreur si '<city>' est Paris.
    
    Quand Je clique via js sur 'demo.DemoPage-smilejs'.
    Et Je vérifie le message 'OK' sur l'alerte
    
    Quand Je clique via js sur xpath './/*[@name=\'smilejs\' and @title=\'smilejs\']' de 'demo.DemoPage' page.
    Et Je vérifie le message 'OK' sur l'alerte
    
    Quand Je clique sur 'demo.DemoPage-<element>'.
    Et Je vérifie le message 'OK' sur l'alerte
    
    Lorsque test pour 'Annulée'
    
    Alors Si '' matche '', je fais '2' fois:
        |key|step|expected|actual|
        |1|J'attends '3' seconds.|1|1|
        |2|J'attends '4' seconds.|1|2|
    
    Alors Si '' matche '', je fais jusqu'à 'myOutLoopKey' respecte 'Rennes' avec '4' essais maxi: 
        |key|step|expected|actual|
        |1|J'attends '3' seconds.|1|1|
        |2|J'attends '4' seconds.|1|2|
        |3|Je met à jour la liste déroulante 'demo.DemoPage-input_select_field' avec '<city>'|||
        |4|Je sauvegarde la valeur de 'demo.DemoPage-input_select_field' dans la clé 'myOutLoopKey' du contexte.|||

    Et Je retourne vers 'DEMO_HOME'
    
  Exemples:
    #DATA
    |id|author|zip|city|element|date|
    #END
