# language: fr
@bonjour
Fonctionnalité: bonjour (Fonction pour valider l environnement.) 

  Plan du Scénario: Function to validate the environment.
    Lorsque 'DEMO_HOME' est ouvert.
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

  Exemples:
    #DATA
    |id|author|zip|city|element|date|
    #END