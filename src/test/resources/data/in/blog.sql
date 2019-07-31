(select t.id as "id", t.blog as "Blog", t.title as "Title", t.text as "Text", t.author as "Author", t.note as "Note", '3' as Result from blog t where t.author = 'anonymous')
UNION
(select t.id as "id", t.blog as "Blog", t.title as "Title", t.text as "Text", t.author as "Author", t.note as "Note", '' as Result from blog t where t.author <> 'anonymous')
ORDER BY "id"