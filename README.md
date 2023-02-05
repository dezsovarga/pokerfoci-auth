# pokerfoci_auth_api
pokerfoci auth api

# Docker command for mysql:

docker run --detach --env MYSQL_ROOT_PASSWORD=root --env MYSQL_USER=pokerfoci --env MYSQL_PASSWORD=root --env MYSQL_DATABASE=pokerfoci_auth --name pokerfoci-mysql --publish 3306:3306 mysql