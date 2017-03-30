echo "******** start log *****"
echo `pwd`
travis logs --help
echo "y" | travis login --github-token "6c33d8f5d9a9f2fbe2bb1a7d1ec243ac9a1aebb0"
varrr = $(`travis logs --no-interactive`)
#echo "yes" | travis logs > /dev/null
echo "******** stop log *****"
echo $varr
