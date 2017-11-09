docker rm $(docker stop $(docker ps -a -q --filter ancestor=eicke74/authenticationserver --format="{{.ID}}"))
docker run -t --net host eicke74/authenticationserver
