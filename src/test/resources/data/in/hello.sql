(select "author" as "author", "zip" as "zip", "city" as "city", "element" as "element", "date" as "date", '19' as Résultat from hello where "city" = 'Paris'
UNION
select "author" as "author", "zip" as "zip", "city" as "city", "element" as "element", "date" as "date", '14' as Résultat from hello where "city" = 'Lorient'
UNION
select "author" as "author", "zip" as "zip", "city" as "city", "element" as "element", "date" as "date", '22' as Résultat from hello where "element" = 'noExistElement'
UNION
select "author" as "author", "zip" as "zip", "city" as "city", "element" as "element", "date" as "date", '' as Résultat from hello where "city" <> 'Paris' AND "city" <> 'Lorient' AND "element" <> 'noExistElement')
ORDER BY "author"