simple cache service

                   \       /            _\/_
                     .-'-.              //o\  _\/_
  _  ___  __  _ --_ /     \ _--_ __  __ _ | __/o\\ _
=-=-_=-=-_=-=_=-_= -=======- = =-=_=-=_,-'|"'""-|-,_
 =- _=-=-_=- _=-= _--=====- _=-=_-_,-"          |
jgs=- =- =-= =- = -  -===- -= - ."

saves mime-type image/* for a rainy day.

specification
    - the files will be cached in $pwd/data, so ensure you've ran mkdir data
    - routes:
        /healthz - health endpoint
        /cache/*.{png,jpeg,jpg,gif} - caches image and returns bytestream

gradle
    - Build: ./gradlew build
    - Run:   ./gradlew run

docker
    - Build:            docker build -t cache .
    - Run*:             docker run docker run -v `pwd`/data:/data -p 8080:8080 --name cache cache
    - Run from source*: docker run -v `pwd`/data:/data -p 8080:8080 zvee/cache --name cache

    * These commands assume you're in a directory which contains another nested
      directory named data.
