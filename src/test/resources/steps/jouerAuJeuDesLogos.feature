# language: fr
@jouerAuJeuDesLogos 
Fonctionnalité: jouerAuJeuDesLogos (Scénario qui joue au jeu des logos) 

  Plan du Scénario: Scénario qui joue au jeu des logos.
    Lorsque Je vérifie que player '<player>' n'est pas vide.
    Lorsque Je vérifie que toutes les marques '<DataJson>' ne sont pas interdites
    
    Lorsque 'LOGOGAME_HOME' est ouvert.
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
    |1|Peter|[{"brand":"amazon"},{"brand":"burgerking"},{"brand":"citroën"},{"brand":"ebay"},{"brand":"hellokitty"},{"brand":"michelin"},{"brand":"napster"},{"brand":"pringles"}]|
    |2|Wendy|[{"brand":"amazon"},{"brand":"burgerking"},{"brand":"citroën"},{"brand":"ebay"},{"brand":"hellokitty"},{"brand":"michelin"},{"brand":"napster"},{"brand":"pringles"},{"brand":"redbull"},{"brand":"reebook"},{"brand":"twitter"},{"brand":"youtube"}]|
    |3|Mr Bean|[{"brand":"hellokitty"},{"brand":"fake"},{"brand":"amazon"}]|
    |4|Steven|[{"brand":"amazon"},{"brand":"michelin"},{"brand":"heineken"},{"brand":"burgerking"}]|
    #END
