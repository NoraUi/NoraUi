(select "Blog" as "Blog", "Titre" as "Titre", "Texte" as "Texte", "Auteur" as "Auteur", "Note" as "Note", '3' as Résultat from blog where "Auteur" = 'anonymous'
UNION
select "Blog" as "Blog", "Titre" as "Titre", "Texte" as "Texte", "Auteur" as "Auteur", "Note" as "Note", '' as Résultat from blog where "Auteur" <> 'anonymous')
ORDER BY "Texte"