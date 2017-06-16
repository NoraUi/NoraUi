# language: fr
@bonjour
Fonctionnalité: bonjour (Fonction pour valider l environnement.) 

  Plan du Scénario: Function to validate the environment.
    Lorsque Je vérifie que author '<author>' n'est pas vide.
    Lorsque Je vérifie que city '<city>' n'est pas vide.
    
    Lorsque Je vérifie les champs obligatoires:
        |author|<author>|
        |city|<city>|
        |element|<element>|
        |element2|<element2>|
        
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

    Lorsque Si '<author>' matche '.+', je fais jusqu'à 'demo.DemoPage-big_title' respecte 'This is a demo for NORAUI.*' avec '3' essais maxi:
        |key|step|expected|actual|
        |1|I wait '1' seconds?|||
        |2|I wait '1' seconds?|(Rennes\|Paris\|New York)|<city>|
        
    Lorsque Si '<author>' matche '.+', Tant que 'demo.DemoPage-big_title' respecte 'This is a demo for NORAUI.*' je fais avec '3' essais maxi:
        |key|step|expected|actual|
        |1|I wait '1' seconds?|||
        |2|I wait '1' seconds?|(Rennes\|Paris\|New York)|<city>|        

    Et Je sauvegarde la valeur de 'demo.DemoPage-big_title' dans la colonne 'title' du fournisseur de données en sortie.

    Et Je vérifie que 'demo.DemoPage-big_title' est présent.
    Et Je vérifie que 'demo.DemoPage-noExistElement' n'est pas présent.

    Et Je vérifie que 'demo.DemoPage-big_title' est visible.
    Et Je vérifie que 'demo.DemoPage-visibility_hidden_title' n'est pas visible.
    Et Je vérifie que 'demo.DemoPage-display_none_title' n'est pas visible.

    Quand Je clarifie le texte dans 'demo.DemoPage-input_text_field'.
    Quand Je met à jour le texte 'demo.DemoPage-input_text_field' et entre ENTRER avec '<zip>'.
    Et Je vérifie le champ obligatoire 'demo.DemoPage-input_text_field' de type 'text'.

    Et Je met à jour la liste radio 'demo.DemoPage-rate' avec '<author>' à partir de ces valeurs:
      |Jenkins T1|week-end|
      |Jenkins T2|night|
      |Default|day|
      
    Lorsque Je met à jour la case à cocher 'demo.DemoPage-iagree' avec 'true'.
    Lorsque Je met à jour la case à cocher 'demo.DemoPage-iagree' avec 'true'?
        |key|expected|actual|
        |wid|.+|<author>|
    Lorsque Je met à jour la case à cocher 'demo.DemoPage-iagree' avec 'yes' à partir de ces valeurs:
        |yes|true|
        |Default|false|
    Lorsque Je met à jour la case à cocher 'demo.DemoPage-iagree' avec '<author>' à partir de ces valeurs:
        |Jenkins T1|true|
        |Default|false| 

    Quand Je clique sur 'demo.DemoPage-open_popup_button' et passe sur 'demo.PopupDemoPage' de type fenêtre.
    Lorsque Je ferme la fenêtre actuelle et passe à la fenêtre 'demo'.

    Quand J'ouvre une nouvelle fenêtre.
    Quand J'ouvre une nouvelle fenêtre.
    Lorsque I close all windows except 'demo'.

    Et Je retourne vers 'DEMO_HOME'

  Exemples:
    #DATA
    |id|author|zip|city|element|element2|date|
    #END
