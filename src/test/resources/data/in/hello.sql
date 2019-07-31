(select t.author as "author", t.zip as "zip", t.city as "city", t.element as "element", t.element2 as "element2", t.date as "date", t.title as "title", '30' as Result from hello t where t.city = 'Paris')
UNION
(select t.author as "author", t.zip as "zip", t.city as "city", t.element as "element", t.element2 as "element2", t.date as "date", t.title as "title", '24' as Result from hello t where t.city = 'Lorient')
UNION
(select t.author as "author", t.zip as "zip", t.city as "city", t.element as "element", t.element2 as "element2", t.date as "date", t.title as "title", '41' as Result from hello t where t.element = 'noExistElement')
UNION
(select t.author as "author", t.zip as "zip", t.city as "city", t.element as "element", t.element2 as "element2", t.date as "date", t.title as "title", '2' as Result from hello t where t.city = '')
UNION
(select t.author as "author", t.zip as "zip", t.city as "city", t.element as "element", t.element2 as "element2", t.date as "date", t.title as "title", '4' as Result from hello t where t.element = '' and t.element2 = '')
UNION
(select t.author as "author", t.zip as "zip", t.city as "city", t.element as "element", t.element2 as "element2", t.date as "date", t.title as "title", '57' as Result from hello t where t.zip = '')
UNION
(select t.author as "author", t.zip as "zip", t.city as "city", t.element as "element", t.element2 as "element2", t.date as "date", t.title as "title", '' as Result from hello t where t.city <> 'Paris' AND t.city <> 'Lorient' AND t.city <> '' AND t.element <> 'noExistElement' AND t.element <> '' AND t.zip <> '')
ORDER BY "author"