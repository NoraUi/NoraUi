(select t.author as "author", t.zip as "zip", t.city as "city", t.element as "element", t.date as "date", '23' as Résultat from hello t where t.city = 'Paris')
UNION
(select t.author as "author", t.zip as "zip", t.city as "city", t.element as "element", t.date as "date", '17' as Résultat from hello t where t.city = 'Lorient')
UNION
(select t.author as "author", t.zip as "zip", t.city as "city", t.element as "element", t.date as "date", '28' as Résultat from hello t where t.element = 'noExistElement')
UNION
(select t.author as "author", t.zip as "zip", t.city as "city", t.element as "element", t.date as "date", '' as Résultat from hello t where t.city <> 'Paris' AND t.city <> 'Lorient' AND t.element <> 'noExistElement')
ORDER BY "author"
