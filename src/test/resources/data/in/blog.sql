select b."Blog", b."Title", b."Text", b."Author", b."Note" from ((select t.blog as "Blog", t.title as "Title", t.text as "Text", t.author as "Author", t.note as "Note", t.id as "id", '3' as Result from blog t where t.author = 'anonymous')
UNION
(select t.blog as "Blog", t.title as "Title", t.text as "Text", t.author as "Author", t.note as "Note", t.id as "id", '' as Result from blog t where t.author <> 'anonymous')
ORDER BY "id") b