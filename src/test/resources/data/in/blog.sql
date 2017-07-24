(select t.Blog as "Blog", t.Titre as "Titre", t.Texte as "Texte", t.Auteur as "Auteur", t.Note as "Note", '3' as Résultat from blog t where t.Auteur = 'anonymous')
UNION
(select t.Blog as "Blog", t.Titre as "Titre", t.Texte as "Texte", t.Auteur as "Auteur", t.Note as "Note", '' as Résultat from blog t where t.Auteur <> 'anonymous')
ORDER BY "Texte"
