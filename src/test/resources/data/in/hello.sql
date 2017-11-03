(select t.author as "author", t.zip as "zip", t.city as "city", t.element as "element", t.element2 as "element2", t.date as "date", t.title as "", '24' as Result from hello t where t.city = 'Paris')
UNION
(select t.author as "author", t.zip as "zip", t.city as "city", t.element as "element", t.element2 as "element2", t.date as "date", t.title as "", '18' as Result from hello t where t.city = 'Lorient')
UNION
(select t.author as "author", t.zip as "zip", t.city as "city", t.element as "element", t.element2 as "element2", t.date as "date", t.title as "", '32' as Result from hello t where t.element = 'noExistElement')
UNION
(select t.author as "author", t.zip as "zip", t.city as "city", t.element as "element", t.element2 as "element2", t.date as "date", t.title as "", '' as Result from hello t where t.city <> 'Paris' AND t.city <> 'Lorient' AND t.element <> 'noExistElement')
UNION
(select t.author as "author", t.zip as "zip", t.city as "city", t.element as "element", t.element2 as "element2", t.date as "date", t.title as "", '2' as Result from hello t where t.city = '')
UNION
(select t.author as "author", t.zip as "zip", t.city as "city", t.element as "element", t.element2 as "element2", t.date as "date", t.title as "", '3' as Result from hello t where t.element = '' and t.element2 = '')
UNION
(select t.author as "author", t.zip as "zip", t.city as "city", t.element as "element", t.element2 as "element2", t.date as "date", t.title as "", '48' as Result from hello t where t.zip = '')
ORDER BY "author"