docker rm $(docker stop $(docker ps -a -q --filter ancestor=eicke74/travelmanager --format="{{.ID}}"))
docker run -t -p 9020:9020 eicke74/travelmanager
