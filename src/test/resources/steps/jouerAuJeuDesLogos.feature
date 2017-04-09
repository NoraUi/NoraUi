# language: fr
@jouerAuJeuDesLogos 
Fonctionnalité: jouerAuJeuDesLogos (Scénario qui joue au jeu des logos) 

  Plan du Scénario: Scénario qui joue au jeu des logos.
    Lorsque Je vérifie que player '<player>' n'est pas vide?
    Lorsque 'LOGOGAME_HOME' est ouvert?
    Alors Le portail LOGOGAME est affiché
    
    Alors Je joue avec 'amazon'
    Alors J'ajoute '8' marque(s) aléatoire(s)
    Et Je vérifie le message d'alerte
    
    Alors Je joue avec mon fichier d'entrée '<DataJson>'
    Alors Je valide dans LOGOGAME
    Alors Je sauvegarde le score
    
    Et Je retourne vers 'LOGOGAME_HOME'
    
  Exemples:
    #DATA
    |id|player|DataJson|
    #END