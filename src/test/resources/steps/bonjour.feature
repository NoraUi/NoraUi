# language: fr
@bonjour
Fonctionnalité: bonjour (Fonction pour valider l environnement.) 

  Plan du Scénario: Function to validate the environment.
    Lorsque Je vérifie que author '<author>' n'est pas vide.
    Lorsque Je vérifie que city '<city>' n'est pas vide.

    Lorsque Je commence la capture vidéo dans 'hello'?
        |key|expected|actual|
        |id|1|<id>|

    Lorsque Je vérifie les champs obligatoires:
        |author|<author>|
        |city|<city>|
        |element|<element>|
        |element2|<element2>|

    Alors Je patiente '1' secondes.

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

    Lorsque Je fait la créaton du fichier test.txt dans repertoire des téléchargements
    Quand J'utilise l'élément 'demo.DemoPage-file_input_text_field' pour uploader le fichier 'test.txt'.
    Et Je clique sur 'demo.DemoPage-send'.
    Alors Je vérifie le texte 'demo.DemoPage-input_text_field' avec 'test.txt'.
    Lorsque Je vide le repertoire des téléchargements.

    Quand Je clarifie le texte dans 'demo.DemoPage-input_text_field'.
    Quand Je mets à jour le texte 'demo.DemoPage-input_text_field' avec '<author>'.
    Quand Je mets à jour le texte 'demo.DemoPage-input_text_field' et entre ENTRER avec '<author>'.
    Et Je vérifie le champ obligatoire 'demo.DemoPage-input_text_field' de type 'text'.
    Et Je vérifie le texte 'demo.DemoPage-input_text_field' avec '<author>'.
    Quand Je mets à jour la liste déroulante 'demo.DemoPage-input_select_field' avec '<city>'.
    Alors Je mets à jour la date 'demo.DemoPage-input_text_field' avec une 'future' date '<date>'.

    Et Je mets à jour la liste radio 'demo.DemoPage-rate' avec 'day'.
    Alors Je mets à jour les checkboxes et vérifie la liste radio 'DEMO_HOME-agree' avec 'yes':
        |yes|true|
        |no|false|

    Et Je sauvegarde la valeur de 'demo.DemoPage-input_text_field'.

    Quand Je clique sur 'demo.DemoPage-submit'.

    Lorsque moi une erreur si '<city>' est Paris.

    Et Je vérifie le texte 'demo.DemoPage-input_current_date' avec '0'.
    Quand Je clique via js sur 'demo.DemoPage-smilejs'.
    Et Je vérifie le message 'OK' sur l'alerte
    Et Je vérifie le texte 'demo.DemoPage-input_current_date' avec '1'.

    Quand Je clique via js sur xpath './/*[@name=\'smilejs\' and @title=\'smilejs\']' de 'demo.DemoPage' page.
    Et Je vérifie le message 'OK' sur l'alerte
    Et Je vérifie le texte 'demo.DemoPage-input_current_date' avec '2'.

    Quand Je clique via js sur 'demo.DemoPage-smilejsIssue80'.
    Et Je vérifie le message 'OK' sur l'alerte
    Et Je vérifie le texte 'demo.DemoPage-input_current_date' avec '3'.

    Quand Je clique sur 'demo.DemoPage-<element>'.
    Et Je vérifie le message 'OK' sur l'alerte
    Et Je vérifie le texte 'demo.DemoPage-input_current_date' avec '4'.

    Lorsque test pour 'Annulée'

    Alors Si '' vérifie '', je fais '2' fois:
        |key|step|expected|actual|
        |1|Je patiente '3' secondes.|1|1|
        |2|Je patiente '4' secondes.|1|2|

    Alors Si '' vérifie '', je fais jusqu'à 'myOutLoopKey' respecte 'Rennes' avec '4' essais maxi: 
        |key|step|expected|actual|
        |1|Je patiente '3' secondes.|1|1|
        |2|Je patiente '4' secondes.|1|2|
        |3|Je mets à jour la liste déroulante 'demo.DemoPage-input_select_field' avec '<city>'.|||
        |4|Je sauvegarde la valeur de 'demo.DemoPage-input_select_field' dans la clé 'myOutLoopKey' du contexte.|||

    Lorsque Si '<author>' vérifie '.+', je fais jusqu'à 'demo.DemoPage-big_title' respecte 'This is a demo for NORAUI.*' avec '3' essais maxi:
        |key|step|expected|actual|
        |1|I wait '1' seconds?|||
        |2|I wait '1' seconds?|(Rennes\|Paris\|New York)|<city>|

    Lorsque Si '<author>' vérifie '.+', Tant que 'demo.DemoPage-big_title' respecte 'This is a demo for NORAUI.*' je fais avec '3' essais maxi:
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
    Quand Je mets à jour le texte 'demo.DemoPage-input_text_field' et entre ENTRER avec '<zip>'.
    Et Je vérifie le champ obligatoire 'demo.DemoPage-input_text_field' de type 'text'.

    Et Je mets à jour la liste radio 'demo.DemoPage-rate' avec '<author>' à partir de ces valeurs:
      |Jenkins T1|week-end|
      |Jenkins T2|night|
      |Default|day|

    Lorsque Je mets à jour la case à cocher 'demo.DemoPage-iagree' avec 'true'.
    Lorsque Je mets à jour la case à cocher 'demo.DemoPage-iagree' avec 'true'?
        |key|expected|actual|
        |wid|.+|<author>|
    Lorsque Je mets à jour la case à cocher 'demo.DemoPage-iagree' avec 'yes' à partir de ces valeurs:
        |yes|true|
        |Default|false|
    Lorsque Je mets à jour la case à cocher 'demo.DemoPage-iagree' avec '<author>' à partir de ces valeurs:
        |Jenkins T1|true|
        |Default|false| 

    Quand Je clique sur 'demo.DemoPage-open_popup_button1' et passe sur 'demo.Popup1DemoPage' de type fenêtre.
    Lorsque Je ferme la fenêtre actuelle et passe à la fenêtre 'demo'.

    Quand Je clique sur 'demo.DemoPage-open_popup_button1' et passe sur 'demo.Popup1DemoPage' de type fenêtre.
    Quand Je passe à la fenêtre 'demo'.
    Quand Je clique sur 'demo.DemoPage-open_popup_button2' et passe sur 'demo.Popup2DemoPage' de type fenêtre.
    Quand Je passe à la fenêtre 'demo'.
    Quand Je clique sur 'demo.DemoPage-open_popup_button3' et passe sur 'demo.Popup3DemoPage' de type fenêtre.
    Quand J'ouvre une nouvelle fenêtre. 
    Lorsque I close all windows except 'demo'.
    Quand Je passe à la fenêtre 'demo'.

    Quand Je clarifie le texte dans 'demo.DemoPage-input_text_field'.
    Quand Je mets à jour le texte 'demo.DemoPage-input_text_field' avec '01234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789'.
    Et Je vérifie le texte 'demo.DemoPage-input_text_field' avec '01234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789'.

    Quand Je clique sur 'demo.DemoPage-changeValueButton'.
    Alors Je m'attends à avoir 'demo.DemoPage-changeValueButton' avec le texte 'My value has changed'.

    Et Je prends une capture d'écran.
    Et Je sauvegarde une capture d'écran dans 'myScreenshot-<id>'.
    Et Je sauvegarde une capture d'écran de 'demo.DemoPage-changeValueButton' dans 'changeValueButton<id>'.

    Quand Je clique sur 'demo.DemoPage-disappearButton'.
    Et Je patiente l'invisibilité de 'demo.DemoPage-disappearButton' avec un timeout de '10' secondes.

    Quand Je clique sur 'demo.DemoPage-staleButton'.
    Et Je patiente la disparition de 'demo.DemoPage-staleButton' avec un timeout de '10' secondes.

    Et Je mets à jour le texte 'demo.DemoPage-input_text_field' avec une valeur aléatoire qui vérifie 'noraui[a-zA-Z0-9]{6}'.

    Quand Je clarifie le texte dans 'demo.DemoPage-input_text_field'.
    Quand Je mets à jour le texte 'demo.DemoPage-input_text_field' avec '℗:AMAapQjwjKaAGUkO6rbttg=='.
    Et Je vérifie le texte 'demo.DemoPage-input_text_field' avec 'foot'.
    Et Je vérifie le texte 'demo.DemoPage-input_text_field' avec '℗:AMAapQjwjKaAGUkO6rbttg=='.

    Alors Je clique sur 'demo.DemoPage-navbarDropdownMenuLink'.
    Et Je passe au dessus de 'demo.DemoPage-navbarDropdownSubMenu'.
    Et Je passe au dessus de 'demo.DemoPage-navbarDropdownSubSubMenu1'.
    Et Je clique sur 'demo.DemoPage-navbarDropdownSubSubMenu2'.

    Et Je sauvegarde la valeur de cette API REST 'GET' 'GITHUBAPI_HOME' '/search/users?q=location:rennes+language:java&page=1&per_page=10' dans 'title' du contexte.
    Et Je sauvegarde la valeur de cette API REST 'GET' 'GITHUBAPI_HOME' '/search/users?q=location:rennes+language:java&page=1&per_page=10' dans 'title' du fournisseur de données en sortie.

    Et Je stop la capture vidéo?
        |key|expected|actual|
        |id|1|<id>|

    Et Je clique sur 'demo.DemoPage-exportCSV'.
    Et Je patiente que le fichier nommé 'stock-data.csv' soit téléchargé avec un timeout de '3' secondes.
    Et Le fichier 'stock-data.csv' encodé en 'ISO-8859-1' vérifie 'Symbol;Company;Price'.
    Lorsque Je supprime le fichier 'stock-data.csv' dans repertoire des téléchargements.

    Et Je retourne vers 'DEMO_HOME'

  Exemples:
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
