mkdir -p $(dirname /tmp/$1) && markdown $1 > /tmp/$1.html && firefox /tmp/$1.html
