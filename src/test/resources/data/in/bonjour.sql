(select t.author as "auteur", t.zip as "codepostal", t.city as "ville", t.element as "élément", t.element2 as "élément2", t.date as "date", t.title as "titre", '30' as Result from hello t where t.city = 'Paris')
UNION
(select t.author as "auteur", t.zip as "codepostal", t.city as "ville", t.element as "élément", t.element2 as "élément2", t.date as "date", t.title as "titre", '24' as Result from hello t where t.city = 'Lorient')
UNION
(select t.author as "auteur", t.zip as "codepostal", t.city as "ville", t.element as "élément", t.element2 as "élément2", t.date as "date", t.title as "titre", '41' as Result from hello t where t.element = 'noExistElement')
UNION
(select t.author as "auteur", t.zip as "codepostal", t.city as "ville", t.element as "élément", t.element2 as "élément2", t.date as "date", t.title as "titre", '2' as Result from hello t where t.city = '')
UNION
(select t.author as "auteur", t.zip as "codepostal", t.city as "ville", t.element as "élément", t.element2 as "élément2", t.date as "date", t.title as "titre", '4' as Result from hello t where t.element = '' and t.element2 = '')
UNION
(select t.author as "auteur", t.zip as "codepostal", t.city as "ville", t.element as "élément", t.element2 as "élément2", t.date as "date", t.title as "titre", '57' as Result from hello t where t.zip = '')
UNION
(select t.author as "auteur", t.zip as "codepostal", t.city as "ville", t.element as "élément", t.element2 as "élément2", t.date as "date", t.title as "titre", '' as Result from hello t where t.city <> 'Paris' AND t.city <> 'Lorient' AND t.city <> '' AND t.element <> 'noExistElement' AND t.element <> '' AND t.zip <> '')
ORDER BY "auteur"