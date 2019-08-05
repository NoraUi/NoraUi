select b.blog as "Blog", b.title as "Title", b.text as "Text", b.author as "Author", b.note as "Note", b.result as "Result" from (
  (select t.id, t.blog, t.title, t.text, t.author, t.note, '3' as "result" from blog t where t.author = 'anonymous')
  UNION
  (select t.id, t.blog, t.title, t.text, t.author, t.note, '' as "result" from blog t where t.author <> 'anonymous')
  ORDER BY "id"
) b