(select t.location as "location", t.language as "language", t.data as "data", '' as Result from sampleRESTAPI t where t.language = 'java')
UNION
(select t.location as "location", t.language as "language", t.data as "data", '' as Result from sampleRESTAPI t where t.language = 'javascript')
ORDER BY "language"